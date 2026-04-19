package com.swartzz.troll.commands;

import com.swartzz.troll.TrollPlugin;
import com.swartzz.troll.inventory.impl.PlayerListGUI;
import com.swartzz.troll.inventory.impl.TrollGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrollCommand implements CommandExecutor, TabCompleter {

    private final TrollPlugin plugin;

    public TrollCommand(TrollPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("open")) {
            plugin.getGuiManager().openGUI(new PlayerListGUI(plugin, 0), player);
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /troll reset <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found: " + ChatColor.WHITE + args[1]);
                return true;
            }
            plugin.getTrollManager().resetTrolls(target);
            player.sendMessage(ChatColor.GREEN + "All active trolls have been cleared for "
                    + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + ChatColor.WHITE + args[0]);
            return true;
        }
        if (target.equals(player)) {
            player.sendMessage(ChatColor.RED + "You cannot troll yourself.");
            return true;
        }
        plugin.getGuiManager().openGUI(new TrollGUI(plugin, target), player);
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "━━━ Troll Commands ━━━");
        player.sendMessage(ChatColor.RED + "/troll <player> "
                + ChatColor.GRAY + "- Open troll menu for a player");
        player.sendMessage(ChatColor.RED + "/troll open "
                + ChatColor.GRAY + "- Browse all online players");
        player.sendMessage(ChatColor.RED + "/troll reset <player> "
                + ChatColor.GRAY + "- Clear all active trolls");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(Arrays.asList("open", "reset"));
            Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .forEach(options::add);
            return options.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}