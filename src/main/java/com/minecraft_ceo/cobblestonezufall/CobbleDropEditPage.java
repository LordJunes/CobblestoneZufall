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

import java.util.Locale;

public class CobbleDropEditPage extends InteractiveCustomUIPage<CobbleDropEditPage.Data> {

    public static final class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("Save", Codec.STRING), (d, v) -> d.save = v, d -> d.save).add()
                .append(new KeyedCodec<>("Back", Codec.STRING), (d, v) -> d.back = v, d -> d.back).add()
                .append(new KeyedCodec<>("Close", Codec.STRING), (d, v) -> d.close = v, d -> d.close).add()
                .append(new KeyedCodec<>("@DropAmountInput", Codec.STRING), (d, v) -> d.dropAmountInput = v, d -> d.dropAmountInput).add()
                .append(new KeyedCodec<>("@PayAmountInput", Codec.STRING), (d, v) -> d.payAmountInput = v, d -> d.payAmountInput).add()
                .append(new KeyedCodec<>("@RepairChanceInput", Codec.STRING), (d, v) -> d.repairChanceInput = v, d -> d.repairChanceInput).add()
                .append(new KeyedCodec<>("@RepairAmountInput", Codec.STRING), (d, v) -> d.repairAmountInput = v, d -> d.repairAmountInput).add()
                .build();

        public String save;
        public String back;
        public String close;
        public String dropAmountInput;
        public String payAmountInput;
        public String repairChanceInput;
        public String repairAmountInput;
    }

    private final PlayerRef playerRef;
    private final ConfigManager configManager;
    private final int tier;
    private final String itemId;

    public CobbleDropEditPage(PlayerRef playerRef, ConfigManager configManager, int tier, String itemId) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
        this.playerRef = playerRef;
        this.configManager = configManager;
        this.tier = tier;
        this.itemId = itemId;
    }

    public static void open(Ref<EntityStore> entityRef,
                            Store<EntityStore> store,
                            PlayerRef playerRef,
                            ConfigManager configManager,
                            int tier,
                            String itemId) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            return;
        }
        player.getPageManager().openCustomPage(entityRef, store, new CobbleDropEditPage(playerRef, configManager, tier, itemId));
    }

    @Override
    public void build(Ref<EntityStore> entityRef,
                      UICommandBuilder commandBuilder,
                      UIEventBuilder eventBuilder,
                      Store<EntityStore> store) {
        commandBuilder.append("Pages/CobbleDropEdit.ui");

        ConfigManager.DropEntry entry = configManager.getDropForTierById(tier, itemId);
        if (entry == null) {
            commandBuilder.set("#PageTitle.TextSpans", Message.raw("Drop Editor"));
            commandBuilder.set("#ItemLabel.Text", "Drop not found: " + itemId);
            commandBuilder.set("#DropAmountInput.Value", "1");
            commandBuilder.set("#PayAmountInput.Value", "$0.00");
            commandBuilder.set("#RepairChanceInput.Value", "0%");
            commandBuilder.set("#RepairAmountInput.Value", "0%");
        } else {
            commandBuilder.set("#PageTitle.TextSpans", Message.raw("Drop Editor"));
            commandBuilder.set("#ItemLabel.Text", itemId + " (Tier " + tier + ")");
            commandBuilder.set("#DropAmountInput.Value", String.valueOf(Math.max(1, entry.amount)));
            commandBuilder.set("#PayAmountInput.Value", "$" + format(entry.payAmount));
            commandBuilder.set("#RepairChanceInput.Value", format(entry.repairChance) + "%");
            commandBuilder.set("#RepairAmountInput.Value", format(entry.repairAmountPercent) + "%");
        }

        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating,
                "#SaveButton",
                EventData.of("Save", "1")
                        .append("@DropAmountInput", "#DropAmountInput.Value")
                        .append("@PayAmountInput", "#PayAmountInput.Value")
                        .append("@RepairChanceInput", "#RepairChanceInput.Value")
                        .append("@RepairAmountInput", "#RepairAmountInput.Value"));

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

        if (isTruthy(data.close) || isTruthy(data.back)) {
            CobbleConfigPage.open(entityRef, store, playerRef, configManager, tier);
            return;
        }

        if (!isTruthy(data.save)) {
            return;
        }

        int dropAmount = parseIntOrDefault(data.dropAmountInput, -1);
        double payAmount = parseDoubleOrDefault(data.payAmountInput, -1.0d);
        double repairChance = parseDoubleOrDefault(data.repairChanceInput, -1.0d);
        double repairAmountPercent = parseDoubleOrDefault(data.repairAmountInput, -1.0d);

        if (dropAmount < 1) {
            player.sendMessage(Message.raw("[CobblestoneZufall] Drop Amount must be >= 1."));
            return;
        }
        if (payAmount < 0.0d) {
            player.sendMessage(Message.raw("[CobblestoneZufall] Pay Amount must be >= 0."));
            return;
        }
        if (repairChance < 0.0d || repairChance > 100.0d) {
            player.sendMessage(Message.raw("[CobblestoneZufall] Repair Chance must be between 0 and 100."));
            return;
        }
        if (repairAmountPercent < 0.0d) {
            player.sendMessage(Message.raw("[CobblestoneZufall] Repair Amount % must be >= 0."));
            return;
        }

        configManager.updateDropDetails(tier, itemId, dropAmount, payAmount, repairChance, repairAmountPercent);
        player.sendMessage(Message.raw("[CobblestoneZufall] Saved " + itemId
                + " | Drop=" + dropAmount
                + " | Pay=" + format(payAmount)
                + " | RepairChance=" + format(repairChance)
                + "% | RepairAmount=" + format(repairAmountPercent) + "%"));
        CobbleConfigPage.open(entityRef, store, playerRef, configManager, tier);
    }

    private static boolean isTruthy(String value) {
        return value != null && !value.isBlank() && !"0".equals(value.trim());
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
            String sanitized = raw.trim()
                    .replace("$", "")
                    .replace("%", "")
                    .replace(" ", "")
                    .replace(",", ".");
            return Double.parseDouble(sanitized);
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static String format(double value) {
        if (value >= 1.0d) {
            return String.format(Locale.US, "%.4f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return String.format(Locale.US, "%.8f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}
