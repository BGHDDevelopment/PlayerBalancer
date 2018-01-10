package com.jaimemartz.playerbalancer.commands;

import com.google.common.base.Strings;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.ping.ServerStatus;
import com.jaimemartz.playerbalancer.section.SectionManager;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class ManageCommand extends Command {
    private final PlayerBalancer plugin;

    public ManageCommand(PlayerBalancer plugin) {
        super("section");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("playerbalancer.admin")) {
            if (args.length != 0) {
                switch (args[0].toLowerCase()) {
                    case "connect": {
                        if (args.length >= 2) {
                            String input = args[1];
                            ServerSection section = plugin.getSectionManager().getByName(input);
                            if (section != null) {
                                if (args.length == 3) {
                                    ProxiedPlayer player = plugin.getProxy().getPlayer(args[2]);
                                    if (player != null) {
                                        ConnectionIntent.simple(plugin, player, section);
                                    } else {
                                        sender.sendMessage(new ComponentBuilder("There is no player with that name connected to this proxy").color(ChatColor.RED).create());
                                    }
                                } else {
                                    if (sender instanceof ProxiedPlayer) {
                                        ConnectionIntent.simple(plugin, (ProxiedPlayer) sender, section);
                                    } else {
                                        sender.sendMessage(new ComponentBuilder("This command variant can only be executed by a player").color(ChatColor.RED).create());
                                    }
                                }
                            } else {
                                MessageUtils.send(sender, plugin.getSettings().getMessagesProps().getUnknownSectionMessage());
                            }
                        } else {
                            sender.sendMessage(new ComponentBuilder("Usage: /section connect <section> [player]").color(ChatColor.RED).create());
                        }
                        break;
                    }

                    case "info": {
                        if (args.length == 2) {
                            String input = args[1];
                            SectionManager manager = plugin.getSectionManager();
                            ServerSection section = manager.getByName(input);

                            if (section != null) {
                                sender.sendMessage(new ComponentBuilder(Strings.repeat("-", 53)).strikethrough(true).color(ChatColor.GRAY).create());

                                sender.sendMessage(new ComponentBuilder("Information of section: ")
                                        .color(ChatColor.GRAY)
                                        .append(section.getName())
                                        .color(ChatColor.RED)
                                        .create());

                                sender.sendMessage(new ComponentBuilder("Principal: ")
                                        .color(ChatColor.GRAY)
                                        .append(manager.isPrincipal(section) ? "yes" : "no")
                                        .color(manager.isPrincipal(section) ? ChatColor.GREEN : ChatColor.RED)
                                        .create());

                                sender.sendMessage(new ComponentBuilder("Dummy: ")
                                        .color(ChatColor.GRAY)
                                        .append(manager.isDummy(section) ? "yes" : "no")
                                        .color(manager.isDummy(section) ? ChatColor.GREEN : ChatColor.RED)
                                        .create()
                                );

                                sender.sendMessage(new ComponentBuilder("Reiterative: ")
                                        .color(ChatColor.GRAY)
                                        .append(manager.isReiterative(section) ? "yes" : "no")
                                        .color(manager.isReiterative(section) ? ChatColor.GREEN : ChatColor.RED)
                                        .create()
                                );

                                if (section.getParent() != null) {
                                    sender.sendMessage(new ComponentBuilder("Parent: ")
                                            .color(ChatColor.GRAY)
                                            .append(section.getParent().getName())
                                            .color(ChatColor.AQUA)
                                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/section info %s", section.getParent().getName())))
                                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click me for info").color(ChatColor.RED).create()))
                                            .create());
                                } else {
                                    sender.sendMessage(new ComponentBuilder("Parent: ")
                                            .color(ChatColor.GRAY)
                                            .append("None")
                                            .color(ChatColor.AQUA)
                                            .create());
                                }

                                if (section.getProps().getAlias() != null) {
                                    sender.sendMessage(new ComponentBuilder("Alias: ")
                                            .color(ChatColor.GRAY)
                                            .append("\"")
                                            .color(ChatColor.AQUA)
                                            .append(section.getProps().getAlias())
                                            .color(ChatColor.RED)
                                            .append("\"")
                                            .color(ChatColor.AQUA)
                                            .create()
                                    );
                                } else {
                                    sender.sendMessage(new ComponentBuilder("Alias: ")
                                            .color(ChatColor.GRAY)
                                            .append("None")
                                            .color(ChatColor.AQUA)
                                            .create()
                                    );
                                }

                                sender.sendMessage(new ComponentBuilder("Position: ")
                                        .color(ChatColor.GRAY)
                                        .append(String.valueOf(section.getPosition()))
                                        .color(ChatColor.AQUA)
                                        .create()
                                );

                                sender.sendMessage(new ComponentBuilder("Provider: ")
                                        .color(ChatColor.GRAY)
                                        .append(section.getImplicitProvider().name())
                                        .color(ChatColor.AQUA)
                                        .append(String.format(" (%s)", section.isInherited() ? "Implicit" : "Explicit"))
                                        .color(ChatColor.GRAY)
                                        .create()
                                );

                                if (section.getServer() != null) {
                                    sender.sendMessage(new ComponentBuilder("Section Server: ")
                                            .color(ChatColor.GRAY)
                                            .append(section.getServer().getName())
                                            .color(ChatColor.AQUA)
                                            .create()
                                    );
                                } else {
                                    sender.sendMessage(new ComponentBuilder("Section Server: ")
                                            .color(ChatColor.GRAY)
                                            .append("None")
                                            .color(ChatColor.AQUA)
                                            .create()
                                    );
                                }

                                if (section.getCommand() != null) {
                                    sender.sendMessage(new ComponentBuilder("Section Command: ")
                                            .color(ChatColor.GRAY)
                                            .append(section.getCommand().getName())
                                            .color(ChatColor.AQUA)
                                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                    new ComponentBuilder("Name: ")
                                                    .color(ChatColor.GRAY)
                                                    .append(section.getCommand().getName())
                                                    .color(ChatColor.AQUA)
                                                    .append("\n")

                                                    .append("Permission: ")
                                                    .color(ChatColor.GRAY)
                                                    .append("\"")
                                                    .color(ChatColor.AQUA)
                                                    .append(section.getCommand().getPermission())
                                                    .color(ChatColor.RED)
                                                    .append("\"")
                                                    .color(ChatColor.AQUA)

                                                    .append("\n")
                                                    .append("Aliases: ")
                                                    .color(ChatColor.GRAY)
                                                    .append(Arrays.toString(section.getCommand().getAliases()))
                                                    .color(ChatColor.AQUA)
                                                    .create())
                                            ).create()
                                    );
                                } else {
                                    sender.sendMessage(new ComponentBuilder("Section Command: ")
                                            .color(ChatColor.GRAY)
                                            .append("None")
                                            .color(ChatColor.AQUA)
                                            .create()
                                    );
                                }

                                if (!section.getServers().isEmpty()) {
                                    sender.sendMessage(new ComponentBuilder("Section Servers: ")
                                            .color(ChatColor.GRAY)
                                            .create()
                                    );

                                    section.getServers().forEach(server -> {
                                        ServerStatus status = plugin.getStatusManager().getStatus(server);
                                        boolean accessible = plugin.getStatusManager().isAccessible(server);
                                        sender.sendMessage(new ComponentBuilder("\u2022 Server: ")
                                                .color(ChatColor.GRAY)
                                                .append(server.getName())
                                                .color(ChatColor.AQUA)
                                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        new ComponentBuilder("Online: ")
                                                        .color(ChatColor.GRAY)
                                                        .append(status.isOnline() ? "yes" : "no")
                                                        .color(status.isOnline() ? ChatColor.GREEN : ChatColor.RED)

                                                        .append("\n")
                                                        .append("Accessible: ")
                                                        .color(ChatColor.GRAY)
                                                        .append(accessible ? "yes" : "no")
                                                        .color(accessible ? ChatColor.GREEN : ChatColor.RED)

                                                        .append("\n")
                                                        .append("Description: ")
                                                        .color(ChatColor.GRAY)
                                                        .append("\"")
                                                        .color(ChatColor.AQUA)
                                                        .append(status.getDescription())
                                                        .color(ChatColor.WHITE)
                                                        .append("\"")
                                                        .color(ChatColor.AQUA)

                                                        .append("\n")
                                                        .append("Address: ")
                                                        .color(ChatColor.GRAY)
                                                        .append(server.getAddress().toString())
                                                        .color(ChatColor.AQUA)
                                                        .create()))
                                                .append(String.format(" (%d/%d) ",
                                                        status.getPlayers(),
                                                        status.getMaximum()))
                                                .color(ChatColor.RED)
                                                .create()
                                        );
                                    });
                                } else {
                                    sender.sendMessage(new ComponentBuilder("Section Servers: ")
                                            .color(ChatColor.GRAY)
                                            .append("None")
                                            .color(ChatColor.AQUA)
                                            .create()
                                    );
                                }

                                sender.sendMessage(new ComponentBuilder(Strings.repeat("-", 53)).strikethrough(true).color(ChatColor.GRAY).create());
                            } else {
                                MessageUtils.send(sender, plugin.getSettings().getMessagesProps().getUnknownSectionMessage());
                            }
                        } else {
                            sender.sendMessage(new ComponentBuilder("Usage: /section info <section>").color(ChatColor.RED).create());
                        }
                        break;
                    }

                    case "list": {
                        if (!plugin.getSectionManager().getSections().isEmpty()) {
                            sender.sendMessage(new ComponentBuilder("These are the registered sections: ").color(ChatColor.GRAY).create());

                            plugin.getSectionManager().getSections().forEach((name, section) -> {
                                sender.sendMessage(new ComponentBuilder("\u2022 Section: ")
                                        .color(ChatColor.GRAY)
                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/section info %s", name)))
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click me for info").color(ChatColor.RED).create()))
                                        .append(name)
                                        .color(ChatColor.AQUA)
                                        .create()
                                );
                            });
                        } else {
                            sender.sendMessage(new ComponentBuilder("There are no sections to list").color(ChatColor.GRAY).create());
                        }
                        break;
                    }

                    default: {
                        sender.sendMessage(new ComponentBuilder("This is not a valid argument for this command! Execute /section for help").color(ChatColor.RED).create());
                    }
                }
            } else {
                sender.sendMessage(new ComponentBuilder(Strings.repeat("-", 53)).strikethrough(true).color(ChatColor.GRAY).create());
                sender.sendMessage(new ComponentBuilder("Available commands:").color(ChatColor.GRAY).create());
                sender.sendMessage(new ComponentBuilder("/section").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Shows you this message").color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder("/section list").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Tells you which sections are configured in the plugin").color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder("/section info <section>").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Tells you info about the specified section").color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder("/section connect <section> [player]").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Connects you or the specified player to that section").color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder(Strings.repeat("-", 53)).strikethrough(true).color(ChatColor.GRAY).create());
            }
        } else {
            sender.sendMessage(new ComponentBuilder("You do not have permission to execute this command!").color(ChatColor.RED).create());
        }
    }
}
