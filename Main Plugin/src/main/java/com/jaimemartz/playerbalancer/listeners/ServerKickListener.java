package com.jaimemartz.playerbalancer.listeners;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.helper.PlayerLocker;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.settings.props.MessagesProps;
import com.jaimemartz.playerbalancer.settings.props.features.KickHandlerProps;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.TimeUnit;

public class ServerKickListener implements Listener {
    private final KickHandlerProps props;
    private final MessagesProps messages;
    private final PlayerBalancer plugin;

    public ServerKickListener(PlayerBalancer plugin) {
        this.props = plugin.getSettings().getFeaturesProps().getKickHandlerProps();
        this.messages = plugin.getSettings().getMessagesProps();
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo from = event.getKickedFrom();

        boolean matches = false;
        String reason = TextComponent.toPlainText(event.getKickReasonComponent());

        for (String string : props.getReasons()) {
            if (reason.matches(string)) {
                matches = true;
                break;
            }
        }

        if (props.isInverted()) {
            matches = !matches;
        }

        if (props.isDebug()) {
            plugin.getLogger().info(String.format("The player %s got kicked from %s, reason: \"%s\". Matched reasons: %s",
                    player.getName(),
                    from.getName(),
                    reason,
                    matches
            ));
        }

        if (!matches)
            return;

        ServerSection section = getSection(player, from);

        if (section == null)
            return;

        ConnectionIntent intent = new ConnectionIntent(plugin, player, section) {
            @Override
            public void connect(ServerInfo server, Callback<Boolean> callback) {
                PlayerLocker.lock(player);
                event.setCancelled(true);
                event.setCancelServer(server);
                MessageUtils.send(player, messages.getKickMessage(), (str) -> str
                        .replace("{reason}", reason)
                        .replace("{from}", from.getName())
                        .replace("{to}", server.getName()));
                plugin.getProxy().getScheduler().schedule(plugin, () -> {
                    PlayerLocker.unlock(player);
                }, 5, TimeUnit.SECONDS);
                callback.done(true, null);
            }
        };

        intent.getExclusions().add(from);
        intent.execute();
    }

    private ServerSection getSection(ProxiedPlayer player, ServerInfo from) {
        if (player.getServer() == null) {
            if (props.isForcePrincipal()) {
                return plugin.getSectionManager().getPrincipal();
            } else {
                return null;
            }
        }

        if (!player.getServer().getInfo().equals(from)) {
            return null;
        }

        ServerSection current = plugin.getSectionManager().getByServer(from);

        if (current != null) {
            if (props.getExcludedSections().contains(current.getName())) {
                return null;
            }
        }

        if (current != null) {
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