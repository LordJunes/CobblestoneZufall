package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * CobblestoneZufall - A Hytale server plugin.
 *
 * @author Lord Junes
 * @version 1.0.0
 */
public class CobblestoneZufallPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String BUILD_MARKER = "2026-02-23-0648";
    private static CobblestoneZufallPlugin instance;

    private ConfigManager configManager;
    private EconomyBridge economyBridge;
    private ScoreboardPlaceholderBridge scoreboardPlaceholderBridge;

    public CobblestoneZufallPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static CobblestoneZufallPlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("[CobblestoneZufall] Setting up...");
        LOGGER.at(Level.INFO).log("[CobblestoneZufall] Build marker: " + BUILD_MARKER);
        LOGGER.at(Level.INFO).log("[CobblestoneZufall] Data directory: " + getDataDirectory());

        configManager = new ConfigManager();
        configManager.load();
        MiningRateTracker.getInstance().load();
        economyBridge = new EconomyBridge();
        economyBridge.load();
        scoreboardPlaceholderBridge = new ScoreboardPlaceholderBridge(configManager);
        scoreboardPlaceholderBridge.register();
        LOGGER.at(Level.INFO).log("[CobblestoneZufall] Config file: " + configManager.getConfigFilePath());
        LOGGER.at(Level.INFO).log("[CobblestoneZufall] Loaded tier1 drop entries: " + configManager.getDropsForTier(1).size());

        getCommandRegistry().registerCommand(new CobCommand(configManager, economyBridge));
        getCommandRegistry().registerCommand(new CobAdminCommand(configManager));
        getCommandRegistry().registerCommand(new MoneyCommand(economyBridge));
        getCommandRegistry().registerCommand(new EcoAdminCommand(economyBridge));

        // This is the key path taken from the working example mod.
        getEntityStoreRegistry().registerSystem(new CobblestoneGeneratorSystem(configManager));
        getEntityStoreRegistry().registerSystem(new CobbleNaturalGenerationOverrideSystem(configManager));

        LOGGER.at(Level.INFO).log("[CobblestoneZufall] Setup complete!");
    }

    @Override
    protected void start() {
        if (scoreboardPlaceholderBridge != null) {
            scoreboardPlaceholderBridge.register();
        }
        LOGGER.at(Level.INFO).log("[CobblestoneZufall] Started!");
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("[CobblestoneZufall] Shutting down...");
        if (scoreboardPlaceholderBridge != null) {
            scoreboardPlaceholderBridge.unregister();
            scoreboardPlaceholderBridge = null;
        }
        if (economyBridge != null) {
            economyBridge.save();
        }
        MiningRateTracker.getInstance().save();
        economyBridge = null;
        instance = null;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public EconomyBridge getEconomyBridge() {
        return economyBridge;
    }
}
