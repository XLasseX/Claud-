package de.donutsmp.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public final class TeamCommand implements CommandExecutor, TabCompleter {
    private final TeamManager teams;
    public TeamCommand(DonutSMPCore plugin, TeamManager teams) { this.teams = teams; }
    private void msg(CommandSender s, String m) { s.sendMessage(ChatColor.BLUE + "[Team] " + ChatColor.WHITE + m); }

    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) { msg(s, "Players only."); return true; }
        if (a.length == 0) { msg(p, "/team create <name>, invite <player>, accept, leave, kick <player>, disband, info"); return true; }
        switch (a[0].toLowerCase()) {
            case "create" -> { if (a.length != 2) { msg(p, "Usage: /team create <name>"); return true; } msg(p, teams.create(p, a[1]) ? "Team created." : "Could not create that team."); }
            case "invite" -> {
                if (a.length != 2) { msg(p, "Usage: /team invite <player>"); return true; }
                Player target = Bukkit.getPlayerExact(a[1]);
                if (target == null) { msg(p, "Player not found."); return true; }
                if (teams.invite(p, target)) { msg(p, "Invite sent."); target.sendMessage(ChatColor.BLUE + "[Team] " + ChatColor.WHITE + "You were invited. Use /team accept."); }
                else msg(p, "Could not invite that player.");
            }
            case "accept" -> msg(p, teams.accept(p) ? "You joined the team." : "No valid invite.");
            case "leave" -> msg(p, teams.leave(p) ? "You left the team." : "You are not in a team.");
            case "disband" -> msg(p, teams.disband(p) ? "Team disbanded." : "You must be the team owner.");
            case "kick" -> {
                if (a.length != 2) { msg(p, "Usage: /team kick <player>"); return true; }
                Player target = Bukkit.getPlayerExact(a[1]);
                msg(p, target != null && teams.kick(p, target) ? "Player kicked." : "Could not kick that player.");
            }
            case "info" -> {
                TeamManager.Team t = teams.getTeam(p);
                if (t == null) { msg(p, "You are not in a team."); return true; }
                msg(p, "Team: " + t.name + " | Members: " + t.members.size());
            }
            default -> msg(p, "Commands: create, invite, accept, leave, kick, disband, info");
        }
        return true;
    }

    @Override public List<String> onTabComplete(CommandSender s, Command c, String l, String[] args) {
        if (args.length == 1) {
            List<String> out = new ArrayList<>();
            for (String x : List.of("create","invite","accept","leave","kick","disband","info")) if (x.startsWith(args[0].toLowerCase())) out.add(x);
            return out;
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("kick"))) {
            List<String> out = new ArrayList<>(); Bukkit.getOnlinePlayers().forEach(p -> { if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) out.add(p.getName()); }); return out;
        }
        return List.of();
    }
}
