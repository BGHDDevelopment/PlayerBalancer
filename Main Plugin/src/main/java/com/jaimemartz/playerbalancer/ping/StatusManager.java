package com.jaimemartz.playerbalancer.ping;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.settings.props.features.ServerCheckerProps;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatusManager implements Listener {
    private final PlayerBalancer plugin;
    private final ServerCheckerProps props;

    private boolean stopped = true;
    private PingTactic tactic;
    private ScheduledTask task;

    private final Map<ServerInfo, ServerStatus> storage = new HashMap<>();
    private final Map<ServerInfo, Boolean> overriders = new HashMap<>();

    public StatusManager(PlayerBalancer plugin) {
        this.props = plugin.getSettings().getServerCheckerProps();
        this.plugin = plugin;
    }

    public void start() {
        if (task != null) {
            stop();
        }

        stopped = false;
        tactic = props.getTactic();
        plugin.getLogger().info(String.format("Starting the ping task, the interval is %s",
                props.getInterval()
        ));

        task = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            storage.forEach((k, v) -> v.setOutdated(true));

            for (ServerSection section : plugin.getSectionManager().getSections().values()) {
                for (ServerInfo server : section.getServers()) {
                    if (stopped) {
                        break;
                    }

                    if (getStatus(server).isOutdated()) {
                        update(server);
                    }
                }
            }
        }, 0L, props.getInterval(), TimeUnit.MILLISECONDS);
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

            if (props.isDebug()) {
                plugin.getLogger().info(String.format(
                        "Updated server %s, status: [Description: \"%s\", Players: %s, Maximum Players: %s, Online: %s]",
                        server.getName(), status.getDescription(), status.getPlayers(), status.getMaximum(), status.isOnline()
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

    public boolean isAccessible(ServerInfo server) {
        if (overriders.containsKey(server)) {
            return overriders.get(server);
        }

        ServerStatus status = getStatus(server);

        if (!status.isOnline()) {
            return false;
        }

        for (String pattern : props.getMarkerDescs()) {
            if (status.getDescription().matches(pattern)) {
                return false;
            }
        }

        return true;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals("PlayerBalancer") && event.getSender() instanceof Server) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String request = in.readUTF();
            ServerInfo sender = ((Server) event.getSender()).getInfo();

            switch (request) {
                case "ClearStatusOverride": {
                    ServerInfo server = plugin.getProxy().getServerInfo(in.readUTF());

                    if (server == null)
                        break;

                    overriders.remove(server);
                    break;
                }

                case "SetStatusOverride": {
                    ServerInfo server = plugin.getProxy().getServerInfo(in.readUTF());

                    if (server == null)
                        break;

                    overriders.put(server, in.readBoolean());
                    break;
                }
            }
        }
    }
}
