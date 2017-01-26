package me.jaimemartz.lobbybalancer.listener;

import me.jaimemartz.faucet.Messager;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.connection.ConnectionIntent;
import me.jaimemartz.lobbybalancer.connection.ServerAssignRegistry;
import me.jaimemartz.lobbybalancer.manager.PlayerLocker;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ServerConnectListener implements Listener {
    private final LobbyBalancer plugin;

    public ServerConnectListener(LobbyBalancer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo target = event.getTarget();
        Messager msgr = new Messager(player);
        ServerSection section = plugin.getSectionManager().getByServer(target);

        if (section == null) {
            return;
        }

        if (PlayerLocker.isLocked(player)) {
            return;
        }

        if (section.getServers().contains(target)) {
            if (player.hasPermission("lobbybalancer.bypass")) {
                msgr.send(ChatColor.RED + "You have not been moved because you have the lobbybalancer.bypass permission");
                return;
            }

            if (player.getServer() != null && section.getServers().contains(player.getServer().getInfo())) {
                if (ConfigEntries.ASSIGN_TARGETS_ENABLED.get()) {
                    ServerAssignRegistry.assignTarget(player, section, target);
                }
                return;
            }
        }

        if (section.isDummy()) {
            return;
        }

        new ConnectionIntent(plugin, player, section) {
            @Override
            public void connect(ServerInfo server) {
                if (ConfigEntries.ASSIGN_TARGETS_ENABLED.get()) {
                    ServerAssignRegistry.assignTarget(player, section, server);
                }
                event.setTarget(server);
            }
        };
    }
}
