package dt.aiotpa.Manager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageManager {
    public static String color(String msg) {
        if (msg == null) return "";
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void sendActionbar(Player player, String msg) {
        if (player == null || msg == null) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(msg)));
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) return;
        player.sendTitle(color(title), color(subtitle), fadeIn, stay, fadeOut);
    }

    public static void sendTitle(Player player, String title, int fadeIn, int stay, int fadeOut) {
        sendTitle(player, title, "", fadeIn, stay, fadeOut);
    }
}