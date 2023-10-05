package com.jaimemartz.playerbalancer.velocity.commands;

import com.google.common.collect.Iterables;
import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.jaimemartz.playerbalancer.velocity.settings.props.MessagesProps;
import com.jaimemartz.playerbalancer.velocity.settings.props.features.FallbackCommandProps;
import com.jaimemartz.playerbalancer.velocity.utils.MessageUtils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

import static com.jaimemartz.playerbalancer.velocity.utils.MessageUtils.safeNull;

public class FallbackCommand implements SimpleCommand {
    private final PlayerBalancer plugin;
    private final MessagesProps messages;
    protected final FallbackCommandProps props;

    /**
     * Constructor for `fallback-command`
     */
    public FallbackCommand(PlayerBalancer plugin) {
        this.messages = plugin.getSettings().getMessagesProps();
        this.props = plugin.getSettings().getFeaturesProps().getFallbackCommandProps();
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (source instanceof Player) {
            Player player = (Player) source;
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
                            ServerInfo server = Iterables.get(target.getServers(), number - 1).getServerInfo();
                            ConnectionIntent.direct(plugin, player, server, null);
                        }
                    } catch (NumberFormatException e) {
                        MessageUtils.send(player, messages.getInvalidInputMessage());
                    }
                } else {
                    if (props.isPreventSameSection()) {
                        Optional<ServerConnection> current = player.getCurrentServer();
                        if (current.isPresent()) {
                            if (target.getServers().contains(current.get().getServer())) {
                                MessageUtils.send(player, plugin.getSettings().getMessagesProps().getSameSectionMessage(),
                                        (str) -> str.replace("{server}", current.get().getServerInfo().getName())
                                                .replace("{section}", target.getName())
                                                .replace("{alias}", safeNull(target.getProps().getAlias()))
                                );
                                return;
                            }
                        }
                    }

                    ConnectionIntent.simple(plugin, player, target);
                }
            }
        } else {
            source.sendMessage(Component.text("This command can only be executed by a player", NamedTextColor.RED));
        }
    }

    public ServerSection getSection(Player player) {
        ServerSection current = plugin.getSectionManager().getByPlayer(player);

        if (current != null) {
            if (props.getExcludedSections().contains(current.getName())) {
                MessageUtils.send(player, messages.getUnavailableServerMessage());
                return null;
            }

            ServerSection target = current.getParent();

            String bindName = props.getRules().get(current.getName());
            if (bindName != null) {
                ServerSection bind = plugin.getSectionManager().getByName(bindName);
                if (bind != null) {
                    target = bind;
                }
            }

            if (target == null) {
                MessageUtils.send(player, messages.getUnavailableServerMessage());
                return null;
            }

            if (props.isRestrictive()) {
                if (current.getPosition() >= 0 && target.getPosition() < 0) {
                    MessageUtils.send(player, messages.getUnavailableServerMessage());
                    return null;
                }
            }

            return target;
        } else {
            if (plugin.getSettings().getFeaturesProps().getBalancerProps().isDefaultPrincipal()) {
                return plugin.getSectionManager().getPrincipal();
            } else {
                MessageUtils.send(player, messages.getUnavailableServerMessage());
            }
        }

        return null;
    }
}
