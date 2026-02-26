package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.math.BigDecimal;

public class EcoAdminCommand extends AbstractPlayerCommand {
    private final EconomyBridge economyBridge;

    public EcoAdminCommand(EconomyBridge economyBridge) {
        super("ecoadmin", "Money admin command: give/set/take/balance");
        this.economyBridge = economyBridge;
        setAllowsExtraArguments(true);
    }

    @Override
    protected void execute(CommandContext context,
                           Store<EntityStore> store,
                           Ref<EntityStore> playerEntityRef,
                           PlayerRef playerRef,
                           World world) {
        Player player = store.getComponent(playerEntityRef, Player.getComponentType());
        if (player == null || !hasAdminPermission(player)) {
            context.sendMessage(Message.raw("[Money] No permission."));
            return;
        }

        String[] args = MoneyCommand.normalizeArgs(context.getInputString(), "ecoadmin");
        if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
            sendUsage(context);
            return;
        }
        if (args.length < 2) {
            sendUsage(context);
            return;
        }

        PlayerRef target = "me".equalsIgnoreCase(args[1]) || "self".equalsIgnoreCase(args[1])
                ? playerRef
                : MoneyCommand.resolvePlayer(args[1]);
        if (target == null) {
            context.sendMessage(Message.raw("[Money] Player not found or offline."));
            return;
        }

        String sub = args[0].toLowerCase();
        if ("balance".equals(sub) || "bal".equals(sub)) {
            context.sendMessage(Message.raw("[Money] " + target.getUsername() + ": "
                    + MoneyCommand.formatMoney(economyBridge.getBalance(target.getUuid()))));
            return;
        }

        if (args.length < 3) {
            sendUsage(context);
            return;
        }

        BigDecimal amount = MoneyCommand.parseAmount(args[2]);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            context.sendMessage(Message.raw("[Money] Invalid amount."));
            return;
        }

        switch (sub) {
            case "give" -> {
                BigDecimal after = economyBridge.addBalance(target.getUuid(), amount);
                context.sendMessage(Message.raw("[Money] Gave " + MoneyCommand.formatMoney(amount) + " to "
                        + target.getUsername() + ". New balance: " + MoneyCommand.formatMoney(after)));
            }
            case "set" -> {
                BigDecimal after = economyBridge.setBalance(target.getUuid(), amount);
                context.sendMessage(Message.raw("[Money] Set " + target.getUsername() + " to "
                        + MoneyCommand.formatMoney(after)));
            }
            case "take", "remove" -> {
                if (!economyBridge.removeBalance(target.getUuid(), amount)) {
                    context.sendMessage(Message.raw("[Money] Player does not have enough money."));
                    return;
                }
                context.sendMessage(Message.raw("[Money] Took " + MoneyCommand.formatMoney(amount) + " from "
                        + target.getUsername() + ". New balance: "
                        + MoneyCommand.formatMoney(economyBridge.getBalance(target.getUuid()))));
            }
            default -> sendUsage(context);
        }
    }

    private static boolean hasAdminPermission(Player player) {
        return player.hasPermission("cob.admin")
                || player.hasPermission("money.admin")
                || player.hasPermission("op")
                || player.hasPermission("*");
    }

    private static void sendUsage(CommandContext context) {
        context.sendMessage(Message.raw("[Money] /ecoadmin give <player|me> <amount>"));
        context.sendMessage(Message.raw("[Money] /ecoadmin set <player|me> <amount>"));
        context.sendMessage(Message.raw("[Money] /ecoadmin take <player|me> <amount>"));
        context.sendMessage(Message.raw("[Money] /ecoadmin balance <player|me>"));
    }
}

