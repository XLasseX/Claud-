package de.donutsmp.core;

import org.bukkit.plugin.java.JavaPlugin;

public final class DonutSMPCore extends JavaPlugin {
    private ShardManager shards;
    private TeamManager teams;
    private PlaytimeManager playtime;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        shards = new ShardManager(this);
        teams = new TeamManager(this);
        playtime = new PlaytimeManager(this);

        ShardsCommand sc = new ShardsCommand(this, shards);
        getCommand("shards").setExecutor(sc);
        getCommand("shards").setTabCompleter(sc);

        TeamCommand tc = new TeamCommand(this, teams);
        getCommand("team").setExecutor(tc);
        getCommand("team").setTabCompleter(tc);

        getServer().getPluginManager().registerEvents(playtime, this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new DonutExpansion(this, shards, teams, playtime).register();
            getLogger().info("PlaceholderAPI expansion registered.");
        }
        getLogger().info("DonutSMPCore 1.1.0 enabled.");
    }

    @Override
    public void onDisable() {
        if (playtime != null) playtime.save();
        if (shards != null) shards.save();
    }

    public ShardManager getShards() { return shards; }
    public TeamManager getTeams() { return teams; }
    public PlaytimeManager getPlaytime() { return playtime; }
}
