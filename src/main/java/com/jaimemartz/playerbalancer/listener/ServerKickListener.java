package com.jaimemartz.playerbalancer.listener;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.manager.PlayerLocker;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServerKickListener implements Listener {
    private final PlayerBalancer plugin;

    public ServerKickListener(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo from = event.getKickedFrom();

        ServerSection section = getSection(player, from);

        if (section != null) {
            List<ServerInfo> servers = new ArrayList<>();
            servers.addAll(section.getMappedServers());
            servers.remove(from);

            new ConnectionIntent(plugin, player, section, servers) {
                @Override
                public void connect(ServerInfo server, Callback<Boolean> callback) {
                    PlayerLocker.lock(player);
                    MessageUtils.send(player, plugin.getSettings().getMessagesProps().getKickMessage(),
                            (str) -> str.replace("{from}", from.getName())
                                    .replace("{to}", server.getName())
                                    .replace("{reason}", event.getKickReason()));
                    event.setCancelled(true);
                    event.setCancelServer(server);
                    plugin.getProxy().getScheduler().schedule(plugin, () -> {
                        PlayerLocker.unlock(player);
                    }, 5, TimeUnit.SECONDS);
                    callback.done(true, null);
                }
            };
        }
    }

    private ServerSection getSection(ProxiedPlayer player, ServerInfo from) {
        if (player.getServer() == null) {
            if (plugin.getSettings().getKickHandlerProps().isForcePrincipal()) {
                return plugin.getSectionManager().getPrincipal();
            } else {
                return null;
            }
        }

        if (!player.getServer().getInfo().equals(from)) {
            return null;
        }

        ServerSection current = plugin.getSectionManager().getByServer(from);

        if (current != null) {
            if (plugin.getSettings().getKickHandlerProps().getExcludedSections().contains(current.getName())) {
                return null;
            }
        }

        boolean matches = false;
        String reason = TextComponent.toPlainText(event.getKickReasonComponent());

        for (String string : settings.getProperty(ReconnectorProperties.REASONS)) {
            if (reason.matches(string) || reason.contains(string)) {
                matches = true;
                break;
            }
        }

        if (settings.getProperty(ReconnectorProperties.INVERTED)) {
            matches.set(!matches.get());
        }

        if (settings.getProperty(ReconnectorProperties.DEBUG)) {
            plugin.getLogger().info(String.format("Kick Reason: \"%s\", Found Match: %s", TextComponent.toPlainText(event.getKickReasonComponent()), matches));
        }

        if (matches.get()) {
            if (current != null) {
                MapBean rules = settings.getProperty(CommandProperties.RULES);
                String bind = rules.getMap().get(current.getName());
                ServerSection target = holder.getByName(bind);

                if (target == null) {
                    target = current.getParentName();
                }

                if (settings.getProperty(ReconnectorProperties.RESTRICTED)) {
                    if (current.getPosition() >= 0 && target.getPosition() < 0) {
                        return null;
                    }
                }

                return target;
            } else {
                if (settings.getProperty(GeneralProperties.FALLBACK_PRINCIPAL)) {
                    return holder.getPrincipal();
                }
            }
        }
        return null;
    }
}