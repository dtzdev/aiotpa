package dt.aiotpa.Manager;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.Plugin;

public class EconomyManager {
    static Plugin plugin;
    public static EconomyManager instance;
    public static Economy economy = null;

    public EconomyManager(Plugin plugin) {
        EconomyManager.plugin = plugin;
        instance = this;
        economy = EconomyManager.setupEconomy();
    }

    public static Economy setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().severe(LanguageManager.getMessage("economy.vault-not-found"));
            return null;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        return (rsp == null) ? null : rsp.getProvider();
    }

    public static void deduct(Player p, double amount) {
        economy.withdrawPlayer(p, amount);
    }

    public static boolean hasEnough(Player p, double amount) {
        return economy.has(p, amount);
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static EconomyManager getInstance() {
        return instance;
    }
}
