package dt.aiotpa.Manager;


import dt.aiotpa.Model.PlayerSettings;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSettingsManager {
    private final Plugin plugin;
    private final Map<UUID, PlayerSettings> cache = new HashMap<>();

    public PlayerSettingsManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public PlayerSettings get(Player p) {
        return cache.computeIfAbsent(p.getUniqueId(), uuid -> load(p.getUniqueId()));
    }

    public void save(Player p) {
        save(p.getUniqueId());
    }

    public void saveAll() {
        cache.keySet().forEach(this::save);
    }

    private PlayerSettings load(UUID uuid) {
        File file = getFile(uuid);
        if (!file.exists()) {
            return new PlayerSettings();
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        PlayerSettings s = new PlayerSettings();
        s.setTpaToggle(cfg.getBoolean("tpaToggle", true));
        s.setAutoAccept(cfg.getBoolean("autoAccept", false));
        s.setAutoDeny(cfg.getBoolean("autoDeny", false));
        return s;
    }

    private void save(UUID uuid) {
        PlayerSettings s = cache.get(uuid);
        if (s == null) return;
        File file = getFile(uuid);
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("tpaToggle", s.isTpaToggle());
        cfg.set("autoAccept", s.isAutoAccept());
        cfg.set("autoDeny", s.isAutoDeny());
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFile(UUID uuid) {
        File dir = new File(plugin.getDataFolder(), "players");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, uuid.toString() + ".yml");
    }
}