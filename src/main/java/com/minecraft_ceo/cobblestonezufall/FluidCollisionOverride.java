package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.fluid.FluidTicker;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Disables natural lava/water cobble creation by overriding fluid collision outputs.
 * This patches runtime fluid collision configs directly from loaded Fluid assets.
 */
public final class FluidCollisionOverride {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String AIR_BLOCK_ID = "Empty";
    private static final long RETRY_COOLDOWN_MS = 3000L;
    private static volatile boolean applied = false;
    private static volatile long lastAttemptAtMs = 0L;

    private FluidCollisionOverride() {
    }

    public static synchronized void apply(boolean debugEnabled) {
        if (applied) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastAttemptAtMs < RETRY_COOLDOWN_MS) {
            return;
        }
        lastAttemptAtMs = now;

        String[] lavaFluids = new String[]{"Lava", "Lava_Source"};
        String[] waterFluids = new String[]{"Water", "Water_Source", "Water_Finite"};
        int patched = 0;
        for (String lava : lavaFluids) {
            for (String water : waterFluids) {
                patched += patchCollision(lava, water, AIR_BLOCK_ID, debugEnabled);
                patched += patchCollision(water, lava, AIR_BLOCK_ID, debugEnabled);
            }
        }

        if (patched > 0) {
            applied = true;
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][GEN] Fluid collision override active. Patched entries: " + patched);
        } else {
            // Asset maps may not be fully available yet during early startup.
            // Keep apply() retryable so later calls can patch once fluids are loaded.
            applied = false;
            LOGGER.at(Level.WARNING).log("[CobblestoneZufall][GEN] Fluid collision override patched 0 entries (assets not ready yet). Will retry.");
        }
    }

    private static int patchCollision(String sourceFluidId, String otherFluidId, String blockToPlaceId, boolean debugEnabled) {
        try {
            Fluid source = Fluid.getAssetMap().getAsset(sourceFluidId);
            if (source == null) {
                if (debugEnabled) {
                    LOGGER.at(Level.INFO).log("[CobblestoneZufall][FINDER] Collision skip: source fluid missing " + sourceFluidId);
                }
                return 0;
            }
            FluidTicker ticker = source.getTicker();
            Int2ObjectMap<?> collisionMap = getCollisionMap(ticker);
            if (collisionMap == null) {
                if (debugEnabled) {
                    String tickerName = ticker == null ? "null" : ticker.getClass().getName();
                    LOGGER.at(Level.INFO).log("[CobblestoneZufall][FINDER] Collision skip: ticker has no collision map source="
                            + sourceFluidId + " ticker=" + tickerName);
                }
                return 0;
            }

            int otherFluidIndex = Fluid.getAssetMap().getIndex(otherFluidId);
            if (otherFluidIndex == Integer.MIN_VALUE) {
                if (debugEnabled) {
                    LOGGER.at(Level.INFO).log("[CobblestoneZufall][FINDER] Collision skip: other fluid index missing " + otherFluidId);
                }
                return 0;
            }

            Object config = collisionMap.get(otherFluidIndex);
            if (config == null) {
                if (debugEnabled) {
                    LOGGER.at(Level.INFO).log("[CobblestoneZufall][FINDER] Collision skip: no config source="
                            + sourceFluidId + " other=" + otherFluidId);
                }
                return 0;
            }

            int blockIndex = BlockType.getAssetMap().getIndex(blockToPlaceId);
            if (blockIndex == Integer.MIN_VALUE) {
                return 0;
            }

            setField(config, "blockToPlace", blockToPlaceId);
            setField(config, "blockToPlaceIndex", blockIndex);

            if (debugEnabled) {
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG] Patched fluid collision: "
                        + sourceFluidId + " + " + otherFluidId + " -> " + blockToPlaceId
                        + " ticker=" + ticker.getClass().getSimpleName());
            }
            return 1;
        } catch (Exception ex) {
            LOGGER.at(Level.WARNING).log("[CobblestoneZufall][GEN] Failed to patch fluid collision "
                    + sourceFluidId + " + " + otherFluidId + " reason=" + ex.getClass().getSimpleName());
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private static Int2ObjectMap<?> getCollisionMap(FluidTicker ticker) {
        if (ticker == null) {
            return null;
        }
        try {
            Method getCollisionMap = ticker.getClass().getMethod("getCollisionMap");
            Object map = getCollisionMap.invoke(ticker);
            if (map instanceof Int2ObjectMap<?>) {
                return (Int2ObjectMap<?>) map;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
