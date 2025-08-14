package dt.aiotpa.Command;

import dt.aiotpa.Manager.LanguageManager;
import dt.aiotpa.Manager.TpaManager;
import dt.aiotpa.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements CommandExecutor {
    private final TpaManager manager;
    private final boolean here;

    public TpaCommand(TpaManager manager, boolean here) {
        this.manager = manager;
        this.here = here;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(LanguageManager.getMessage("only-players-can-use-command"));
            return true;
        }
        if (!p.hasPermission(here ? "aiotpa.tpahere" : "aiotpa.tpa")) {
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("command.no-permission"));
            return true;
        }
        if (args.length != 1) {
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("command.tpa.usage-start")
                    .replace("{command}", label));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("command.tpa.player-not-found")
                    .replace("{player}", args[0]));
            return true;
        }
        if (target.getUniqueId().equals(p.getUniqueId())) {
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("command.tpa.cannot-tpa-self"));
            return true;
        }
        manager.sendRequest(p, target, here);
        return true;
    }
}