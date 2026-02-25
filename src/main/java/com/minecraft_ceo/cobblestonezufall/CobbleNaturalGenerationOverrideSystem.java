package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Replaces naturally generated cobblestone (lava + water contact) with configured drops.
 * This closes the gap where natural generation can appear before block-break handling kicks in.
 */
public class CobbleNaturalGenerationOverrideSystem extends EntityEventSystem<EntityStore, PlaceBlockEvent> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final long COOLDOWN_MS = 120L;
    private static final String AIR_BLOCK_ID = "Air";

    private final ConfigManager configManager;
    private final ConcurrentHashMap<String, Long> recentlyPatched = new ConcurrentHashMap<>();

    public CobbleNaturalGenerationOverrideSystem(ConfigManager configManager) {
        super(PlaceBlockEvent.class);
        this.configManager = configManager;
    }

    @Override
    public void handle(int index,
                       ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store,
                       CommandBuffer<EntityStore> commandBuffer,
                       PlaceBlockEvent event) {
        if (event == null) {
            return;
        }
        if (hasPlayerItemInHand(event)) {
            // Do not touch player block placements.
            return;
        }

        Vector3i position = event.getTargetBlock();
        if (position == null) {
            return;
        }

        EntityStore entityStore = store.getExternalData();
        if (entityStore == null) {
            return;
        }
        World world = entityStore.getWorld();
        if (world == null) {
            return;
        }

        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        String key = x + ":" + y + ":" + z;
        long now = System.currentTimeMillis();
        Long until = recentlyPatched.get(key);
        if (until != null && until > now) {
            return;
        }
        if (CobblestoneGeneratorSystem.isRegenPending(x, y, z)) {
            event.setCancelled(true);
            try {
                world.setBlock(x, y, z, AIR_BLOCK_ID, 0);
            } catch (Exception ignored) {
            }
            return;
        }

        recentlyPatched.put(key, now + COOLDOWN_MS);

        CompletableFuture.delayedExecutor(25L, TimeUnit.MILLISECONDS).execute(() ->
                world.execute(() -> {
                    try {
                        BlockType blockType = world.getBlockType(x, y, z);
                        String placedId = blockType == null ? null : blockType.getId();
                        if (!isCobbleId(placedId)) {
                            return;
                        }
                        if (!hasNearbyFluid(world, x, y, z, true) || !hasNearbyFluid(world, x, y, z, false)) {
                            return;
                        }

                        String replacement = resolveReplacementBlockId(configManager.getRandomDropForTier(1), 1);
                        if (replacement == null || replacement.equals(placedId)) {
                            return;
                        }

                        world.setBlock(x, y, z, replacement, 0);
                        LOGGER.at(Level.INFO).log("[CobblestoneZufall][GEN] Natural cobble override at " + key
                                + " -> " + replacement);
                    } catch (Exception ex) {
                        LOGGER.at(Level.WARNING).log("[CobblestoneZufall][GEN] Natural override failed at " + key
                                + " reason=" + ex.getClass().getSimpleName());
                    } finally {
                        recentlyPatched.put(key, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1));
                    }
                })
        );
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }

    private String resolveReplacementBlockId(String configuredId, int tier) {
        String direct = resolveConfiguredToBlockId(configuredId);
        if (direct != null) {
            return direct;
        }
        for (ConfigManager.DropEntry entry : configManager.getDropsForTier(tier)) {
            String blockId = resolveConfiguredToBlockId(entry == null ? null : entry.itemId);
            if (blockId != null) {
                return blockId;
            }
        }
        return null;
    }

    private static String resolveConfiguredToBlockId(String configuredId) {
        if (configuredId == null || configuredId.isBlank()) {
            return null;
        }

        BlockType directBlock = BlockType.fromString(configuredId);
        if (directBlock != null && !directBlock.isUnknown()) {
            return directBlock.getId();
        }

        Item item = Item.getAssetMap().getAsset(configuredId);
        if (item != null && item.getBlockId() != null) {
            BlockType itemBlock = BlockType.fromString(item.getBlockId());
            if (itemBlock != null && !itemBlock.isUnknown()) {
                return itemBlock.getId();
            }
        }
        return null;
    }

    private static boolean hasNearbyFluid(World world, int x, int y, int z, boolean lava) {
        int[][] offsets = new int[][]{
                {1, 0, 0},
                {-1, 0, 0},
                {0, 0, 1},
                {0, 0, -1},
                {0, -1, 0},
                {0, 1, 0}
        };
        for (int[] offset : offsets) {
            int fluidId = world.getFluidId(x + offset[0], y + offset[1], z + offset[2]);
            if (isFluidType(fluidId, lava)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFluidType(int fluidId, boolean lava) {
        if (fluidId <= 0) {
            return false;
        }
        try {
            var fluid = com.hypixel.hytale.server.core.asset.type.fluid.Fluid.getAssetMap().getAsset(fluidId);
            if (fluid == null || fluid.getId() == null) {
                return false;
            }
            String fluidKey = fluid.getId().toLowerCase(Locale.ROOT);
            return lava ? fluidKey.contains("lava") : fluidKey.contains("water");
        } catch (Exception ignored) {
            return false;
        }
    }

    private static boolean isCobbleId(String id) {
        if (id == null) {
            return false;
        }
        return id.equals("Rock_Stone_Cobble") || id.contains("Cobble") || id.contains("cobble");
    }

    private static boolean hasPlayerItemInHand(PlaceBlockEvent event) {
        try {
            var stack = event.getItemInHand();
            return stack != null && !stack.isEmpty();
        } catch (Exception ignored) {
            return false;
        }
    }
}
