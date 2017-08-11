package com.jaimemartz.playerbalancer.commands;

import com.google.common.collect.Iterables;
import com.jaimemartz.playerbalancer.settings.Settings;
import com.jaimemartz.playerbalancer.settings.types.CommandProperties;
import com.jaimemartz.playerbalancer.settings.types.SectionsHolder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import javax.inject.Inject;

public class FallbackCommand extends Command {
    @Inject
    private Settings settings;

    @Inject
    private SectionsHolder holder;

    @Inject //todo maybe make this job of the main class (initializer)
    public FallbackCommand(Settings settings) {
        super(
                settings.getProperty(CommandProperties.COMMAND).getName(),
                settings.getProperty(CommandProperties.COMMAND).getPermission(),
                Iterables.toArray(settings.getProperty(CommandProperties.COMMAND).getAliases(), String.class)
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        /*
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            Callable<ServerSection> callable = () -> {
                ServerSection current = holder.getByPlayer(player);

                if (current != null) {
                    if (settings.getProperty(CommandProperties.IGNORED_SECTIONS).contains(current.getName())) {
                        MessageUtils.send(player, settings.getProperty(MessageProperties.UNAVAILABLE_SERVER));
                        return null;
                    }

                    MapBean rules = settings.getProperty(CommandProperties.RULES);
                    String bind = rules.getMap().get(current.getName());
                    ServerSection target = holder.getByName(bind);

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
                        return holder.getPrincipal();
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
                                MessageUtils.send(player, settings.getProperty(MessageProperties.INVALID_INPUT));
                            } else if (number > target.getServers().size()) {
                                MessageUtils.send(player, settings.getProperty(MessageProperties.MISC_FAILURE));
                            } else {
                                ServerInfo server = target.getSortedServers().get(number - 1);
                                ConnectionIntent.direct(plugin, player, server, (response, throwable) -> {});
                            }
                        } catch (NumberFormatException e) {
                            MessageUtils.send(player, settings.getProperty(MessageProperties.INVALID_INPUT));
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
        */
    }
}
