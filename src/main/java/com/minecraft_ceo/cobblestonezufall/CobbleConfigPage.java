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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CobbleConfigPage extends InteractiveCustomUIPage<CobbleConfigPage.Data> {

    private static final int MAX_ROWS = 12;

    public static final class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("Close", Codec.STRING), (d, v) -> d.close = v, d -> d.close).add()
                .append(new KeyedCodec<>("OpenModel", Codec.STRING), (d, v) -> d.openModel = v, d -> d.openModel).add()
                .append(new KeyedCodec<>("EditIndex", Codec.STRING), (d, v) -> d.editIndex = v, d -> d.editIndex).add()
                .append(new KeyedCodec<>("SaveRegenDelay", Codec.STRING), (d, v) -> d.saveRegenDelay = v, d -> d.saveRegenDelay).add()
                .append(new KeyedCodec<>("SaveExpectedBps", Codec.STRING), (d, v) -> d.saveExpectedBps = v, d -> d.saveExpectedBps).add()
                .append(new KeyedCodec<>("CycleCollectRange", Codec.STRING), (d, v) -> d.cycleCollectRange = v, d -> d.cycleCollectRange).add()
                .append(new KeyedCodec<>("PrevCollectRange", Codec.STRING), (d, v) -> d.prevCollectRange = v, d -> d.prevCollectRange).add()
                .append(new KeyedCodec<>("CycleRangeType", Codec.STRING), (d, v) -> d.cycleRangeType = v, d -> d.cycleRangeType).add()
                .append(new KeyedCodec<>("PrevRangeType", Codec.STRING), (d, v) -> d.prevRangeType = v, d -> d.prevRangeType).add()
                .append(new KeyedCodec<>("@RegenDelayInput", Codec.STRING), (d, v) -> d.regenDelayInput = v, d -> d.regenDelayInput).add()
                .append(new KeyedCodec<>("@ExpectedBpsInput", Codec.STRING), (d, v) -> d.expectedBpsInput = v, d -> d.expectedBpsInput).add()
                .build();

        public String close;
        public String openModel;
        public String editIndex;
        public String saveRegenDelay;
        public String saveExpectedBps;
        public String cycleCollectRange;
        public String prevCollectRange;
        public String cycleRangeType;
        public String prevRangeType;
        public String regenDelayInput;
        public String expectedBpsInput;
    }

    private final ConfigManager configManager;
    private final PlayerRef pagePlayerRef;
    private final UUID playerUuid;
    private final List<ConfigManager.DropEntry> visibleEntries = new ArrayList<>();
    private int editingTier;

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
    public void build(Ref<EntityStore> entityRef,
                      UICommandBuilder commandBuilder,
                      UIEventBuilder eventBuilder,
                      Store<EntityStore> store) {
        commandBuilder.append("Pages/CobbleConfig.ui");

        int maxTier = configManager.getMaxTier();
        editingTier = Math.max(1, Math.min(maxTier, editingTier));

        int playerTier = configManager.getPlayerTier(playerUuid);
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
            rebuild();
            return;
        }

        if (data.editIndex != null && !data.editIndex.isBlank()) {
            try {
                int index = Integer.parseInt(data.editIndex.trim());
                if (index >= 0 && index < visibleEntries.size()) {
                    String itemId = visibleEntries.get(index).itemId;
                    CobbleDropEditPage.open(entityRef, store, pagePlayerRef, configManager, editingTier, itemId);
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private static boolean isTruthy(String value) {
        return value != null && !value.isBlank() && !"0".equals(value.trim());
    }

    private static String formatNumber(double value) {
        if (value >= 1.0d) {
            return String.format(Locale.US, "%.4f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return String.format(Locale.US, "%.8f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static String formatMoney(double value) {
        return String.format(Locale.US, "%,.2f", Math.max(0.0d, value));
    }

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
    }
}
