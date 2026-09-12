package de.donutsmp.core;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class PlaytimeManager implements Listener {
    private final DonutSMPCore plugin;
    private final File file;
    private final YamlConfiguration data;
    private final Map<UUID, Long> sessionStart = new HashMap<>();

    public PlaytimeManager(DonutSMPCore plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "playtime.yml");
        if (!file.exists()) try { file.getParentFile().mkdirs(); file.createNewFile(); }
        catch (IOException e) { plugin.getLogger().severe("Could not create playtime.yml: " + e.getMessage()); }
        data = YamlConfiguration.loadConfiguration(file);
        for (Player p : plugin.getServer().getOnlinePlayers()) sessionStart.put(p.getUniqueId(), System.currentTimeMillis());
        long interval = Math.max(10, plugin.getConfig().getLong("playtime.save-interval-seconds", 60));
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> saveOnlineTime(), interval * 20L, interval * 20L);
    }

    @EventHandler public void join(PlayerJoinEvent e) { sessionStart.put(e.getPlayer().getUniqueId(), System.currentTimeMillis()); }
    @EventHandler public void quit(PlayerQuitEvent e) { flush(e.getPlayer()); sessionStart.remove(e.getPlayer().getUniqueId()); }

    public long getSeconds(UUID uuid) {
        long stored = data.getLong(uuid.toString(), 0L);
        Long start = sessionStart.get(uuid);
        return stored + (start == null ? 0 : Math.max(0, (System.currentTimeMillis() - start) / 1000));
    }

    private void flush(Player p) {
        UUID id = p.getUniqueId();
        data.set(id.toString(), getSeconds(id));
        sessionStart.put(id, System.currentTimeMillis());
    }

    private void saveOnlineTime() {
        for (Player p : plugin.getServer().getOnlinePlayers()) flush(p);
        save();
    }

    public void save() { try { data.save(file); } catch (IOException e) { plugin.getLogger().severe("Could not save playtime.yml: " + e.getMessage()); } }
}
