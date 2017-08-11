package com.jaimemartz.playerbalancer.connection;

public abstract class ConnectionIntent {
    /*
    protected final PlayerBalancer plugin;
    protected final ProxiedPlayer player;
    protected final ServerSection section;

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ProviderType provider, ServerSection section, List<ServerInfo> servers) {
        this.plugin = plugin;
        this.player = player;
        this.section = section;

        MessageUtils.send(player, ConfigEntries.CONNECTING_MESSAGE.get(),
                (str) -> str.replace("{section}", section.getName())
        );

        if (servers == section.getServers()) {
            throw new IllegalStateException("The servers list parameter is the same reference, this cannot happen");
        }

        Server current = player.getServer();
        if (current != null) {
            if (section.getServers().contains(current.getInfo())) {
                MessageUtils.send(player, ConfigEntries.SAME_SECTION.get());
                return;
            }
        }

        if (section.getProvider() != ProviderType.NONE) {
            ServerInfo target = this.fetchServer(plugin, player, section, provider, servers);
            if (target != null) {
                this.connect(target, (response, throwable) -> {
                    if (response) { //only if the connect has been executed correctly
                        MessageUtils.send(player, ConfigEntries.CONNECTED_MESSAGE.get(),
                                (str) -> str.replace("{server}", target.getName())
                        );
                    }
                });
            } else {
                MessageUtils.send(player, ConfigEntries.FAILURE_MESSAGE.get());
            }
        }
    }

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ServerSection section) {
        this(plugin, player, section.getProvider(), section);
    }

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ProviderType type, ServerSection section) {
        this(plugin, player, type, section, new ArrayList<>(section.getServers()));
    }

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ServerSection section, List<ServerInfo> servers) {
        this(plugin, player, section.getProvider(), section, servers);
    }

    private ServerInfo fetchServer(PlayerBalancer plugin, ProxiedPlayer player, ServerSection section, ProviderType provider, List<ServerInfo> servers) {
        if (ConfigEntries.ASSIGN_TARGETS_ENABLED.get()) {
            if (ServerAssignRegistry.hasAssignedServer(player, section)) {
                ServerInfo target = ServerAssignRegistry.getAssignedServer(player, section);
                ServerStatus status = plugin.getStatusManager().getStatus(target);
                if (status.isAccessible()) {
                    return target;
                } else {
                    ServerAssignRegistry.revokeTarget(player, section);
                }
            }
        }

        int intents = ConfigEntries.SERVER_CHECK_ATTEMPTS.get();
        for (int intent = 1; intent <= intents; intent++) {
            if (servers.size() == 0) return null;

            ServerInfo target = provider.requestTarget(plugin, section, servers, player);
            if (target == null) continue;

            ServerStatus status = plugin.getStatusManager().getStatus(target);
            if (status.isAccessible()) {
                return target;
            } else {
                servers.remove(target);
            }
        }

        return null;
    }

    public abstract void connect(ServerInfo server, Callback<Boolean> callback);

    //todo create this as a type
    public static void simple(PlayerBalancer plugin, ProxiedPlayer player, ServerSection section) {
        new ConnectionIntent(plugin, player, section) {
            @Override
            public void connect(ServerInfo server, Callback<Boolean> callback) {
                ConnectionIntent.direct(plugin, player, server, callback);
            }
        };
    }

    //todo create this as a type
    public static void direct(PlayerBalancer plugin, ProxiedPlayer player, ServerInfo server, Callback<Boolean> callback) {
        PlayerLocker.lock(player);
        player.connect(server, (result, throwable) -> {
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                PlayerLocker.unlock(player);
            }, 5, TimeUnit.SECONDS);
            callback.done(result, throwable);
        });
    }
    */
}