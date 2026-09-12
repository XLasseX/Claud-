package de.donutsmp.core;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.*;

public final class TeamManager {
    public static final class Team {
        public String name;
        public UUID owner;
        public final Set<UUID> members = new LinkedHashSet<>();
        Team(String name, UUID owner) { this.name = name; this.owner = owner; members.add(owner); }
    }
    private final DonutSMPCore plugin;
    private final File file;
    private final Map<String, Team> teams = new HashMap<>();
    private final Map<UUID, String> invites = new HashMap<>();

    public TeamManager(DonutSMPCore plugin) { this.plugin = plugin; file = new File(plugin.getDataFolder(), "teams.yml"); load(); }

    private void load() {
        YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
        if (y.getConfigurationSection("teams") == null) return;
        for (String key : y.getConfigurationSection("teams").getKeys(false)) {
            String p = "teams." + key;
            try {
                UUID owner = UUID.fromString(y.getString(p + ".owner"));
                Team t = new Team(y.getString(p + ".name", key), owner);
                t.members.clear();
                for (String id : y.getStringList(p + ".members")) try { t.members.add(UUID.fromString(id)); } catch (Exception ignored) {}
                teams.put(key.toLowerCase(), t);
            } catch (Exception ignored) {}
        }
    }
    private void saveInternal() {
        YamlConfiguration y = new YamlConfiguration();
        for (Team t : teams.values()) {
            String p = "teams." + t.name.toLowerCase();
            y.set(p + ".name", t.name); y.set(p + ".owner", t.owner.toString());
            y.set(p + ".members", t.members.stream().map(UUID::toString).toList());
        }
        try { file.getParentFile().mkdirs(); y.save(file); } catch (IOException e) { plugin.getLogger().severe("Could not save teams.yml: " + e.getMessage()); }
    }
    public Team getTeam(Player p) { for (Team t : teams.values()) if (t.members.contains(p.getUniqueId())) return t; return null; }
    public Team get(String name) { return teams.get(name.toLowerCase()); }
    public boolean create(Player p, String name) {
        int max = plugin.getConfig().getInt("team.name-max-length", 16);
        if (getTeam(p) != null || get(name) != null || name.length() > max || !name.matches("[A-Za-z0-9_]+")) return false;
        teams.put(name.toLowerCase(), new Team(name, p.getUniqueId())); saveInternal(); return true;
    }
    public boolean invite(Player from, Player target) {
        Team t = getTeam(from);
        if (t == null || !t.owner.equals(from.getUniqueId()) || getTeam(target) != null || t.members.size() >= plugin.getConfig().getInt("team.max-size", 8)) return false;
        invites.put(target.getUniqueId(), t.name.toLowerCase()); return true;
    }
    public boolean accept(Player p) {
        String key = invites.remove(p.getUniqueId()); if (key == null) return false;
        Team t = get(key); if (t == null || getTeam(p) != null || t.members.size() >= plugin.getConfig().getInt("team.max-size", 8)) return false;
        t.members.add(p.getUniqueId()); saveInternal(); return true;
    }
    public boolean leave(Player p) {
        Team t = getTeam(p); if (t == null) return false;
        if (t.owner.equals(p.getUniqueId())) return disband(p);
        t.members.remove(p.getUniqueId()); saveInternal(); return true;
    }
    public boolean disband(Player p) {
        Team t = getTeam(p); if (t == null || !t.owner.equals(p.getUniqueId())) return false;
        teams.remove(t.name.toLowerCase()); saveInternal(); return true;
    }
    public boolean kick(Player owner, Player target) {
        Team t = getTeam(owner);
        if (t == null || !t.owner.equals(owner.getUniqueId()) || !t.members.remove(target.getUniqueId())) return false;
        saveInternal(); return true;
    }
}
