package com.jaimemartz.playerbalancer.velocity.ping;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.jaimemartz.playerbalancer.velocity.settings.props.features.ServerCheckerProps;
import com.jaimemartz.playerbalancer.velocity.utils.RegExUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class StatusManager {
    private final PlayerBalancer plugin;
    private final ServerCheckerProps props;

    private boolean stopped = true;
    private PingTactic tactic;
    private ScheduledTask task;

    private final Map<ServerInfo, ServerStatus> storage = new HashMap<>();
    private final Map<ServerInfo, Boolean> overriders = new HashMap<>();

    public StatusManager(PlayerBalancer plugin) {
        this.props = plugin.getSettings().getFeaturesProps().getServerCheckerProps();
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

        task = plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {
            storage.forEach((k, v) -> v.setOutdated(true));

            for (ServerSection section : plugin.getSectionManager().getSections().values()) {
                for (RegisteredServer server : section.getServers()) {
                    if (stopped) {
                        break;
                    }

                    if (getStatus(server.getServerInfo()).isOutdated()) {
                        update(server);
                    }
                }
            }
        }).repeat(props.getInterval(), TimeUnit.MILLISECONDS).schedule();
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
            stopped = true;
        }
    }

    private void update(RegisteredServer server) {
        tactic.ping(server, (status) -> {
            if (status == null) {
                status = new ServerStatus();
            }

            if (props.isDebug()) {
                plugin.getLogger().info(String.format(
                        "Updated server %s, status: [Description: \"%s\", Players: %s, Maximum Players: %s, Online: %s]",
                        server.getServerInfo().getName(), status.getDescription(), status.getPlayers(), status.getMaximum(), status.isOnline()
                ));
            }

            status.setOutdated(false);
            storage.put(server.getServerInfo(), status);
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
        Boolean override = overriders.get(server);

        if (override != null) {
            return override;
        }

        ServerStatus status = getStatus(server);

        if (!status.isOnline()) {
            return false;
        }

        for (String pattern : props.getMarkerDescs()) {
            if (RegExUtils.matches(LegacyComponentSerializer.legacyAmpersand().serialize(status.getDescription()), pattern)) {
                return false;
            }
        }
        return true;
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getIdentifier().equals(PlayerBalancer.PB_CHANNEL) && event.getSource() instanceof ServerConnection) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String request = in.readUTF();
            ServerInfo sender = ((ServerConnection) event.getSource()).getServerInfo();

            switch (request) {
                case "ClearStatusOverride": {
                    Optional<RegisteredServer> server = plugin.getProxyServer().getServer(in.readUTF());

                    if (!server.isPresent())
                        break;

                    overriders.remove(server.get().getServerInfo());
                    break;
                }

                case "SetStatusOverride": {
                    Optional<RegisteredServer> server = plugin.getProxyServer().getServer(in.readUTF());

                    if (!server.isPresent())
                        break;

                    overriders.put(server.get().getServerInfo(), in.readBoolean());
                    break;
                }
            }
        }
    }
}
