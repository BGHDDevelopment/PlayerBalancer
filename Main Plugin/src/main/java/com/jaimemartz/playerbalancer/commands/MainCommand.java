package com.jaimemartz.playerbalancer.commands;

import com.google.common.base.Strings;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.manager.PasteHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
                        PasteHelper.PLUGIN.send(plugin, sender);
                        PasteHelper.BUNGEE.send(plugin, sender);
                    } else {
                        sender.sendMessage(new ComponentBuilder("You do not have permission to execute this command!").color(ChatColor.RED).create());
                    }
                    break;
                }

                case "reload": {
                    if (sender.hasPermission("playerbalancer.admin")) {
                        sender.sendMessage(new ComponentBuilder("Reloading the configuration, this may take a while...").color(ChatColor.GREEN).create());
                        if (plugin.reloadPlugin()) {
                            sender.sendMessage(new ComponentBuilder("The plugin has successfully reloaded").color(ChatColor.GREEN).create());
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
            sender.sendMessage(new ComponentBuilder("/balancer paste").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Creates a paste with the important files").color(ChatColor.RED).create());
            sender.sendMessage(new ComponentBuilder("/balancer reload").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Reloads the plugin completely").color(ChatColor.RED).create());
            sender.sendMessage(new ComponentBuilder(Strings.repeat("-", 53)).strikethrough(true).color(ChatColor.GRAY).create());
        }
    }
}