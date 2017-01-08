package me.jaimemartz.lobbybalancer.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

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
                default: {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    try {
                        out.writeUTF("The plugin message api for LobbyBalancer is not ready yet, it will be implemented in a future version");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sender.sendData("LobbyBalancer", stream.toByteArray());
                }
            }
        }
    }
}
