package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
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
import java.util.logging.Level;

public class CobbleCurvePage extends InteractiveCustomUIPage<CobbleCurvePage.Data> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String[] MODES = new String[]{
            "LINEAR", "POWER", "EASE_IN", "EASE_OUT", "EASE_IN_OUT", "EXPONENTIAL", "LOGARITHMIC"
    };

    private final ConfigManager configManager;
    private final PlayerRef playerRef;
    private String chanceModeSelection;
    private String costModeSelection;

    public static final class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("SaveModel", Codec.STRING), (d, v) -> d.saveModel = v, d -> d.saveModel).add()
                .append(new KeyedCodec<>("ApplyModel", Codec.STRING), (d, v) -> d.applyModel = v, d -> d.applyModel).add()
                .append(new KeyedCodec<>("ShowExample", Codec.STRING), (d, v) -> d.showExample = v, d -> d.showExample).add()
                .append(new KeyedCodec<>("ChanceModePrev", Codec.STRING), (d, v) -> d.chanceModePrev = v, d -> d.chanceModePrev).add()
                .append(new KeyedCodec<>("ChanceModeNext", Codec.STRING), (d, v) -> d.chanceModeNext = v, d -> d.chanceModeNext).add()
                .append(new KeyedCodec<>("CostModePrev", Codec.STRING), (d, v) -> d.costModePrev = v, d -> d.costModePrev).add()
                .append(new KeyedCodec<>("CostModeNext", Codec.STRING), (d, v) -> d.costModeNext = v, d -> d.costModeNext).add()
                .append(new KeyedCodec<>("Back", Codec.STRING), (d, v) -> d.back = v, d -> d.back).add()
                .append(new KeyedCodec<>("Close", Codec.STRING), (d, v) -> d.close = v, d -> d.close).add()
                .append(new KeyedCodec<>("@MaxTierInput", Codec.STRING), (d, v) -> d.maxTierInput = v, d -> d.maxTierInput).add()
                .append(new KeyedCodec<>("@MaxCostInput", Codec.STRING), (d, v) -> d.maxCostInput = v, d -> d.maxCostInput).add()
                .append(new KeyedCodec<>("@ChanceExpInput", Codec.STRING), (d, v) -> d.chanceExpInput = v, d -> d.chanceExpInput).add()
                .append(new KeyedCodec<>("@CostExpInput", Codec.STRING), (d, v) -> d.costExpInput = v, d -> d.costExpInput).add()
                .append(new KeyedCodec<>("@Item0Start", Codec.STRING), (d, v) -> d.item0Start = v, d -> d.item0Start).add()
                .append(new KeyedCodec<>("@Item0End", Codec.STRING), (d, v) -> d.item0End = v, d -> d.item0End).add()
                .append(new KeyedCodec<>("@Item1Start", Codec.STRING), (d, v) -> d.item1Start = v, d -> d.item1Start).add()
                .append(new KeyedCodec<>("@Item1End", Codec.STRING), (d, v) -> d.item1End = v, d -> d.item1End).add()
                .append(new KeyedCodec<>("@Item2Start", Codec.STRING), (d, v) -> d.item2Start = v, d -> d.item2Start).add()
                .append(new KeyedCodec<>("@Item2End", Codec.STRING), (d, v) -> d.item2End = v, d -> d.item2End).add()
                .append(new KeyedCodec<>("@Item3Start", Codec.STRING), (d, v) -> d.item3Start = v, d -> d.item3Start).add()
                .append(new KeyedCodec<>("@Item3End", Codec.STRING), (d, v) -> d.item3End = v, d -> d.item3End).add()
                .append(new KeyedCodec<>("@Item4Start", Codec.STRING), (d, v) -> d.item4Start = v, d -> d.item4Start).add()
                .append(new KeyedCodec<>("@Item4End", Codec.STRING), (d, v) -> d.item4End = v, d -> d.item4End).add()
                .append(new KeyedCodec<>("@Item5Start", Codec.STRING), (d, v) -> d.item5Start = v, d -> d.item5Start).add()
                .append(new KeyedCodec<>("@Item5End", Codec.STRING), (d, v) -> d.item5End = v, d -> d.item5End).add()
                .append(new KeyedCodec<>("@Item6Start", Codec.STRING), (d, v) -> d.item6Start = v, d -> d.item6Start).add()
                .append(new KeyedCodec<>("@Item6End", Codec.STRING), (d, v) -> d.item6End = v, d -> d.item6End).add()
                .append(new KeyedCodec<>("@Item7Start", Codec.STRING), (d, v) -> d.item7Start = v, d -> d.item7Start).add()
                .append(new KeyedCodec<>("@Item7End", Codec.STRING), (d, v) -> d.item7End = v, d -> d.item7End).add()
                .append(new KeyedCodec<>("@Item8Start", Codec.STRING), (d, v) -> d.item8Start = v, d -> d.item8Start).add()
                .append(new KeyedCodec<>("@Item8End", Codec.STRING), (d, v) -> d.item8End = v, d -> d.item8End).add()
                .build();

        public String saveModel;
        public String applyModel;
        public String showExample;
        public String chanceModePrev;
        public String chanceModeNext;
        public String costModePrev;
        public String costModeNext;
        public String back;
        public String close;
        public String maxTierInput;
        public String maxCostInput;
        public String chanceExpInput;
        public String costExpInput;
        public String item0Start;
        public String item0End;
        public String item1Start;
        public String item1End;
        public String item2Start;
        public String item2End;
        public String item3Start;
        public String item3End;
        public String item4Start;
        public String item4End;
        public String item5Start;
        public String item5End;
        public String item6Start;
        public String item6End;
        public String item7Start;
        public String item7End;
        public String item8Start;
        public String item8End;
    }

    public CobbleCurvePage(PlayerRef playerRef, ConfigManager configManager) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
        this.playerRef = playerRef;
        this.configManager = configManager;
        ConfigManager.CurveModelConfig model = configManager.getCurveModelCopy();
        this.chanceModeSelection = normalizeMode(model.chanceMode);
        this.costModeSelection = normalizeMode(model.costMode);
    }

    public static void open(Ref<EntityStore> entityRef, Store<EntityStore> store, PlayerRef playerRef, ConfigManager configManager) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            return;
        }
        player.getPageManager().openCustomPage(entityRef, store, new CobbleCurvePage(playerRef, configManager));
    }

    @Override
    public void build(Ref<EntityStore> entityRef, UICommandBuilder commandBuilder, UIEventBuilder eventBuilder, Store<EntityStore> store) {
        commandBuilder.append("Pages/CobbleCurveModel.ui");
        ConfigManager.CurveModelConfig model = configManager.getCurveModelCopy();
        chanceModeSelection = normalizeMode(chanceModeSelection == null ? model.chanceMode : chanceModeSelection);
        costModeSelection = normalizeMode(costModeSelection == null ? model.costMode : costModeSelection);

        commandBuilder.set("#MaxTierInput.Value", String.valueOf(model.maxTier));
        commandBuilder.set("#MaxCostInput.Value", format(model.maxUpgradeCost));
        commandBuilder.set("#ChanceModeValue.Text", chanceModeSelection);
        commandBuilder.set("#ChanceExpInput.Value", format(model.chanceExponent));
        commandBuilder.set("#CostModeValue.Text", costModeSelection);
        commandBuilder.set("#CostExpInput.Value", format(model.costExponent));

        double startTotal = 0.0d;
        double endTotal = 0.0d;
        List<ConfigManager.CurveItemConfig> items = new ArrayList<>(model.items);
        for (int i = 0; i < Math.min(9, items.size()); i++) {
            ConfigManager.CurveItemConfig item = items.get(i);
            commandBuilder.set("#Item" + i + "Name.Text", item.itemId);
            commandBuilder.set("#Item" + i + "Start.Value", format(item.startChance));
            commandBuilder.set("#Item" + i + "End.Value", format(item.endChance));
            startTotal += item.startChance;
            endTotal += item.endChance;
        }
        commandBuilder.set("#StartTotalInfo.Text", "Start total used: " + format(startTotal) + "%");
        commandBuilder.set("#EndTotalInfo.Text", "End total used: " + format(endTotal) + "%");

        EventData payloadSave = EventData.of("SaveModel", "1")
                .append("@MaxTierInput", "#MaxTierInput.Value")
                .append("@MaxCostInput", "#MaxCostInput.Value")
                .append("@ChanceExpInput", "#ChanceExpInput.Value")
                .append("@CostExpInput", "#CostExpInput.Value")
                .append("@Item0Start", "#Item0Start.Value").append("@Item0End", "#Item0End.Value")
                .append("@Item1Start", "#Item1Start.Value").append("@Item1End", "#Item1End.Value")
                .append("@Item2Start", "#Item2Start.Value").append("@Item2End", "#Item2End.Value")
                .append("@Item3Start", "#Item3Start.Value").append("@Item3End", "#Item3End.Value")
                .append("@Item4Start", "#Item4Start.Value").append("@Item4End", "#Item4End.Value")
                .append("@Item5Start", "#Item5Start.Value").append("@Item5End", "#Item5End.Value")
                .append("@Item6Start", "#Item6Start.Value").append("@Item6End", "#Item6End.Value")
                .append("@Item7Start", "#Item7Start.Value").append("@Item7End", "#Item7End.Value")
                .append("@Item8Start", "#Item8Start.Value").append("@Item8End", "#Item8End.Value");

        EventData payloadApply = EventData.of("ApplyModel", "1")
                .append("@MaxTierInput", "#MaxTierInput.Value")
                .append("@MaxCostInput", "#MaxCostInput.Value")
                .append("@ChanceExpInput", "#ChanceExpInput.Value")
                .append("@CostExpInput", "#CostExpInput.Value")
                .append("@Item0Start", "#Item0Start.Value").append("@Item0End", "#Item0End.Value")
                .append("@Item1Start", "#Item1Start.Value").append("@Item1End", "#Item1End.Value")
                .append("@Item2Start", "#Item2Start.Value").append("@Item2End", "#Item2End.Value")
                .append("@Item3Start", "#Item3Start.Value").append("@Item3End", "#Item3End.Value")
                .append("@Item4Start", "#Item4Start.Value").append("@Item4End", "#Item4End.Value")
                .append("@Item5Start", "#Item5Start.Value").append("@Item5End", "#Item5End.Value")
                .append("@Item6Start", "#Item6Start.Value").append("@Item6End", "#Item6End.Value")
                .append("@Item7Start", "#Item7Start.Value").append("@Item7End", "#Item7End.Value")
                .append("@Item8Start", "#Item8Start.Value").append("@Item8End", "#Item8End.Value");

        EventData payloadPreview = EventData.of("ShowExample", "1")
                .append("@MaxTierInput", "#MaxTierInput.Value")
                .append("@MaxCostInput", "#MaxCostInput.Value")
                .append("@ChanceExpInput", "#ChanceExpInput.Value")
                .append("@CostExpInput", "#CostExpInput.Value")
                .append("@Item0Start", "#Item0Start.Value").append("@Item0End", "#Item0End.Value")
                .append("@Item1Start", "#Item1Start.Value").append("@Item1End", "#Item1End.Value")
                .append("@Item2Start", "#Item2Start.Value").append("@Item2End", "#Item2End.Value")
                .append("@Item3Start", "#Item3Start.Value").append("@Item3End", "#Item3End.Value")
                .append("@Item4Start", "#Item4Start.Value").append("@Item4End", "#Item4End.Value")
                .append("@Item5Start", "#Item5Start.Value").append("@Item5End", "#Item5End.Value")
                .append("@Item6Start", "#Item6Start.Value").append("@Item6End", "#Item6End.Value")
                .append("@Item7Start", "#Item7Start.Value").append("@Item7End", "#Item7End.Value")
                .append("@Item8Start", "#Item8Start.Value").append("@Item8End", "#Item8End.Value");

        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveModelButton", payloadSave);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ApplyModelButton", payloadApply);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ShowExampleButton", payloadPreview);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ChanceModePrevButton", EventData.of("ChanceModePrev", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ChanceModeNextButton", EventData.of("ChanceModeNext", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CostModePrevButton", EventData.of("CostModePrev", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CostModeNextButton", EventData.of("CostModeNext", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BackButton", EventData.of("Back", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton", EventData.of("Close", "1"));
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
        if (isTruthy(data.close)) {
            close();
            return;
        }
        if (isTruthy(data.back)) {
            CobbleConfigPage.open(entityRef, store, playerRef, configManager);
            return;
        }
        if (isTruthy(data.chanceModePrev)) {
            chanceModeSelection = shiftMode(chanceModeSelection, -1);
            rebuild();
            return;
        }
        if (isTruthy(data.chanceModeNext)) {
            chanceModeSelection = shiftMode(chanceModeSelection, 1);
            rebuild();
            return;
        }
        if (isTruthy(data.costModePrev)) {
            costModeSelection = shiftMode(costModeSelection, -1);
            rebuild();
            return;
        }
        if (isTruthy(data.costModeNext)) {
            costModeSelection = shiftMode(costModeSelection, 1);
            rebuild();
            return;
        }

        if (isTruthy(data.showExample)) {
            try {
                ConfigManager.CurveModelConfig model = buildModelFromData(data);
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][CURVE] Opening preview");
                CobbleCurvePreviewPage.open(entityRef, store, playerRef, configManager, model, false);
                return;
            } catch (Exception ex) {
                LOGGER.at(Level.SEVERE).log("[CobblestoneZufall][CURVE] Failed opening preview: " + ex.getMessage());
                player.sendMessage(Message.raw("[CobblestoneZufall] Invalid curve model values."));
                rebuild();
                return;
            }
        }

        if (isTruthy(data.saveModel) || isTruthy(data.applyModel)) {
            try {
                ConfigManager.CurveModelConfig model = buildModelFromData(data);
                configManager.saveCurveModel(model);
                if (isTruthy(data.applyModel)) {
                    configManager.applyCurveModelToAllTiers();
                    player.sendMessage(Message.raw("[CobblestoneZufall] Curve model applied to all levels (1-" + configManager.getMaxTier() + ")."));
                } else {
                    player.sendMessage(Message.raw("[CobblestoneZufall] Curve model saved."));
                }
            } catch (Exception ex) {
                LOGGER.at(Level.SEVERE).log("[CobblestoneZufall][CURVE] Failed handling curve event: " + ex.getMessage());
                player.sendMessage(Message.raw("[CobblestoneZufall] Invalid curve model values."));
            }
            rebuild();
        }
    }

    private ConfigManager.CurveModelConfig buildModelFromData(Data data) {
        ConfigManager.CurveModelConfig model = configManager.getCurveModelCopy();
        model.maxTier = parseInt(data.maxTierInput, model.maxTier);
        model.maxUpgradeCost = Math.max(0.0d, parseDouble(data.maxCostInput, model.maxUpgradeCost));
        model.chanceMode = normalizeMode(chanceModeSelection);
        model.chanceExponent = Math.max(0.001d, parseDouble(data.chanceExpInput, model.chanceExponent));
        model.costMode = normalizeMode(costModeSelection);
        model.costExponent = Math.max(0.001d, parseDouble(data.costExpInput, model.costExponent));

        double[] starts = new double[]{
                parseDouble(data.item0Start, 0.0d), parseDouble(data.item1Start, 0.0d), parseDouble(data.item2Start, 0.0d),
                parseDouble(data.item3Start, 0.0d), parseDouble(data.item4Start, 0.0d), parseDouble(data.item5Start, 0.0d),
                parseDouble(data.item6Start, 0.0d), parseDouble(data.item7Start, 0.0d), parseDouble(data.item8Start, 0.0d)
        };
        double[] ends = new double[]{
                parseDouble(data.item0End, 0.0d), parseDouble(data.item1End, 0.0d), parseDouble(data.item2End, 0.0d),
                parseDouble(data.item3End, 0.0d), parseDouble(data.item4End, 0.0d), parseDouble(data.item5End, 0.0d),
                parseDouble(data.item6End, 0.0d), parseDouble(data.item7End, 0.0d), parseDouble(data.item8End, 0.0d)
        };
        for (int i = 0; i < Math.min(9, model.items.size()); i++) {
            model.items.get(i).startChance = Math.max(0.0d, starts[i]);
            model.items.get(i).endChance = Math.max(0.0d, ends[i]);
        }
        return model;
    }

    private static String shiftMode(String current, int step) {
        int idx = 0;
        String normalized = normalizeMode(current);
        for (int i = 0; i < MODES.length; i++) {
            if (MODES[i].equals(normalized)) {
                idx = i;
                break;
            }
        }
        int next = (idx + step) % MODES.length;
        if (next < 0) {
            next += MODES.length;
        }
        return MODES[next];
    }

    private static String normalizeMode(String input) {
        if (input == null || input.isBlank()) {
            return "POWER";
        }
        String upper = input.trim().toUpperCase(Locale.ROOT);
        if ("CURVE".equals(upper) || "NON_LINEAR".equals(upper) || "NONLINEAR".equals(upper)) {
            return "POWER";
        }
        for (String mode : MODES) {
            if (mode.equals(upper)) {
                return mode;
            }
        }
        return "POWER";
    }

    private static int parseInt(String raw, int fallback) {
        try {
            return Integer.parseInt(raw == null ? "" : raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static double parseDouble(String raw, double fallback) {
        try {
            return Double.parseDouble((raw == null ? "" : raw.trim()).replace(',', '.'));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static boolean isTruthy(String value) {
        return value != null && !value.isBlank() && !"0".equals(value.trim());
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.6f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}
