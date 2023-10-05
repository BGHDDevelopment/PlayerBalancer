package com.jaimemartz.playerbalancer.velocity.connection;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.helper.PlayerLocker;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.jaimemartz.playerbalancer.velocity.utils.MessageUtils;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.jaimemartz.playerbalancer.velocity.utils.MessageUtils.safeNull;

public abstract class ConnectionIntent {
    private final PlayerBalancer plugin;
    private final Player player;
    private final ServerSection section;
    private final List<ServerInfo> exclusions;

    public ConnectionIntent(PlayerBalancer plugin, Player player, ServerSection section) {
        this.plugin = plugin;
        this.player = player;
        this.section = tryRoute(player, section);
        this.exclusions = new ArrayList<>();
    }

    public List<ServerInfo> getExclusions() {
        return exclusions;
    }

    public void execute() {
        MessageUtils.send(player, plugin.getSettings().getMessagesProps().getConnectingMessage(),
                (str) -> str.replace("{section}", section.getName())
                        .replace("{alias}", safeNull(section.getProps().getAlias()))
        );

        // Ensure a new copy of the section servers
        List<RegisteredServer> servers = new ArrayList<>(section.getServers());

        // Prevents connections to the same server
        player.getCurrentServer().ifPresent(current -> servers.remove(current.getServer()));

        if (section.getImplicitProvider() != ProviderType.NONE) {
            ServerInfo target = this.fetchServer(player, section, section.getImplicitProvider(), servers);
            if (target != null) {
                this.connect(target, (response) -> {
                    if (response) { // only if the connect has been executed correctly
                        MessageUtils.send(player, plugin.getSettings().getMessagesProps().getConnectedMessage(),
                                (str) -> str.replace("{server}", target.getName())
                                        .replace("{section}", section.getName())
                                        .replace("{alias}", safeNull(section.getProps().getAlias()))
                        );
                    }
                });
            } else {
                MessageUtils.send(player, plugin.getSettings().getMessagesProps().getFailureMessage());
            }
        }
    }

    private ServerInfo fetchServer(Player player, ServerSection section, ProviderType provider, List<RegisteredServer> servers) {
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

            RegisteredServer target = provider.requestTarget(plugin, section, servers, player);
            if (target == null) continue;

            if (plugin.getStatusManager().isAccessible(target.getServerInfo())) {
                return target.getServerInfo();
            } else {
                servers.remove(target);
            }
        }

        return null;
    }

    private ServerSection tryRoute(Player player, ServerSection section) {
        if (plugin.getSettings().getFeaturesProps().getPermissionRouterProps().isEnabled()) {
            Map<String, String> routes = plugin.getSettings().getFeaturesProps().getPermissionRouterProps().getRules().get(section.getName());
            ServerSection current = plugin.getSectionManager().getByPlayer(player);

            if (routes != null) {
                for (Map.Entry<String, String> route : routes.entrySet()) {
                    if (player.hasPermission(route.getKey())) {
                        ServerSection bind = plugin.getSectionManager().getByName(route.getValue());

                        if (bind != null) {
                            if (current == bind) {
                                break;
                            }

                            return bind;
                        }

                        break;
                    }
                }
            }
        }

        return section;
    }

    public abstract void connect(ServerInfo server, Consumer<Boolean> callback);

    public static void simple(PlayerBalancer plugin, Player player, ServerSection section) {
        new ConnectionIntent(plugin, player, section) {
            @Override
            public void connect(ServerInfo server, Consumer<Boolean> callback) {
                ConnectionIntent.direct(plugin, player, server, callback);
            }
        }.execute();
    }

    public static void direct(PlayerBalancer plugin, Player player, ServerInfo server, Consumer<Boolean> callback) {
        PlayerLocker.lock(player);
        plugin.getProxyServer().getServer(server.getName()).ifPresent((rServer) -> {
            player.createConnectionRequest(rServer).connect()
                    .whenComplete((result, throwable) -> {
                        plugin.getProxyServer().getScheduler().buildTask(plugin, () -> PlayerLocker.unlock(player))
                                .delay(5, TimeUnit.SECONDS).schedule();

                        callback.accept(result.isSuccessful());
                    });
        });
    }
}
