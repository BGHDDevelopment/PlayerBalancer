package com.jaimemartz.playerbalancer.commands;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.configuration.ConfigEntries;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.concurrent.Callable;

public class FallbackCommand extends Command {
    private final PlayerBalancer plugin;

    public FallbackCommand(PlayerBalancer plugin) {
        super(ConfigEntries.FALLBACK_COMMAND_NAME.get(), ConfigEntries.FALLBACK_COMMAND_PERMISSION.get(), (ConfigEntries.FALLBACK_COMMAND_ALIASES.get().stream()).toArray(String[]::new));
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            Callable<ServerSection> callable = () -> {
                ServerSection current = plugin.getSectionManager().getByPlayer(player);

                if (current != null) {
                    if ((ConfigEntries.FALLBACK_COMMAND_IGNORED_SECTIONS.get()).contains(current.getName())) {
                        MessageUtils.send(player, ConfigEntries.UNAVAILABLE_MESSAGE.get());
                        return null;
                    }

                    Configuration rules = plugin.getConfigHandle().getSection("settings.fallback-command.rules");
                    String bind = rules.getString(current.getName());
                    ServerSection target = plugin.getSectionManager().getByName(bind);

                    if (target == null) {
                        if (current.getParent() != null) {
                            target = current.getParent();
                        } else {
                            MessageUtils.send(player, ConfigEntries.UNAVAILABLE_MESSAGE.get());
                            return null;
                        }
                    }

                    if (ConfigEntries.FALLBACK_COMMAND_RESTRICTED.get()) {
                        if (current.getPosition() >= 0 && target.getPosition() < 0) {
                            MessageUtils.send(player, ConfigEntries.UNAVAILABLE_MESSAGE.get());
                            return null;
                        }
                    }

                    return target;
                } else {
                    if (ConfigEntries.FALLBACK_PRINCIPAL_ENABLED.get()) {
                        return plugin.getSectionManager().getPrincipal();
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
                                ConnectionIntent.direct(plugin, player, server);
                            }
                        } catch (NumberFormatException e) {
                            MessageUtils.send(player, ConfigEntries.INVALID_INPUT_MESSAGE.get());
                        }
                    } else {
                        ServerSection current = plugin.getSectionManager().getByPlayer(player);
                        if (current != target) {
                            ConnectionIntent.simple(plugin, player, target);
                        } else {
                            MessageUtils.send(player, ConfigEntries.SAME_SECTION.get());
                        }
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
