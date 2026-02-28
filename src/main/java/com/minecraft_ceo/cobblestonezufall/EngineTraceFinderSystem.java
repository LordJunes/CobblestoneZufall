package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Deep runtime probe for engine-side block/fluid transitions.
 * Watches coordinates and logs changes with millisecond timestamps.
 */
public class EngineTraceFinderSystem extends TickingSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final long POLL_INTERVAL_MS = 25L;
    private static final long HEARTBEAT_MS = 500L;
    private static final ConcurrentHashMap<String, WatchState> WATCHES = new ConcurrentHashMap<>();

    private final ConfigManager configManager;
    private long lastPollAtMs = 0L;

    public EngineTraceFinderSystem(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public static void watch(int x, int y, int z, long durationMs, String reason) {
        long now = System.currentTimeMillis();
        String key = key(x, y, z);
        WATCHES.compute(key, (k, prev) -> {
            long until = now + Math.max(5000L, durationMs);
            if (prev == null) {
                WatchState next = new WatchState(x, y, z, now, until);
                next.lastReason = reason;
                return next;
            }
            prev.untilMs = Math.max(prev.untilMs, until);
            prev.lastReason = reason;
            return prev;
        });
        LOGGER.at(Level.INFO).log("[CobblestoneZufall][FINDER " + x + "," + y + "," + z + "] ts=" + now
                + " phase=WATCH_ARMED reason=" + reason + " durationMs=" + Math.max(5000L, durationMs));
    }

    public static void event(int x, int y, int z, String phase, String details) {
        long now = System.currentTimeMillis();
        LOGGER.at(Level.INFO).log("[CobblestoneZufall][FINDER " + x + "," + y + "," + z + "] ts=" + now
                + " phase=" + phase + " " + details);
    }

    @Override
    public void tick(float deltaSeconds, int index, Store<EntityStore> store) {
        if (store == null || configManager == null || !configManager.isDebugEnabled()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastPollAtMs < POLL_INTERVAL_MS) {
            return;
        }
        lastPollAtMs = now;

        EntityStore entityStore = store.getExternalData();
        if (entityStore == null || entityStore.getWorld() == null) {
            return;
        }
        World world = entityStore.getWorld();

        for (Map.Entry<String, WatchState> entry : WATCHES.entrySet()) {
            WatchState state = entry.getValue();
            if (state == null) {
                continue;
            }
            if (state.untilMs < now) {
                WATCHES.remove(entry.getKey(), state);
                continue;
            }
            probeOne(world, now, state);
        }
    }

    private static void probeOne(World world, long now, WatchState state) {
        String blockId;
        int centerFluidId;
        String neighborSignature;
        try {
            BlockType type = world.getBlockType(state.x, state.y, state.z);
            blockId = type == null ? "null" : type.getId();
            centerFluidId = world.getFluidId(state.x, state.y, state.z);
            neighborSignature = neighborFluids(world, state.x, state.y, state.z);
        } catch (Exception ex) {
            event(state.x, state.y, state.z, "PROBE_ERROR", "error=" + ex.getClass().getSimpleName());
            return;
        }

        boolean pending = CobblestoneGeneratorSystem.isRegenPending(state.x, state.y, state.z);
        boolean allowed = CobblestoneGeneratorSystem.isPluginPlacementAllowed(state.x, state.y, state.z);
        String snapshot = "block=" + blockId
                + " centerFluid=" + fluidName(centerFluidId)
                + " neighborFluids=" + neighborSignature
                + " pending=" + pending
                + " pluginAllowed=" + allowed
                + " reason=" + state.lastReason;

        boolean changed = !Objects.equals(blockId, state.lastBlockId)
                || centerFluidId != state.lastCenterFluidId
                || !Objects.equals(neighborSignature, state.lastNeighborSignature);
        boolean heartbeat = now - state.lastHeartbeatMs >= HEARTBEAT_MS;
        if (changed || heartbeat) {
            long dt = now - state.startMs;
            event(state.x, state.y, state.z, changed ? "STATE_CHANGE" : "HEARTBEAT", "dt=+" + dt + "ms " + snapshot);
            state.lastHeartbeatMs = now;
        }

        if (isCobbleId(blockId) && pending && !allowed) {
            long dt = now - state.startMs;
            event(state.x, state.y, state.z, "ALERT_EARLY_COBBLE", "dt=+" + dt + "ms " + snapshot);
        }

        state.lastBlockId = blockId;
        state.lastCenterFluidId = centerFluidId;
        state.lastNeighborSignature = neighborSignature;
    }

    private static String neighborFluids(World world, int x, int y, int z) {
        int[][] offsets = new int[][]{
                {1, 0, 0},
                {-1, 0, 0},
                {0, 0, 1},
                {0, 0, -1},
                {0, 1, 0},
                {0, -1, 0}
        };
        StringBuilder sb = new StringBuilder(96);
        for (int i = 0; i < offsets.length; i++) {
            int[] off = offsets[i];
            int id = world.getFluidId(x + off[0], y + off[1], z + off[2]);
            if (i > 0) {
                sb.append('|');
            }
            sb.append(i).append('=').append(fluidName(id));
        }
        return sb.toString();
    }

    private static String fluidName(int fluidId) {
        if (fluidId <= 0) {
            return "none";
        }
        try {
            Fluid fluid = Fluid.getAssetMap().getAsset(fluidId);
            if (fluid == null || fluid.getId() == null) {
                return "id:" + fluidId;
            }
            return fluid.getId().toLowerCase(Locale.ROOT);
        } catch (Exception ignored) {
            return "id:" + fluidId;
        }
    }

    private static boolean isCobbleId(String id) {
        if (id == null) {
            return false;
        }
        return id.equals("Rock_Stone_Cobble") || id.contains("Cobble") || id.contains("cobble");
    }

    private static String key(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }

    private static final class WatchState {
        private final int x;
        private final int y;
        private final int z;
        private final long startMs;
        private volatile long untilMs;
        private volatile long lastHeartbeatMs;
        private volatile String lastReason;
        private volatile String lastBlockId;
        private volatile int lastCenterFluidId = Integer.MIN_VALUE;
        private volatile String lastNeighborSignature;

        private WatchState(int x, int y, int z, long startMs, long untilMs) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.startMs = startMs;
            this.untilMs = untilMs;
            this.lastHeartbeatMs = 0L;
            this.lastReason = "unknown";
        }
    }
}
