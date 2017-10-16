package com.jaimemartz.playerbalancer.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.*;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.utils.ServerInfoAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.stream.Collectors;

public class PluginMessageListener implements Listener {
    private final PlayerBalancer plugin;
    private final Gson gson;

    public PluginMessageListener(PlayerBalancer plugin) {
        this.plugin = plugin;
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(ServerInfo.class, (JsonSerializer<ServerInfo>) (server, type, context) ->
                context.serialize(server.getName())
        );

        builder.serializeNulls();
        gson = builder.create();
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals("PlayerBalancer") && event.getSender() instanceof Server) {
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

                    sender.sendData("PlayerBalancer", stream.toByteArray());
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

                    sender.sendData("PlayerBalancer", stream.toByteArray());
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

                        sender.sendData("PlayerBalancer", stream.toByteArray());
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
                        out.writeInt(section.getServers().stream()
                                .mapToInt(a -> a.getPlayers().size()).sum());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sender.sendData("PlayerBalancer", stream.toByteArray());
                    break;
                }
            }
        }
    }
}
