package dt.aiotpa.Manager;

import dt.aiotpa.Model.Request;
import dt.aiotpa.Aiotpa;
import dt.aiotpa.Model.TpaConfig;
import dt.aiotpa.Utility;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TpaManager {
    private final Aiotpa plugin;
    private final Map<UUID, List<Request>> requests = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final int requestExpireSeconds = 60;
    private final int cooldownSeconds = 10;
    private final TpaConfig cfg;
    private final PlayerSettingsManager settingsManager;


    public TpaManager(Aiotpa plugin, TpaConfig cfg, PlayerSettingsManager settingsManager) {
        this.plugin = plugin;
        this.cfg = cfg;
        this.settingsManager = settingsManager;
    }

    public boolean canReceive(Player p) {
        return settingsManager.get(p).isTpaToggle();
    }

    public boolean hasCooldown(Player p) {
        if (p.hasPermission("aiotpa.bypass.timer")) return false;
        return cooldowns.containsKey(p.getUniqueId()) && cooldowns.get(p.getUniqueId()) > System.currentTimeMillis();
    }

    public boolean canSendTpa(Player from) {
        return cfg.tpaEnabled;
    }

    private boolean takeEconomy(Player p, double cost) {
        if (!cfg.ecoEnabled) return true;

        if(EconomyManager.hasEnough(p, cost)) {
            EconomyManager.deduct(p, cost);
            return true;
        }

        p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.not-enough-money")
                .replace("{cost}", String.valueOf(cost)));
        return false;
    }

    public void sendRequest(Player from, Player to, boolean here) {
        if (!canSendTpa(from)) {
            from.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.cannot-send"));
            return;
        }
        if (!canReceive(to) && !from.hasPermission("aiotpa.bypass.toggle")) {
            if(from.hasPermission("aiotpa.bypass.toggle")) {
                from.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.bypass-toggle")
                        .replace("{player}", to.getName()));
            }
            else {
                from.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.target-not-accepting"));
            }
            return;
        }
        if (hasCooldown(from)) {
            from.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.cooldown"));
            return;
        }
        if (!from.getWorld().equals(to.getWorld()) && cfg.crossworldDeny) {
            from.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.crossworld-disabled"));
            return;
        }

        takeEconomy(from, cfg.tpaCost);

        Request req = new Request(from.getUniqueId(), to.getUniqueId(), here);
        requests.computeIfAbsent(from.getUniqueId(), k -> new ArrayList<>()).add(req);

        from.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.request-sent")
                .replace("{player}", to.getName()));

        if (settingsManager.get(to).isAutoAccept()) {
            accept(to, from.getName());
            return;
        } else if (settingsManager.get(to).isAutoDeny()) {
            deny(to, from.getName());
            return;
        }

        String baseMsg = LanguageManager.getMessage("tpa.request-message")
                .replace("{player}", from.getName())
                .replace("{type}", here
                        ? LanguageManager.getMessage("tpa.request-type-here")
                        : LanguageManager.getMessage("tpa.request-type-toyou"));

        TextComponent acceptBtn = new TextComponent(LanguageManager.getMessage("tpa.button-accept"));
        acceptBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + from.getName()));
        acceptBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(LanguageManager.getMessage("tpa.button-accept-hover")).create()));

        TextComponent denyBtn = new TextComponent(" " + LanguageManager.getMessage("tpa.button-deny"));
        denyBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + from.getName()));
        denyBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(LanguageManager.getMessage("tpa.button-deny-hover")).create()));

        to.spigot().sendMessage(new TextComponent(baseMsg));
        to.spigot().sendMessage(acceptBtn, denyBtn);

        cooldowns.put(from.getUniqueId(), System.currentTimeMillis() + cooldownSeconds * 1000L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (requests.remove(from.getUniqueId()) != null) {
                    from.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.request-expired-sender").replace("{player}", to.getName()));
                    to.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.request-expired-target").replace("{player}", from.getName()));
                }
            }
        }.runTaskLater(plugin, requestExpireSeconds * 20L);
    }

    private void warmupTeleport(Player teleporter, Player target, boolean here) {
        Player affected = here ? target : teleporter;

        affected.sendMessage(
                Utility.getPrefix() + LanguageManager.getMessage("tpa.warmup")
                        .replace("{seconds}", String.valueOf(cfg.warmupSeconds))
        );

        final double startX = affected.getLocation().getX();
        final double startY = affected.getLocation().getY();
        final double startZ = affected.getLocation().getZ();

        BukkitTask teleportTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!teleporter.isOnline() || !target.isOnline()) return;

            if (here) {
                target.teleport(teleporter);
            } else {
                teleporter.teleport(target);
            }

            if (cfg.messagesEnabled) {
                if (cfg.titleEnabled) {
                    MessageManager.sendTitle(affected, cfg.titleMain, cfg.titleSub, cfg.fadeIn, cfg.stay, cfg.fadeOut);
                }
                if (cfg.actionbarEnabled) {
                    MessageManager.sendActionbar(affected, cfg.actionbarText);
                }
            }

            affected.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.teleported"));
        }, cfg.warmupSeconds * 20L);

        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (!affected.isOnline()) {
                task.cancel();
                teleportTask.cancel();
                return;
            }
            double dist = affected.getLocation().distanceSquared(new Location(
                    affected.getWorld(), startX, startY, startZ));

            if (dist > 0.25) {
                affected.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.warmup-cancelled"));
                teleportTask.cancel();
                task.cancel();
            }
        }, 0L, 2L);

        if (cfg.messagesEnabled && cfg.countdownEnabled && cfg.countdownSeconds > 0) {
            for (int i = 0; i < cfg.countdownSeconds; i++) {
                int timeLeft = cfg.countdownSeconds - i;
                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                                MessageManager.sendActionbar(affected,
                                        cfg.countdownText.replace("{seconds}", String.valueOf(timeLeft))),
                        i * 20L);
            }
        }
    }

    public void accept(Player p, String targetName) {
        Request req = null;
        if (targetName != null && !targetName.isEmpty()) {
            Player target = plugin.getServer().getPlayer(targetName);
            if (target == null) {
                p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.player-not-found"));
                return;
            }
            for (List<Request> list : requests.values()) {
                for (Request r : list) {
                    if (r.getFrom().equals(target.getUniqueId()) && r.getTo().equals(p.getUniqueId())) {
                        req = r;
                        break;
                    }
                }
                if (req != null) break;
            }
        } else {
            for (List<Request> list : requests.values()) {
                for (Request r : list) {
                    if (r.getTo().equals(p.getUniqueId())) {
                        req = r;
                    }
                }
            }
        }

        if (req == null) {
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.no-incoming"));
            return;
        }

        Player from = plugin.getServer().getPlayer(req.getFrom());
        if (from == null) {
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.player-offline"));
            requests.remove(req.getFrom());
            return;
        }

        requests.remove(req.getFrom());
        warmupTeleport(from, p, req.isHere());
        p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.accepted"));
        from.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.accepted-other"));
    }


    public void deny(Player p, String targetName) {
        Request req = null;

        if (targetName != null && !targetName.isEmpty()) {
            Player target = plugin.getServer().getPlayer(targetName);
            if (target == null) {
                p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.player-not-found"));
                return;
            }
            for (List<Request> list : requests.values()) {
                for (Request r : list) {
                    if (r.getFrom().equals(target.getUniqueId()) && r.getTo().equals(p.getUniqueId())) {
                        req = r;
                        break;
                    }
                }
                if (req != null) break;
            }
        } else {
            for (List<Request> list : requests.values()) {
                for (Request r : list) {
                    if (r.getTo().equals(p.getUniqueId())) {
                        req = r;
                    }
                }
            }
        }

        if (req == null) {
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.no-incoming"));
            return;
        }

        requests.remove(req.getFrom());
        Player from = plugin.getServer().getPlayer(req.getFrom());
        if (from != null) {
            from.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.denied-other"));
        }
        p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.denied"));
    }

    public void cancel(Player p) {
        List<Request> reqs = requests.get(p.getUniqueId());
        if (reqs == null || reqs.isEmpty()) {
            p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.no-outgoing"));
            return;
        }
        for (Request req : new ArrayList<>(reqs)) {
            Player to = plugin.getServer().getPlayer(req.getTo());
            if (to != null) {
                to.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.cancelled-other").replace("{player}", p.getName()));
            }
            reqs.remove(req);
        }
        if (reqs.isEmpty()) requests.remove(p.getUniqueId());
        p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage("tpa.cancelled"));
    }

    public void toggle(Player p) {
        boolean newState = !settingsManager.get(p).isTpaToggle();
        settingsManager.get(p).setTpaToggle(newState);
        settingsManager.save(p);
        p.sendMessage(Utility.getPrefix() + LanguageManager.getMessage(newState ? "tpa.toggle-accepting" : "tpa.toggle-blocking"));
    }

    public List<Request> getIncoming(Player p) {
        List<Request> list = new ArrayList<>();
        for (List<Request> reqs : requests.values()) {
            for (Request r : reqs) {
                if (r.getTo().equals(p.getUniqueId())) list.add(r);
            }
        }
        return list;
    }

    public List<Request> getOutgoing(Player p) {
        return requests.getOrDefault(p.getUniqueId(), List.of());
    }
}