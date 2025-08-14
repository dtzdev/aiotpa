package dt.aiotpa.Command;

import dt.aiotpa.Manager.LanguageManager;
import dt.aiotpa.Manager.TpaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpToggleCommand implements CommandExecutor {
    private final TpaManager manager;

    public TpToggleCommand(TpaManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(LanguageManager.getMessage("command.only-players-can-use-command"));
            return true;
        }
        manager.toggle(p);
        return true;
    }
}