package com.jaimemartz.playerbalancer.commands;

import com.google.common.base.Strings;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.manager.PasteHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MainCommand extends Command {
    private final PlayerBalancer plugin;

    public MainCommand(PlayerBalancer plugin) {
        super("balancer", "", "playerbalancer");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (args.length != 0) {
            switch (args[0].toLowerCase()) {
                case "paste": {
                    if (sender.hasPermission("playerbalancer.admin")) {
                        if (args.length == 2) {
                            switch (args[1].toLowerCase()) {
                                case "all": {
                                    PasteHelper.PLUGIN.send(plugin, sender);
                                    PasteHelper.BUNGEE.send(plugin, sender);
                                    PasteHelper.LOGS.send(plugin, sender);
                                    break;
                                }

                                case "plugin": {
                                    PasteHelper.PLUGIN.send(plugin, sender);
                                    break;
                                }

                                case "bungee": {
                                    PasteHelper.BUNGEE.send(plugin, sender);
                                    break;
                                }

                                case "logs": {
                                    PasteHelper.LOGS.send(plugin, sender);
                                    break;
                                }

                                default: {
                                    sender.sendMessage(new ComponentBuilder("This is not a valid argument for this command! Execute /balancer paste for help").color(ChatColor.RED).create());
                                }
                            }
                        } else {
                            if (sender instanceof ProxiedPlayer) {
                                sender.sendMessage(new ComponentBuilder("Available paste types:")
                                        .color(ChatColor.AQUA)
                                        .create());

                                sender.sendMessage(new ComponentBuilder("Click one:")
                                        .color(ChatColor.AQUA)
                                        .append(new ComponentBuilder(" [")
                                                .color(ChatColor.GRAY)
                                                .append(new ComponentBuilder("All")
                                                        .color(ChatColor.RED)
                                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/balancer paste all"))
                                                        .create())
                                                .append("]")
                                                .color(ChatColor.GRAY)
                                                .create())
                                        .append(new ComponentBuilder(" [")
                                                .color(ChatColor.GRAY)
                                                .append(new ComponentBuilder("Plugin")
                                                        .color(ChatColor.RED)
                                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/balancer paste plugin"))
                                                        .create())
                                                .append("]")
                                                .color(ChatColor.GRAY)
                                                .create())
                                        .append(new ComponentBuilder(" [")
                                                .color(ChatColor.GRAY)
                                                .append(new ComponentBuilder("Bungee")
                                                        .color(ChatColor.RED)
                                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/balancer paste bungee"))
                                                        .create())
                                                .append("]")
                                                .color(ChatColor.GRAY)
                                                .create())
                                        .append(new ComponentBuilder(" [")
                                                .color(ChatColor.GRAY)
                                                .append(new ComponentBuilder("Logs")
                                                        .color(ChatColor.RED)
                                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/balancer paste logs"))
                                                        .create())
                                                .append("]")
                                                .color(ChatColor.GRAY)
                                                .create())
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                new ComponentBuilder("Click one of the types to paste it")
                                                        .color(ChatColor.RED)
                                                        .create()))
                                        .create());
                            } else {
                                sender.sendMessage(new ComponentBuilder("Usage: /balancer paste [all|plugin|bungee|logs]").color(ChatColor.RED).create());
                            }

                        }
                    } else {
                        sender.sendMessage(new ComponentBuilder("You do not have permission to execute this command!").color(ChatColor.RED).create());
                    }
                    break;
                }

                case "reload": {
                    if (sender.hasPermission("playerbalancer.admin")) {
                        sender.sendMessage(new ComponentBuilder("Reloading the configuration, this may take a while...").color(ChatColor.GREEN).create());
                        if (plugin.reloadPlugin()) {
                            sender.sendMessage(new ComponentBuilder("The plugin has been successfully reloaded").color(ChatColor.GREEN).create());
                        } else {
                            sender.sendMessage(new ComponentBuilder("Something went badly while reloading the plugin").color(ChatColor.RED).create());
                        }
                    } else {
                        sender.sendMessage(new ComponentBuilder("You do not have permission to execute this command!").color(ChatColor.RED).create());
                    }
                    break;
                }

                default: {
                    sender.sendMessage(new ComponentBuilder("This is not a valid argument for this command! Execute /balancer for help").color(ChatColor.RED).create());
                }
            }
        } else {
            sender.sendMessage(new ComponentBuilder(Strings.repeat("-", 53)).strikethrough(true).color(ChatColor.GRAY).create());
            sender.sendMessage(new ComponentBuilder("PlayerBalancer " + plugin.getDescription().getVersion()).color(ChatColor.GRAY).create());
            sender.sendMessage(new ComponentBuilder("Available commands:").color(ChatColor.GRAY).create());
            sender.sendMessage(new ComponentBuilder("/balancer").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Shows you this message").color(ChatColor.RED).create());
            sender.sendMessage(new ComponentBuilder("/balancer paste [all|plugin|bungee|logs]").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Creates a paste with the important files").color(ChatColor.RED).create());
            sender.sendMessage(new ComponentBuilder("/balancer reload").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Reloads the plugin completely").color(ChatColor.RED).create());
            sender.sendMessage(new ComponentBuilder(Strings.repeat("-", 53)).strikethrough(true).color(ChatColor.GRAY).create());
        }
    }
}