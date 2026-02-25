package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * Registers placeholders into BetterScoreBoard (if installed) without compile-time dependency.
 */
public final class ScoreboardPlaceholderBridge {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String BSB_API = "com.gillodaby.betterscoreboard.BetterScoreBoardAPI";
    private static final String BSB_PROVIDER = "com.gillodaby.betterscoreboard.BetterScoreBoardAPI$PlaceholderProvider";

    private static final String[] KEY_TIER = {"cob_tier"};
    private static final String[] KEY_NEXT_TIER = {"cob_next_tier"};
    private static final String[] KEY_MAX_TIER = {"cob_max_tier"};

    private static final String[] KEY_AVG_BLOCKS_SEC = {"Avg. Blocks/sec", "avg. blocks/sec", "avg_blocks_sec", "cob_avg_blocks_sec", "avgblockssec"};
    private static final String[] KEY_AVG_BLOCKS_MIN = {"Avg. Blocks/min", "avg. blocks/min", "avg_blocks_min", "cob_avg_blocks_min", "avgblocksmin"};
    private static final String[] KEY_EST_BLOCKS_HR = {"Est. Blocks/hr", "est. blocks/hr", "est_blocks_hr", "cob_est_blocks_hr", "estblockshr"};
    private static final String[] KEY_AVG_BLOCKS_HR = {"Avg. Blocks/hr", "avg. blocks/hr", "avg_blocks_hr", "cob_avg_blocks_hr", "avgblockshr"};
    private static final String[] KEY_TOTAL_BLOCKS = {"Total Blocks", "total blocks", "total_blocks", "cob_total_blocks", "totalblocks"};

    private static final String[] KEY_MONEY = {"money", "economy_money", "cob_money"};
    private static final Map<String, String> SCOREBOARD_TOKEN_FIXES = Map.of(
            "{Avg. Blocks/sec}", "{avg. blocks/sec}",
            "{Avg. Blocks/min}", "{avg. blocks/min}",
            "{Est. Blocks/hr}", "{est. blocks/hr}",
            "{Avg. Blocks/hr}", "{avg. blocks/hr}",
            "{Total Blocks}", "{total blocks}"
    );

    private final ConfigManager configManager;
    private boolean registered;

    public ScoreboardPlaceholderBridge(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void register() {
        if (registered) {
            return;
        }
        try {
            Class<?> apiClass = Class.forName(BSB_API);
            Class<?> providerClass = Class.forName(BSB_PROVIDER);
            Method registerMethod = apiClass.getMethod("registerPlaceholder", String.class, providerClass);

            registerAll(registerMethod, providerClass, KEY_TIER, PlaceholderKind.TIER);
            registerAll(registerMethod, providerClass, KEY_NEXT_TIER, PlaceholderKind.NEXT_TIER);
            registerAll(registerMethod, providerClass, KEY_MAX_TIER, PlaceholderKind.MAX_TIER);

            registerAll(registerMethod, providerClass, KEY_AVG_BLOCKS_SEC, PlaceholderKind.AVG_BLOCKS_SEC);
            registerAll(registerMethod, providerClass, KEY_AVG_BLOCKS_MIN, PlaceholderKind.AVG_BLOCKS_MIN);
            registerAll(registerMethod, providerClass, KEY_EST_BLOCKS_HR, PlaceholderKind.EST_BLOCKS_HR);
            registerAll(registerMethod, providerClass, KEY_AVG_BLOCKS_HR, PlaceholderKind.AVG_BLOCKS_HR);
            registerAll(registerMethod, providerClass, KEY_TOTAL_BLOCKS, PlaceholderKind.TOTAL_BLOCKS);

            registerAll(registerMethod, providerClass, KEY_MONEY, PlaceholderKind.MONEY);
            normalizeBetterScoreboardConfigs();

            registered = true;
            LOGGER.at(Level.INFO).log("[CobblestoneZufall] BetterScoreBoard placeholders registered (tier/stats/money). Use keys like {cob_tier}, {avg_blocks_min}, {money}.");
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][PH] IMPORTANT: BetterScoreBoard matches placeholders in lowercase text (e.g. {avg_blocks_min}).");
        } catch (ClassNotFoundException ignored) {
            LOGGER.at(Level.INFO).log("[CobblestoneZufall] BetterScoreBoard not found. Placeholder bridge skipped.");
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).log("[CobblestoneZufall] Failed to register BetterScoreBoard placeholders: " + e.getClass().getSimpleName());
        }
    }

    public void unregister() {
        if (!registered) {
            return;
        }
        try {
            Class<?> apiClass = Class.forName(BSB_API);
            Method unregisterMethod = apiClass.getMethod("unregisterPlaceholder", String.class);

            unregisterAll(unregisterMethod, KEY_TIER);
            unregisterAll(unregisterMethod, KEY_NEXT_TIER);
            unregisterAll(unregisterMethod, KEY_MAX_TIER);
            unregisterAll(unregisterMethod, KEY_AVG_BLOCKS_SEC);
            unregisterAll(unregisterMethod, KEY_AVG_BLOCKS_MIN);
            unregisterAll(unregisterMethod, KEY_EST_BLOCKS_HR);
            unregisterAll(unregisterMethod, KEY_AVG_BLOCKS_HR);
            unregisterAll(unregisterMethod, KEY_TOTAL_BLOCKS);
            unregisterAll(unregisterMethod, KEY_MONEY);

            LOGGER.at(Level.INFO).log("[CobblestoneZufall] BetterScoreBoard placeholders unregistered.");
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).log("[CobblestoneZufall] Failed to unregister BetterScoreBoard placeholders: " + e.getClass().getSimpleName());
        } finally {
            registered = false;
        }
    }

    private void registerAll(Method registerMethod,
                             Class<?> providerClass,
                             String[] keys,
                             PlaceholderKind kind) {
        Object provider = createProvider(providerClass, kind);
        for (String key : keys) {
            try {
                registerMethod.invoke(null, key, provider);
                LOGGER.at(Level.INFO).log("[CobblestoneZufall] Registered placeholder {" + key + "}");
            } catch (Exception e) {
                LOGGER.at(Level.WARNING).log("[CobblestoneZufall] Failed placeholder {" + key + "}: " + e.getClass().getSimpleName());
            }
        }
    }

    private static void unregisterAll(Method unregisterMethod, String[] keys) {
        for (String key : keys) {
            try {
                unregisterMethod.invoke(null, key);
            } catch (Exception ignored) {
            }
        }
    }

    private Object createProvider(Class<?> providerInterface, PlaceholderKind kind) {
        InvocationHandler handler = (proxy, method, args) -> {
            if (method == null || !"resolve".equals(method.getName())) {
                return "";
            }
            if (args == null || args.length == 0 || args[0] == null) {
                return fallback(kind);
            }
            UUID uuid = extractUuid(args[0]);
            if (uuid == null) {
                return fallback(kind);
            }

            int tier = configManager.getPlayerTier(uuid);
            int maxTier = configManager.getMaxTier();

            double avgPerSec = MiningRateTracker.getInstance().getAverageBlocksPerSecond(uuid);
            double avgPerMin = avgPerSec * 60.0d;
            double estPerHour = avgPerMin * 60.0d;
            double avgPerHour = MiningRateTracker.getInstance().getAverageBlocksPerHourRolling(uuid);
            long totalBlocks = MiningRateTracker.getInstance().getTotalBlocks(uuid);

            double money = 0.0d;
            CobblestoneZufallPlugin plugin = CobblestoneZufallPlugin.getInstance();
            if (plugin != null && plugin.getEconomyBridge() != null) {
                money = plugin.getEconomyBridge().getBalance(uuid).doubleValue();
            }

            return switch (kind) {
                case TIER -> Integer.toString(tier);
                case NEXT_TIER -> Integer.toString(Math.min(maxTier, tier + 1));
                case MAX_TIER -> Integer.toString(maxTier);
                case AVG_BLOCKS_SEC -> format2(avgPerSec);
                case AVG_BLOCKS_MIN -> format2(avgPerMin);
                case EST_BLOCKS_HR -> format2(estPerHour);
                case AVG_BLOCKS_HR -> format2(avgPerHour);
                case TOTAL_BLOCKS -> formatWhole(totalBlocks);
                case MONEY -> formatMoney(money);
            };
        };
        return Proxy.newProxyInstance(providerInterface.getClassLoader(), new Class[]{providerInterface}, handler);
    }

    private static UUID extractUuid(Object playerObj) {
        if (playerObj instanceof Player) {
            PlayerRef pref = ((Player) playerObj).getPlayerRef();
            if (pref != null) {
                return pref.getUuid();
            }
        }
        try {
            Method getPlayerRef = playerObj.getClass().getMethod("getPlayerRef");
            Object playerRef = getPlayerRef.invoke(playerObj);
            if (playerRef != null) {
                Method getUuid = playerRef.getClass().getMethod("getUuid");
                Object uuid = getUuid.invoke(playerRef);
                if (uuid instanceof UUID) {
                    return (UUID) uuid;
                }
            }
        } catch (Exception ignored) {
        }
        try {
            Method getUuid = playerObj.getClass().getMethod("getUuid");
            Object uuid = getUuid.invoke(playerObj);
            if (uuid instanceof UUID) {
                return (UUID) uuid;
            }
        } catch (Exception ignored) {
        }
        try {
            Method getUniqueId = playerObj.getClass().getMethod("getUniqueId");
            Object uuid = getUniqueId.invoke(playerObj);
            if (uuid instanceof UUID) {
                return (UUID) uuid;
            }
        } catch (Exception ignored) {
        }
        try {
            Method getId = playerObj.getClass().getMethod("getId");
            Object uuid = getId.invoke(playerObj);
            if (uuid instanceof UUID) {
                return (UUID) uuid;
            }
            if (uuid instanceof String) {
                return UUID.fromString((String) uuid);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static String fallback(PlaceholderKind kind) {
        return switch (kind) {
            case MAX_TIER -> "100";
            case TOTAL_BLOCKS -> "0";
            case AVG_BLOCKS_SEC, AVG_BLOCKS_MIN, EST_BLOCKS_HR, AVG_BLOCKS_HR, MONEY -> "0.00";
            default -> "1";
        };
    }

    private static String format2(double value) {
        return String.format(Locale.US, "%.2f", Math.max(0.0d, value));
    }

    private static String formatMoney(double value) {
        return String.format(Locale.US, "%,.2f", Math.max(0.0d, value));
    }

    private static String formatWhole(long value) {
        return String.format(Locale.US, "%,d", Math.max(0L, value));
    }

    private static void normalizeBetterScoreboardConfigs() {
        Path base = Paths.get(System.getProperty("user.home"), "AppData", "Roaming", "Hytale", "UserData", "Saves");
        if (!Files.exists(base)) {
            return;
        }
        try (Stream<Path> files = Files.find(base, 8, (p, a) -> a.isRegularFile() && "config.yaml".equalsIgnoreCase(p.getFileName().toString()))) {
            files.forEach(ScoreboardPlaceholderBridge::normalizeOneConfig);
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).log("[CobblestoneZufall][PH] Scoreboard config normalization failed: " + e.getClass().getSimpleName());
        }
    }

    private static void normalizeOneConfig(Path file) {
        String full = file.toString();
        if (!full.contains("GilloDaby_Better ScoreBoard")) {
            return;
        }
        try {
            String text = Files.readString(file, StandardCharsets.UTF_8);
            String updated = text;
            for (Map.Entry<String, String> entry : SCOREBOARD_TOKEN_FIXES.entrySet()) {
                updated = updated.replace(entry.getKey(), entry.getValue());
            }
            if (!updated.equals(text)) {
                Files.writeString(file, updated, StandardCharsets.UTF_8);
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][PH] Normalized placeholders in " + file);
            }
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).log("[CobblestoneZufall][PH] Failed to normalize " + file + ": " + e.getClass().getSimpleName());
        }
    }

    private enum PlaceholderKind {
        TIER,
        NEXT_TIER,
        MAX_TIER,
        AVG_BLOCKS_SEC,
        AVG_BLOCKS_MIN,
        EST_BLOCKS_HR,
        AVG_BLOCKS_HR,
        TOTAL_BLOCKS,
        MONEY
    }
}
