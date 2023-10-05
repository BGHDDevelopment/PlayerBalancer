package com.jaimemartz.playerbalancer.velocity.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.velocity.helper.PlayerLocker;
import com.jaimemartz.playerbalancer.velocity.ping.ServerStatus;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;

public class PluginMessageListener {
    private final PlayerBalancer plugin;
    private final Gson gson;

    public PluginMessageListener(PlayerBalancer plugin) {
        this.plugin = plugin;
        GsonBuilder builder = new GsonBuilder();

        // Only serialize the name of ServerInfo
        builder.registerTypeAdapter(ServerInfo.class, (JsonSerializer<ServerInfo>) (server, type, context) ->
                context.serialize(server.getName())
        );

        builder.serializeNulls();
        gson = builder.create();
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getIdentifier().equals(PlayerBalancer.PB_CHANNEL) && event.getSource() instanceof ServerConnection) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String request = in.readUTF();
            ServerConnection serverConnection = ((ServerConnection) event.getSource());
            ServerInfo sender = serverConnection.getServerInfo();

            switch (request) {
                case "Connect": {
                    if (event.getTarget() instanceof Player) {
                        Player player = (Player) event.getTarget();
                        ServerSection section = plugin.getSectionManager().getByName(in.readUTF());

                        if (section == null)
                            break;

                        ConnectionIntent.simple(plugin, player, section);
                    }
                    break;
                }

                case "ConnectOther": {
                    Optional<Player> player = plugin.getProxyServer().getPlayer(in.readUTF());

                    if (!player.isPresent())
                        break;

                    ServerSection section = plugin.getSectionManager().getByName(in.readUTF());

                    if (section == null)
                        break;

                    ConnectionIntent.simple(plugin, player.get(), section);
                    break;
                }

                case "GetSectionByName": {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    ServerSection section = plugin.getSectionManager().getByName(in.readUTF());

                    if (section == null)
                        break;

                    try {
                        String output = gson.toJson(section);
                        out.writeUTF("GetSectionByName");
                        out.writeUTF(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    serverConnection.sendPluginMessage(PlayerBalancer.PB_CHANNEL, stream.toByteArray());
                    break;
                }

                case "GetSectionByServer": {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    Optional<RegisteredServer> server = plugin.getProxyServer().getServer(in.readUTF());

                    if (!server.isPresent())
                        break;

                    ServerSection section = plugin.getSectionManager().getByServer(server.get());

                    if (section == null)
                        break;

                    try {
                        String output = gson.toJson(section);
                        out.writeUTF("GetSectionByServer");
                        out.writeUTF(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    serverConnection.sendPluginMessage(PlayerBalancer.PB_CHANNEL, stream.toByteArray());
                    break;
                }

                case "GetSectionOfPlayer": {
                    if (event.getTarget() instanceof Player) {
                        Player player = (Player) event.getTarget();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(stream);

                        ServerSection section = plugin.getSectionManager().getByPlayer(player);

                        if (section == null)
                            break;

                        try {
                            String output = gson.toJson(section);
                            out.writeUTF("GetSectionOfPlayer");
                            out.writeUTF(output);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        serverConnection.sendPluginMessage(PlayerBalancer.PB_CHANNEL, stream.toByteArray());
                    }
                    break;
                }

                case "GetSectionPlayerCount": {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    ServerSection section = plugin.getSectionManager().getByName(in.readUTF());

                    if (section == null)
                        break;

                    try {
                        out.writeUTF("GetSectionPlayerCount");
                        out.writeInt(section.getServers().stream().reduce(
                                0,
                                (integer, serverInfo) -> integer + serverInfo.getPlayersConnected().size(),
                                Integer::sum));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    serverConnection.sendPluginMessage(PlayerBalancer.PB_CHANNEL, stream.toByteArray());
                    break;
                }

                case "GetServerStatus": {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    Optional<RegisteredServer> server = plugin.getProxyServer().getServer(in.readUTF());
                    if (!server.isPresent())
                        break;

                    ServerStatus status = plugin.getStatusManager().getStatus(server.get().getServerInfo());

                    try {
                        String output = gson.toJson(status);
                        out.writeUTF("GetServerStatus");
                        out.writeUTF(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    serverConnection.sendPluginMessage(PlayerBalancer.PB_CHANNEL, stream.toByteArray());
                }

                case "ClearPlayerBypass": {
                    if (event.getTarget() instanceof Player) {
                        Player player = (Player) event.getTarget();
                        PlayerLocker.unlock(player);
                    }
                    break;
                }

                case "SetPlayerBypass": {
                    if (event.getTarget() instanceof Player) {
                        Player player = (Player) event.getTarget();
                        PlayerLocker.lock(player);
                    }
                    break;
                }

                case "BypassConnect": {
                    if (event.getTarget() instanceof Player) {
                        Player player = (Player) event.getTarget();

                        Optional<RegisteredServer> server = plugin.getProxyServer().getServer(in.readUTF());
                        if (!server.isPresent())
                            break;

                        ConnectionIntent.direct(
                                plugin,
                                player,
                                server.get().getServerInfo(),
                                null
                        );
                    }
                    break;
                }

                case "FallbackPlayer": {
                    if (event.getTarget() instanceof Player) {
                        Player player = (Player) event.getTarget();
                        ServerSection target = plugin.getFallbackCommand().getSection(player);

                        if (target == null)
                            break;

                        ConnectionIntent.simple(
                                plugin,
                                player,
                                target
                        );
                    }

                    break;
                }

                case "FallbackOtherPlayer": {
                    Optional<Player> player = plugin.getProxyServer().getPlayer(in.readUTF());

                    if (!player.isPresent())
                        break;

                    ServerSection target = plugin.getFallbackCommand().getSection(player.get());

                    if (target == null)
                        break;

                    ConnectionIntent.simple(
                            plugin,
                            player.get(),
                            target
                    );

                    break;
                }
            }
        }
    }
}

