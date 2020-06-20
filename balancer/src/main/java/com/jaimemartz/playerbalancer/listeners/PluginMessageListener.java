package com.jaimemartz.playerbalancer.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.helper.PlayerLocker;
import com.jaimemartz.playerbalancer.ping.ServerStatus;
import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.jaimemartz.playerbalancer.PlayerBalancer.PB_CHANNEL;

public class PluginMessageListener implements Listener {
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

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals(PB_CHANNEL) && event.getSender() instanceof Server) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String request = in.readUTF();
            ServerInfo sender = ((Server) event.getSender()).getInfo();

            switch (request) {
                case "Connect": {
                    if (event.getReceiver() instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
                        ServerSection section = plugin.getSectionManager().getByName(in.readUTF());

                        if (section == null)
                            break;

                        ConnectionIntent.simple(plugin, player, section);
                    }
                    break;
                }

                case "ConnectOther": {
                    ProxiedPlayer player = plugin.getProxy().getPlayer(in.readUTF());

                    if (player == null)
                        break;

                    ServerSection section = plugin.getSectionManager().getByName(in.readUTF());

                    if (section == null)
                        break;

                    ConnectionIntent.simple(plugin, player, section);
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

                    sender.sendData(PB_CHANNEL, stream.toByteArray());
                    break;
                }

                case "GetSectionByServer": {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    ServerInfo server = plugin.getProxy().getServerInfo(in.readUTF());

                    if (server == null)
                        break;

                    ServerSection section = plugin.getSectionManager().getByServer(server);

                    if (section == null)
                        break;

                    try {
                        String output = gson.toJson(section);
                        out.writeUTF("GetSectionByServer");
                        out.writeUTF(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sender.sendData(PB_CHANNEL, stream.toByteArray());
                    break;
                }

                case "GetSectionOfPlayer": {
                    if (event.getReceiver() instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
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

                        sender.sendData(PB_CHANNEL, stream.toByteArray());
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
                                (integer, serverInfo) -> integer + serverInfo.getPlayers().size(),
                                Integer::sum));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sender.sendData(PB_CHANNEL, stream.toByteArray());
                    break;
                }

                case "GetServerStatus": {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    ServerInfo server = plugin.getProxy().getServerInfo(in.readUTF());
                    if (server == null)
                        break;

                    ServerStatus status = plugin.getStatusManager().getStatus(server);

                    try {
                        String output = gson.toJson(status);
                        out.writeUTF("GetServerStatus");
                        out.writeUTF(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sender.sendData(PB_CHANNEL, stream.toByteArray());
                }

                case "ClearPlayerBypass": {
                    if (event.getReceiver() instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
                        PlayerLocker.unlock(player);
                    }
                    break;
                }

                case "SetPlayerBypass": {
                    if (event.getReceiver() instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
                        PlayerLocker.lock(player);
                    }
                    break;
                }

                case "BypassConnect": {
                    if (event.getReceiver() instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

                        ServerInfo server = plugin.getProxy().getServerInfo(in.readUTF());
                        if (server == null)
                            break;

                        ConnectionIntent.direct(
                                plugin,
                                player,
                                server,
                                null
                        );
                    }
                    break;
                }

                case "FallbackPlayer": {
                    if (event.getReceiver() instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
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
                    ProxiedPlayer player = plugin.getProxy().getPlayer(in.readUTF());

                    if (player == null)
                        break;

                    ServerSection target = plugin.getFallbackCommand().getSection(player);

                    if (target == null)
                        break;

                    ConnectionIntent.simple(
                            plugin,
                            player,
                            target
                    );

                    break;
                }
            }
        }
    }
}

