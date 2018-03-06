package com.jaimemartz.playerbalancer.section;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.listeners.PluginMessageListener;
import com.jaimemartz.playerbalancer.settings.props.features.BalancerProps;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class SectionServer implements Listener {
    private final PlayerBalancer plugin;
    private final BalancerProps props;
    private final ServerSection section;

    public SectionServer(PlayerBalancer plugin, BalancerProps props, ServerSection section) {
        this.plugin = plugin;
        this.props = props;
        this.section = section;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PluginMessageEvent event) {
        if (event.getTag().equals("BungeeCord")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String request = in.readUTF();

            if (request.equals("Connect")) {
                if (event.getReceiver() instanceof ProxiedPlayer) {
                    ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
                    String target = in.readUTF();

                    if (target.equals("@" + section.getProps().getServerName())) {
                        ConnectionIntent.simple(plugin, player, section);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
