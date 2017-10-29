package com.jaimemartz.playerbalancer.services;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.settings.props.MessagesProps;
import com.jaimemartz.playerbalancer.settings.props.features.FallbackCommandProps;
import com.jaimemartz.playerbalancer.settings.props.shared.CommandProps;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class FallbackService extends Command implements Listener {
    protected final PlayerBalancer plugin;
    protected final MessagesProps messages;
    private final FallbackCommandProps props;

    public FallbackService(PlayerBalancer plugin, CommandProps props) {
        super(props.getName(), props.getPermission(), props.getAliasesArray());
        this.props = plugin.getSettings().getFallbackCommandProps();
        this.messages = plugin.getSettings().getMessagesProps();
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            ServerSection target = this.getSection(player);

            if (target != null) {
                if (args.length == 1) {
                    try {
                        int number = Integer.parseInt(args[0]);
                        if (number <= 0) {
                            MessageUtils.send(player, messages.getInvalidInputMessage());
                        } else if (number > target.getServers().size()) {
                            MessageUtils.send(player, messages.getInvalidInputMessage());
                        } else {
                            ServerInfo server = Iterables.get(target.getServers(), number - 1);
                            ConnectionIntent.direct(plugin, player, server, (response, throwable) -> {});
                        }
                    } catch (NumberFormatException e) {
                        MessageUtils.send(player, messages.getInvalidInputMessage());
                    }
                } else {
                    ConnectionIntent.simple(plugin, player, target);
                }
            }
        } else {
            sender.sendMessage(new ComponentBuilder("This command can only be executed by a player").color(ChatColor.RED).create());
        }
    }

    public ServerSection getSection(ProxiedPlayer player) {
        ServerSection current = plugin.getSectionManager().getByPlayer(player);

        if (current != null) {
            if (props.getExcludedSections().contains(current.getName())) {
                MessageUtils.send(player, messages.getUnavailableServerMessage());
                return null;
            }

            ServerSection target = plugin.getSectionManager().getBind(props.getRules(), current)
                    .orElse(current.getParent());
            if (target == null) {
                MessageUtils.send(player, messages.getUnavailableServerMessage());
                return null;
            }

            if (props.isRestrictive()) {
                if (current.getPosition() >= 0 && target.getPosition() < 0) {
                    MessageUtils.send(player, messages.getUnavailableServerMessage());
                    return null;
                }
            }

            return target;
        } else {
            if (plugin.getSettings().getBalancerProps().isDefaultPrincipal()) {
                return plugin.getSectionManager().getPrincipal();
            } else {
                MessageUtils.send(player, messages.getUnavailableServerMessage());
            }
        }

        return null;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals("PlayerBalancer") && event.getSender() instanceof Server) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String request = in.readUTF();
            ServerInfo sender = ((Server) event.getSender()).getInfo();

            switch (request) {
                case "FallbackPlayer": {
                    if (event.getReceiver() instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
                        ServerSection target = getSection(player);

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

                    ServerSection target = getSection(player);

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
