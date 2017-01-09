package me.jaimemartz.lobbybalancer.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.connection.ConnectionIntent;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PluginMessageListener implements Listener {
    private final LobbyBalancer plugin;

    public PluginMessageListener(LobbyBalancer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals("LobbyBalancer") && event.getSender() instanceof Server) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String request = in.readUTF();
            ServerInfo sender = ((Server) event.getSender()).getInfo();
            switch (request) {
                case "Connect": {
                    if (event.getReceiver() instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
                        ServerSection section = plugin.getSectionManager().getByName(in.readUTF());

                        if (section == null) {
                            return;
                        }

                        new ConnectionIntent(plugin, player, section) {
                            @Override
                            public void connect(ServerInfo server) {
                                player.connect(server);
                            }
                        };
                    }
                    break;
                }

                case "ConnectOther": {
                    ProxiedPlayer player = plugin.getProxy().getPlayer(in.readUTF());
                    if (player == null) {
                        return;
                    }

                    ServerSection section = plugin.getSectionManager().getByName(in.readUTF());
                    if (section == null) {
                        return;
                    }

                    new ConnectionIntent(plugin, player, section) {
                        @Override
                        public void connect(ServerInfo server) {
                            player.connect(server);
                        }
                    };
                    break;
                }

                case "GetSectionByName": {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    ServerSection section = plugin.getSectionManager().getByName(in.readUTF());
                    if (section == null) {
                        return;
                    }

                    try {
                        String output = plugin.getGson().toJson(section);
                        out.writeUTF(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sender.sendData("LobbyBalancer", stream.toByteArray());
                    break;
                }

                case "GetSectionByServer": {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    ServerInfo server = plugin.getProxy().getServerInfo(in.readUTF());
                    if (server == null) {
                        return;
                    }

                    ServerSection section = plugin.getSectionManager().getByServer(server);
                    if (section == null) {
                        return;
                    }

                    try {
                        String output = plugin.getGson().toJson(section);
                        out.writeUTF(output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sender.sendData("LobbyBalancer", stream.toByteArray());
                    break;
                }
            }
        }
    }
}
