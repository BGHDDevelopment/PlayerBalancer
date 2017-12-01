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
                                    sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "There is no player with that name connected to this server");
                                }
                            } else {
                                if (sender instanceof Player) {
                                    plugin.getManager().connectPlayer((Player) sender, input);
                                    sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
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
                                sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
                            } else {
                                sender.sendMessage(ChatColor.RED + "There is no player with that name connected to this server");
                            }
                        } else {
                            if (sender instanceof Player) {
                                plugin.getManager().fallbackPlayer((Player) sender);
                                sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
                            } else {
                                sender.sendMessage(ChatColor.RED + "This command variant can only be executed by a player");
                            }
                        }
                        break;
                    }

                    case "bypassconnect": {
                        if (args.length >= 2) {
                            String input = args[1];
                            if (args.length >= 3) {
                                Player player = plugin.getServer().getPlayer(args[2]);
                                if (player != null) {
                                    plugin.getManager().bypassConnect(player, input);
                                    sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "There is no player with that name connected to this server");
                                }
                            } else {
                                if (sender instanceof Player) {
                                    plugin.getManager().bypassConnect((Player) sender, input);
                                    sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "This command variant can only be executed by a player");
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /section bypassconnect <server> [player]");
                        }
                        break;
                    }

                    case "setbypass": {
                        if (args.length >= 2) {
                            Player player = plugin.getServer().getPlayer(args[1]);
                            if (player != null) {
                                plugin.getManager().setPlayerBypass(player);
                                sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
                            } else {
                                sender.sendMessage(ChatColor.RED + "There is no player with that name connected to this server");
                            }
                        } else {
                            if (sender instanceof Player) {
                                plugin.getManager().setPlayerBypass((Player) sender);
                                sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
                            } else {
                                sender.sendMessage(ChatColor.RED + "This command variant can only be executed by a player");
                            }
                        }
                        break;
                    }

                    case "clearbypass": {
                        if (args.length >= 2) {
                            Player player = plugin.getServer().getPlayer(args[1]);
                            if (player != null) {
                                plugin.getManager().clearPlayerBypass((Player) sender);
                                sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
                            } else {
                                sender.sendMessage(ChatColor.RED + "There is no player with that name connected to this server");
                            }
                        } else {
                            if (sender instanceof Player) {
                                plugin.getManager().clearPlayerBypass((Player) sender);
                                sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
                            } else {
                                sender.sendMessage(ChatColor.RED + "This command variant can only be executed by a player");
                            }
                        }
                        break;
                    }

                    case "overridestatus": {
                        if (args.length >= 3) {
                            if (args[2].equals("false") || args[2].equals("true")) {
                                plugin.getManager().setStatusOverride(args[1], Boolean.valueOf(args[2]));
                                sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
                            } else {
                                sender.sendMessage(ChatColor.RED + "The status parameter of this command variant has to be a boolean type, either false or true");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /section overridestatus <section> <status: false|true>");
                        }
                        break;
                    }

                    case "clearoverride": {
                        if (args.length >= 2) {
                            plugin.getManager().clearStatusOverride(args[1]);
                            sender.sendMessage(ChatColor.GREEN + "Successfully sent request to the plugin");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /section clearoverride <server>");
                        }
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
                sender.sendMessage(ChatColor.AQUA + "/spb overridestatus <server> <status: false|true>" + ChatColor.GRAY + " - " + ChatColor.RED + "Overrides the accessible status of a specific server, over anything else");
                sender.sendMessage(ChatColor.AQUA + "/spb clearoverride <server>" + ChatColor.GRAY + " - " + ChatColor.RED + "Clears the overridden status of a specific server");
                sender.sendMessage(ChatColor.STRIKETHROUGH + ChatColor.GRAY.toString() + Strings.repeat("-", 53));
            }
        }
        return false;
    }
}
