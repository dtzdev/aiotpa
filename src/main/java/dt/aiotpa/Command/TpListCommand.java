package dt.aiotpa.Command;

import dt.aiotpa.Manager.LanguageManager;
import dt.aiotpa.Manager.TpaManager;
import dt.aiotpa.Model.Request;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpListCommand implements CommandExecutor {
    private final TpaManager manager;

    public TpListCommand(TpaManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(LanguageManager.getMessage("command.only-players-can-use-command"));
            return true;
        }

        p.sendMessage(LanguageManager.getMessage("command.tplist.header"));
        p.sendMessage(LanguageManager.getMessage("command.tplist.incoming-header"));
        for (Request r : manager.getIncoming(p)) {
            Player from = Bukkit.getPlayer(r.getFrom());
            if (from != null) {
                String type = r.isHere()
                        ? LanguageManager.getMessage("command.tplist.type-here")
                        : LanguageManager.getMessage("command.tplist.type-to-you");

                p.sendMessage(LanguageManager.getMessage("command.tplist.incoming-entry")
                        .replace("{player}", from.getName())
                        .replace("{type}", type));
            }
        }

        p.sendMessage(LanguageManager.getMessage("command.tplist.outgoing-header"));
        for (Request r : manager.getOutgoing(p)) {
            Player to = Bukkit.getPlayer(r.getTo());
            if (to != null) {
                String type = r.isHere()
                        ? LanguageManager.getMessage("command.tplist.type-here")
                        : LanguageManager.getMessage("command.tplist.type-to-them");

                p.sendMessage(LanguageManager.getMessage("command.tplist.outgoing-entry")
                        .replace("{player}", to.getName())
                        .replace("{type}", type));
            }
        }
        return true;
    }
}