package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Locale;
import java.util.logging.Level;

/**
 * Event-independent fallback: continuously suppresses natural lava+water cobble near players.
 * This closes cases where fluid-driven block changes bypass PlaceBlockEvent.
 */
public class CobbleNaturalSuppressionTickSystem extends TickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String AIR_BLOCK_ID = "Empty";
    private static final long SWEEP_INTERVAL_MS = 150L;
    private static final long DEBUG_IDLE_LOG_INTERVAL_MS = 2000L;
    private static final int SCAN_RADIUS_XZ = 6;
    private static final int SCAN_RADIUS_Y = 3;
    private static final int MAX_CHANGES_PER_SWEEP = 128;

    private final ConfigManager configManager;
    private long lastSweepAtMs = 0L;
    private long lastIdleDebugAtMs = 0L;

    public CobbleNaturalSuppressionTickSystem(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void tick(float deltaSeconds, int index, Store<EntityStore> store) {
        if (store == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastSweepAtMs < SWEEP_INTERVAL_MS) {
            return;
        }
        lastSweepAtMs = now;

        EntityStore entityStore = store.getExternalData();
        if (entityStore == null || entityStore.getWorld() == null) {
            return;
        }
        World world = entityStore.getWorld();
        var playerType = Player.getComponentType();
        var transformType = TransformComponent.getComponentType();
        Query<EntityStore> playerQuery = Query.and(playerType, transformType);

        final int[] changes = {0};
        final int[] scannedPlayers = {0};
        final int[] scannedBlocks = {0};
        final int[] cobbleCandidates = {0};
        store.forEachChunk(playerQuery, (ArchetypeChunk<EntityStore> chunk, CommandBuffer<EntityStore> commandBuffer) -> {
            for (int i = 0; i < chunk.size(); i++) {
                if (changes[0] >= MAX_CHANGES_PER_SWEEP) {
                    return;
                }
                TransformComponent transform = commandBuffer.getComponent(chunk.getReferenceTo(i), transformType);
                if (transform == null || transform.getPosition() == null) {
                    continue;
                }
                scannedPlayers[0]++;
                int centerX = (int) Math.floor(transform.getPosition().getX());
                int centerY = (int) Math.floor(transform.getPosition().getY());
                int centerZ = (int) Math.floor(transform.getPosition().getZ());

                suppressNaturalCobbleAround(world, centerX, centerY, centerZ, changes, scannedBlocks, cobbleCandidates);
            }
        });

        if (changes[0] > 0 && configManager.isDebugEnabled()) {
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG] Tick suppression removed natural cobble blocks: " + changes[0]);
            return;
        }
        if (configManager.isDebugEnabled() && now - lastIdleDebugAtMs >= DEBUG_IDLE_LOG_INTERVAL_MS) {
            lastIdleDebugAtMs = now;
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG] Tick suppression sweep:"
                    + " players=" + scannedPlayers[0]
                    + " scannedBlocks=" + scannedBlocks[0]
                    + " cobbleCandidates=" + cobbleCandidates[0]
                    + " removed=" + changes[0]);
        }
    }

    private static void suppressNaturalCobbleAround(World world,
                                                    int centerX,
                                                    int centerY,
                                                    int centerZ,
                                                    int[] changes,
                                                    int[] scannedBlocks,
                                                    int[] cobbleCandidates) {
        int minX = centerX - SCAN_RADIUS_XZ;
        int maxX = centerX + SCAN_RADIUS_XZ;
        int minY = centerY - SCAN_RADIUS_Y;
        int maxY = centerY + SCAN_RADIUS_Y;
        int minZ = centerZ - SCAN_RADIUS_XZ;
        int maxZ = centerZ + SCAN_RADIUS_XZ;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (changes[0] >= MAX_CHANGES_PER_SWEEP) {
                        return;
                    }
                    scannedBlocks[0]++;
                    if (!CobblestoneGeneratorSystem.isRegenPending(x, y, z)) {
                        continue;
                    }
                    if (CobblestoneGeneratorSystem.isPluginPlacementAllowed(x, y, z)) {
                        continue;
                    }
                    BlockType type = world.getBlockType(x, y, z);
                    if (isCobbleId(type == null ? null : type.getId())) {
                        cobbleCandidates[0]++;
                    }
                    if (!isNaturalCobbleAt(world, x, y, z)) {
                        continue;
                    }
                    try {
                        world.setBlock(x, y, z, AIR_BLOCK_ID, 0);
                        EngineTraceFinderSystem.event(x, y, z, "TICK_SUPPRESS_REMOVED", "source=tick-system");
                        changes[0]++;
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    private static boolean isNaturalCobbleAt(World world, int x, int y, int z) {
        BlockType type = world.getBlockType(x, y, z);
        String id = type == null ? null : type.getId();
        if (!isCobbleId(id)) {
            return false;
        }
        return hasNearbyFluid(world, x, y, z, true) && hasNearbyFluid(world, x, y, z, false);
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
}
