package dt.aiotpa;

import net.md_5.bungee.api.ChatColor;

import static dt.aiotpa.Aiotpa.getInstance;

public class Utility {
    public static String getPrefix() {
        if(getInstance().getConfig().getBoolean("enable-prefix", true)) {
            String prefix = getInstance().getConfig().getString("prefix", "&#108630ai&#54B682ot&#5AA27Bpa&#5AA27B: ");
            return Utility.translateColorCodes(prefix);
        }
        return "";
    }

    public static String translateColorCodes(String text) {
        String[] texts = text.split(String.format("((?<=%1$s)|(?=%1$s))", "&"));
        StringBuilder finalText = new StringBuilder();
        for (int i = 0; i < texts.length; i++) {
            if (texts[i].equalsIgnoreCase("&")) {
                i++;
                if (texts[i].charAt(0) == '#') {
                    finalText.append(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7)) + texts[i].substring(7));
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                }
            } else {
                finalText.append(texts[i]);
            }
        }
        return finalText.toString();
    }
}
