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
<<<<<<< HEAD
 * Replaces naturally generated cobblestone (lava + water contact) with configured drops.
 * This closes the gap where natural generation can appear before block-break handling kicks in.
=======
 * Hard-stops natural lava+water cobble generation.
 * Only plugin-managed regeneration is allowed to place generator blocks.
>>>>>>> cd2cc2b (Prepare clean project state)
 */
public class CobbleNaturalGenerationOverrideSystem extends EntityEventSystem<EntityStore, PlaceBlockEvent> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
<<<<<<< HEAD
    private static final long COOLDOWN_MS = 120L;
    private static final String AIR_BLOCK_ID = "Air";
=======
    private static final String AIR_BLOCK_ID = "Empty";
    private static final long PATCH_COOLDOWN_MS = 120L;
    private static final long PATCH_DELAY_MS = 25L;
>>>>>>> cd2cc2b (Prepare clean project state)

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
<<<<<<< HEAD
        if (hasPlayerItemInHand(event)) {
            // Do not touch player block placements.
            return;
        }
=======
>>>>>>> cd2cc2b (Prepare clean project state)

        Vector3i position = event.getTargetBlock();
        if (position == null) {
            return;
        }

        EntityStore entityStore = store.getExternalData();
<<<<<<< HEAD
        if (entityStore == null) {
            return;
        }
        World world = entityStore.getWorld();
        if (world == null) {
            return;
        }
=======
        if (entityStore == null || entityStore.getWorld() == null) {
            return;
        }
        World world = entityStore.getWorld();
>>>>>>> cd2cc2b (Prepare clean project state)

        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
<<<<<<< HEAD
        String key = x + ":" + y + ":" + z;
        long now = System.currentTimeMillis();
        Long until = recentlyPatched.get(key);
        if (until != null && until > now) {
            return;
        }
=======
        boolean playerPlaced = hasPlayerItemInHand(event);
        if (playerPlaced) {
            return;
        }

        String key = x + ":" + y + ":" + z;
        long now = System.currentTimeMillis();
        Long blockedUntil = recentlyPatched.get(key);
        if (blockedUntil != null && blockedUntil > now) {
            return;
        }

        // Hard stop: while a generator slot is pending regeneration, never allow natural cobble at that position.
>>>>>>> cd2cc2b (Prepare clean project state)
        if (CobblestoneGeneratorSystem.isRegenPending(x, y, z)) {
            event.setCancelled(true);
            try {
                world.setBlock(x, y, z, AIR_BLOCK_ID, 0);
            } catch (Exception ignored) {
            }
<<<<<<< HEAD
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
=======
            EngineTraceFinderSystem.event(x, y, z, "PLACE_CANCELLED_PENDING", "action=cancel+air");
            if (configManager.isDebugEnabled()) {
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG] Cancelled natural place during regen-pending at "
                        + x + ":" + y + ":" + z);
            }
            return;
        }

        recentlyPatched.put(key, now + PATCH_COOLDOWN_MS);
        CompletableFuture.delayedExecutor(PATCH_DELAY_MS, TimeUnit.MILLISECONDS).execute(() ->
                world.execute(() -> applyNaturalOverride(world, x, y, z, key))
>>>>>>> cd2cc2b (Prepare clean project state)
        );
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }

<<<<<<< HEAD
=======
    private void applyNaturalOverride(World world, int x, int y, int z, String key) {
        try {
            if (CobblestoneGeneratorSystem.isPluginPlacementAllowed(x, y, z)) {
                EngineTraceFinderSystem.event(x, y, z, "NATURAL_OVERRIDE_SKIP", "reason=plugin-placement-window");
                return;
            }
            BlockType currentType = world.getBlockType(x, y, z);
            String currentId = currentType == null ? null : currentType.getId();
            if (!isCobbleId(currentId)) {
                EngineTraceFinderSystem.event(x, y, z, "NATURAL_OVERRIDE_SKIP", "reason=not-cobble blockId=" + currentId);
                return;
            }
            if (!hasNearbyFluid(world, x, y, z, true) || !hasNearbyFluid(world, x, y, z, false)) {
                EngineTraceFinderSystem.event(x, y, z, "NATURAL_OVERRIDE_SKIP", "reason=missing-fluid-adjacency blockId=" + currentId);
                return;
            }

            String replacement = resolveReplacementBlockId(configManager.getRandomDropForTier(1), 1);
            if (replacement == null || replacement.equals(currentId)) {
                EngineTraceFinderSystem.event(x, y, z, "NATURAL_OVERRIDE_SKIP", "reason=no-valid-replacement current=" + currentId + " replacement=" + replacement);
                return;
            }
            world.setBlock(x, y, z, replacement, 0);
            EngineTraceFinderSystem.event(x, y, z, "NATURAL_OVERRIDE_APPLIED", "from=" + currentId + " to=" + replacement);
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][GEN] Natural override patched at " + key
                    + " replacement=" + replacement);
        } catch (Exception ex) {
            EngineTraceFinderSystem.event(x, y, z, "NATURAL_OVERRIDE_ERROR", "error=" + ex.getClass().getSimpleName());
            LOGGER.at(Level.WARNING).log("[CobblestoneZufall][GEN] Natural override failed at " + key
                    + " reason=" + ex.getClass().getSimpleName());
        } finally {
            recentlyPatched.put(key, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1));
        }
    }

>>>>>>> cd2cc2b (Prepare clean project state)
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
<<<<<<< HEAD

=======
>>>>>>> cd2cc2b (Prepare clean project state)
        BlockType directBlock = BlockType.fromString(configuredId);
        if (directBlock != null && !directBlock.isUnknown()) {
            return directBlock.getId();
        }
<<<<<<< HEAD

=======
>>>>>>> cd2cc2b (Prepare clean project state)
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
