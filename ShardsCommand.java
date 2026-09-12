package de.donutsmp.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public final class ShardsCommand implements CommandExecutor, TabCompleter {
    private final ShardManager shards;
    public ShardsCommand(DonutSMPCore plugin, ShardManager shards) { this.shards = shards; }
    private void msg(CommandSender s, String m) { s.sendMessage(ChatColor.AQUA + "[Shards] " + ChatColor.WHITE + m); }

    @Override public boolean onCommand(CommandSender s, Command c, String label, String[] a) {
        if (a.length == 0 || a[0].equalsIgnoreCase("balance")) {
            Player p = s instanceof Player ? (Player)s : null;
            if (p == null && a.length < 2) { msg(s, "Usage: /shards balance <player>"); return true; }
            Player target = a.length >= 2 ? Bukkit.getPlayerExact(a[1]) : p;
            if (target == null) { msg(s, "Player not found."); return true; }
            msg(s, target.getName() + " has " + shards.get(target) + " shards.");
            return true;
        }
        if (!s.hasPermission("donutsmp.shards.admin")) { msg(s, "No permission."); return true; }
        if (a.length != 3) { msg(s, "Usage: /shards <give|take|set> <player> <amount>"); return true; }
        Player target = Bukkit.getPlayerExact(a[1]);
        if (target == null) { msg(s, "Player not found."); return true; }
        long amount;
        try { amount = Long.parseLong(a[2]); } catch (NumberFormatException e) { msg(s, "Amount must be a whole number."); return true; }
        if (amount < 0) { msg(s, "Amount cannot be negative."); return true; }
        switch (a[0].toLowerCase()) {
            case "give" -> shards.add(target.getUniqueId(), amount);
            case "take" -> shards.add(target.getUniqueId(), -amount);
            case "set" -> shards.set(target.getUniqueId(), amount);
            default -> { msg(s, "Usage: /shards <balance|give|take|set> <player> <amount>"); return true; }
        }
        msg(s, "Updated " + target.getName() + "'s shards.");
        return true;
    }

    @Override public List<String> onTabComplete(CommandSender s, Command c, String label, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) for (String x : List.of("balance","give","take","set")) if (x.startsWith(args[0].toLowerCase())) out.add(x);
        else if (args.length == 2) Bukkit.getOnlinePlayers().forEach(p -> { if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) out.add(p.getName()); });
        return out;
    }
}
