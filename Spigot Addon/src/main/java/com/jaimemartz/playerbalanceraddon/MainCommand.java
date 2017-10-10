package com.jaimemartz.playerbalanceraddon;

import com.google.common.base.Strings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {
    private final PlayerBalancerAddon plugin;

    public MainCommand(PlayerBalancerAddon plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("spb")) {
            if (args.length != 0) {
                switch (args[0].toLowerCase()) {
                    case "connect": {
                        if (args.length >= 2) {
                            String input = args[1];
                            if (args.length == 3) {
                                Player player = plugin.getServer().getPlayer(args[2]);
                                if (player != null) {
                                    plugin.getManager().connectPlayer(player, input);
                                } else {
                                    sender.spigot().sendMessage(new ComponentBuilder("There is no player with that name connected to this proxy").color(ChatColor.RED).create());
                                }
                            } else {
                                if (sender instanceof Player) {
                                    plugin.getManager().connectPlayer((Player) sender, input);
                                } else {
                                    sender.spigot().sendMessage(new ComponentBuilder("This command can only be executed by a player").color(ChatColor.RED).create());
                                }
                            }
                        } else {
                            sender.spigot().sendMessage(new ComponentBuilder("Usage: /section connect <section> [player]").color(ChatColor.RED).create());
                        }
                        break;
                    }

                    case "info": {
                        plugin.getManager().getSectionOfPlayer((Player) sender, (a) -> {
                            System.out.println(a);
                            sender.sendMessage(a);
                        });
                        break;
                    }
                }
            } else {
                sender.spigot().sendMessage(new ComponentBuilder(Strings.repeat("-", 53)).strikethrough(true).color(ChatColor.GRAY).create());
                sender.spigot().sendMessage(new ComponentBuilder("Available commands:").color(ChatColor.GRAY).create());
                sender.spigot().sendMessage(new ComponentBuilder("/spb connect <section> [player]").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Connects you or the specified player to that section").color(ChatColor.RED).create());
                sender.spigot().sendMessage(new ComponentBuilder("/spb fallback").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Connects you to the parent section").color(ChatColor.RED).create());
                sender.spigot().sendMessage(new ComponentBuilder(Strings.repeat("-", 53)).strikethrough(true).color(ChatColor.GRAY).create());
            }
        }
        return false;
    }
}
