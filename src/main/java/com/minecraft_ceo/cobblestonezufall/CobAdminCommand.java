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

public class CobAdminCommand extends AbstractPlayerCommand {

    private final ConfigManager configManager;

    public CobAdminCommand(ConfigManager configManager) {
        super("cobadmin", "Admin menu for cobblestone generator tiers/chances/costs. Use /cobadmin");
        this.configManager = configManager;
        setAllowsExtraArguments(true);
    }

    @Override
    protected void execute(CommandContext context,
                           Store<EntityStore> store,
                           Ref<EntityStore> playerEntityRef,
                           PlayerRef playerRef,
                           World world) {
        Player player = store.getComponent(playerEntityRef, Player.getComponentType());
        if (player == null) {
            context.sendMessage(Message.raw("[CobblestoneZufall] Spieler konnte nicht geladen werden."));
            return;
        }

        if (!hasAdminPermission(player)) {
            context.sendMessage(Message.raw("[CobblestoneZufall] Keine Berechtigung fuer /cobadmin (benoetigt cob.admin oder OP)."));
            return;
        }

        String input = context.getInputString() == null ? "" : context.getInputString().trim();
        if (input.endsWith(" help")) {
            context.sendMessage(Message.raw("[CobblestoneZufall] /cobadmin -> Oeffnet Admin-UI."));
            context.sendMessage(Message.raw("[CobblestoneZufall] /cobadmin curve -> Oeffnet Curve-Model UI (Start/End/Linear/Curve/Apply)."));
            return;
        }
        if (input.endsWith(" curve")) {
            CobbleCurvePage.open(playerEntityRef, store, playerRef, configManager);
            return;
        }

        CobbleConfigPage.open(playerEntityRef, store, playerRef, configManager);
    }

    private static boolean hasAdminPermission(Player player) {
        return player.hasPermission("cob.admin")
                || player.hasPermission("op")
                || player.hasPermission("*");
    }
}
