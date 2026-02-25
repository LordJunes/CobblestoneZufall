package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class CobCommand extends AbstractPlayerCommand {

    private final ConfigManager configManager;
    private final EconomyBridge economyBridge;

    public CobCommand(ConfigManager configManager, EconomyBridge economyBridge) {
        super("cob", "Player menu for cobblestone generator upgrades. Use /cob");
        this.configManager = configManager;
        this.economyBridge = economyBridge;
        setAllowsExtraArguments(true);
    }

    @Override
    protected void execute(CommandContext context,
                           Store<EntityStore> store,
                           Ref<EntityStore> playerEntityRef,
                           PlayerRef playerRef,
                           World world) {
        String input = context.getInputString() == null ? "" : context.getInputString().trim();
        if (input.endsWith(" help")) {
            context.sendMessage(Message.raw("[CobblestoneZufall] /cob -> Oeffnet das Upgrade-Menue."));
            context.sendMessage(Message.raw("[CobblestoneZufall] Tier-Up nutzt die interne Money-Waehrung ($)."));
            context.sendMessage(Message.raw("[CobblestoneZufall] Geld: /money | Admin: /ecoadmin give|set|take|balance"));
            return;
        }
        CobUpgradePage.open(playerEntityRef, store, playerRef, configManager, economyBridge);
    }
}
