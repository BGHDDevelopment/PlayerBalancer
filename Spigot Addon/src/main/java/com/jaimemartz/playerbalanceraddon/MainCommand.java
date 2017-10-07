package com.jaimemartz.playerbalanceraddon;

import com.google.common.base.Strings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    private final PlayerBalancerAddon plugin;

    public MainCommand(PlayerBalancerAddon plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("balanceraddon"))

        sender.spigot().sendMessage(new ComponentBuilder(Strings.repeat("-", 53)).strikethrough(true).color(ChatColor.GRAY).create());
        sender.spigot().sendMessage(new ComponentBuilder("Available commands:").color(ChatColor.GRAY).create());
        sender.spigot().sendMessage(new ComponentBuilder("/section connect <section> [player]").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Connects you or the specified player to that section").color(ChatColor.RED).create());
        sender.spigot().sendMessage(new ComponentBuilder(Strings.repeat("-", 53)).strikethrough(true).color(ChatColor.GRAY).create());
        return false;
    }
}
