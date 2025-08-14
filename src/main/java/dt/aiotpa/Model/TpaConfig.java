package dt.aiotpa.Model;

import org.bukkit.configuration.file.FileConfiguration;

public class TpaConfig {
    public final int warmupSeconds;
    public final boolean messagesEnabled;
    public final boolean countdownEnabled;
    public final int countdownSeconds;
    public final String countdownText;
    public final boolean titleEnabled;
    public final String titleMain;
    public final String titleSub;
    public final int fadeIn;
    public final int stay;
    public final int fadeOut;
    public final boolean actionbarEnabled;
    public final String actionbarText;
    public final boolean ecoEnabled;
    public final boolean tpaEnabled;
    public final double tpaCost;
    public final boolean crossworldDeny;
    public TpaConfig(FileConfiguration cfg) {
        // TPA configuration
        tpaEnabled = cfg.getBoolean("tpa.enabled", true);
        warmupSeconds =  cfg.getInt("tpa.warmup-seconds", 5);;
        crossworldDeny = cfg.getBoolean("tpa.crossworld-deny", false); // NEW


        // Messages configuration
        messagesEnabled = cfg.getBoolean("tpa.messages.enabled", true);
        countdownEnabled = cfg.getBoolean("tpa.messages.countdown.enabled", true);
        countdownSeconds = cfg.getInt("tpa.messages.countdown.seconds", 5);
        countdownText = cfg.getString("tpa.messages.countdown.text", "&aTeleporting in &c{seconds} &aseconds...");
        titleEnabled = cfg.getBoolean("tpa.messages.title.enabled", true);
        titleMain = cfg.getString("tpa.messages.title.main", "&aTeleporting...");
        titleSub = cfg.getString("tpa.messages.title.subtitle", "&7Don't move!");
        fadeIn = cfg.getInt("tpa.messages.title.fadeIn", 10);
        stay = cfg.getInt("tpa.messages.title.stay", 40);
        fadeOut = cfg.getInt("tpa.messages.title.fadeOut", 10);
        actionbarEnabled = cfg.getBoolean("tpa.messages.actionbar.enabled", true);
        actionbarText = cfg.getString("tpa.messages.actionbar.text", "&aTeleport successful!");

        // Economy configuration
        ecoEnabled = cfg.getBoolean("tpa.economy.enabled", false);
        tpaCost = cfg.getDouble("tpa.economy.cost", 0.0);
    }
}
