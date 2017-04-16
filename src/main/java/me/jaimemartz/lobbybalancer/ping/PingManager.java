package me.jaimemartz.lobbybalancer.ping;

import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PingManager {
    private boolean stopped = true;
    private PingTactic tactic;
    private ScheduledTask task;
    private final Map<ServerInfo, PingStatus> storage = new HashMap<>();

    public PingManager(LobbyBalancer plugin) {
        if (task != null) {
            stop();
        }

        stopped = false;
        tactic = PingTactic.valueOf((ConfigEntries.SERVER_CHECK_MODE.get()).toUpperCase());
        plugin.getLogger().info(String.format("Starting the ping task, the interval is %s", ConfigEntries.SERVER_CHECK_INTERVAL.get()));

        task = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            storage.forEach((k, v) -> v.setOutdated(true));

            for (ServerSection section : plugin.getSectionManager().getSections().values()) {
                for (ServerInfo server : section.getServers()) {
                    if (stopped) {
                        break;
                    }

                    if (getStatus(server).isOutdated()) {
                        update(plugin, server);
                    }
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

    private void update(LobbyBalancer plugin, ServerInfo server) {
        tactic.ping(server, (status, throwable) -> {
            if (status == null) {
                status = new PingStatus();
            }

            status.setOutdated(false);
            storage.put(server, status);
        }, plugin);
    }

    public PingStatus getStatus(ServerInfo server) {
        PingStatus status = storage.get(server);

        if (status == null) {
            return new PingStatus(server);
        } else {
            return status;
        }
    }
}
