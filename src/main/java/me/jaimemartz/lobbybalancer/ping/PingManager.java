package me.jaimemartz.lobbybalancer.ping;

import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.utils.FixedAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PingManager {
    private final LobbyBalancer plugin;
    private boolean stopped = true;
    private PingTactic tactic;
    private ScheduledTask task;
    private final Map<ServerInfo, ServerStatus> storage = new HashMap<>();

    public PingManager(LobbyBalancer plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (task != null) {
            stop();
        }

        stopped = false;
        tactic = PingTactic.valueOf((ConfigEntries.SERVER_CHECK_MODE.get()).toUpperCase());
        plugin.getLogger().info(String.format("Starting the ping task, the interval is %s", ConfigEntries.SERVER_CHECK_INTERVAL.get()));

        task = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            for (ServerInfo server : plugin.getProxy().getServers().values()) {
                if (stopped) {
                    break;
                }

                if (server != null) {
                    if (FixedAdapter.getFakeServers().containsKey(server.getName())) {
                        continue;
                    }

                    track(server);
                }
            }
        }, 0L, ConfigEntries.SERVER_CHECK_INTERVAL.get(), TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
            stopped = true;
        }
    }

    private void track(ServerInfo server) {
        tactic.ping(server, (status, throwable) -> {
            if (status == null) {
                status = new ServerStatus("Server Unreachable", 0, 0);
            }

            if (ConfigEntries.SERVER_CHECK_PRINT_INFO.get()) {
                plugin.getLogger().info(String.format(
                        "Tracking server %s, status: [Description: \"%s\", Online Players: %s, Maximum Players: %s, Accessible: %s]",
                        server.getName(), status.getDescription(), status.getOnlinePlayers(), status.getMaximumPlayers(), status.isAccessible()
                ));
            }

            storage.put(server, status);
        }, plugin);
    }

    public ServerStatus getStatus(ServerInfo server) {
        ServerStatus status = storage.get(server);

        if (status == null) {
            status = new ServerStatus(server.getMotd(), server.getPlayers().size(), Integer.MAX_VALUE);
        }

        return status;
    }
}
