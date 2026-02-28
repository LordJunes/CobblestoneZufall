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
<<<<<<< HEAD
import java.util.Comparator;
=======
>>>>>>> cd2cc2b (Prepare clean project state)
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CobbleConfigPage extends InteractiveCustomUIPage<CobbleConfigPage.Data> {

<<<<<<< HEAD
    private static final int MAX_ROWS = 12;
=======
    private static final int MAX_ROWS = 9;
    private static final int DEFAULT_REGEN_DELAY_MS = 1000;
    private static final int DEFAULT_TRAVELTIME_MS = 100;
    private static final double DEFAULT_ESTIMATED_BPS = 1.0d;
>>>>>>> cd2cc2b (Prepare clean project state)

    public static final class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("Close", Codec.STRING), (d, v) -> d.close = v, d -> d.close).add()
                .append(new KeyedCodec<>("OpenModel", Codec.STRING), (d, v) -> d.openModel = v, d -> d.openModel).add()
<<<<<<< HEAD
                .append(new KeyedCodec<>("EditIndex", Codec.STRING), (d, v) -> d.editIndex = v, d -> d.editIndex).add()
                .append(new KeyedCodec<>("SaveRegenDelay", Codec.STRING), (d, v) -> d.saveRegenDelay = v, d -> d.saveRegenDelay).add()
                .append(new KeyedCodec<>("SaveExpectedBps", Codec.STRING), (d, v) -> d.saveExpectedBps = v, d -> d.saveExpectedBps).add()
                .append(new KeyedCodec<>("CycleCollectRange", Codec.STRING), (d, v) -> d.cycleCollectRange = v, d -> d.cycleCollectRange).add()
                .append(new KeyedCodec<>("PrevCollectRange", Codec.STRING), (d, v) -> d.prevCollectRange = v, d -> d.prevCollectRange).add()
                .append(new KeyedCodec<>("CycleRangeType", Codec.STRING), (d, v) -> d.cycleRangeType = v, d -> d.cycleRangeType).add()
                .append(new KeyedCodec<>("PrevRangeType", Codec.STRING), (d, v) -> d.prevRangeType = v, d -> d.prevRangeType).add()
                .append(new KeyedCodec<>("@RegenDelayInput", Codec.STRING), (d, v) -> d.regenDelayInput = v, d -> d.regenDelayInput).add()
                .append(new KeyedCodec<>("@ExpectedBpsInput", Codec.STRING), (d, v) -> d.expectedBpsInput = v, d -> d.expectedBpsInput).add()
=======
                .append(new KeyedCodec<>("OpenBlockPicker", Codec.STRING), (d, v) -> d.openBlockPicker = v, d -> d.openBlockPicker).add()
                .append(new KeyedCodec<>("EditIndex", Codec.STRING), (d, v) -> d.editIndex = v, d -> d.editIndex).add()
                .append(new KeyedCodec<>("RemoveIndex", Codec.STRING), (d, v) -> d.removeIndex = v, d -> d.removeIndex).add()
                .append(new KeyedCodec<>("ScrollUp", Codec.STRING), (d, v) -> d.scrollUp = v, d -> d.scrollUp).add()
                .append(new KeyedCodec<>("ScrollDown", Codec.STRING), (d, v) -> d.scrollDown = v, d -> d.scrollDown).add()
                .append(new KeyedCodec<>("IncRegenDelay", Codec.STRING), (d, v) -> d.incRegenDelay = v, d -> d.incRegenDelay).add()
                .append(new KeyedCodec<>("DecRegenDelay", Codec.STRING), (d, v) -> d.decRegenDelay = v, d -> d.decRegenDelay).add()
                .append(new KeyedCodec<>("ResetRegenDelay", Codec.STRING), (d, v) -> d.resetRegenDelay = v, d -> d.resetRegenDelay).add()
                .append(new KeyedCodec<>("DecEstimatedBps", Codec.STRING), (d, v) -> d.decEstimatedBps = v, d -> d.decEstimatedBps).add()
                .append(new KeyedCodec<>("IncEstimatedBps", Codec.STRING), (d, v) -> d.incEstimatedBps = v, d -> d.incEstimatedBps).add()
                .append(new KeyedCodec<>("ResetEstimatedBps", Codec.STRING), (d, v) -> d.resetEstimatedBps = v, d -> d.resetEstimatedBps).add()
                .append(new KeyedCodec<>("IncTravelTime", Codec.STRING), (d, v) -> d.incTravelTime = v, d -> d.incTravelTime).add()
                .append(new KeyedCodec<>("DecTravelTime", Codec.STRING), (d, v) -> d.decTravelTime = v, d -> d.decTravelTime).add()
                .append(new KeyedCodec<>("ResetTravelTime", Codec.STRING), (d, v) -> d.resetTravelTime = v, d -> d.resetTravelTime).add()
                .append(new KeyedCodec<>("ToggleDebug", Codec.STRING), (d, v) -> d.toggleDebug = v, d -> d.toggleDebug).add()
>>>>>>> cd2cc2b (Prepare clean project state)
                .build();

        public String close;
        public String openModel;
<<<<<<< HEAD
        public String editIndex;
        public String saveRegenDelay;
        public String saveExpectedBps;
        public String cycleCollectRange;
        public String prevCollectRange;
        public String cycleRangeType;
        public String prevRangeType;
        public String regenDelayInput;
        public String expectedBpsInput;
=======
        public String openBlockPicker;
        public String editIndex;
        public String removeIndex;
        public String scrollUp;
        public String scrollDown;
        public String incRegenDelay;
        public String decRegenDelay;
        public String resetRegenDelay;
        public String decEstimatedBps;
        public String incEstimatedBps;
        public String resetEstimatedBps;
        public String incTravelTime;
        public String decTravelTime;
        public String resetTravelTime;
        public String toggleDebug;
>>>>>>> cd2cc2b (Prepare clean project state)
    }

    private final ConfigManager configManager;
    private final PlayerRef pagePlayerRef;
    private final UUID playerUuid;
<<<<<<< HEAD
    private final List<ConfigManager.DropEntry> visibleEntries = new ArrayList<>();
    private int editingTier;
=======
    private final List<String> visibleItemIds = new ArrayList<>();
    private int editingTier;
    private int scrollOffset = 0;
    private String status = "";
>>>>>>> cd2cc2b (Prepare clean project state)

    public CobbleConfigPage(PlayerRef playerRef, ConfigManager configManager) {
        this(playerRef, configManager, configManager.getPlayerTier(playerRef.getUuid()));
    }

    public CobbleConfigPage(PlayerRef playerRef, ConfigManager configManager, int initialTier) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
        this.configManager = configManager;
        this.pagePlayerRef = playerRef;
        this.playerUuid = playerRef.getUuid();
        this.editingTier = Math.max(1, Math.min(configManager.getMaxTier(), initialTier));
    }

    public static void open(Ref<EntityStore> entityRef,
                            Store<EntityStore> store,
                            PlayerRef playerRef,
                            ConfigManager configManager) {
        open(entityRef, store, playerRef, configManager, configManager.getPlayerTier(playerRef.getUuid()));
    }

    public static void open(Ref<EntityStore> entityRef,
                            Store<EntityStore> store,
                            PlayerRef playerRef,
                            ConfigManager configManager,
                            int initialTier) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            return;
        }
        player.getPageManager().openCustomPage(entityRef, store, new CobbleConfigPage(playerRef, configManager, initialTier));
    }

    @Override
<<<<<<< HEAD
    public void build(Ref<EntityStore> entityRef,
                      UICommandBuilder commandBuilder,
                      UIEventBuilder eventBuilder,
                      Store<EntityStore> store) {
        commandBuilder.append("Pages/CobbleConfig.ui");
=======
    public void build(Ref<EntityStore> entityRef, UICommandBuilder c, UIEventBuilder e, Store<EntityStore> store) {
        c.append("Pages/CobbleConfig.ui");
>>>>>>> cd2cc2b (Prepare clean project state)

        int maxTier = configManager.getMaxTier();
        editingTier = Math.max(1, Math.min(maxTier, editingTier));

        int playerTier = configManager.getPlayerTier(playerUuid);
<<<<<<< HEAD
        commandBuilder.set("#PageTitle.TextSpans", Message.raw("Cobblestone Admin Menu"));
        commandBuilder.set("#TierInfo.Text", "Player Tier: " + playerTier + " | Edit Tier: " + editingTier + " / " + maxTier);
        commandBuilder.set("#TierMeta.Text", "Edit per-block Drop/Pay/Repair values here. Chance is managed in CURVE MODEL.");
        commandBuilder.set("#RegenDelayInput.Value", String.valueOf(configManager.getRegenDelayMs()));
        commandBuilder.set("#ExpectedBpsInput.Value", formatNumber(configManager.getExpectedBlocksPerSecond()));
        commandBuilder.set("#AutoCollectToggleButton.Text", getRangeButtonText(configManager.getAutoCollectRange()));
        commandBuilder.set("#RangeTypeButton.Text", getRangeTypeButtonText(configManager.getAutoCollectRangeTypeMs()));

        visibleEntries.clear();
        List<ConfigManager.DropEntry> drops = new ArrayList<>(configManager.getDropsForTier(editingTier));
        drops.sort(Comparator.comparingDouble((ConfigManager.DropEntry e) -> e.chance).reversed());
        visibleEntries.addAll(drops);

        for (int i = 0; i < MAX_ROWS; i++) {
            String row = "#Entry" + i;
            if (i >= visibleEntries.size()) {
                commandBuilder.set(row + ".Visible", false);
                continue;
            }
            ConfigManager.DropEntry entry = visibleEntries.get(i);
            commandBuilder.set(row + ".Visible", true);
            commandBuilder.set(row + "Icon.ItemId", entry.itemId);
            commandBuilder.set(row + "Name.Text", entry.itemId);
            commandBuilder.set(row + "Drop.Text", "x" + Math.max(1, entry.amount));
            commandBuilder.set(row + "Pay.Text", "$" + formatMoney(entry.payAmount));
            commandBuilder.set(row + "Repair.Text", formatNumber(entry.repairChance) + "% / " + formatNumber(entry.repairAmountPercent) + "%");
            commandBuilder.set(row + "Edit.Text", "EDIT");
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, row + "Edit", EventData.of("EditIndex", String.valueOf(i)));
        }

        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#OpenModelButton", EventData.of("OpenModel", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveRegenDelayButton",
                EventData.of("SaveRegenDelay", "1").append("@RegenDelayInput", "#RegenDelayInput.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveExpectedBpsButton",
                EventData.of("SaveExpectedBps", "1").append("@ExpectedBpsInput", "#ExpectedBpsInput.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#AutoCollectToggleButton", EventData.of("CycleCollectRange", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.RightClicking, "#AutoCollectToggleButton", EventData.of("PrevCollectRange", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#RangeTypeButton", EventData.of("CycleRangeType", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.RightClicking, "#RangeTypeButton", EventData.of("PrevRangeType", "1"));
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> entityRef, Store<EntityStore> store, Data data) {
        if (data == null) {
=======
        c.set("#PageTitle.TextSpans", Message.raw("Cobblestone Admin Menu"));
        c.set("#TierInfo.Text", "Player Tier: " + playerTier + " | Edit Tier: " + editingTier + " / " + maxTier);
        c.set("#TierMeta.Text", "Use Add Block to open full block list with search.");
        c.set("#BlockDelayButton.Text", getDelayButtonText(configManager.getRegenDelayMs()));
        c.set("#EstimatedBpsButton.Text", getEstimatedButtonText(configManager.getExpectedBlocksPerSecond()));
        c.set("#RangeTypeButton.Text", getTravelTimeButtonText(configManager.getAutoCollectRangeTypeMs()));
        c.set("#DebugToggleButton.Text", getDebugButtonText(configManager.isDebugEnabled()));

        List<String> managed = configManager.getManagedBlockIds();
        scrollOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, managed.size() - MAX_ROWS)));
        visibleItemIds.clear();

        int from = managed.isEmpty() ? 0 : scrollOffset + 1;
        int to = Math.min(managed.size(), scrollOffset + MAX_ROWS);
        c.set("#ListInfo.Text", "Showing " + from + "-" + to + " of " + managed.size() + " (Scroll)");

        for (int i = 0; i < MAX_ROWS; i++) {
            String row = "#Entry" + i;
            int idx = scrollOffset + i;
            if (idx >= managed.size()) {
                c.set(row + ".Visible", false);
                continue;
            }

            String itemId = managed.get(idx);
            visibleItemIds.add(itemId);

            ConfigManager.DropEntry entry = configManager.getDropForTierById(editingTier, itemId);
            if (entry == null) {
                entry = ConfigManager.getDefaultValueTemplate(itemId);
                entry.amount = 1;
            }

            c.set(row + ".Visible", true);
            c.set(row + "Remove.Text", "-");
            c.set(row + "Icon.ItemId", itemId);
            c.set(row + "Name.Text", itemId);
            c.set(row + "Drop.Text", "x" + Math.max(1, entry.amount));
            c.set(row + "Pay.Text", "$" + formatMoney(entry.payAmount));
            c.set(row + "Repair.Text", formatNumber(entry.repairChance) + "% / " + formatNumber(entry.repairAmountPercent) + "%");
            c.set(row + "Edit.Text", "EDIT");
            e.addEventBinding(CustomUIEventBindingType.Activating, row + "Edit", EventData.of("EditIndex", String.valueOf(i)));
            e.addEventBinding(CustomUIEventBindingType.Activating, row + "Remove", EventData.of("RemoveIndex", String.valueOf(i)));
        }

        c.set("#StatusInfo.Text", status == null ? "" : status);

        e.addEventBinding(CustomUIEventBindingType.Activating, "#OpenModelButton", EventData.of("OpenModel", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#OpenBlockPickerButton", EventData.of("OpenBlockPicker", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ListUpButton", EventData.of("ScrollUp", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ListDownButton", EventData.of("ScrollDown", "1"));

        e.addEventBinding(CustomUIEventBindingType.Activating, "#BlockDelayButton", EventData.of("IncRegenDelay", "1"));
        e.addEventBinding(CustomUIEventBindingType.RightClicking, "#BlockDelayButton", EventData.of("DecRegenDelay", "1"));
        addMiddleClickBinding(e, "#BlockDelayButton", EventData.of("ResetRegenDelay", "1"));

        e.addEventBinding(CustomUIEventBindingType.Activating, "#EstimatedBpsButton", EventData.of("DecEstimatedBps", "1"));
        e.addEventBinding(CustomUIEventBindingType.RightClicking, "#EstimatedBpsButton", EventData.of("IncEstimatedBps", "1"));
        addMiddleClickBinding(e, "#EstimatedBpsButton", EventData.of("ResetEstimatedBps", "1"));

        e.addEventBinding(CustomUIEventBindingType.Activating, "#RangeTypeButton", EventData.of("IncTravelTime", "1"));
        e.addEventBinding(CustomUIEventBindingType.RightClicking, "#RangeTypeButton", EventData.of("DecTravelTime", "1"));
        addMiddleClickBinding(e, "#RangeTypeButton", EventData.of("ResetTravelTime", "1"));

        e.addEventBinding(CustomUIEventBindingType.Activating, "#DebugToggleButton", EventData.of("ToggleDebug", "1"));

        bindWheel(e, "#Entries", EventData.of("ScrollUp", "1"), EventData.of("ScrollDown", "1"));
        bindWheel(e, "#Content", EventData.of("ScrollUp", "1"), EventData.of("ScrollDown", "1"));
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> entityRef, Store<EntityStore> store, Data d) {
        if (d == null) {
>>>>>>> cd2cc2b (Prepare clean project state)
            return;
        }

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
        if (isTruthy(data.openModel)) {
            CobbleCurvePage.open(entityRef, store, pagePlayerRef, configManager);
            return;
        }
        if (isTruthy(data.saveRegenDelay)) {
            int parsed = parseIntOrDefault(data.regenDelayInput, -1);
            if (parsed < 1) {
                player.sendMessage(Message.raw("[CobblestoneZufall] Block Delay must be >= 1 ms."));
                return;
            }
            configManager.setRegenDelayMs(parsed);
            player.sendMessage(Message.raw("[CobblestoneZufall] Block Delay saved: " + parsed + " ms"));
            rebuild();
            return;
        }
        if (isTruthy(data.saveExpectedBps)) {
            double parsed = parseDoubleOrDefault(data.expectedBpsInput, -1.0d);
            if (parsed < 0.0d) {
                player.sendMessage(Message.raw("[CobblestoneZufall] Expected BPS must be >= 0."));
                return;
            }
            configManager.setExpectedBlocksPerSecond(parsed);
            player.sendMessage(Message.raw("[CobblestoneZufall] Expected BPS saved: " + formatNumber(parsed)));
            rebuild();
            return;
        }
        if (isTruthy(data.cycleCollectRange)) {
            int current = configManager.getAutoCollectRange();
            int next = (current >= 32) ? 0 : (current + 1);
            configManager.setAutoCollectRange(next);
            player.sendMessage(Message.raw("[CobblestoneZufall] Collect Mode: " + getRangeButtonText(next)));
            rebuild();
            return;
        }
        if (isTruthy(data.prevCollectRange)) {
            int current = configManager.getAutoCollectRange();
            int prev = (current <= 0) ? 32 : (current - 1);
            configManager.setAutoCollectRange(prev);
            player.sendMessage(Message.raw("[CobblestoneZufall] Collect Mode: " + getRangeButtonText(prev)));
            rebuild();
            return;
        }
        if (isTruthy(data.cycleRangeType)) {
            int current = configManager.getAutoCollectRangeTypeMs();
            int next;
            if (current == -1) {
                next = 200;
            } else if (current >= 5000) {
                next = 5000;
            } else {
                next = Math.min(5000, current + 100);
            }
            configManager.setAutoCollectRangeTypeMs(next);
            player.sendMessage(Message.raw("[CobblestoneZufall] RangeTyp: " + getRangeTypeButtonText(next)));
            rebuild();
            return;
        }
        if (isTruthy(data.prevRangeType)) {
            int current = configManager.getAutoCollectRangeTypeMs();
            int prev;
            if (current == -1) {
                prev = -1;
            } else if (current <= 100) {
                prev = -1;
            } else {
                prev = Math.max(100, current - 100);
            }
            configManager.setAutoCollectRangeTypeMs(prev);
            player.sendMessage(Message.raw("[CobblestoneZufall] RangeTyp: " + getRangeTypeButtonText(prev)));
=======
        status = "";

        if (isTruthy(d.close)) {
            close();
            return;
        }
        if (isTruthy(d.openModel)) {
            CobbleCurvePage.open(entityRef, store, pagePlayerRef, configManager);
            return;
        }
        if (isTruthy(d.openBlockPicker)) {
            CobbleBlockPickerPage.open(entityRef, store, pagePlayerRef, configManager, editingTier);
            return;
        }

        if (isTruthy(d.scrollUp)) {
            scrollOffset = Math.max(0, scrollOffset - 1);
            rebuild();
            return;
        }
        if (isTruthy(d.scrollDown)) {
            int maxOffset = Math.max(0, configManager.getManagedBlockIds().size() - MAX_ROWS);
            scrollOffset = Math.min(maxOffset, scrollOffset + 1);
>>>>>>> cd2cc2b (Prepare clean project state)
            rebuild();
            return;
        }

<<<<<<< HEAD
        if (data.editIndex != null && !data.editIndex.isBlank()) {
            try {
                int index = Integer.parseInt(data.editIndex.trim());
                if (index >= 0 && index < visibleEntries.size()) {
                    String itemId = visibleEntries.get(index).itemId;
                    CobbleDropEditPage.open(entityRef, store, pagePlayerRef, configManager, editingTier, itemId);
                }
            } catch (NumberFormatException ignored) {
=======
        if (isTruthy(d.incRegenDelay)) {
            long next = Math.min(60_000L, configManager.getRegenDelayMs() + 100L);
            configManager.setRegenDelayMs(next);
            player.sendMessage(Message.raw("[CobblestoneZufall] " + getDelayButtonText(next)));
            rebuild();
            return;
        }
        if (isTruthy(d.decRegenDelay)) {
            long next = Math.max(0L, configManager.getRegenDelayMs() - 100L);
            configManager.setRegenDelayMs(next);
            player.sendMessage(Message.raw("[CobblestoneZufall] " + getDelayButtonText(next)));
            rebuild();
            return;
        }
        if (isTruthy(d.resetRegenDelay)) {
            configManager.setRegenDelayMs(DEFAULT_REGEN_DELAY_MS);
            player.sendMessage(Message.raw("[CobblestoneZufall] " + getDelayButtonText(DEFAULT_REGEN_DELAY_MS)));
            rebuild();
            return;
        }

        if (isTruthy(d.decEstimatedBps)) {
            double next = clampBps(configManager.getExpectedBlocksPerSecond() - 0.1d);
            configManager.setExpectedBlocksPerSecond(next);
            player.sendMessage(Message.raw("[CobblestoneZufall] " + getEstimatedButtonText(next)));
            rebuild();
            return;
        }
        if (isTruthy(d.incEstimatedBps)) {
            double next = clampBps(configManager.getExpectedBlocksPerSecond() + 0.1d);
            configManager.setExpectedBlocksPerSecond(next);
            player.sendMessage(Message.raw("[CobblestoneZufall] " + getEstimatedButtonText(next)));
            rebuild();
            return;
        }
        if (isTruthy(d.resetEstimatedBps)) {
            configManager.setExpectedBlocksPerSecond(DEFAULT_ESTIMATED_BPS);
            player.sendMessage(Message.raw("[CobblestoneZufall] " + getEstimatedButtonText(DEFAULT_ESTIMATED_BPS)));
            rebuild();
            return;
        }

        if (isTruthy(d.incTravelTime)) {
            int next = Math.min(5000, configManager.getAutoCollectRangeTypeMs() + 100);
            configManager.setAutoCollectRangeTypeMs(next);
            player.sendMessage(Message.raw("[CobblestoneZufall] " + getTravelTimeButtonText(next)));
            rebuild();
            return;
        }
        if (isTruthy(d.decTravelTime)) {
            int next = Math.max(0, configManager.getAutoCollectRangeTypeMs() - 100);
            configManager.setAutoCollectRangeTypeMs(next);
            player.sendMessage(Message.raw("[CobblestoneZufall] " + getTravelTimeButtonText(next)));
            rebuild();
            return;
        }
        if (isTruthy(d.resetTravelTime)) {
            configManager.setAutoCollectRangeTypeMs(DEFAULT_TRAVELTIME_MS);
            player.sendMessage(Message.raw("[CobblestoneZufall] " + getTravelTimeButtonText(DEFAULT_TRAVELTIME_MS)));
            rebuild();
            return;
        }
        if (isTruthy(d.toggleDebug)) {
            boolean next = !configManager.isDebugEnabled();
            configManager.setDebugEnabled(next);
            player.sendMessage(Message.raw("[CobblestoneZufall] " + getDebugButtonText(next)));
            rebuild();
            return;
        }

        if (isTruthy(d.removeIndex)) {
            int idx = parseIntSafe(d.removeIndex, -1);
            if (idx >= 0 && idx < visibleItemIds.size()) {
                String id = visibleItemIds.get(idx);
                boolean changed = configManager.removeManagedBlock(id);
                if (changed) {
                    status = "Removed: " + id;
                    player.sendMessage(Message.raw("[CobblestoneZufall] Removed block: " + id));
                } else {
                    status = "Cannot remove last remaining block.";
                }
                rebuild();
                return;
            }
        }

        if (d.editIndex != null && !d.editIndex.isBlank()) {
            int idx = parseIntSafe(d.editIndex, -1);
            if (idx >= 0 && idx < visibleItemIds.size()) {
                CobbleDropEditPage.open(entityRef, store, pagePlayerRef, configManager, editingTier, visibleItemIds.get(idx));
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

    private static void addMiddleClickBinding(UIEventBuilder e, String s, EventData p) {
        String[] c = new String[]{"MiddleClicking", "MiddleClick", "MiddleMouseClicking"};
        for (String n : c) {
            try {
                e.addEventBinding(CustomUIEventBindingType.valueOf(n), s, p);
                return;
            } catch (Exception ignored) {
>>>>>>> cd2cc2b (Prepare clean project state)
            }
        }
    }

    private static boolean isTruthy(String value) {
        return value != null && !value.isBlank() && !"0".equals(value.trim());
    }

<<<<<<< HEAD
=======
    private static int parseIntSafe(String raw, int fallback) {
        try {
            return Integer.parseInt(raw == null ? "" : raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static double clampBps(double value) {
        double clamped = Math.max(0.0d, Math.min(100.0d, value));
        return Math.round(clamped * 10.0d) / 10.0d;
    }

>>>>>>> cd2cc2b (Prepare clean project state)
    private static String formatNumber(double value) {
        if (value >= 1.0d) {
            return String.format(Locale.US, "%.4f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return String.format(Locale.US, "%.8f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static String formatMoney(double value) {
        return String.format(Locale.US, "%,.2f", Math.max(0.0d, value));
    }

<<<<<<< HEAD
    private static String getRangeButtonText(int range) {
        if (range <= 0) {
            return "VANILLA";
        }
        if (range == 1) {
            return "RANGE 1 BLOCK";
        }
        return "RANGE " + range + " BLOCKS";
    }

    private static String getRangeTypeButtonText(int rangeTypeMs) {
        if (rangeTypeMs <= 0) {
            return "RangeTyp: Teleport";
        }
        return "RangeTyp: " + rangeTypeMs + "ms";
    }

    private static int parseIntOrDefault(String raw, int fallback) {
        if (raw == null || raw.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static double parseDoubleOrDefault(String raw, double fallback) {
        if (raw == null || raw.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Double.parseDouble(raw.trim().replace(',', '.'));
        } catch (Exception ignored) {
            return fallback;
        }
=======
    private static String getDelayButtonText(long delayMs) {
        if (delayMs <= 0L) {
            return "Block Delay: NO DELAY";
        }
        return "Block Delay: " + delayMs + "ms";
    }

    private static String getTravelTimeButtonText(int travelTimeMs) {
        if (travelTimeMs <= 0) {
            return "BLOCK TRAVELTIME: NO DELAY";
        }
        return "BLOCK TRAVELTIME: " + travelTimeMs + "ms";
    }

    private static String getEstimatedButtonText(double bps) {
        return "Estimated Mined Blocks/Sec: " + String.format(Locale.US, "%.1f", clampBps(bps));
    }

    private static String getDebugButtonText(boolean enabled) {
        return "DEBUG: " + (enabled ? "ON" : "OFF");
>>>>>>> cd2cc2b (Prepare clean project state)
    }
}
