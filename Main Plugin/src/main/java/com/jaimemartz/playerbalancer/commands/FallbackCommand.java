package com.jaimemartz.playerbalancer.commands;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.settings.props.MessagesProps;
import com.jaimemartz.playerbalancer.settings.props.features.FallbackCommandProps;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FallbackCommand extends AbstractMoveCommand {
    private final PlayerBalancer plugin;
    private final MessagesProps messages;
    private final FallbackCommandProps props;

    public FallbackCommand(PlayerBalancer plugin) {
        super(plugin, plugin.getSettings().getFallbackCommandProps().getCommand());
        this.props = plugin.getSettings().getFallbackCommandProps();
        this.messages = plugin.getSettings().getMessagesProps();
        this.plugin = plugin;
    }

    @Override
    public ServerSection getSection(ProxiedPlayer player) {
        ServerSection current = plugin.getSectionManager().getByPlayer(player);

        if (current != null) {
            if (props.getExcludedSections().contains(current.getName())) {
                MessageUtils.send(player, messages.getUnavailableServerMessage());
                return null;
            }

            ServerSection target = plugin.getSectionManager().getBind(props.getRules(), current)
                    .orElse(current.getParent());
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
            if (plugin.getSettings().getBalancerProps().isDefaultPrincipal()) {
                return plugin.getSectionManager().getPrincipal();
            } else {
                MessageUtils.send(player, messages.getUnavailableServerMessage());
            }
        }

        return null;
    }
}
