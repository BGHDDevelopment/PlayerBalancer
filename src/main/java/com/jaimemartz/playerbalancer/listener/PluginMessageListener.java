package com.jaimemartz.playerbalancer.listener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.settings.Settings;
import com.jaimemartz.playerbalancer.settings.types.SectionsHolder;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.inject.Inject;

public class PluginMessageListener implements Listener {
    @Inject
    private PlayerBalancer plugin;

    @Inject
    private Settings settings;

    @Inject
    private SectionsHolder holder;

    private final Gson gson;

    public PluginMessageListener() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        builder.excludeFieldsWithoutExposeAnnotation();
        gson = builder.create();
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        /*
        if (event.getTag().equals("PlayerBalancer") && event.getSender() instanceof Server) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String request = in.readUTF();
            ServerInfo sender = ((Server) event.getSender()).getInfo();
            switch (request) {
                case "Connect": {
                    if (event.getReceiver() instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
                        ServerSection section = holder.getByName(in.readUTF());

                        if (section == null) {
                            return;
                        }

                        ConnectionIntent.simple(plugin, player, section);
                    }
                    break;
                }

                case "ConnectOther": {
                    ProxiedPlayer player = plugin.getProxy().getPlayer(in.readUTF());
                    if (player == null) {
                        return;
                    }

                    ServerSection section = holder.getByName(in.readUTF());
                    if (section == null) {
                        return;
                    }

                    ConnectionIntent.simple(plugin, player, section);
                    break;
                }

                case "GetSectionByName": {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    ServerSection section = holder.getByName(in.readUTF());
                    if (section == null) {
                        return;
                    }

                    try {
                        String output = gson.toJson(section);
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
                    if (server == null) {
                        return;
                    }

                    ServerSection section = holder.getByServer(server);
                    if (section == null) {
                        return;
                    }

                    try {
                        String output = gson.toJson(section);
                        out.writeUTF(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sender.sendData("PlayerBalancer", stream.toByteArray());
                    break;
                }
            }
        }
        */
    }
}
