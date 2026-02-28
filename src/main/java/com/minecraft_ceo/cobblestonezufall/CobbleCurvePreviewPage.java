package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CobbleCurvePreviewPage extends InteractiveCustomUIPage<CobbleCurvePreviewPage.Data> {

    public static final class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("Accept", Codec.STRING), (d, v) -> d.accept = v, d -> d.accept).add()
                .append(new KeyedCodec<>("Deny", Codec.STRING), (d, v) -> d.deny = v, d -> d.deny).add()
                .append(new KeyedCodec<>("PrevPage", Codec.STRING), (d, v) -> d.prevPage = v, d -> d.prevPage).add()
                .append(new KeyedCodec<>("NextPage", Codec.STRING), (d, v) -> d.nextPage = v, d -> d.nextPage).add()
                .build();
        public String accept;
        public String deny;
        public String prevPage;
        public String nextPage;
    }

    private final PlayerRef playerRef;
    private final ConfigManager configManager;
    private final ConfigManager.CurveModelConfig model;
    private final boolean compareMode;
    private int pageStartLevel = 1;
    private static final int PAGE_SIZE = 12;

    public CobbleCurvePreviewPage(PlayerRef playerRef, ConfigManager configManager, ConfigManager.CurveModelConfig model, boolean compareMode) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
        this.playerRef = playerRef;
        this.configManager = configManager;
        this.model = model;
        this.compareMode = compareMode;
    }

    public static void open(Ref<EntityStore> entityRef, Store<EntityStore> store, PlayerRef playerRef, ConfigManager configManager, ConfigManager.CurveModelConfig model, boolean compareMode) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            return;
        }
        player.getPageManager().openCustomPage(entityRef, store, new CobbleCurvePreviewPage(playerRef, configManager, model, compareMode));
    }

    @Override
    public void build(Ref<EntityStore> entityRef, UICommandBuilder commandBuilder, UIEventBuilder eventBuilder, Store<EntityStore> store) {
        commandBuilder.append("Pages/CobbleCurvePreview.ui");
        commandBuilder.set("#PreviewTitle.Text", "Example Preview (1.." + model.maxTier + ")");
        commandBuilder.set("#PreviewSummary.Text", "Chance: " + model.chanceMode + " (exp " + format(model.chanceExponent) + ") | Cost: " + model.costMode + " (exp " + format(model.costExponent) + ")");
        commandBuilder.set("#HeaderCost.Text", compareMode ? "Chance Factor" : "Cost");
        commandBuilder.set("#HeaderCobble.Text", compareMode ? "Cost Factor" : "Cobble %");
        commandBuilder.set("#HeaderOres.Text", compareMode ? "Delta" : "Total Ores %");
        int max = Math.max(1, model.maxTier);
        if (pageStartLevel < 1) {
            pageStartLevel = 1;
        }
        if (pageStartLevel > max) {
            pageStartLevel = Math.max(1, max - PAGE_SIZE + 1);
        }
        int pageEnd = Math.min(max, pageStartLevel + PAGE_SIZE - 1);
        commandBuilder.set("#PageInfo.Text", "Levels " + pageStartLevel + " - " + pageEnd + " / " + max);
        for (int i = 0; i < PAGE_SIZE; i++) {
            int level = pageStartLevel + i;
            String lvSelector = "#Row" + i + "Lvl.Text";
            String costSelector = "#Row" + i + "Cost.Text";
            String cobSelector = "#Row" + i + "Cobble.Text";
            String oreSelector = "#Row" + i + "Ores.Text";
            if (level <= max) {
                List<Double> chances = calculateChancesForLevel(level);
                double cobble = chances.isEmpty() ? 0.0d : chances.get(0);
                double ores = Math.max(0.0d, 100.0d - cobble);
                double chanceFactor = factor(model.chanceMode, model.chanceExponent, level, max);
                double costFactor = factor(model.costMode, model.costExponent, level, max);
                double cost = model.maxUpgradeCost * costFactor;
                double bps = Math.max(0.0d, configManager.getExpectedBlocksPerSecond());
                double blocksPerMinute = bps * 60.0d;
                double blocksPerHour = blocksPerMinute * 60.0d;
                double blocksPerDay = blocksPerHour * 24.0d;
                double cobbleOutMin = blocksPerMinute * (cobble / 100.0d);
                double cobbleOutHour = blocksPerHour * (cobble / 100.0d);
                double cobbleOutDay = blocksPerDay * (cobble / 100.0d);
                commandBuilder.set(lvSelector, "LV " + level);
                if (compareMode) {
                    commandBuilder.set(costSelector, format(chanceFactor * 100.0d) + "%");
                    commandBuilder.set(cobSelector, format(costFactor * 100.0d) + "%");
                    commandBuilder.set(oreSelector, format((costFactor - chanceFactor) * 100.0d) + "%");
                } else {
                    commandBuilder.set(costSelector, "$ " + formatMoney(cost));
                    commandBuilder.set(cobSelector, format(cobble) + "%");
                    commandBuilder.set(oreSelector, format(ores) + "%");
                }
                commandBuilder.set("#Row" + i + "Min.Text", formatAmount(cobbleOutMin));
                commandBuilder.set("#Row" + i + "Hour.Text", formatAmount(cobbleOutHour));
                commandBuilder.set("#Row" + i + "Day.Text", formatAmount(cobbleOutDay));
            } else {
                commandBuilder.set(lvSelector, "");
                commandBuilder.set(costSelector, "");
                commandBuilder.set(cobSelector, "");
                commandBuilder.set(oreSelector, "");
                commandBuilder.set("#Row" + i + "Min.Text", "");
                commandBuilder.set("#Row" + i + "Hour.Text", "");
                commandBuilder.set("#Row" + i + "Day.Text", "");
            }
        }
        int detailLevel = Math.max(1, model.maxTier);
<<<<<<< HEAD
        commandBuilder.set("#DetailTitle.Text", "Detailed resource split at level " + detailLevel
                + " | Collect Range: " + configManager.getAutoCollectRange()
=======
        int travelTimeMs = configManager.getAutoCollectRangeTypeMs();
        commandBuilder.set("#DetailTitle.Text", "Detailed resource split at level " + detailLevel
                + " | Block TravelTime: " + (travelTimeMs <= 0 ? "NO DELAY" : (travelTimeMs + "ms"))
>>>>>>> cd2cc2b (Prepare clean project state)
                + " | Expected BPS: " + format(configManager.getExpectedBlocksPerSecond()));
        List<Double> detailed = calculateChancesForLevel(detailLevel);
        for (int i = 0; i < Math.min(9, model.items.size()); i++) {
            commandBuilder.set("#Res" + i + "Name.Text", shortName(model.items.get(i).itemId));
            commandBuilder.set("#Res" + i + "Chance.Text", format(detailed.get(i)) + "%");
        }
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#AcceptButton", EventData.of("Accept", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#DenyButton", EventData.of("Deny", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#PrevPageButton", EventData.of("PrevPage", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#NextPageButton", EventData.of("NextPage", "1"));
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> entityRef, Store<EntityStore> store, Data data) {
        if (data == null) {
            return;
        }
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            close();
            return;
        }
        if (isTruthy(data.prevPage)) {
            pageStartLevel = Math.max(1, pageStartLevel - PAGE_SIZE);
            rebuild();
            return;
        }
        if (isTruthy(data.nextPage)) {
            int max = Math.max(1, model.maxTier);
            pageStartLevel = Math.min(max, pageStartLevel + PAGE_SIZE);
            rebuild();
            return;
        }
        if (isTruthy(data.accept)) {
            configManager.saveCurveModel(model);
            configManager.applyCurveModelToAllTiers();
            player.sendMessage(Message.raw("[CobblestoneZufall] Example accepted and applied to all levels."));
            CobbleCurvePage.open(entityRef, store, playerRef, configManager);
            return;
        }
        if (isTruthy(data.deny)) {
            player.sendMessage(Message.raw("[CobblestoneZufall] Example cancelled."));
            CobbleCurvePage.open(entityRef, store, playerRef, configManager);
        }
    }

    private List<Double> calculateChancesForLevel(int level) {
        int max = Math.max(1, model.maxTier);
        double f = factor(model.chanceMode, model.chanceExponent, level, max);
        double[] raw = new double[model.items.size()];
        double total = 0.0d;
        for (int i = 0; i < model.items.size(); i++) {
            ConfigManager.CurveItemConfig item = model.items.get(i);
            raw[i] = Math.max(0.0d, item.startChance + (item.endChance - item.startChance) * f);
            total += raw[i];
        }
        List<Double> out = new ArrayList<>(model.items.size());
        for (int i = 0; i < model.items.size(); i++) {
            out.add(total > 0.0d ? (raw[i] / total) * 100.0d : 0.0d);
        }
        return out;
    }

    private static double factor(String mode, double exponent, int tier, int maxTier) {
        if (maxTier <= 1) return 1.0d;
        double progress = (double) (Math.max(1, Math.min(maxTier, tier)) - 1) / (double) (maxTier - 1);
        double exp = Math.max(0.001d, exponent);
        String m = mode == null ? "POWER" : mode.toUpperCase(Locale.ROOT);
        switch (m) {
            case "LINEAR": return progress;
            case "EASE_IN": return Math.pow(progress, exp);
            case "EASE_OUT": return 1.0d - Math.pow(1.0d - progress, exp);
            case "EASE_IN_OUT":
                if (progress < 0.5d) return 0.5d * Math.pow(progress * 2.0d, exp);
                return 1.0d - 0.5d * Math.pow((1.0d - progress) * 2.0d, exp);
            case "EXPONENTIAL":
                double denomE = Math.exp(exp) - 1.0d;
                return denomE <= 1e-9d ? progress : (Math.exp(exp * progress) - 1.0d) / denomE;
            case "LOGARITHMIC":
                double denomL = Math.log1p(exp);
                return Math.abs(denomL) <= 1e-9d ? progress : Math.log1p(exp * progress) / denomL;
            case "POWER":
            default:
                return Math.pow(progress, exp);
        }
    }

    private static boolean isTruthy(String value) {
        return value != null && !value.isBlank() && !"0".equals(value.trim());
    }

    private static String shortName(String itemId) {
        if (itemId == null) return "Unknown";
        return itemId.replace("Rock_Stone_", "").replace("Ore_", "").replace("_Stone", "").replace("_Mud", "").replace("_Magma", "").replace("_Shale", "");
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.4f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static String formatMoney(double value) {
        return String.format(Locale.US, "%,.2f", value);
    }

    private static String formatAmount(double value) {
        return String.format(Locale.US, "%,.2f", Math.max(0.0d, value));
    }
}
