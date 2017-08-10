package com.jaimemartz.playerbalancer.commands;

import ch.jalu.configme.SettingsManager;
import com.google.common.collect.Iterables;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.settings.beans.MapBean;
import com.jaimemartz.playerbalancer.settings.types.CommandProperties;
import com.jaimemartz.playerbalancer.settings.types.GeneralProperties;
import com.jaimemartz.playerbalancer.settings.types.MessageProperties;
import com.jaimemartz.playerbalancer.settings.types.SectionsHolder;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import javax.inject.Inject;
import java.util.concurrent.Callable;

public class FallbackCommand extends Command {
    @Inject
    private SettingsManager settings;

    @Inject
    private SectionsHolder sections;

    @Inject
    public FallbackCommand(SettingsManager settings) {
        super(
                settings.getProperty(CommandProperties.COMMAND).getName(),
                settings.getProperty(CommandProperties.COMMAND).getPermission(),
                Iterables.toArray(settings.getProperty(CommandProperties.COMMAND).getAliases(), String.class)
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            Callable<ServerSection> callable = () -> {
                ServerSection current = sections.getByPlayer(player);

                if (current != null) {
                    if (settings.getProperty(CommandProperties.IGNORED_SECTIONS).contains(current.getName())) {
                        MessageUtils.send(player, settings.getProperty(MessageProperties.UNAVAILABLE_SERVER));
                        return null;
                    }

                    MapBean rules = settings.getProperty(CommandProperties.RULES);
                    String bind = rules.getMap().get(current.getName());
                    ServerSection target = sections.getByName(bind);

                    if (target == null) {
                        if (current.getParent() != null) {
                            target = current.getParent();
                        } else {
                            MessageUtils.send(player, settings.getProperty(MessageProperties.UNAVAILABLE_SERVER));
                            return null;
                        }
                    }

                    if (settings.getProperty(CommandProperties.RESTRICTED)) {
                        if (current.getPosition() >= 0 && target.getPosition() < 0) {
                            MessageUtils.send(player, settings.getProperty(MessageProperties.UNAVAILABLE_SERVER));
                            return null;
                        }
                    }

                    return target;
                } else {
                    if (settings.getProperty(GeneralProperties.FALLBACK_PRINCIPAL)) {
                        return sections.getPrincipal();
                    }
                }

                return null;
            };

            try {
                ServerSection target = callable.call();
                if (target != null) {
                    if (args.length == 1) {
                        try {
                            int number = Integer.parseInt(args[0]);
                            if (number <= 0) {
                                MessageUtils.send(player, ConfigEntries.INVALID_INPUT_MESSAGE.get());
                            } else if (number > target.getServers().size()) {
                                MessageUtils.send(player, ConfigEntries.FAILURE_MESSAGE.get());
                            } else {
                                ServerInfo server = target.getSortedServers().get(number - 1);
                                ConnectionIntent.direct(plugin, player, server, (response, throwable) -> {});
                            }
                        } catch (NumberFormatException e) {
                            MessageUtils.send(player, ConfigEntries.INVALID_INPUT_MESSAGE.get());
                        }
                    } else {
                        ConnectionIntent.simple(plugin, player, target);
                    }
                }
            } catch (Exception e) {
                //Nothing to do
            }
        } else {
            sender.sendMessage(new ComponentBuilder("This command can only be executed by a player").color(ChatColor.RED).create());
        }
    }
}
