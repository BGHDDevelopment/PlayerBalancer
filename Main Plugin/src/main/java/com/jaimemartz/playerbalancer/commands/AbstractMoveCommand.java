package com.jaimemartz.playerbalancer.commands;

import com.google.common.collect.Iterables;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.settings.props.MessagesProps;
import com.jaimemartz.playerbalancer.settings.props.shared.CommandProps;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public abstract class AbstractMoveCommand extends Command {
    private final PlayerBalancer plugin;
    private final MessagesProps messages;

    public AbstractMoveCommand(PlayerBalancer plugin, CommandProps commandProps) {
        super(commandProps.getName(), commandProps.getPermission(), commandProps.getAliasesArray());
        this.messages = plugin.getSettings().getMessagesProps();
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            ServerSection target = getSection(player);

            if (target != null) {
                if (args.length == 1) {
                    try {
                        int number = Integer.parseInt(args[0]);
                        if (number <= 0) {
                            MessageUtils.send(player, messages.getInvalidInputMessage());
                        } else if (number > target.getServers().size()) {
                            MessageUtils.send(player, messages.getInvalidInputMessage());
                        } else {
                            ServerInfo server = Iterables.get(target.getServers(), number - 1);
                            ConnectionIntent.direct(plugin, player, server, (response, throwable) -> {});
                        }
                    } catch (NumberFormatException e) {
                        MessageUtils.send(player, messages.getInvalidInputMessage());
                    }
                } else {
                    ConnectionIntent.simple(plugin, player, target);
                }
            }
        } else {
            sender.sendMessage(new ComponentBuilder("This command can only be executed by a player").color(ChatColor.RED).create());
        }
    }

    public abstract ServerSection getSection(ProxiedPlayer player);
}
