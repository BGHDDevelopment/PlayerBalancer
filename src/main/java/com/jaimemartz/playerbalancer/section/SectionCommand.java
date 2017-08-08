package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.configuration.ConfigEntries;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

public class SectionCommand extends Command {
    private final PlayerBalancer plugin;
    private final ServerSection section;

    public SectionCommand(PlayerBalancer plugin, String name, String permission, List<String> aliases, ServerSection section) {
        super(name, permission, aliases.toArray(new String[aliases.size()]));
        this.plugin = plugin;
        this.section = section;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            //todo share this code with the fallback command instead of having it duplicated
            if (args.length == 1) {
                try {
                    int number = Integer.parseInt(args[0]);
                    if (number <= 0) {
                        MessageUtils.send(sender, ConfigEntries.INVALID_INPUT_MESSAGE.get());
                    } else if (number > section.getServers().size()) {
                        MessageUtils.send(sender, ConfigEntries.FAILURE_MESSAGE.get());
                    } else {
                        ServerInfo server = section.getSortedServers().get(number - 1);
                        ConnectionIntent.direct(plugin, player, server);
                    }
                } catch (NumberFormatException e) {
                    MessageUtils.send(sender, ConfigEntries.INVALID_INPUT_MESSAGE.get());
                }
            } else {
                ConnectionIntent.simple(plugin, player, section);
            }
        } else {
            sender.sendMessage(new ComponentBuilder("This command can only be executed by a player").color(ChatColor.RED).create());
        }
    }
}
