package com.jaimemartz.playerbalancer.velocity.listeners;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.velocity.helper.PlayerLocker;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.jaimemartz.playerbalancer.velocity.settings.props.MessagesProps;
import com.jaimemartz.playerbalancer.velocity.settings.props.features.KickHandlerProps;
import com.jaimemartz.playerbalancer.velocity.utils.MessageUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ServerKickListener {
    private final KickHandlerProps props;
    private final MessagesProps messages;
    private final PlayerBalancer plugin;

    public ServerKickListener(PlayerBalancer plugin) {
        this.props = plugin.getSettings().getFeaturesProps().getKickHandlerProps();
        this.messages = plugin.getSettings().getMessagesProps();
        this.plugin = plugin;
    }

    @Subscribe
    public void onKick(KickedFromServerEvent event) {
        Player player = event.getPlayer();
        RegisteredServer from = event.getServer();

        boolean matches = false;
        String reason = PlainTextComponentSerializer.plainText().serialize(event.getServerKickReason().orElse(Component.empty()));

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
                    player.getUsername(),
                    from.getServerInfo().getName(),
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
            public void connect(ServerInfo server, Consumer<Boolean> callback) {
                PlayerLocker.lock(player);
                Optional<RegisteredServer> registeredServer = plugin.getProxyServer().getServer(server.getName());
                if (registeredServer.isPresent()) {
                    event.setResult(KickedFromServerEvent.RedirectPlayer.create(registeredServer.get()));
                    MessageUtils.send(player, messages.getKickMessage(), (str) -> str
                            .replace("{reason}", reason)
                            .replace("{from}", from.getServerInfo().getName())
                            .replace("{to}", server.getName()));
                    callback.accept(true);
                }
                plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {
                    PlayerLocker.unlock(player);
                }).delay(5, TimeUnit.SECONDS).schedule();
            }
        };

        intent.getExclusions().add(from.getServerInfo());
        intent.execute();
    }

    private ServerSection getSection(Player player, RegisteredServer from) {
        Optional<ServerConnection> serverConnection = player.getCurrentServer();
        if (!serverConnection.isPresent()) {
            if (props.isForcePrincipal()) {
                return plugin.getSectionManager().getPrincipal();
            } else {
                return null;
            }
        }

        if (!serverConnection.get().getServer().equals(from)) {
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