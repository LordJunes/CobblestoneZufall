package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MoneyCommand extends AbstractPlayerCommand {
    private final EconomyBridge economyBridge;

    public MoneyCommand(EconomyBridge economyBridge) {
        super("money", "Check balance and pay players: /money, /money pay <player> <amount>");
        this.economyBridge = economyBridge;
        setAllowsExtraArguments(true);
    }

    @Override
    protected void execute(CommandContext context,
                           Store<EntityStore> store,
                           Ref<EntityStore> playerEntityRef,
                           PlayerRef playerRef,
                           World world) {
        String[] args = normalizeArgs(context.getInputString(), "money");
        if (args.length == 0 || "balance".equalsIgnoreCase(args[0]) || "bal".equalsIgnoreCase(args[0])) {
            context.sendMessage(Message.raw("[Money] Balance: " + formatMoney(economyBridge.getBalance(playerRef.getUuid()))));
            return;
        }
        if ("pay".equalsIgnoreCase(args[0])) {
            if (args.length < 3) {
                context.sendMessage(Message.raw("[Money] Usage: /money pay <player> <amount>"));
                return;
            }
            PlayerRef target = resolvePlayer(args[1]);
            if (target == null) {
                context.sendMessage(Message.raw("[Money] Player not found or offline."));
                return;
            }
            if (target.getUuid().equals(playerRef.getUuid())) {
                context.sendMessage(Message.raw("[Money] You cannot pay yourself."));
                return;
            }
            BigDecimal amount = parseAmount(args[2]);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                context.sendMessage(Message.raw("[Money] Amount must be > 0."));
                return;
            }
            if (!economyBridge.removeBalance(playerRef.getUuid(), amount)) {
                context.sendMessage(Message.raw("[Money] Not enough money."));
                return;
            }
            economyBridge.addBalance(target.getUuid(), amount);
            context.sendMessage(Message.raw("[Money] Sent " + formatMoney(amount) + " to " + target.getUsername() + "."));
            target.sendMessage(Message.raw("[Money] You received " + formatMoney(amount) + " from " + playerRef.getUsername() + "."));
            return;
        }
        context.sendMessage(Message.raw("[Money] Usage: /money, /money balance, /money pay <player> <amount>"));
    }

    static PlayerRef resolvePlayer(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        PlayerRef exact = Universe.get().getPlayerByUsername(token, NameMatching.EXACT_IGNORE_CASE);
        if (exact != null) {
            return exact;
        }
        return Universe.get().getPlayerByUsername(token, NameMatching.STARTS_WITH_IGNORE_CASE);
    }

    static BigDecimal parseAmount(String raw) {
        if (raw == null || raw.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(raw.replace(",", "")).max(BigDecimal.ZERO);
        } catch (Exception ignored) {
            return BigDecimal.ZERO;
        }
    }

    static String formatMoney(BigDecimal amount) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat df = new DecimalFormat("#,##0.##", symbols);
        return "$ " + df.format(amount == null ? BigDecimal.ZERO : amount.max(BigDecimal.ZERO));
    }

    static String[] normalizeArgs(String inputRaw, String commandName) {
        String input = inputRaw == null ? "" : inputRaw.trim();
        if (input.isBlank()) {
            return new String[0];
        }
        String[] parts = input.split("\\s+");
        if (parts.length > 0 && parts[0].equalsIgnoreCase(commandName)) {
            String[] shifted = new String[parts.length - 1];
            System.arraycopy(parts, 1, shifted, 0, shifted.length);
            return shifted;
        }
        return parts;
    }
}

