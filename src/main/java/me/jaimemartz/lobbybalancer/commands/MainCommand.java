package me.jaimemartz.lobbybalancer.commands;

import me.jaimemartz.faucet.Messager;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.manager.PasteHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MainCommand extends Command {
    private final LobbyBalancer plugin;

    public MainCommand(LobbyBalancer plugin) {
        super("balancer");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        Messager msgr = new Messager(sender);
        if (args.length == 0) {
            msgr.send(
                    "&e=====================================================",
                    "&7Information: LobbyBalancer version " + plugin.getDescription().getVersion(),
                    "&7Available commands:",
                    "&3/balancer &7- &cShows this message",
                    "&3/balancer paste &7- &cCreates a paste with the important files",
                    "&3/balancer reload &7- &cReloads the plugin completely",
                    "&e====================================================="
            );
        } else {
            switch (args[0]) {
                case "paste": {
                    if (sender.hasPermission("lobbybalancer.admin")) {
                        PasteHelper.LOGS.send(plugin, sender, "Last log file paste link: {link}");
                        PasteHelper.PLUGIN.send(plugin, sender, "Plugin config paste link: {link}");
                        PasteHelper.BUNGEE.send(plugin, sender, "Bungee config paste link (sensitive): {link}");
                    } else {
                        msgr.send(ChatColor.RED + "You do not have permission to execute this command!");
                    }
                    break;
                }
                case "reload": {
                    if (sender.hasPermission("lobbybalancer.admin")) {
                        msgr.send(ChatColor.GREEN + "Reloading the configuration, this may take a while...");
                        plugin.reloadPlugin();
                        msgr.send(ChatColor.GREEN + "The configuration has been reloaded");
                    } else {
                        msgr.send(ChatColor.RED + "You do not have permission to execute this command!");
                    }
                    break;
                }
                default: {
                    msgr.send(ChatColor.RED + "This is not a valid argument for this command!");
                }
            }
        }
    }
}