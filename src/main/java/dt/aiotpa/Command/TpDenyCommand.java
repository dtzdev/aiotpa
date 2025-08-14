package dt.aiotpa.Command;

import dt.aiotpa.Manager.LanguageManager;
import dt.aiotpa.Manager.TpaManager;
import dt.aiotpa.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDenyCommand implements CommandExecutor {
    private final TpaManager manager;

    public TpDenyCommand(TpaManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(LanguageManager.getMessage("command.only-players-can-use-command"));
            return true;
        }
        if (!p.hasPermission("aiotpa.deny")) {
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("command.no-permission"));
            return true;
        }

        String targetName = args.length > 0 ? args[0] : null;
        manager.deny(p, targetName);
        return true;
    }
}