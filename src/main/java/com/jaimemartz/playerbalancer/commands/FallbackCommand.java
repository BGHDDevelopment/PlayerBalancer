package com.jaimemartz.playerbalancer.commands;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.settings.MainSettings;
import com.jaimemartz.playerbalancer.settings.props.MessagesProps;
import com.jaimemartz.playerbalancer.settings.shared.CommandProps;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class FallbackCommand extends Command {
    protected final PlayerBalancer plugin;
    protected final MessagesProps messages;
    protected final MainSettings settings;

    public FallbackCommand(PlayerBalancer plugin, CommandProps props) {
        super(props.getName(), props.getPermission(), props.getAliasesArray());
        this.settings = plugin.getSettings();
        this.messages = settings.getMessagesProps();
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            ServerSection target = this.getSection(player);

            if (target != null) {
                if (args.length == 1) {
                    try {
                        int number = Integer.parseInt(args[0]);
                        if (number <= 0) {
                            MessageUtils.send(player, messages.getInvalidInputMessage());
                        } else if (number > target.getMappedServers().size()) {
                            MessageUtils.send(player, messages.getFailureMessage());
                        } else {
                            ServerInfo server = target.getSortedServers().get(number - 1);
                            ConnectionIntent.direct(plugin, player, server, (response, throwable) -> {
                            });
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

    public ServerSection getSection(ProxiedPlayer player) {
        ServerSection current = plugin.getSectionManager().getByPlayer(player);

        if (current != null) {
            if (settings.getFallbackCommandProps().getExcludedSections().contains(current.getName())) {
                MessageUtils.send(player, messages.getUnavailableServerMessage());
                return null;
            }

            String bind = settings.getFallbackCommandProps().getRules().get(current.getName());
            ServerSection target = plugin.getSectionManager().getByName(bind);

            if (target == null) {
                if (current.getParent() != null) {
                    target = current.getParent();
                } else {
                    MessageUtils.send(player, messages.getUnavailableServerMessage());
                    return null;
                }
            }

            if (settings.getFallbackCommandProps().isRestrictive()) {
                if (current.getPosition() >= 0 && target.getPosition() < 0) {
                    MessageUtils.send(player, messages.getUnavailableServerMessage());
                    return null;
                }
            }

            return target;
        } else {
            if (settings.getGeneralProps().isFallbackPrincipal()) {
                return plugin.getSectionManager().getPrincipal();
            }
        }

        return null;
    }
}
