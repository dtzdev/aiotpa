package dt.aiotpa.Command;


import dt.aiotpa.Manager.LanguageManager;
import dt.aiotpa.Manager.PlayerSettingsManager;
import dt.aiotpa.Manager.TpaManager;
import dt.aiotpa.Model.PlayerSettings;
import dt.aiotpa.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TpSettingsCommand implements CommandExecutor, Listener {
    private final TpaManager manager;
    private final PlayerSettingsManager settingsManager;
    private final Map<UUID, Long> clickCooldown = new HashMap<>();
    private final long changeCooldown = 1500;

    public TpSettingsCommand(TpaManager manager, PlayerSettingsManager settingsManager) {
        this.manager = manager;
        this.settingsManager = settingsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(LanguageManager.getMessage("command.only-players-can-use-command"));
            return true;
        }
        if (!p.hasPermission("aiotpa.settings")) {
            p.sendMessage(LanguageManager.getMessage(Utility.getPrefix() + "command.no-permission"));
            return true;
        }

        openSettingsGUI(p);
        return true;
    }

    private void openSettingsGUI(Player p) {
        PlayerSettings s = settingsManager.get(p);

        Inventory inv = Bukkit.createInventory(null, 27, LanguageManager.getMessage("command.tpsettings.gui-title"));

        inv.setItem(11, createItem(
                s.isTpaToggle() ? Material.LIME_DYE : Material.GRAY_DYE,
                LanguageManager.getMessage("command.tpsettings.toggle-tpa.name"),
                s.isTpaToggle()
                        ? LanguageManager.getMessage("command.tpsettings.toggle-tpa.lore-current-enabled")
                        : LanguageManager.getMessage("command.tpsettings.toggle-tpa.lore-current-disabled"),
                LanguageManager.getMessage("command.tpsettings.toggle-tpa.lore-click")
        ));

        inv.setItem(13, createItem(
                s.isAutoAccept() ? Material.LIME_DYE : Material.GRAY_DYE,
                LanguageManager.getMessage("command.tpsettings.auto-accept.name"),
                s.isAutoAccept()
                        ? LanguageManager.getMessage("command.tpsettings.auto-accept.lore-current-enabled")
                        : LanguageManager.getMessage("command.tpsettings.auto-accept.lore-current-disabled"),
                LanguageManager.getMessage("command.tpsettings.auto-accept.lore-click")
        ));

        inv.setItem(15, createItem(
                s.isAutoDeny() ? Material.LIME_DYE : Material.GRAY_DYE,
                LanguageManager.getMessage("command.tpsettings.auto-deny.name"),
                s.isAutoDeny()
                        ? LanguageManager.getMessage("command.tpsettings.auto-deny.lore-current-enabled")
                        : LanguageManager.getMessage("command.tpsettings.auto-deny.lore-current-disabled"),
                LanguageManager.getMessage("command.tpsettings.auto-deny.lore-click")
        ));

        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!e.getView().getTitle().equals(LanguageManager.getMessage("command.tpsettings.gui-title"))) return;
        e.setCancelled(true);

        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;

        long now = System.currentTimeMillis();
        if (clickCooldown.containsKey(p.getUniqueId()) &&
                now - clickCooldown.get(p.getUniqueId()) < changeCooldown) {
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("command.tpsettings.cooldown-message"));
            return;
        }
        clickCooldown.put(p.getUniqueId(), now);

        PlayerSettings s = settingsManager.get(p);
        String itemName = e.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(LanguageManager.getMessage("command.tpsettings.toggle-tpa.name"))) {
            s.setTpaToggle(!s.isTpaToggle());
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage(s.isTpaToggle()
                    ? "command.tpsettings.toggle-tpa.chat-enabled"
                    : "command.tpsettings.toggle-tpa.chat-disabled"));
        } else if (itemName.equals(LanguageManager.getMessage("command.tpsettings.auto-accept.name"))) {
            s.setAutoAccept(!s.isAutoAccept());
            if (s.isAutoAccept()) s.setAutoDeny(false);
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage(s.isAutoAccept()
                    ? "command.tpsettings.auto-accept.chat-enabled"
                    : "command.tpsettings.auto-accept.chat-disabled"));
        } else if (itemName.equals(LanguageManager.getMessage("command.tpsettings.auto-deny.name"))) {
            s.setAutoDeny(!s.isAutoDeny());
            if (s.isAutoDeny()) s.setAutoAccept(false);
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage(s.isAutoDeny()
                    ? "command.tpsettings.auto-deny.chat-enabled"
                    : "command.tpsettings.auto-deny.chat-disabled"));
        }

        settingsManager.save(p);
        openSettingsGUI(p);
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}