package dt.aiotpa;

import dt.aiotpa.Command.*;
import dt.aiotpa.Manager.EconomyManager;
import dt.aiotpa.Manager.LanguageManager;
import dt.aiotpa.Manager.PlayerSettingsManager;
import dt.aiotpa.Manager.TpaManager;
import dt.aiotpa.Model.TpaConfig;

import org.bukkit.plugin.java.JavaPlugin;

public final class Aiotpa extends JavaPlugin {
    private TpaManager tpaManager;
    private static EconomyManager economyManager;
    private static LanguageManager languageManager;
    private static Aiotpa instance;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        // Load language manager
        getLogger().info("Loading language manager...");
        languageManager = new LanguageManager(this);
        LanguageManager.createDefaultLanguageFiles(this);

        getLogger().info("Loading economy manager...");
        // Load economy manager
        economyManager = new EconomyManager(this);

        getLogger().info("Loading TPA manager...");
        // Load TPA manager
        TpaConfig tpaConfig = new TpaConfig(getConfig());
        PlayerSettingsManager settingManager = new PlayerSettingsManager(this);
        tpaManager = new TpaManager(this, tpaConfig, settingManager);

        getLogger().info("Registering commands and listeners...");
        // Listeners
        TpSettingsCommand settingsCmd = new TpSettingsCommand(tpaManager, settingManager);
        getServer().getPluginManager().registerEvents(settingsCmd, this);

        // Register commands
        getCommand("tpa").setExecutor(new TpaCommand(tpaManager, false));
        getCommand("tpahere").setExecutor(new TpaCommand(tpaManager, true));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(tpaManager));
        getCommand("tpadeny").setExecutor(new TpDenyCommand(tpaManager));
        getCommand("tpcancel").setExecutor(new TpCancelCommand(tpaManager));
        getCommand("tpatoggle").setExecutor(new TpToggleCommand(tpaManager));
        getCommand("tpasettings").setExecutor(settingsCmd);
        getCommand("tpalist").setExecutor(new TpListCommand(tpaManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Aiotpa getInstance() {
        return instance;
    }

    public TpaManager getTpaManager() {
        return tpaManager;
    }

    public static EconomyManager getEconomyManager() {
        return economyManager;
    }

    public static LanguageManager getLanguageManager() {
        return languageManager;
    }
}
