package com.jaimemartz.playerbalancer.ping;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatusManager {
    private final PlayerBalancer plugin;
    private boolean stopped = true;
    private PingTactic tactic;
    private ScheduledTask task;
    private final Map<ServerInfo, ServerStatus> storage = new HashMap<>();

    public StatusManager(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (task != null) {
            stop();
        }

        stopped = false;
        tactic = plugin.getSettings().getServerCheckerProps().getTactic();
        plugin.getLogger().info(String.format("Starting the ping task, the interval is %s",
                plugin.getSettings().getServerCheckerProps().getInterval()));

        task = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            storage.forEach((k, v) -> v.setOutdated(true));

            for (ServerSection section : plugin.getSectionManager().getSections().values()) {
                for (ServerInfo server : section.getMappedServers()) {
                    if (stopped) {
                        break;
                    }

                    if (getStatus(server).isOutdated()) {
                        update(server);
                    }
                }
            }

        }, 0L, plugin.getSettings().getServerCheckerProps().getInterval(), TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
            stopped = true;
        }
    }

    private void update(ServerInfo server) {
        tactic.ping(server, (status, throwable) -> {
            if (status == null) {
                status = new ServerStatus();
            }

            if (plugin.getSettings().getServerCheckerProps().isDebug()) {
                plugin.getLogger().info(String.format("Updated server %s, status: [Description: \"%s\", Online Players: %s, Maximum Players: %s, Accessible: %s]",
                        server.getName(), status.getDescription(), status.getOnline(), status.getMaximum(), status.isAccessible(plugin, null)
                ));
            }

            status.setOutdated(false);
            storage.put(server, status);
        }, plugin);
    }

    public ServerStatus getStatus(ServerInfo server) {
        ServerStatus status = storage.get(server);

        if (status == null) {
            return new ServerStatus(server);
        } else {
            return status;
        }
    }
}
