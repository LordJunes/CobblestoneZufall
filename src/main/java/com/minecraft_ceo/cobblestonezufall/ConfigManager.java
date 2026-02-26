package com.minecraft_ceo.cobblestonezufall;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.Collections;
import java.util.Comparator;

public class ConfigManager {
    private static final String CONFIG_FILE = "cobble_config.json";
    private static final String PLAYER_TABLE_FILE = "CobPlayerSaveTable.csv";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int DEFAULT_MAX_TIER = 100;
    private static final double DEFAULT_MAX_UPGRADE_COST = 1_000_000_000.0d;
    private static final double DEFAULT_CHANCE_EXPONENT = 1.8d;
    private static final double DEFAULT_COST_EXPONENT = 4.0d;
    private static final String[] DEFAULT_ITEMS = new String[]{
            "Rock_Stone_Cobble",
            "Ore_Copper_Stone",
            "Ore_Iron_Stone",
            "Ore_Gold_Stone",
            "Ore_Silver_Stone",
            "Ore_Cobalt_Shale",
            "Ore_Thorium_Mud",
            "Ore_Adamantite_Magma",
            "Ore_Mithril_Stone"
    };
    private static final double[] DEFAULT_START_VALUES = new double[]{
            99.500d, 0.400d, 0.080d, 0.010d, 0.005d, 0.002d, 0.0015d, 0.001d, 0.0005d
    };
    private static final double[] DEFAULT_TARGET_VALUES = new double[]{
            86.4d, 4.0d, 3.0d, 2.6d, 2.0d, 0.8d, 0.6d, 0.4d, 0.2d
    };
    private static final String CSV_DEFAULTS_RESOURCE = "/data/cob_levels.csv";
    private static volatile Map<Integer, CsvTierDefaults> cachedCsvDefaults;

    public static class DropEntry {
        public String itemId;
        public double chance;
        public int amount = 1;
        public double payAmount = 0.0d;
        public double repairChance = 0.0d;
        public double repairAmountPercent = 0.0d;

        public DropEntry() {
        }

        public DropEntry(String itemId, double chance) {
            this.itemId = itemId;
            this.chance = chance;
        }

        public DropEntry(String itemId, double chance, int amount) {
            this.itemId = itemId;
            this.chance = chance;
            this.amount = Math.max(1, amount);
        }

        public DropEntry(String itemId, double chance, int amount, double payAmount, double repairChance, double repairAmountPercent) {
            this.itemId = itemId;
            this.chance = chance;
            this.amount = Math.max(1, amount);
            this.payAmount = Math.max(0.0d, payAmount);
            this.repairChance = Math.max(0.0d, repairChance);
            this.repairAmountPercent = Math.max(0.0d, repairAmountPercent);
        }
    }

    public static class TierConfig {
        public List<DropEntry> drops = new ArrayList<>();
        public double upgradeCost = 0.0d;
    }

    public static class CurveItemConfig {
        public String itemId;
        public double startChance;
        public double endChance;

        public CurveItemConfig() {
        }

        public CurveItemConfig(String itemId, double startChance, double endChance) {
            this.itemId = itemId;
            this.startChance = startChance;
            this.endChance = endChance;
        }
    }

    public static class CurveModelConfig {
        public int maxTier = DEFAULT_MAX_TIER;
        public double maxUpgradeCost = DEFAULT_MAX_UPGRADE_COST;
        public String chanceMode = "POWER";
        public double chanceExponent = DEFAULT_CHANCE_EXPONENT;
        public String costMode = "POWER";
        public double costExponent = DEFAULT_COST_EXPONENT;
        public List<CurveItemConfig> items = new ArrayList<>();
    }

    public static class PlayerTierData {
        public int tier = 1;
    }

    private static class CsvTierDefaults {
        final double price;
        final List<DropEntry> drops;

        CsvTierDefaults(double price, List<DropEntry> drops) {
            this.price = price;
            this.drops = drops;
        }
    }

    public static class ConfigData {
        public int maxTier = DEFAULT_MAX_TIER;
        public long regenDelayMs = 150L;
        public int autoCollectRange = 0;
        public boolean autoCollectToInventory = true;
        public int autoCollectRangeTypeMs = 100;
        public double expectedBlocksPerSecond = 3.0d;
        public Map<String, TierConfig> tiers = new HashMap<>();
        public Map<String, PlayerTierData> players = new HashMap<>();
        public CurveModelConfig curveModel = new CurveModelConfig();
    }

    private ConfigData data = new ConfigData();

    public synchronized void load() {
        File file = getConfigFile();
        if (!file.exists()) {
            ensureDefaults();
            return;
        }

        try (Reader reader = new FileReader(file)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (root == null || root.isJsonNull()) {
                ensureDefaults();
                return;
            }

            // Backwards compatibility: old file was a plain drop list.
            if (root.isJsonArray()) {
                Type listType = new TypeToken<ArrayList<DropEntry>>() {}.getType();
                List<DropEntry> oldDrops = GSON.fromJson(root, listType);
                data = new ConfigData();
                data.tiers.put("1", new TierConfig());
                if (oldDrops != null) {
                    data.tiers.get("1").drops.addAll(oldDrops);
                }
                ensureDefaults();
                save();
                return;
            }

            data = GSON.fromJson(root, ConfigData.class);
            if (data == null) {
                data = new ConfigData();
            }
            ensureDefaults();
            writePlayerSaveTable();
        } catch (IOException e) {
            e.printStackTrace();
            ensureDefaults();
            writePlayerSaveTable();
        }
    }

    public synchronized void save() {
        File file = getConfigFile();
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(data, writer);
            writePlayerSaveTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void ensureDefaults() {
        if (data == null) {
            data = new ConfigData();
        }
        if (data.maxTier < 1) {
            data.maxTier = DEFAULT_MAX_TIER;
        } else if (data.maxTier == 999) {
            // Legacy default from older builds -> switch to new default.
            data.maxTier = DEFAULT_MAX_TIER;
        }
        if (data.tiers == null) {
            data.tiers = new HashMap<>();
        }
        if (data.players == null) {
            data.players = new HashMap<>();
        }
        if (data.regenDelayMs < 1L) {
            data.regenDelayMs = 150L;
        }
        if (data.autoCollectRange < 0) {
            data.autoCollectRange = 0;
        }
        if (data.autoCollectRange == 0 && data.autoCollectToInventory) {
            // Legacy migration: old ON/OFF -> range based collection.
            data.autoCollectRange = 6;
        }
        if (data.autoCollectRangeTypeMs != -1) {
            if (data.autoCollectRangeTypeMs < 100) {
                data.autoCollectRangeTypeMs = 100;
            }
            if (data.autoCollectRangeTypeMs > 5000) {
                data.autoCollectRangeTypeMs = 5000;
            }
            data.autoCollectRangeTypeMs = (data.autoCollectRangeTypeMs / 100) * 100;
            if (data.autoCollectRangeTypeMs < 100) {
                data.autoCollectRangeTypeMs = 100;
            }
        }
        if (data.expectedBlocksPerSecond < 0.0d) {
            data.expectedBlocksPerSecond = 0.0d;
        }
        if (data.curveModel == null) {
            data.curveModel = new CurveModelConfig();
        }
        normalizeCurveModel(data.curveModel);
        data.tiers.computeIfAbsent("1", k -> new TierConfig());
        for (TierConfig tierConfig : data.tiers.values()) {
            if (tierConfig == null) {
                continue;
            }
            if (tierConfig.drops == null) {
                tierConfig.drops = new ArrayList<>();
                continue;
            }
            for (DropEntry drop : tierConfig.drops) {
                if (drop.amount < 1) {
                    drop.amount = 1;
                }
                if (drop.payAmount < 0.0d) {
                    drop.payAmount = 0.0d;
                }
                if (drop.repairChance < 0.0d) {
                    drop.repairChance = 0.0d;
                }
                if (drop.repairAmountPercent < 0.0d) {
                    drop.repairAmountPercent = 0.0d;
                }
            }
        }
    }

    private static void normalizeCurveModel(CurveModelConfig model) {
        if (model.maxTier < 1) {
            model.maxTier = DEFAULT_MAX_TIER;
        }
        if (model.maxUpgradeCost < 0.0d) {
            model.maxUpgradeCost = 0.0d;
        }
        if (model.chanceExponent <= 0.0d) {
            model.chanceExponent = DEFAULT_CHANCE_EXPONENT;
        }
        if (model.costExponent <= 0.0d) {
            model.costExponent = DEFAULT_COST_EXPONENT;
        }
        model.chanceMode = normalizeMode(model.chanceMode);
        model.costMode = normalizeMode(model.costMode);
        if (model.items == null) {
            model.items = new ArrayList<>();
        }
        if (model.items.isEmpty()) {
            for (int i = 0; i < DEFAULT_ITEMS.length; i++) {
                model.items.add(new CurveItemConfig(DEFAULT_ITEMS[i], DEFAULT_START_VALUES[i], DEFAULT_TARGET_VALUES[i]));
            }
        } else {
            Map<String, CurveItemConfig> byId = new HashMap<>();
            for (CurveItemConfig item : model.items) {
                if (item == null || item.itemId == null || item.itemId.isBlank()) {
                    continue;
                }
                byId.put(item.itemId, item);
            }
            List<CurveItemConfig> normalized = new ArrayList<>();
            for (int i = 0; i < DEFAULT_ITEMS.length; i++) {
                String itemId = DEFAULT_ITEMS[i];
                CurveItemConfig existing = byId.get(itemId);
                if (existing == null) {
                    normalized.add(new CurveItemConfig(itemId, DEFAULT_START_VALUES[i], DEFAULT_TARGET_VALUES[i]));
                } else {
                    normalized.add(new CurveItemConfig(itemId, existing.startChance, existing.endChance));
                }
            }
            model.items = normalized;
        }
    }

    private static String normalizeMode(String mode) {
        if (mode == null) {
            return "POWER";
        }
        String upper = mode.trim().toUpperCase(Locale.ROOT);
        if ("CURVE".equals(upper) || "NON_LINEAR".equals(upper) || "NONLINEAR".equals(upper)) {
            return "POWER";
        }
        if ("LINEAR".equals(upper) || "POWER".equals(upper) || "EASE_IN".equals(upper) || "EASE_OUT".equals(upper)
                || "EASE_IN_OUT".equals(upper) || "EXPONENTIAL".equals(upper) || "LOGARITHMIC".equals(upper)) {
            return upper;
        }
        return "POWER";
    }

    public synchronized int getMaxTier() {
        ensureDefaults();
        return data.maxTier;
    }

    public synchronized long getRegenDelayMs() {
        ensureDefaults();
        return Math.max(1L, data.regenDelayMs);
    }

    public synchronized void setRegenDelayMs(long regenDelayMs) {
        ensureDefaults();
        data.regenDelayMs = Math.max(1L, regenDelayMs);
        save();
    }

    public synchronized boolean isAutoCollectToInventory() {
        ensureDefaults();
        return data.autoCollectRange > 0;
    }

    public synchronized void setAutoCollectToInventory(boolean enabled) {
        ensureDefaults();
        data.autoCollectToInventory = enabled;
        data.autoCollectRange = enabled ? Math.max(1, data.autoCollectRange) : 0;
        save();
    }

    public synchronized int getAutoCollectRange() {
        ensureDefaults();
        return Math.max(0, data.autoCollectRange);
    }

    public synchronized void setAutoCollectRange(int range) {
        ensureDefaults();
        data.autoCollectRange = Math.max(0, Math.min(64, range));
        data.autoCollectToInventory = data.autoCollectRange > 0;
        save();
    }

    public synchronized int getAutoCollectRangeTypeMs() {
        ensureDefaults();
        return data.autoCollectRangeTypeMs == -1 ? -1 : Math.max(100, Math.min(5000, data.autoCollectRangeTypeMs));
    }

    public synchronized void setAutoCollectRangeTypeMs(int value) {
        ensureDefaults();
        if (value == -1) {
            data.autoCollectRangeTypeMs = -1;
            save();
            return;
        }
        int clamped = Math.max(100, Math.min(5000, value));
        clamped = (clamped / 100) * 100;
        if (clamped < 100) {
            clamped = 100;
        }
        data.autoCollectRangeTypeMs = clamped;
        save();
    }

    public synchronized double getExpectedBlocksPerSecond() {
        ensureDefaults();
        return Math.max(0.0d, data.expectedBlocksPerSecond);
    }

    public synchronized void setExpectedBlocksPerSecond(double value) {
        ensureDefaults();
        data.expectedBlocksPerSecond = Math.max(0.0d, value);
        save();
    }

    public synchronized void setMaxTier(int maxTier) {
        ensureDefaults();
        data.maxTier = Math.max(1, maxTier);
        if (data.curveModel != null) {
            data.curveModel.maxTier = data.maxTier;
        }
        for (PlayerTierData player : data.players.values()) {
            if (player == null) {
                continue;
            }
            if (player.tier < 1) {
                player.tier = 1;
            } else if (player.tier > data.maxTier) {
                player.tier = data.maxTier;
            }
        }
        save();
    }

    public synchronized CurveModelConfig getCurveModelCopy() {
        ensureDefaults();
        CurveModelConfig src = data.curveModel;
        CurveModelConfig copy = new CurveModelConfig();
        copy.maxTier = src.maxTier;
        copy.maxUpgradeCost = src.maxUpgradeCost;
        copy.chanceMode = src.chanceMode;
        copy.chanceExponent = src.chanceExponent;
        copy.costMode = src.costMode;
        copy.costExponent = src.costExponent;
        copy.items = new ArrayList<>();
        for (CurveItemConfig item : src.items) {
            copy.items.add(new CurveItemConfig(item.itemId, item.startChance, item.endChance));
        }
        return copy;
    }

    public synchronized void saveCurveModel(CurveModelConfig model) {
        ensureDefaults();
        if (model == null) {
            return;
        }
        CurveModelConfig next = new CurveModelConfig();
        next.maxTier = model.maxTier;
        next.maxUpgradeCost = model.maxUpgradeCost;
        next.chanceMode = model.chanceMode;
        next.chanceExponent = model.chanceExponent;
        next.costMode = model.costMode;
        next.costExponent = model.costExponent;
        next.items = new ArrayList<>();
        if (model.items != null) {
            for (CurveItemConfig item : model.items) {
                if (item == null) {
                    continue;
                }
                next.items.add(new CurveItemConfig(item.itemId, item.startChance, item.endChance));
            }
        }
        normalizeCurveModel(next);
        data.curveModel = next;
        save();
    }

    public synchronized void applyCurveModelToAllTiers() {
        ensureDefaults();
        CurveModelConfig model = getCurveModelCopy();
        normalizeCurveModel(model);
        setMaxTier(model.maxTier);

        for (int tier = 1; tier <= model.maxTier; tier++) {
            TierConfig tierConfig = getTierConfig(tier, true);
            tierConfig.drops.clear();
            double chanceFactor = getFactor(model.chanceMode, model.chanceExponent, tier, model.maxTier);
            List<DropEntry> generatedDrops = buildDropsFromCurveModel(model, chanceFactor);
            tierConfig.drops.addAll(generatedDrops);
            tierConfig.upgradeCost = model.maxUpgradeCost * getFactor(model.costMode, model.costExponent, tier, model.maxTier);
        }

        save();
    }

    public synchronized int getPlayerTier(UUID playerUuid) {
        ensureDefaults();
        String key = playerUuid.toString();
        PlayerTierData player = data.players.computeIfAbsent(key, ignored -> new PlayerTierData());
        if (player.tier < 1) {
            player.tier = 1;
        }
        if (player.tier > data.maxTier) {
            player.tier = data.maxTier;
        }
        return player.tier;
    }

    public synchronized int getPlayerTier(String playerUuid) {
        return getPlayerTier(UUID.fromString(playerUuid));
    }

    public synchronized int upgradePlayerTier(UUID playerUuid) {
        int current = getPlayerTier(playerUuid);
        if (current >= data.maxTier) {
            return current;
        }
        setPlayerTier(playerUuid, current + 1);
        return current + 1;
    }

    public synchronized int downgradePlayerTier(UUID playerUuid) {
        int current = getPlayerTier(playerUuid);
        if (current <= 1) {
            return current;
        }
        setPlayerTier(playerUuid, current - 1);
        return current - 1;
    }

    public synchronized void setPlayerTier(UUID playerUuid, int newTier) {
        ensureDefaults();
        int clamped = Math.max(1, Math.min(data.maxTier, newTier));
        PlayerTierData player = data.players.computeIfAbsent(playerUuid.toString(), ignored -> new PlayerTierData());
        player.tier = clamped;
        save();
    }

    public synchronized double getUpgradeCostForTier(int tier) {
        TierConfig cfg = getTierConfig(tier, true);
        double configured = Math.max(0.0d, cfg.upgradeCost);
        if (configured > 0.0d) {
            return configured;
        }
        return getCalculatedUpgradeCostForTier(tier);
    }

    public synchronized void setUpgradeCostForTier(int tier, double cost) {
        TierConfig cfg = getTierConfig(tier, true);
        cfg.upgradeCost = Math.max(0.0d, cost);
        save();
    }

    public synchronized List<DropEntry> getDropsForTier(int tier) {
        TierConfig cfg = getTierConfig(tier, false);
        if (cfg == null || cfg.drops == null || cfg.drops.isEmpty()) {
            return getCalculatedDropsForTier(tier);
        }
        List<DropEntry> out = new ArrayList<>(cfg.drops.size());
        for (DropEntry drop : cfg.drops) {
            out.add(copyDrop(drop));
        }
        return out;
    }

    public synchronized void upsertDrop(int tier, String itemId, double chance) {
        upsertDrop(tier, itemId, chance, 1);
    }

    public synchronized void upsertDrop(int tier, String itemId, double chance, int amount) {
        TierConfig cfg = getTierConfig(tier, true);
        for (DropEntry drop : cfg.drops) {
            if (drop.itemId.equals(itemId)) {
                drop.chance = chance;
                drop.amount = Math.max(1, amount);
                save();
                return;
            }
        }
        cfg.drops.add(new DropEntry(itemId, chance, amount));
        save();
    }

    public synchronized DropEntry getDropForTierById(int tier, String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        String normalized = id.trim().toLowerCase(Locale.ROOT);
        List<DropEntry> drops = getDropsForTier(tier);
        for (DropEntry drop : drops) {
            if (drop == null || drop.itemId == null) {
                continue;
            }
            if (drop.itemId.trim().toLowerCase(Locale.ROOT).equals(normalized)) {
                return copyDrop(drop);
            }
        }
        return null;
    }

    public synchronized void updateDropDetails(int tier,
                                               String itemId,
                                               int amount,
                                               double payAmount,
                                               double repairChance,
                                               double repairAmountPercent) {
        TierConfig cfg = getTierConfig(tier, true);
        if (cfg == null) {
            return;
        }
        if (cfg.drops == null) {
            cfg.drops = new ArrayList<>();
        }
        if (cfg.drops.isEmpty()) {
            cfg.drops.addAll(getCalculatedDropsForTier(tier));
        }
        String normalized = itemId == null ? "" : itemId.trim().toLowerCase(Locale.ROOT);
        for (DropEntry drop : cfg.drops) {
            if (drop == null || drop.itemId == null) {
                continue;
            }
            if (drop.itemId.trim().toLowerCase(Locale.ROOT).equals(normalized)) {
                drop.amount = Math.max(1, amount);
                drop.payAmount = Math.max(0.0d, payAmount);
                drop.repairChance = Math.max(0.0d, repairChance);
                drop.repairAmountPercent = Math.max(0.0d, repairAmountPercent);
                save();
                return;
            }
        }
        if (itemId != null && !itemId.isBlank()) {
            cfg.drops.add(new DropEntry(itemId, 0.0d, amount, payAmount, repairChance, repairAmountPercent));
            save();
        }
    }

    public synchronized void copyTier(int fromTier, int toTier) {
        TierConfig from = getTierConfig(fromTier, false);
        TierConfig to = getTierConfig(toTier, true);
        to.drops.clear();
        if (from != null && from.drops != null) {
            for (DropEntry drop : from.drops) {
                to.drops.add(new DropEntry(
                        drop.itemId,
                        drop.chance,
                        drop.amount,
                        drop.payAmount,
                        drop.repairChance,
                        drop.repairAmountPercent
                ));
            }
        }
        save();
    }

    public synchronized void removeDrop(int tier, String itemId) {
        TierConfig cfg = getTierConfig(tier, false);
        if (cfg == null || cfg.drops == null) {
            return;
        }
        cfg.drops.removeIf(d -> d.itemId.equals(itemId));
        save();
    }

    public synchronized double getTotalChanceForTier(int tier) {
        double sum = 0.0d;
        for (DropEntry d : getDropsForTier(tier)) {
            sum += d.chance;
        }
        return sum;
    }

    public synchronized boolean containsDropIdInTier(int tier, String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        String normalized = id.trim().toLowerCase(Locale.ROOT);
        for (DropEntry drop : getDropsForTier(tier)) {
            if (drop.itemId != null && drop.itemId.trim().toLowerCase(Locale.ROOT).equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean containsDropIdAnyTier(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        String normalized = id.trim().toLowerCase(Locale.ROOT);
        for (String defaultItem : DEFAULT_ITEMS) {
            if (defaultItem != null && defaultItem.toLowerCase(Locale.ROOT).equals(normalized)) {
                return true;
            }
        }
        for (TierConfig cfg : data.tiers.values()) {
            if (cfg == null || cfg.drops == null) {
                continue;
            }
            for (DropEntry drop : cfg.drops) {
                if (drop.itemId != null && drop.itemId.trim().toLowerCase(Locale.ROOT).equals(normalized)) {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized String getRandomDropForTier(int tier) {
        List<DropEntry> drops = getDropsForTier(tier);
        if (drops.isEmpty()) {
            return null;
        }
        double total = 0.0d;
        for (DropEntry drop : drops) {
            if (drop == null) {
                continue;
            }
            total += Math.max(0.0d, drop.chance);
        }
        if (total <= 0.0d) {
            return null;
        }

        double roll = Math.random() * total;
        double cumulative = 0.0d;
        for (DropEntry drop : drops) {
            if (drop == null || drop.itemId == null || drop.itemId.isBlank()) {
                continue;
            }
            cumulative += Math.max(0.0d, drop.chance);
            if (roll <= cumulative) {
                return drop.itemId;
            }
        }
        return null;
    }

    public synchronized int getAmountForTierDrop(int tier, String itemIdOrBlockId) {
        if (itemIdOrBlockId == null || itemIdOrBlockId.isBlank()) {
            return 1;
        }
        TierConfig cfg = getTierConfig(tier, false);
        if (cfg == null || cfg.drops == null) {
            return 1;
        }
        String normalized = itemIdOrBlockId.trim().toLowerCase(Locale.ROOT);
        for (DropEntry drop : cfg.drops) {
            if (drop.itemId != null && drop.itemId.trim().toLowerCase(Locale.ROOT).equals(normalized)) {
                return Math.max(1, drop.amount);
            }
        }
        return 1;
    }

    private static double getFactor(String mode, double exponent, int tier, int maxTier) {
        if (maxTier <= 1) {
            return 1.0d;
        }
        int clamped = Math.max(1, Math.min(maxTier, tier));
        double progress = (double) (clamped - 1) / (double) (maxTier - 1);
        double exp = exponent > 0.0d ? exponent : 1.0d;
        String normalized = normalizeMode(mode);
        switch (normalized) {
            case "LINEAR":
                return progress;
            case "EASE_IN":
                return Math.pow(progress, exp);
            case "EASE_OUT":
                return 1.0d - Math.pow(1.0d - progress, exp);
            case "EASE_IN_OUT": {
                if (progress <= 0.0d) return 0.0d;
                if (progress >= 1.0d) return 1.0d;
                if (progress < 0.5d) {
                    return 0.5d * Math.pow(progress * 2.0d, exp);
                }
                return 1.0d - 0.5d * Math.pow((1.0d - progress) * 2.0d, exp);
            }
            case "EXPONENTIAL": {
                double k = Math.max(0.001d, exp);
                double denom = Math.exp(k) - 1.0d;
                if (denom <= 1e-9d) {
                    return progress;
                }
                return (Math.exp(k * progress) - 1.0d) / denom;
            }
            case "LOGARITHMIC": {
                double k = Math.max(0.001d, exp);
                double denom = Math.log1p(k);
                if (Math.abs(denom) <= 1e-9d) {
                    return progress;
                }
                return Math.log1p(k * progress) / denom;
            }
            case "POWER":
            default:
                return Math.pow(progress, exp);
        }
    }

    private static List<DropEntry> buildDropsFromCurveModel(CurveModelConfig model, double factor) {
        List<DropEntry> generated = new ArrayList<>();
        if (model.items == null || model.items.isEmpty()) {
            generated.add(new DropEntry("Rock_Stone_Cobble", 100.0d, 1));
            return generated;
        }

        double[] raw = new double[model.items.size()];
        double total = 0.0d;
        for (int i = 0; i < model.items.size(); i++) {
            CurveItemConfig item = model.items.get(i);
            double value = item.startChance + (item.endChance - item.startChance) * factor;
            raw[i] = Math.max(0.0d, value);
            total += raw[i];
        }
        if (total <= 0.0d) {
            generated.add(new DropEntry("Rock_Stone_Cobble", 100.0d, 1));
            return generated;
        }
        for (int i = 0; i < model.items.size(); i++) {
            CurveItemConfig item = model.items.get(i);
            if (item.itemId == null || item.itemId.isBlank()) {
                continue;
            }
            generated.add(new DropEntry(item.itemId, (raw[i] / total) * 100.0d, 1));
        }
        return generated;
    }

    private static DropEntry copyDrop(DropEntry source) {
        if (source == null) {
            return null;
        }
        return new DropEntry(
                source.itemId,
                source.chance,
                source.amount,
                source.payAmount,
                source.repairChance,
                source.repairAmountPercent
        );
    }

    private double getCalculatedUpgradeCostForTier(int tier) {
        CsvTierDefaults csvTier = getCsvDefaults().get(Math.max(1, Math.min(getMaxTier(), tier)));
        if (csvTier != null) {
            return Math.max(0.0d, csvTier.price);
        }
        CurveModelConfig model = getCurveModelCopy();
        return Math.max(0.0d, model.maxUpgradeCost) * getFactor(model.costMode, model.costExponent, tier, getMaxTier());
    }

    private List<DropEntry> getCalculatedDropsForTier(int tier) {
        CsvTierDefaults csvTier = getCsvDefaults().get(Math.max(1, Math.min(getMaxTier(), tier)));
        if (csvTier != null) {
            List<DropEntry> copy = new ArrayList<>(csvTier.drops.size());
            for (DropEntry drop : csvTier.drops) {
                copy.add(new DropEntry(drop.itemId, drop.chance, drop.amount));
            }
            return copy;
        }

        CurveModelConfig model = getCurveModelCopy();
        double factor = getFactor(model.chanceMode, model.chanceExponent, tier, getMaxTier());
        return buildDropsFromCurveModel(model, factor);
    }

    private static Map<Integer, CsvTierDefaults> getCsvDefaults() {
        Map<Integer, CsvTierDefaults> local = cachedCsvDefaults;
        if (local != null) {
            return local;
        }
        synchronized (ConfigManager.class) {
            if (cachedCsvDefaults != null) {
                return cachedCsvDefaults;
            }
            cachedCsvDefaults = Collections.unmodifiableMap(loadCsvDefaults());
            return cachedCsvDefaults;
        }
    }

    private static Map<Integer, CsvTierDefaults> loadCsvDefaults() {
        Map<Integer, CsvTierDefaults> result = new HashMap<>();
        InputStream stream = ConfigManager.class.getResourceAsStream(CSV_DEFAULTS_RESOURCE);
        if (stream == null) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line = reader.readLine(); // header
            if (line == null) {
                return result;
            }
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] cols = line.split(",");
                if (cols.length < 11) {
                    continue;
                }
                int level = parseIntSafe(cols[0], -1);
                double price = parseDoubleSafe(cols[1], 0.0d);
                if (level < 1) {
                    continue;
                }
                List<DropEntry> drops = new ArrayList<>(DEFAULT_ITEMS.length);
                for (int i = 0; i < DEFAULT_ITEMS.length; i++) {
                    double chance = parseDoubleSafe(cols[2 + i], 0.0d);
                    drops.add(new DropEntry(DEFAULT_ITEMS[i], chance, 1));
                }
                result.put(level, new CsvTierDefaults(price, drops));
            }
        } catch (IOException ignored) {
        }
        return result;
    }

    private static int parseIntSafe(String raw, int fallback) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static double parseDoubleSafe(String raw, double fallback) {
        try {
            return Double.parseDouble(raw.trim().replace(',', '.'));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private TierConfig getTierConfig(int tier, boolean create) {
        ensureDefaults();
        int clampedTier = Math.max(1, Math.min(data.maxTier, tier));
        String key = String.valueOf(clampedTier);
        TierConfig cfg = data.tiers.get(key);
        if (cfg == null && create) {
            cfg = new TierConfig();
            data.tiers.put(key, cfg);
        }
        if (cfg != null && cfg.drops == null) {
            cfg.drops = new ArrayList<>();
        }
        return cfg;
    }

    private File getConfigFile() {
        CobblestoneZufallPlugin plugin = CobblestoneZufallPlugin.getInstance();
        if (plugin != null && plugin.getDataDirectory() != null) {
            return plugin.getDataDirectory().resolve(CONFIG_FILE).toFile();
        }
        return new File(CONFIG_FILE);
    }

    public synchronized String getConfigFilePath() {
        return getConfigFile().getAbsolutePath();
    }

    private void writePlayerSaveTable() {
        File tableFile = getPlayerTableFile();
        File parent = tableFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        List<Map.Entry<String, PlayerTierData>> entries = new ArrayList<>(data.players.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        try (PrintWriter out = new PrintWriter(new FileWriter(tableFile))) {
            out.println("PlayerUUID,CobblestoneGeneratorLevel");
            for (Map.Entry<String, PlayerTierData> entry : entries) {
                if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                int tier = Math.max(1, Math.min(getMaxTier(), entry.getValue().tier));
                out.println(entry.getKey() + "," + tier);
            }
        } catch (IOException ignored) {
        }
    }

    private File getPlayerTableFile() {
        CobblestoneZufallPlugin plugin = CobblestoneZufallPlugin.getInstance();
        if (plugin != null && plugin.getDataDirectory() != null) {
            return plugin.getDataDirectory().resolve(PLAYER_TABLE_FILE).toFile();
        }
        return new File(PLAYER_TABLE_FILE);
    }
}
