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
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CobUpgradePage extends InteractiveCustomUIPage<CobUpgradePage.Data> {
<<<<<<< HEAD
    private static final int MAX_ROWS = 12;
=======
    private static final int UI_MAX_ROWS = 12;
>>>>>>> cd2cc2b (Prepare clean project state)
    private static final long UPGRADE_DENIED_MESSAGE_MS = 2_000L;
    private static final long UPGRADE_BUY_UNLOCK_MS = 3_000L;
    private static final long PROMPT_REFRESH_MS = 100L;

    public static final class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("Upgrade", Codec.STRING), (d, value) -> d.upgrade = value, d -> d.upgrade).add()
                .append(new KeyedCodec<>("CycleProjection", Codec.STRING), (d, value) -> d.cycleProjection = value, d -> d.cycleProjection).add()
                .append(new KeyedCodec<>("PrevProjection", Codec.STRING), (d, value) -> d.prevProjection = value, d -> d.prevProjection).add()
                .append(new KeyedCodec<>("PromptBuy", Codec.STRING), (d, value) -> d.promptBuy = value, d -> d.promptBuy).add()
                .append(new KeyedCodec<>("PromptCancel", Codec.STRING), (d, value) -> d.promptCancel = value, d -> d.promptCancel).add()
                .append(new KeyedCodec<>("ConfirmBuy", Codec.STRING), (d, value) -> d.confirmBuy = value, d -> d.confirmBuy).add()
                .append(new KeyedCodec<>("ConfirmCancel", Codec.STRING), (d, value) -> d.confirmCancel = value, d -> d.confirmCancel).add()
                .append(new KeyedCodec<>("Close", Codec.STRING), (d, value) -> d.close = value, d -> d.close).add()
                .build();

        public String upgrade;
        public String cycleProjection;
        public String prevProjection;
        public String promptBuy;
        public String promptCancel;
        public String confirmBuy;
        public String confirmCancel;
        public String close;
    }

    private enum ConfirmState {
        NONE,
        PROMPT,
        FINAL
    }

    private final UUID playerUuid;
    private final ConfigManager configManager;
    private final EconomyBridge economyBridge;
    private int projectionOffset = 0;
    private ConfirmState confirmState = ConfirmState.NONE;
    private long buyUnlockAtMs = 0L;
    private long promptRebuildToken = 0L;
    private volatile boolean dismissed = false;
    private volatile World countdownWorld = null;
    private long deniedMessageUntilMs = 0L;
    private String deniedMessage = "";
    private Double lockedAverageBps = null;
<<<<<<< HEAD
=======
    private final int visibleRows;
>>>>>>> cd2cc2b (Prepare clean project state)

    public CobUpgradePage(PlayerRef playerRef, ConfigManager configManager, EconomyBridge economyBridge) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
        this.playerUuid = playerRef.getUuid();
        this.configManager = configManager;
        this.economyBridge = economyBridge;
<<<<<<< HEAD
=======
        this.visibleRows = Math.max(1, Math.min(UI_MAX_ROWS, configManager.getUpgradeRowsVisible()));
>>>>>>> cd2cc2b (Prepare clean project state)
    }

    public static void open(Ref<EntityStore> entityRef,
                            Store<EntityStore> store,
                            PlayerRef playerRef,
                            ConfigManager configManager,
                            EconomyBridge economyBridge) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            return;
        }
        player.getPageManager().openCustomPage(entityRef, store, new CobUpgradePage(playerRef, configManager, economyBridge));
    }

    @Override
    public void build(Ref<EntityStore> entityRef,
                      UICommandBuilder commandBuilder,
                      UIEventBuilder eventBuilder,
                      Store<EntityStore> store) {
        EntityStore entityStore = store.getExternalData();
        if (entityStore != null && entityStore.getWorld() != null) {
            countdownWorld = entityStore.getWorld();
        }

        long now = System.currentTimeMillis();
        if (deniedMessageUntilMs > 0L && now >= deniedMessageUntilMs) {
            deniedMessageUntilMs = 0L;
            deniedMessage = "";
        }

        int tier = configManager.getPlayerTier(playerUuid);
        int maxTier = configManager.getMaxTier();
<<<<<<< HEAD
        int nextTier = Math.min(maxTier, tier + 1);
=======
        int upgradeTargetTier = Math.min(maxTier, tier + 1);
>>>>>>> cd2cc2b (Prepare clean project state)

        projectionOffset = clampProjectionOffset(projectionOffset, tier, maxTier);
        int projectionTier = tier + projectionOffset;

        List<ConfigManager.DropEntry> currentDrops = configManager.getDropsForTier(tier);
<<<<<<< HEAD
        List<ConfigManager.DropEntry> nextDrops = configManager.getDropsForTier(nextTier);
=======
        List<ConfigManager.DropEntry> viewedDrops = configManager.getDropsForTier(projectionTier);
>>>>>>> cd2cc2b (Prepare clean project state)
        List<ConfigManager.DropEntry> projectionDrops = configManager.getDropsForTier(projectionTier);

        commandBuilder.append("Pages/CobUpgrade.ui");
        commandBuilder.set("#PageTitle.TextSpans", Message.raw("Cobblestone Upgrade Menu"));
        commandBuilder.set("#TierLabel.Text", "Your Tier: " + tier + " / " + maxTier);
<<<<<<< HEAD
        commandBuilder.set("#TierCompare.Text", "Current LV " + tier + "  ->  Next LV " + nextTier);
        commandBuilder.set("#RowsHeader.Text", "Resource breakdown");
        commandBuilder.set("#ColChance.Text", "Current Chance (%)");
        commandBuilder.set("#ColNext.Text", "Next Level Chance (%)");
=======
        commandBuilder.set("#TierCompare.Text", "Current LV " + tier + "  ->  Viewed LV " + projectionTier);
        commandBuilder.set("#RowsHeader.Text", "Resource breakdown");
        commandBuilder.set("#ColChance.Text", "Current Chance (%)");
        commandBuilder.set("#ColNext.Text", "Viewed Tier Chance (%)");
>>>>>>> cd2cc2b (Prepare clean project state)
        commandBuilder.set("#ColMin.Text", "Blocks per Minute");
        commandBuilder.set("#ColHour.Text", "Blocks per Hour");
        commandBuilder.set("#ColDay.Text", "Blocks per Day");
        if (lockedAverageBps == null) {
            lockedAverageBps = MiningRateTracker.getInstance().getAverageBlocksPerSecond(playerUuid);
        }
        double averageBpsLastMinute = Math.max(0.0d, lockedAverageBps);
        commandBuilder.set("#YieldInfo.Text", "The figures for minutes/hours/day are based on the average amount you mined in the last 60 seconds ["
                + formatBps(averageBpsLastMinute) + " blocks per second]");

<<<<<<< HEAD
        List<RowData> rows = buildRows(currentDrops, nextDrops, projectionDrops, averageBpsLastMinute);
        int visible = Math.min(MAX_ROWS, rows.size());
        for (int i = 0; i < MAX_ROWS; i++) {
=======
        List<RowData> rows = buildRows(currentDrops, viewedDrops, projectionDrops, averageBpsLastMinute);
        int visible = Math.min(visibleRows, rows.size());
        for (int i = 0; i < UI_MAX_ROWS; i++) {
>>>>>>> cd2cc2b (Prepare clean project state)
            String row = "#Row" + i;
            if (i >= visible) {
                commandBuilder.set(row + ".Visible", false);
                continue;
            }

            RowData data = rows.get(i);
            commandBuilder.set(row + ".Visible", true);
            commandBuilder.set(row + "Icon.ItemId", data.itemId);
            commandBuilder.set(row + "Name.Text", data.displayName);
            commandBuilder.set(row + "Chance.Text", format(data.currentChance) + "%");

            commandBuilder.set(row + "NextBase.Text", format(data.nextChance) + "%");
            setSignedText(commandBuilder, row + "Next", data.deltaChance, true);

            commandBuilder.set(row + "MinBase.Text", formatAmount(data.currentPerMin));
            setSignedText(commandBuilder, row + "Min", data.deltaPerMin, false);

            commandBuilder.set(row + "HourBase.Text", formatAmount(data.currentPerHour));
            setSignedText(commandBuilder, row + "Hour", data.deltaPerHour, false);

            commandBuilder.set(row + "DayBase.Text", formatAmount(data.currentPerDay));
            setSignedText(commandBuilder, row + "Day", data.deltaPerDay, false);
        }

        PlayerRef playerRef = store.getComponent(entityRef, PlayerRef.getComponentType());
        double upgradeCost = tier >= maxTier ? 0.0d : configManager.getUpgradeCostForTier(tier);
        EconomyBridge.BalanceResult balanceResult = economyBridge.getBalance(playerRef);

        commandBuilder.set("#UpgradePrompt.Visible", confirmState == ConfirmState.PROMPT);
        commandBuilder.set("#UpgradeConfirm.Visible", confirmState == ConfirmState.FINAL);

        if (confirmState == ConfirmState.PROMPT) {
            long remainingMs = Math.max(0L, buyUnlockAtMs - now);
<<<<<<< HEAD
            commandBuilder.set("#PromptText.Text", "Upgrade LV " + tier + " -> " + nextTier + " for " + formatMoney(upgradeCost) + "?");
=======
            commandBuilder.set("#PromptText.Text", "Upgrade LV " + tier + " -> " + upgradeTargetTier + " for " + formatMoney(upgradeCost) + "?");
>>>>>>> cd2cc2b (Prepare clean project state)
            commandBuilder.set("#PromptBuyButton.Text", remainingMs > 0L
                    ? ("BUY in " + formatCountdownTenths(remainingMs))
                    : "CONFIRM BUY");
            commandBuilder.set("#PromptCancelButton.Text", "CANCEL");
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#PromptBuyButton", EventData.of("PromptBuy", "1"));
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#PromptCancelButton", EventData.of("PromptCancel", "1"));
        }

        if (confirmState == ConfirmState.FINAL) {
            double balance = balanceResult.success ? balanceResult.balance : 0.0d;
            double after = Math.max(0.0d, balance - upgradeCost);
            commandBuilder.set("#ConfirmText.Text", "Confirm purchase for " + formatMoney(upgradeCost)
                    + "? Current: " + formatMoney(balance) + " | After: " + formatMoney(after));
            commandBuilder.set("#ConfirmBuyButton.Text", "CONFIRM BUY");
            commandBuilder.set("#ConfirmCancelButton.Text", "CANCEL");
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ConfirmBuyButton", EventData.of("ConfirmBuy", "1"));
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ConfirmCancelButton", EventData.of("ConfirmCancel", "1"));
        }

        boolean atMaxTier = tier >= maxTier;
        boolean hasFunds = balanceResult.success && balanceResult.balance + 1e-9 >= upgradeCost;
        boolean showDenied = deniedMessageUntilMs > now && deniedMessage != null && !deniedMessage.isBlank();
        String upgradeText;
        if (atMaxTier) {
            upgradeText = "MAX TIER";
        } else if (showDenied) {
            upgradeText = deniedMessage;
        } else {
            upgradeText = "UPGRADE (" + formatMoney(upgradeCost) + ")";
        }

        commandBuilder.set("#UpgradeButtonEnabled.Visible", !atMaxTier && hasFunds);
        commandBuilder.set("#UpgradeButtonDisabled.Visible", atMaxTier || !hasFunds);
        commandBuilder.set("#UpgradeButtonEnabled.Text", upgradeText);
        commandBuilder.set("#UpgradeButtonDisabled.Text", upgradeText);
        if (!atMaxTier) {
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#UpgradeButtonEnabled", EventData.of("Upgrade", "1"));
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#UpgradeButtonDisabled", EventData.of("Upgrade", "1"));
        }

        commandBuilder.set("#ProjectionPrevButton.Text", "<");
        commandBuilder.set("#ProjectionButton.Text", buildProjectionButtonLabel(tier, projectionTier));
        commandBuilder.set("#ProjectionNextButton.Text", ">");
        commandBuilder.set("#CloseButton.Text", "CLOSE");

        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ProjectionButton", EventData.of("CycleProjection", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.RightClicking, "#ProjectionButton", EventData.of("PrevProjection", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ProjectionPrevButton", EventData.of("PrevProjection", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ProjectionNextButton", EventData.of("CycleProjection", "1"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton", EventData.of("Close", "1"));
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> entityRef, Store<EntityStore> store, Data data) {
        if (data == null) {
            return;
        }

        if (isTruthy(data.close)) {
            stopPromptCountdownLoop();
            close();
            return;
        }

        if (isTruthy(data.cycleProjection)) {
            int currentTier = configManager.getPlayerTier(playerUuid);
            projectionOffset = shiftProjection(projectionOffset, +1, currentTier, configManager.getMaxTier());
            rebuild();
            return;
        }

        if (isTruthy(data.prevProjection)) {
            int currentTier = configManager.getPlayerTier(playerUuid);
            projectionOffset = shiftProjection(projectionOffset, -1, currentTier, configManager.getMaxTier());
            rebuild();
            return;
        }

        if (isTruthy(data.promptCancel) || isTruthy(data.confirmCancel)) {
            clearConfirmState();
            rebuild();
            return;
        }

        if (isTruthy(data.promptBuy)) {
            if (confirmState != ConfirmState.PROMPT) {
                return;
            }
            if (System.currentTimeMillis() < buyUnlockAtMs) {
                rebuild();
                return;
            }
            stopPromptCountdownLoop();
            confirmState = ConfirmState.FINAL;
            rebuild();
            return;
        }

        if (isTruthy(data.confirmBuy)) {
            if (confirmState != ConfirmState.FINAL) {
                return;
            }
            handleUpgradePurchase(entityRef, store);
            return;
        }

        if (isTruthy(data.upgrade)) {
            handleUpgradeStart(entityRef, store);
        }
    }

    private void handleUpgradeStart(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(entityRef, PlayerRef.getComponentType());
        if (player == null || playerRef == null) {
            close();
            return;
        }

        int tier = configManager.getPlayerTier(playerUuid);
        int maxTier = configManager.getMaxTier();
        if (tier >= maxTier) {
            player.sendMessage(Message.raw("[CobblestoneZufall] Max tier reached."));
            rebuild();
            return;
        }

        double cost = configManager.getUpgradeCostForTier(tier);
        EconomyBridge.BalanceResult balance = economyBridge.getBalance(playerRef);
        if (!balance.success) {
            player.sendMessage(Message.raw("[CobblestoneZufall] Economy error: " + balance.message));
            return;
        }

        if (balance.balance + 1e-9 < cost) {
            double missing = Math.max(0.0d, cost - balance.balance);
            deniedMessage = "Need " + formatMoney(missing) + " more";
            deniedMessageUntilMs = System.currentTimeMillis() + UPGRADE_DENIED_MESSAGE_MS;
            clearConfirmState();
            rebuild();
            return;
        }

        confirmState = ConfirmState.PROMPT;
        buyUnlockAtMs = System.currentTimeMillis() + UPGRADE_BUY_UNLOCK_MS;
        deniedMessageUntilMs = 0L;
        deniedMessage = "";
        rebuild();
        startPromptCountdownLoop();
    }

    private void handleUpgradePurchase(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(entityRef, PlayerRef.getComponentType());
        if (player == null || playerRef == null) {
            close();
            return;
        }

        int tier = configManager.getPlayerTier(playerUuid);
        int maxTier = configManager.getMaxTier();
        if (tier >= maxTier) {
            clearConfirmState();
            rebuild();
            return;
        }

        double cost = configManager.getUpgradeCostForTier(tier);
        EconomyBridge.BalanceResult balance = economyBridge.getBalance(playerRef);
        if (!balance.success) {
            player.sendMessage(Message.raw("[CobblestoneZufall] Economy error: " + balance.message));
            clearConfirmState();
            rebuild();
            return;
        }
        if (balance.balance + 1e-9 < cost) {
            double missing = Math.max(0.0d, cost - balance.balance);
            deniedMessage = "Need " + formatMoney(missing) + " more";
            deniedMessageUntilMs = System.currentTimeMillis() + UPGRADE_DENIED_MESSAGE_MS;
            clearConfirmState();
            rebuild();
            return;
        }

        EconomyBridge.ChargeResult charge = economyBridge.chargePlayer(playerRef, cost);
        if (!charge.success) {
            player.sendMessage(Message.raw("[CobblestoneZufall] Upgrade failed: " + charge.message));
            clearConfirmState();
            rebuild();
            return;
        }

        int upgraded = configManager.upgradePlayerTier(playerUuid);
        player.sendMessage(Message.raw("[CobblestoneZufall] Upgrade successful: Tier " + tier + " -> " + upgraded));
        projectionOffset = 0;
        clearConfirmState();
        rebuild();
    }

    private void clearConfirmState() {
        stopPromptCountdownLoop();
        confirmState = ConfirmState.NONE;
        buyUnlockAtMs = 0L;
    }

    private void startPromptCountdownLoop() {
        long token = ++promptRebuildToken;
        schedulePromptCountdownTick(token);
    }

    private void stopPromptCountdownLoop() {
        promptRebuildToken++;
    }

    private void schedulePromptCountdownTick(long token) {
        CompletableFuture.delayedExecutor(PROMPT_REFRESH_MS, TimeUnit.MILLISECONDS).execute(() -> {
            if (token != promptRebuildToken || dismissed) {
                return;
            }
            World world = countdownWorld;
            if (world == null) {
                return;
            }
            try {
                world.execute(() -> {
                    if (token != promptRebuildToken || dismissed || confirmState != ConfirmState.PROMPT) {
                        return;
                    }
                    long remainingMs = Math.max(0L, buyUnlockAtMs - System.currentTimeMillis());
                    rebuild();
                    if (remainingMs > 0L) {
                        schedulePromptCountdownTick(token);
                    }
                });
            } catch (IllegalThreadStateException ignored) {
                stopPromptCountdownLoop();
            }
        });
    }

    private static String formatCountdownTenths(long remainingMs) {
        long tenths = Math.max(0L, (remainingMs + 99L) / 100L);
        long whole = tenths / 10L;
        long frac = tenths % 10L;
        return whole + "," + frac;
    }

    @Override
    public void onDismiss(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        dismissed = true;
        stopPromptCountdownLoop();
        lockedAverageBps = null;
        super.onDismiss(entityRef, store);
    }

    private static void setSignedText(UICommandBuilder commandBuilder, String selectorPrefix, double value, boolean percent) {
        boolean zero = isZero(value);
        boolean positive = value > 0.0d && !zero;
        boolean negative = value < 0.0d && !zero;

        commandBuilder.set(selectorPrefix + "Pos.Visible", positive);
        commandBuilder.set(selectorPrefix + "Neg.Visible", negative);
        commandBuilder.set(selectorPrefix + "Zero.Visible", zero);

        String unit = percent ? "%" : "";
        if (positive) {
            commandBuilder.set(selectorPrefix + "Pos.Text", "+" + formatAbs(value, percent) + unit);
        }
        if (negative) {
            commandBuilder.set(selectorPrefix + "Neg.Text", "-" + formatAbs(value, percent) + unit);
        }
        if (zero) {
            commandBuilder.set(selectorPrefix + "Zero.Text", "+0" + unit);
        }
    }

    private static String formatAbs(double value, boolean percent) {
        double abs = Math.abs(value);
        return percent ? format(abs) : formatAmount(abs);
    }

    private static boolean isZero(double value) {
        return Math.abs(value) < 1.0E-9;
    }

    private static boolean isTruthy(String value) {
        return value != null && !value.isBlank() && !"0".equals(value.trim());
    }

    private static String format(double value) {
        if (value >= 1.0d) {
            return String.format(Locale.ROOT, "%.2f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return String.format(Locale.ROOT, "%.8f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static String formatMoney(double value) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat df = new DecimalFormat("#,##0.0########", symbols);
        return "$ " + df.format(Math.max(0.0d, value));
    }

    private static String formatAmount(double value) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(Math.max(0.0d, value));
    }

    private static String formatBps(double value) {
        return String.format(Locale.US, "%.2f", Math.max(0.0d, value));
    }

    private static String buildProjectionButtonLabel(int currentTier, int projectionTier) {
        if (projectionTier == currentTier) {
            return "SHOW: CURRENT";
        }
        return "SHOW: TIER " + projectionTier;
    }

    private static int clampProjectionOffset(int offset, int currentTier, int maxTier) {
        int min = 1 - currentTier;
        int max = maxTier - currentTier;
        return Math.max(min, Math.min(max, offset));
    }

    private static int shiftProjection(int offset, int step, int currentTier, int maxTier) {
        return clampProjectionOffset(offset + step, currentTier, maxTier);
    }

    private List<RowData> buildRows(List<ConfigManager.DropEntry> currentDrops,
<<<<<<< HEAD
                                    List<ConfigManager.DropEntry> nextDrops,
                                    List<ConfigManager.DropEntry> projectionDrops,
                                    double blocksPerSecond) {
        Map<String, ConfigManager.DropEntry> currentMap = toDropMap(currentDrops);
        Map<String, ConfigManager.DropEntry> nextMap = toDropMap(nextDrops);
=======
                                    List<ConfigManager.DropEntry> viewedDrops,
                                    List<ConfigManager.DropEntry> projectionDrops,
                                    double blocksPerSecond) {
        Map<String, ConfigManager.DropEntry> currentMap = toDropMap(currentDrops);
        Map<String, ConfigManager.DropEntry> viewedMap = toDropMap(viewedDrops);
>>>>>>> cd2cc2b (Prepare clean project state)
        Map<String, ConfigManager.DropEntry> projectionMap = toDropMap(projectionDrops);

        Set<String> ids = new HashSet<>();
        ids.addAll(currentMap.keySet());
<<<<<<< HEAD
        ids.addAll(nextMap.keySet());
=======
        ids.addAll(viewedMap.keySet());
>>>>>>> cd2cc2b (Prepare clean project state)
        ids.addAll(projectionMap.keySet());

        List<RowData> rows = new ArrayList<>();
        double perMinuteBase = blocksPerSecond * 60.0d;
        double perHourBase = perMinuteBase * 60.0d;
        double perDayBase = perHourBase * 24.0d;

        for (String itemId : ids) {
            ConfigManager.DropEntry current = currentMap.get(itemId);
<<<<<<< HEAD
            ConfigManager.DropEntry next = nextMap.get(itemId);
            ConfigManager.DropEntry projected = projectionMap.get(itemId);

            double currentChance = current == null ? 0.0d : Math.max(0.0d, current.chance);
            double nextChance = next == null ? 0.0d : Math.max(0.0d, next.chance);
=======
            ConfigManager.DropEntry viewed = viewedMap.get(itemId);
            ConfigManager.DropEntry projected = projectionMap.get(itemId);

            double currentChance = current == null ? 0.0d : Math.max(0.0d, current.chance);
            double viewedChance = viewed == null ? 0.0d : Math.max(0.0d, viewed.chance);
>>>>>>> cd2cc2b (Prepare clean project state)
            double projectedChance = projected == null ? 0.0d : Math.max(0.0d, projected.chance);
            int currentAmount = current == null ? 1 : Math.max(1, current.amount);
            int projectedAmount = projected == null ? 1 : Math.max(1, projected.amount);

            double currentMultiplier = (currentChance / 100.0d) * currentAmount;
            double projectedMultiplier = (projectedChance / 100.0d) * projectedAmount;

            RowData row = new RowData();
            row.itemId = itemId;
            row.displayName = shortName(itemId);
            row.currentChance = currentChance;
<<<<<<< HEAD
            row.nextChance = nextChance;
            row.deltaChance = nextChance - currentChance;
=======
            row.nextChance = viewedChance;
            row.deltaChance = viewedChance - currentChance;
>>>>>>> cd2cc2b (Prepare clean project state)
            row.currentPerMin = perMinuteBase * currentMultiplier;
            row.currentPerHour = perHourBase * currentMultiplier;
            row.currentPerDay = perDayBase * currentMultiplier;
            row.projectedPerMin = perMinuteBase * projectedMultiplier;
            row.projectedPerHour = perHourBase * projectedMultiplier;
            row.projectedPerDay = perDayBase * projectedMultiplier;
            row.deltaPerMin = row.projectedPerMin - row.currentPerMin;
            row.deltaPerHour = row.projectedPerHour - row.currentPerHour;
            row.deltaPerDay = row.projectedPerDay - row.currentPerDay;
<<<<<<< HEAD
            row.sortWeight = Math.max(Math.max(currentChance, nextChance), projectedChance);
=======
            row.sortWeight = Math.max(Math.max(currentChance, viewedChance), projectedChance);
>>>>>>> cd2cc2b (Prepare clean project state)
            rows.add(row);
        }

        rows.sort(Comparator.comparingDouble((RowData r) -> r.sortWeight).reversed());
        return rows;
    }

    private static Map<String, ConfigManager.DropEntry> toDropMap(List<ConfigManager.DropEntry> drops) {
        Map<String, ConfigManager.DropEntry> map = new HashMap<>();
        if (drops == null) {
            return map;
        }
        for (ConfigManager.DropEntry drop : drops) {
            if (drop == null || drop.itemId == null || drop.itemId.isBlank()) {
                continue;
            }
            map.put(drop.itemId, drop);
        }
        return map;
    }

    private static String shortName(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return "Unknown";
        }
        if ("Rock_Stone_Cobble".equals(itemId)) {
            return "Cobble";
        }
        if (itemId.startsWith("Ore_")) {
            String[] parts = itemId.split("_");
            if (parts.length >= 2) {
                return parts[1];
            }
        }
        return itemId;
    }

    private static final class RowData {
        String itemId;
        String displayName;
        double currentChance;
        double nextChance;
        double deltaChance;
        double currentPerMin;
        double currentPerHour;
        double currentPerDay;
        double projectedPerMin;
        double projectedPerHour;
        double projectedPerDay;
        double deltaPerMin;
        double deltaPerHour;
        double deltaPerDay;
        double sortWeight;
    }
}
