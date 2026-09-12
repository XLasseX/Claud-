package de.donutsmp.core;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public final class DonutExpansion extends PlaceholderExpansion {
    private final DonutSMPCore plugin;
    private final ShardManager shards;
    private final TeamManager teams;
    private final PlaytimeManager playtime;

    public DonutExpansion(DonutSMPCore plugin, ShardManager shards, TeamManager teams, PlaytimeManager playtime) {
        this.plugin = plugin; this.shards = shards; this.teams = teams; this.playtime = playtime;
    }
    @Override public String getIdentifier() { return "donutsmp"; }
    @Override public String getAuthor() { return "DonutSMP"; }
    @Override public String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }

    @Override public String onPlaceholderRequest(Player p, String params) {
        if (p == null) return "";
        return switch (params.toLowerCase()) {
            case "shards" -> String.valueOf(shards.get(p));
            case "team" -> { TeamManager.Team t = teams.getTeam(p); yield t == null ? "N/A" : t.name; }
            case "team_size" -> { TeamManager.Team t = teams.getTeam(p); yield t == null ? "0" : String.valueOf(t.members.size()); }
            case "playtime_seconds" -> String.valueOf(playtime.getSeconds(p.getUniqueId()));
            case "playtime_minutes" -> String.valueOf(playtime.getSeconds(p.getUniqueId()) / 60);
            case "playtime_hours" -> String.valueOf(playtime.getSeconds(p.getUniqueId()) / 3600);
            case "playtime" -> format(playtime.getSeconds(p.getUniqueId()));
            default -> null;
        };
    }

    private String format(long seconds) {
        long days = seconds / 86400; seconds %= 86400;
        long hours = seconds / 3600; seconds %= 3600;
        long minutes = seconds / 60; seconds %= 60;
        if (days > 0) return days + "d " + hours + "h";
        if (hours > 0) return hours + "h " + minutes + "m";
        return minutes + "m " + seconds + "s";
    }
}
