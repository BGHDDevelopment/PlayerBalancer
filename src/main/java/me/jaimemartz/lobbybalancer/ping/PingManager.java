package me.jaimemartz.lobbybalancer.ping;

import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static me.jaimemartz.lobbybalancer.LobbyBalancer.printStartupInfo;

public class PingManager {
    private final LobbyBalancer plugin;
    private boolean stopped = true;
    private PingTacticType tactic;
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
        tactic = PingTacticType.valueOf((ConfigEntries.SERVER_CHECK_MODE.get()).toUpperCase());
        printStartupInfo(String.format("Starting the ping task, the interval is %s", ConfigEntries.SERVER_CHECK_INTERVAL.get()));
        task = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            for (ServerInfo server : plugin.getProxy().getServers().values()) {
                if (stopped) break;
                if (server != null) {
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
        tactic.ping(server, new PingCallback() {
            @Override
            public void onPong(ServerStatus status) {
                if (ConfigEntries.SERVER_CHECK_PRINT_INFO.get()) {
                    plugin.getLogger().info(String.format(
                            "Tracking server %s, status: [Description: \"%s\", Online Players: %s, Maximum Players: %s, Accessible: %s]",
                            server.getName(), status.getDescription(), status.getOnlinePlayers(), status.getMaximumPlayers(), status.isAccessible())
                    );
                }
                storage.put(server, status);
            }
        }, plugin);
    }

    public ServerStatus getStatus(ServerInfo server) {
        if (stopped) {
            return new ServerStatus(server.getMotd(), server.getPlayers().size(), Integer.MAX_VALUE);
        } else {
            return storage.get(server);
        }
    }
}
