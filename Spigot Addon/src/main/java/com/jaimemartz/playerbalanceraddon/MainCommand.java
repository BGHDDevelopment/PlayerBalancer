package com.jaimemartz.playerbalanceraddon;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
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
                            if (args.length >= 3) {
                                Player player = plugin.getServer().getPlayer(args[2]);
                                if (player != null) {
                                    plugin.getManager().connectPlayer(player, input);
                                } else {
                                    sender.sendMessage(ChatColor.RED + "There is no player with that name connected to this server");
                                }
                            } else {
                                if (sender instanceof Player) {
                                    plugin.getManager().connectPlayer((Player) sender, input);
                                } else {
                                    sender.sendMessage(ChatColor.RED + "This command variant can only be executed by a player");
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /section connect <section> [player]");
                        }
                        break;
                    }

                    case "fallback": {
                        if (args.length >= 2) {
                            Player player = plugin.getServer().getPlayer(args[1]);
                            if (player != null) {
                                plugin.getManager().fallbackPlayer((Player) sender);
                            } else {
                                sender.sendMessage(ChatColor.RED + "There is no player with that name connected to this server");
                            }
                        } else {
                            if (sender instanceof Player) {
                                plugin.getManager().fallbackPlayer((Player) sender);
                            } else {
                                sender.sendMessage(ChatColor.RED + "This command variant can only be executed by a player");
                            }
                        }
                        break;
                    }

                    case "bypassconnect": {
                        sender.sendMessage(ChatColor.RED + "This command variant is not implemented yet");
                        break;
                    }

                    case "setbypass": {
                        sender.sendMessage(ChatColor.RED + "This command variant is not implemented yet");
                        break;
                    }

                    case "clearbypass": {
                        sender.sendMessage(ChatColor.RED + "This command variant is not implemented yet");
                        break;
                    }

                    case "overridestatus": {
                        sender.sendMessage(ChatColor.RED + "This command variant is not implemented yet");
                        break;
                    }

                    case "clearoverride": {
                        sender.sendMessage(ChatColor.RED + "This command variant is not implemented yet");
                        break;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.STRIKETHROUGH + ChatColor.GRAY.toString() + Strings.repeat("-", 53));
                sender.sendMessage(ChatColor.GRAY + "Available commands:");
                sender.sendMessage(ChatColor.AQUA + "/spb connect <section> [player]" + ChatColor.GRAY + " - " + ChatColor.RED + "Connects you or the specified player to that section");
                sender.sendMessage(ChatColor.AQUA + "/spb fallback [player]" + ChatColor.GRAY + " - " + ChatColor.RED + "Connects you or the specified player to the parent of the current section");
                sender.sendMessage(ChatColor.AQUA + "/spb bypassconnect <server> [player]" + ChatColor.GRAY + " - " + ChatColor.RED + "Connects you or the specified player to a specific server without balancing");
                sender.sendMessage(ChatColor.AQUA + "/spb setbypass [player]" + ChatColor.GRAY + " - " + ChatColor.RED + "Sets a bypass for you or the specified player");
                sender.sendMessage(ChatColor.AQUA + "/spb clearbypass [player]" + ChatColor.GRAY + " - " + ChatColor.RED + "Clears the bypass for you or the specified player");
                sender.sendMessage(ChatColor.AQUA + "/spb overridestatus <server> <status>" + ChatColor.GRAY + " - " + ChatColor.RED + "Overrides the accessible status of a specific server, over anything else");
                sender.sendMessage(ChatColor.AQUA + "/spb clearoverride <server>" + ChatColor.GRAY + " - " + ChatColor.RED + "Clears the overridden status of a specific server");
                sender.sendMessage(ChatColor.STRIKETHROUGH + ChatColor.GRAY.toString() + Strings.repeat("-", 53));
            }
        }
        return false;
    }
}
