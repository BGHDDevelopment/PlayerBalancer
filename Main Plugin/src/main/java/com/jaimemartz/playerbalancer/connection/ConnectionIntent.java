package com.jaimemartz.playerbalancer.connection;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.manager.PlayerLocker;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//TODO I don't like this, improve it
public abstract class ConnectionIntent {
    protected final PlayerBalancer plugin;
    protected final ProxiedPlayer player;
    protected final ServerSection section;

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ProviderType provider, ServerSection section, List<ServerInfo> servers) {
        this.plugin = plugin;
        this.player = player;
        this.section = section;

        MessageUtils.send(player, plugin.getSettings().getMessagesProps().getConnectingMessage(),
                (str) -> str.replace("{section}", section.getName())
        );

        //Prevents removing servers from the section
        if (servers == section.getServers()) {
            throw new IllegalStateException("The servers list parameter is the same reference, this cannot happen");
        }

        //Prevents connections to the same server
        Server current = player.getServer();
        if (current != null) {
            servers.remove(current.getInfo());
        }

        if (section.getImplicitProvider() != ProviderType.NONE) {
            ServerInfo target = this.fetchServer(player, section, provider, servers);
            if (target != null) {
                this.connect(target, (response, throwable) -> {
                    if (response) { //only if the connect has been executed correctly
                        MessageUtils.send(player, plugin.getSettings().getMessagesProps().getConnectedMessage(),
                                (str) -> str.replace("{server}", target.getName())
                        );
                    }
                });
            } else {
                MessageUtils.send(player, plugin.getSettings().getMessagesProps().getFailureMessage());
            }
        }
    }

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ServerSection section) {
        this(plugin, player, section.getImplicitProvider(), section);
    }

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ProviderType type, ServerSection section) {
        this(plugin, player, type, section, new ArrayList<>(section.getServers()));
    }

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ServerSection section, List<ServerInfo> servers) {
        this(plugin, player, section.getImplicitProvider(), section, servers);
    }

    private ServerInfo fetchServer(ProxiedPlayer player, ServerSection section, ProviderType provider, List<ServerInfo> servers) {
        if (plugin.getSectionManager().isReiterative(section)) {
            if (ServerAssignRegistry.hasAssignedServer(player, section)) {
                ServerInfo target = ServerAssignRegistry.getAssignedServer(player, section);
                if (plugin.getStatusManager().isAccessible(target)) {
                    return target;
                } else {
                    ServerAssignRegistry.revokeTarget(player, section);
                }
            }
        }

        int intents = plugin.getSettings().getFeaturesProps().getServerCheckerProps().getAttempts();
        for (int intent = 1; intent <= intents; intent++) {
            if (servers.size() == 0) return null;

            ServerInfo target = provider.requestTarget(plugin, section, servers, player);
            if (target == null) continue;

            if (plugin.getStatusManager().isAccessible(target)) {
                return target;
            } else {
                servers.remove(target);
            }
        }

        return null;
    }

    public abstract void connect(ServerInfo server, Callback<Boolean> callback);

    //TODO Create this as a type
    public static void simple(PlayerBalancer plugin, ProxiedPlayer player, ServerSection section) {
        new ConnectionIntent(plugin, player, section) {
            @Override
            public void connect(ServerInfo server, Callback<Boolean> callback) {
                ConnectionIntent.direct(plugin, player, server, callback);
            }
        };
    }

    //TODO Create this as a type
    public static void direct(PlayerBalancer plugin, ProxiedPlayer player, ServerInfo server, Callback<Boolean> callback) {
        PlayerLocker.lock(player);
        player.connect(server, (result, throwable) -> {
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                PlayerLocker.unlock(player);
            }, 5, TimeUnit.SECONDS);

            if (callback != null)
                callback.done(result, throwable);
        });
    }
}