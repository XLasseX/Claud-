package de.donutsmp.core;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class ShardManager {
    private final DonutSMPCore plugin;
    private final File file;
    private final YamlConfiguration data;

    public ShardManager(DonutSMPCore plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "shards.yml");
        if (!file.exists()) try { file.getParentFile().mkdirs(); file.createNewFile(); }
        catch (IOException e) { plugin.getLogger().severe("Could not create shards.yml: " + e.getMessage()); }
        data = YamlConfiguration.loadConfiguration(file);
    }
    public long get(UUID uuid) { return data.getLong(uuid.toString(), 0L); }
    public void set(UUID uuid, long amount) { data.set(uuid.toString(), Math.max(0L, amount)); save(); }
    public void add(UUID uuid, long amount) { set(uuid, get(uuid) + amount); }
    public void save() { try { data.save(file); } catch (IOException e) { plugin.getLogger().severe("Could not save shards.yml: " + e.getMessage()); } }
    public long get(Player p) { return get(p.getUniqueId()); }
}
