package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
<<<<<<< HEAD
import com.hypixel.hytale.logger.HytaleLogger;
=======
>>>>>>> cd2cc2b (Prepare clean project state)
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
<<<<<<< HEAD
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
=======
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CobbleCurvePage extends InteractiveCustomUIPage<CobbleCurvePage.Data> {
    private static final int PAGE = 9;
    private static final int POINTS = 1_000_000;
    private static final double POINTS_PER_PERCENT = POINTS / 100.0d;
    private static final double EPS = 0.0001d;
    private static final String[] MODES = new String[]{"LINEAR", "POWER", "EASE_IN", "EASE_OUT", "EASE_IN_OUT", "EXPONENTIAL", "LOGARITHMIC"};
    private static final String[] PRESETS = new String[]{"COMMON", "BALANCED", "RARE", "LEGENDARY"};

    private enum InputMode {PERCENT, POINTS, WEIGHT}
>>>>>>> cd2cc2b (Prepare clean project state)

    public static final class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("SaveModel", Codec.STRING), (d, v) -> d.saveModel = v, d -> d.saveModel).add()
<<<<<<< HEAD
                .append(new KeyedCodec<>("ApplyModel", Codec.STRING), (d, v) -> d.applyModel = v, d -> d.applyModel).add()
                .append(new KeyedCodec<>("ShowExample", Codec.STRING), (d, v) -> d.showExample = v, d -> d.showExample).add()
                .append(new KeyedCodec<>("ChanceModePrev", Codec.STRING), (d, v) -> d.chanceModePrev = v, d -> d.chanceModePrev).add()
                .append(new KeyedCodec<>("ChanceModeNext", Codec.STRING), (d, v) -> d.chanceModeNext = v, d -> d.chanceModeNext).add()
                .append(new KeyedCodec<>("CostModePrev", Codec.STRING), (d, v) -> d.costModePrev = v, d -> d.costModePrev).add()
                .append(new KeyedCodec<>("CostModeNext", Codec.STRING), (d, v) -> d.costModeNext = v, d -> d.costModeNext).add()
=======
                .append(new KeyedCodec<>("ShowExample", Codec.STRING), (d, v) -> d.showExample = v, d -> d.showExample).add()
                .append(new KeyedCodec<>("ChanceModePrev", Codec.STRING), (d, v) -> d.chanceModePrev = v, d -> d.chanceModePrev).add()
                .append(new KeyedCodec<>("ChanceModeNext", Codec.STRING), (d, v) -> d.chanceModeNext = v, d -> d.chanceModeNext).add()
                .append(new KeyedCodec<>("ChanceModeReset", Codec.STRING), (d, v) -> d.chanceModeReset = v, d -> d.chanceModeReset).add()
                .append(new KeyedCodec<>("CostModePrev", Codec.STRING), (d, v) -> d.costModePrev = v, d -> d.costModePrev).add()
                .append(new KeyedCodec<>("CostModeNext", Codec.STRING), (d, v) -> d.costModeNext = v, d -> d.costModeNext).add()
                .append(new KeyedCodec<>("CostModeReset", Codec.STRING), (d, v) -> d.costModeReset = v, d -> d.costModeReset).add()
                .append(new KeyedCodec<>("SetModePercent", Codec.STRING), (d, v) -> d.setModePercent = v, d -> d.setModePercent).add()
                .append(new KeyedCodec<>("SetModePoints", Codec.STRING), (d, v) -> d.setModePoints = v, d -> d.setModePoints).add()
                .append(new KeyedCodec<>("SetModeWeight", Codec.STRING), (d, v) -> d.setModeWeight = v, d -> d.setModeWeight).add()
                .append(new KeyedCodec<>("AutoNormalize", Codec.STRING), (d, v) -> d.autoNormalize = v, d -> d.autoNormalize).add()
                .append(new KeyedCodec<>("PresetPrev", Codec.STRING), (d, v) -> d.presetPrev = v, d -> d.presetPrev).add()
                .append(new KeyedCodec<>("PresetNext", Codec.STRING), (d, v) -> d.presetNext = v, d -> d.presetNext).add()
                .append(new KeyedCodec<>("ApplyPreset", Codec.STRING), (d, v) -> d.applyPreset = v, d -> d.applyPreset).add()
                .append(new KeyedCodec<>("ScrollUp", Codec.STRING), (d, v) -> d.scrollUp = v, d -> d.scrollUp).add()
                .append(new KeyedCodec<>("ScrollDown", Codec.STRING), (d, v) -> d.scrollDown = v, d -> d.scrollDown).add()
>>>>>>> cd2cc2b (Prepare clean project state)
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

<<<<<<< HEAD
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

=======
        public String saveModel, showExample, chanceModePrev, chanceModeNext, chanceModeReset, costModePrev, costModeNext, costModeReset;
        public String setModePercent, setModePoints, setModeWeight, autoNormalize, presetPrev, presetNext, applyPreset;
        public String scrollUp, scrollDown, back, close, maxTierInput, maxCostInput, chanceExpInput, costExpInput;
        public String item0Start, item0End, item1Start, item1End, item2Start, item2End, item3Start, item3End, item4Start, item4End;
        public String item5Start, item5End, item6Start, item6End, item7Start, item7End, item8Start, item8End;
    }

    private final ConfigManager configManager;
    private final PlayerRef playerRef;
    private String chanceModeSelection;
    private String costModeSelection;
    private ConfigManager.CurveModelConfig model;
    private int scrollOffset = 0;
    private String status = "";
    private InputMode inputMode = InputMode.PERCENT;
    private int presetIndex = 1;
    private final Map<String, double[]> balancedTemplateById = new HashMap<>();
    private final int visibleRowsPerPage;

>>>>>>> cd2cc2b (Prepare clean project state)
    public CobbleCurvePage(PlayerRef playerRef, ConfigManager configManager) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
        this.playerRef = playerRef;
        this.configManager = configManager;
<<<<<<< HEAD
        ConfigManager.CurveModelConfig model = configManager.getCurveModelCopy();
        this.chanceModeSelection = normalizeMode(model.chanceMode);
        this.costModeSelection = normalizeMode(model.costMode);
=======
        this.model = copy(configManager.getCurveModelCopy());
        this.chanceModeSelection = normalizeMode(model.chanceMode);
        this.costModeSelection = normalizeMode(model.costMode);
        this.visibleRowsPerPage = Math.max(1, Math.min(PAGE, configManager.getCurveRowsPerPage()));
        captureBalancedTemplate();
>>>>>>> cd2cc2b (Prepare clean project state)
    }

    public static void open(Ref<EntityStore> entityRef, Store<EntityStore> store, PlayerRef playerRef, ConfigManager configManager) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
<<<<<<< HEAD
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
=======
        if (player != null) player.getPageManager().openCustomPage(entityRef, store, new CobbleCurvePage(playerRef, configManager));
    }

    @Override
    public void build(Ref<EntityStore> entityRef, UICommandBuilder c, UIEventBuilder e, Store<EntityStore> store) {
        c.append("Pages/CobbleCurveModel.ui");
        normalize();
        c.set("#MaxTierInput.Value", String.valueOf(model.maxTier));
        c.set("#MaxCostInput.Value", fmt(model.maxUpgradeCost));
        c.set("#ChanceModeValue.Text", chanceModeSelection);
        c.set("#ChanceExpInput.Value", fmt(model.chanceExponent));
        c.set("#CostModeValue.Text", costModeSelection);
        c.set("#CostExpInput.Value", fmt(model.costExponent));
        c.set("#ModeValue.Text", modeDescription());
        c.set("#PresetValue.Text", PRESETS[presetIndex]);
        c.set("#StartHeader.Text", inputMode == InputMode.WEIGHT ? "Start Weight" : (inputMode == InputMode.POINTS ? "Start Pts" : "Start %"));
        c.set("#EndHeader.Text", inputMode == InputMode.WEIGHT ? "End Weight" : (inputMode == InputMode.POINTS ? "End Pts" : "End %"));

        int total = model.items.size();
        int from = total == 0 ? 0 : scrollOffset + 1;
        int to = Math.min(total, scrollOffset + visibleRowsPerPage);
        c.set("#PageInfo.Text", "Showing " + from + "-" + to + " of " + total);

        for (int i = 0; i < PAGE; i++) {
            int idx = scrollOffset + i;
            boolean visible = i < visibleRowsPerPage && idx < total;
            c.set("#Item" + i + "Name.Visible", visible);
            c.set("#Item" + i + "Start.Visible", visible);
            c.set("#Item" + i + "End.Visible", visible);
            if (!visible) continue;
            ConfigManager.CurveItemConfig item = model.items.get(idx);
            c.set("#Item" + i + "Name.Text", item.itemId);
            c.set("#Item" + i + "Start.Value", display(item.startChance));
            c.set("#Item" + i + "End.Value", display(item.endChance));
        }

        updateTotalsGraph(c);
        bindEvents(e);
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> entityRef, Store<EntityStore> store, Data d) {
        if (d == null) return;
>>>>>>> cd2cc2b (Prepare clean project state)
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            close();
            return;
        }
<<<<<<< HEAD
        if (isTruthy(data.close)) {
            close();
            return;
        }
        if (isTruthy(data.back)) {
            CobbleConfigPage.open(entityRef, store, playerRef, configManager);
            return;
        }
        if (isTruthy(data.chanceModePrev)) {
=======
        if (truthy(d.close)) {
            close();
            return;
        }
        if (truthy(d.back)) {
            CobbleConfigPage.open(entityRef, store, playerRef, configManager);
            return;
        }

        merge(d);
        status = "";

        if (truthy(d.setModePercent)) {
            inputMode = InputMode.PERCENT;
            rebuild();
            return;
        }
        if (truthy(d.setModePoints)) {
            inputMode = InputMode.POINTS;
            rebuild();
            return;
        }
        if (truthy(d.setModeWeight)) {
            inputMode = InputMode.WEIGHT;
            rebuild();
            return;
        }
        if (truthy(d.autoNormalize)) {
            normalizeCurveItems(model.items);
            status = "Auto Normalize done.";
            rebuild();
            return;
        }
        if (truthy(d.presetPrev)) {
            presetIndex = (presetIndex - 1 + PRESETS.length) % PRESETS.length;
            rebuild();
            return;
        }
        if (truthy(d.presetNext)) {
            presetIndex = (presetIndex + 1) % PRESETS.length;
            rebuild();
            return;
        }
        if (truthy(d.applyPreset)) {
            applyPreset(PRESETS[presetIndex]);
            status = "Preset applied: " + PRESETS[presetIndex];
            rebuild();
            return;
        }

        if (truthy(d.chanceModePrev)) {
>>>>>>> cd2cc2b (Prepare clean project state)
            chanceModeSelection = shiftMode(chanceModeSelection, -1);
            rebuild();
            return;
        }
<<<<<<< HEAD
        if (isTruthy(data.chanceModeNext)) {
=======
        if (truthy(d.chanceModeNext)) {
>>>>>>> cd2cc2b (Prepare clean project state)
            chanceModeSelection = shiftMode(chanceModeSelection, 1);
            rebuild();
            return;
        }
<<<<<<< HEAD
        if (isTruthy(data.costModePrev)) {
=======
        if (truthy(d.chanceModeReset)) {
            chanceModeSelection = "POWER";
            rebuild();
            return;
        }
        if (truthy(d.costModePrev)) {
>>>>>>> cd2cc2b (Prepare clean project state)
            costModeSelection = shiftMode(costModeSelection, -1);
            rebuild();
            return;
        }
<<<<<<< HEAD
        if (isTruthy(data.costModeNext)) {
=======
        if (truthy(d.costModeNext)) {
>>>>>>> cd2cc2b (Prepare clean project state)
            costModeSelection = shiftMode(costModeSelection, 1);
            rebuild();
            return;
        }
<<<<<<< HEAD

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
=======
        if (truthy(d.costModeReset)) {
            costModeSelection = "POWER";
            rebuild();
            return;
        }

        if (truthy(d.scrollUp)) {
            scrollOffset = Math.max(0, scrollOffset - 1);
            rebuild();
            return;
        }
        if (truthy(d.scrollDown)) {
            scrollOffset = Math.min(maxOffset(), scrollOffset + 1);
            rebuild();
            return;
        }

        if (truthy(d.showExample)) {
            if (!validateTotals(player, false)) {
                rebuild();
                return;
            }
            CobbleCurvePreviewPage.open(entityRef, store, playerRef, configManager, normalizedCopy(), false);
            return;
        }

        if (truthy(d.saveModel)) {
            if (!validateTotals(player, true)) {
                rebuild();
                return;
            }
            ConfigManager.CurveModelConfig out = normalizedCopy();
            configManager.saveCurveModel(out);
            configManager.applyCurveModelToAllTiers();
            model = copy(out);
            player.sendMessage(Message.raw("[CobblestoneZufall] Saved + applied to levels 1-" + configManager.getMaxTier() + "."));
            status = "Saved and applied.";
>>>>>>> cd2cc2b (Prepare clean project state)
            rebuild();
        }
    }

<<<<<<< HEAD
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
=======
    private void bindEvents(UIEventBuilder e) {
        e.addEventBinding(CustomUIEventBindingType.Activating, "#SaveButton", payload(EventData.of("SaveModel", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ShowExampleButton", payload(EventData.of("ShowExample", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ModePercentButton", payload(EventData.of("SetModePercent", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ModePointsButton", payload(EventData.of("SetModePoints", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ModeWeightButton", payload(EventData.of("SetModeWeight", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#AutoNormalizeButton", payload(EventData.of("AutoNormalize", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#PresetPrevButton", payload(EventData.of("PresetPrev", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#PresetNextButton", payload(EventData.of("PresetNext", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ApplyPresetButton", payload(EventData.of("ApplyPreset", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ChanceModePrevButton", EventData.of("ChanceModePrev", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ChanceModeNextButton", EventData.of("ChanceModeNext", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#CostModePrevButton", EventData.of("CostModePrev", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#CostModeNextButton", EventData.of("CostModeNext", "1"));
        bindMiddleClick(e, "#ChanceModePrevButton", EventData.of("ChanceModeReset", "1"));
        bindMiddleClick(e, "#ChanceModeNextButton", EventData.of("ChanceModeReset", "1"));
        bindMiddleClick(e, "#CostModePrevButton", EventData.of("CostModeReset", "1"));
        bindMiddleClick(e, "#CostModeNextButton", EventData.of("CostModeReset", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#RowsUpButton", payload(EventData.of("ScrollUp", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#RowsDownButton", payload(EventData.of("ScrollDown", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#BackButton", EventData.of("Back", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton", EventData.of("Close", "1"));
        bindWheel(e, "#RowsContainer", payload(EventData.of("ScrollUp", "1")), payload(EventData.of("ScrollDown", "1")));
        bindWheel(e, "#Content", payload(EventData.of("ScrollUp", "1")), payload(EventData.of("ScrollDown", "1")));
    }

    private void merge(Data d) {
        normalize();
        model.maxTier = Math.max(1, parseI(d.maxTierInput, model.maxTier));
        model.maxUpgradeCost = Math.max(0.0d, parseD(d.maxCostInput, model.maxUpgradeCost));
        model.chanceMode = normalizeMode(chanceModeSelection);
        model.chanceExponent = Math.max(0.001d, parseD(d.chanceExpInput, model.chanceExponent));
        model.costMode = normalizeMode(costModeSelection);
        model.costExponent = Math.max(0.001d, parseD(d.costExpInput, model.costExponent));

        String[] s = new String[]{d.item0Start, d.item1Start, d.item2Start, d.item3Start, d.item4Start, d.item5Start, d.item6Start, d.item7Start, d.item8Start};
        String[] t = new String[]{d.item0End, d.item1End, d.item2End, d.item3End, d.item4End, d.item5End, d.item6End, d.item7End, d.item8End};
        for (int i = 0; i < PAGE; i++) {
            int idx = scrollOffset + i;
            if (idx >= model.items.size()) continue;
            ConfigManager.CurveItemConfig item = model.items.get(idx);
            item.startChance = parseByMode(s[i], item.startChance);
            item.endChance = parseByMode(t[i], item.endChance);
        }
    }

    private double parseByMode(String raw, double fallbackPercent) {
        double parsed = Math.max(0.0d, parseD(raw, displayNum(fallbackPercent)));
        return inputMode == InputMode.POINTS ? parsed / POINTS_PER_PERCENT : parsed;
    }

    private double displayNum(double percent) {
        return inputMode == InputMode.POINTS ? percent * POINTS_PER_PERCENT : percent;
    }

    private String display(double percent) {
        return inputMode == InputMode.POINTS ? String.valueOf(Math.round(percent * POINTS_PER_PERCENT)) : fmt(percent);
    }

    private void updateTotalsGraph(UICommandBuilder c) {
        double start = total(model, true);
        double end = total(model, false);
        boolean valid = validSilent();
        c.set("#StartTotalInfoGood.Visible", valid);
        c.set("#StartTotalInfoBad.Visible", !valid);
        c.set("#EndTotalInfoGood.Visible", valid);
        c.set("#EndTotalInfoBad.Visible", !valid);
        if (inputMode == InputMode.PERCENT) {
            c.set("#StartTotalInfoGood.Text", "Start total: " + fmt(start) + "% (target 100%)");
            c.set("#StartTotalInfoBad.Text", "Start total: " + fmt(start) + "% (must be 100%)");
            c.set("#EndTotalInfoGood.Text", "End total: " + fmt(end) + "% (target 100%)");
            c.set("#EndTotalInfoBad.Text", "End total: " + fmt(end) + "% (must be 100%)");
        } else if (inputMode == InputMode.POINTS) {
            long s = Math.round(start * POINTS_PER_PERCENT);
            long e = Math.round(end * POINTS_PER_PERCENT);
            c.set("#StartTotalInfoGood.Text", "Start total: " + fmti(s) + " pts (target 1,000,000)");
            c.set("#StartTotalInfoBad.Text", "Start total: " + fmti(s) + " pts (must be 1,000,000)");
            c.set("#EndTotalInfoGood.Text", "End total: " + fmti(e) + " pts (target 1,000,000)");
            c.set("#EndTotalInfoBad.Text", "End total: " + fmti(e) + " pts (must be 1,000,000)");
        } else {
            c.set("#StartTotalInfoGood.Text", "Start weight sum: " + fmt(start) + " (free)");
            c.set("#StartTotalInfoBad.Text", "Start weight sum must be > 0");
            c.set("#EndTotalInfoGood.Text", "End weight sum: " + fmt(end) + " (free)");
            c.set("#EndTotalInfoBad.Text", "End weight sum must be > 0");
        }
        c.set("#SaveButton.Text", valid ? "SAVE" : "SAVE (TOTAL INVALID)");
        c.set("#StatusInfo.Text", status == null ? "" : status);
        List<ConfigManager.CurveItemConfig> sList = new ArrayList<>(model.items);
        sList.sort((a, b) -> Double.compare(b.startChance, a.startChance));
        List<ConfigManager.CurveItemConfig> eList = new ArrayList<>(model.items);
        eList.sort((a, b) -> Double.compare(b.endChance, a.endChance));
        c.set("#GraphStart1.Text", graph("S1", sList, 0, true));
        c.set("#GraphStart2.Text", graph("S2", sList, 1, true));
        c.set("#GraphEnd1.Text", graph("E1", eList, 0, false));
        c.set("#GraphEnd2.Text", graph("E2", eList, 1, false));
    }

    private String graph(String label, List<ConfigManager.CurveItemConfig> list, int idx, boolean start) {
        if (idx >= list.size()) return label + ": -";
        ConfigManager.CurveItemConfig i = list.get(idx);
        double v = start ? i.startChance : i.endChance;
        int bars = Math.max(0, Math.min(20, (int) Math.round(v / 5.0d)));
        return label + " " + i.itemId + " [" + "#".repeat(bars) + "-".repeat(20 - bars) + "] " + fmt(v) + "%";
    }

    private String modeDescription() {
        if (inputMode == InputMode.PERCENT) return "Percent Strict (must be 100%)";
        if (inputMode == InputMode.POINTS) return "Points Strict (must be 1,000,000)";
        return "Weight Flexible (auto-normalized on save)";
    }

    private boolean validateTotals(Player p, boolean msg) {
        boolean ok = validSilent();
        if (!ok && msg) {
            if (inputMode == InputMode.PERCENT) p.sendMessage(Message.raw("[CobblestoneZufall] Start/End totals must be exactly 100%."));
            else if (inputMode == InputMode.POINTS) p.sendMessage(Message.raw("[CobblestoneZufall] Start/End totals must be exactly 1,000,000 points."));
            else p.sendMessage(Message.raw("[CobblestoneZufall] Weight sums must be > 0."));
            status = "Save blocked: invalid totals for current mode.";
        }
        return ok;
    }

    private boolean validSilent() {
        double s = total(model, true), e = total(model, false);
        if (inputMode == InputMode.PERCENT) return Math.abs(s - 100.0d) <= EPS && Math.abs(e - 100.0d) <= EPS;
        if (inputMode == InputMode.POINTS) return Math.round(s * POINTS_PER_PERCENT) == POINTS && Math.round(e * POINTS_PER_PERCENT) == POINTS;
        return s > EPS && e > EPS;
    }

    private void applyPreset(String name) {
        if (model.items == null || model.items.isEmpty()) return;
        if ("BALANCED".equals(name)) {
            for (ConfigManager.CurveItemConfig i : model.items) {
                double[] t = balancedTemplateById.get(i.itemId);
                if (t != null) {
                    i.startChance = t[0];
                    i.endChance = t[1];
                }
            }
            normalizeCurveItems(model.items);
            return;
        }
        double[] s = new double[model.items.size()], e = new double[model.items.size()];
        for (int i = 0; i < model.items.size(); i++) {
            ConfigManager.CurveItemConfig it = model.items.get(i);
            double[] t = balancedTemplateById.get(it.itemId);
            double bs = t == null ? Math.max(0.0001d, it.startChance) : Math.max(0.0001d, t[0]);
            double be = t == null ? Math.max(0.0001d, it.endChance) : Math.max(0.0001d, t[1]);
            s[i] = transformPreset(name, bs);
            e[i] = transformPreset(name, be);
        }
        normalize100(s);
        normalize100(e);
        for (int i = 0; i < model.items.size(); i++) {
            model.items.get(i).startChance = s[i];
            model.items.get(i).endChance = e[i];
        }
    }

    private static double transformPreset(String n, double v) {
        if ("COMMON".equals(n)) return Math.pow(v, 1.35d);
        if ("RARE".equals(n)) return Math.pow(v, 0.75d);
        if ("LEGENDARY".equals(n)) return 1.0d / Math.pow(Math.max(0.0001d, v), 0.55d);
        return v;
    }

    private void captureBalancedTemplate() {
        balancedTemplateById.clear();
        for (ConfigManager.CurveItemConfig i : model.items) {
            if (i == null || i.itemId == null || i.itemId.isBlank()) continue;
            balancedTemplateById.put(i.itemId, new double[]{Math.max(0.0d, i.startChance), Math.max(0.0d, i.endChance)});
        }
    }

    private ConfigManager.CurveModelConfig normalizedCopy() {
        ConfigManager.CurveModelConfig c = copy(model);
        normalizeCurveItems(c.items);
        return c;
    }

    private static void normalizeCurveItems(List<ConfigManager.CurveItemConfig> items) {
        if (items == null || items.isEmpty()) return;
        double[] s = new double[items.size()], e = new double[items.size()];
        for (int i = 0; i < items.size(); i++) {
            s[i] = Math.max(0.0d, items.get(i).startChance);
            e[i] = Math.max(0.0d, items.get(i).endChance);
        }
        normalize100(s);
        normalize100(e);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).startChance = s[i];
            items.get(i).endChance = e[i];
        }
    }

    private static void normalize100(double[] v) {
        double sum = 0.0d;
        for (double x : v) sum += Math.max(0.0d, x);
        if (sum <= 0.0d) {
            if (v.length > 0) {
                v[0] = 100.0d;
                for (int i = 1; i < v.length; i++) v[i] = 0.0d;
            }
            return;
        }
        for (int i = 0; i < v.length; i++) v[i] = Math.max(0.0d, v[i]) * 100.0d / sum;
    }

    private void normalize() {
        if (model == null) model = copy(configManager.getCurveModelCopy());
        if (model.items == null || model.items.isEmpty()) model = copy(configManager.getCurveModelCopy());
        chanceModeSelection = normalizeMode(chanceModeSelection == null ? model.chanceMode : chanceModeSelection);
        costModeSelection = normalizeMode(costModeSelection == null ? model.costMode : costModeSelection);
        model.chanceMode = chanceModeSelection;
        model.costMode = costModeSelection;
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxOffset()));
    }

    private int maxOffset() {
        return Math.max(0, model.items.size() - visibleRowsPerPage);
    }

    private static ConfigManager.CurveModelConfig copy(ConfigManager.CurveModelConfig src) {
        ConfigManager.CurveModelConfig c = new ConfigManager.CurveModelConfig();
        c.maxTier = src.maxTier;
        c.maxUpgradeCost = src.maxUpgradeCost;
        c.chanceMode = src.chanceMode;
        c.chanceExponent = src.chanceExponent;
        c.costMode = src.costMode;
        c.costExponent = src.costExponent;
        c.items = new ArrayList<>();
        if (src.items != null) {
            for (ConfigManager.CurveItemConfig i : src.items) {
                if (i != null) c.items.add(new ConfigManager.CurveItemConfig(i.itemId, i.startChance, i.endChance));
            }
        }
        return c;
    }

    private static EventData payload(EventData e) {
        return e.append("@MaxTierInput", "#MaxTierInput.Value")
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
    }

    private static void bindMiddleClick(UIEventBuilder e, String selector, EventData payload) {
        String[] candidates = new String[]{"MiddleClicking", "MiddleClick", "MiddleMouseClicking"};
        for (String c : candidates) {
            try {
                e.addEventBinding(CustomUIEventBindingType.valueOf(c), selector, payload);
                return;
            } catch (Exception ignored) {
            }
        }
    }

    private static void bindWheel(UIEventBuilder e, String selector, EventData up, EventData down) {
        String[] upEvents = new String[]{"MouseWheelUp", "WheelUp", "ScrollUp", "MouseScrollingUp"};
        String[] downEvents = new String[]{"MouseWheelDown", "WheelDown", "ScrollDown", "MouseScrollingDown"};
        for (String n : upEvents) {
            try {
                e.addEventBinding(CustomUIEventBindingType.valueOf(n), selector, up);
            } catch (Exception ignored) {
            }
        }
        for (String n : downEvents) {
            try {
                e.addEventBinding(CustomUIEventBindingType.valueOf(n), selector, down);
            } catch (Exception ignored) {
            }
        }
    }

    private static double total(ConfigManager.CurveModelConfig model, boolean start) {
        double t = 0.0d;
        for (ConfigManager.CurveItemConfig i : model.items) {
            if (i == null) continue;
            t += Math.max(0.0d, start ? i.startChance : i.endChance);
        }
        return t;
    }

    private static String shiftMode(String current, int step) {
        String n = normalizeMode(current);
        int idx = 0;
        for (int i = 0; i < MODES.length; i++) if (MODES[i].equals(n)) idx = i;
        int next = (idx + step) % MODES.length;
        if (next < 0) next += MODES.length;
>>>>>>> cd2cc2b (Prepare clean project state)
        return MODES[next];
    }

    private static String normalizeMode(String input) {
<<<<<<< HEAD
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
=======
        if (input == null || input.isBlank()) return "POWER";
        String u = input.trim().toUpperCase(Locale.ROOT);
        if ("CURVE".equals(u) || "NON_LINEAR".equals(u) || "NONLINEAR".equals(u)) return "POWER";
        for (String m : MODES) if (m.equals(u)) return m;
        return "POWER";
    }

    private static boolean truthy(String v) {
        return v != null && !v.isBlank() && !"0".equals(v.trim());
    }

    private static int parseI(String raw, int fallback) {
>>>>>>> cd2cc2b (Prepare clean project state)
        try {
            return Integer.parseInt(raw == null ? "" : raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

<<<<<<< HEAD
    private static double parseDouble(String raw, double fallback) {
        try {
            return Double.parseDouble((raw == null ? "" : raw.trim()).replace(',', '.'));
=======
    private static double parseD(String raw, double fallback) {
        try {
            return Double.parseDouble((raw == null ? "" : raw.trim()).replace(',', '.').replace("_", "").replace(" ", ""));
>>>>>>> cd2cc2b (Prepare clean project state)
        } catch (Exception ignored) {
            return fallback;
        }
    }

<<<<<<< HEAD
    private static boolean isTruthy(String value) {
        return value != null && !value.isBlank() && !"0".equals(value.trim());
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.6f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
=======
    private static String fmt(double value) {
        return String.format(Locale.ROOT, "%.6f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static String fmti(long value) {
        return String.format(Locale.ROOT, "%,d", value);
    }
>>>>>>> cd2cc2b (Prepare clean project state)
}
