package com.jaimemartz.playerbalancer.velocity.commands;

import com.google.common.base.Strings;
import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.velocity.ping.ServerStatus;
import com.jaimemartz.playerbalancer.velocity.section.SectionManager;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.jaimemartz.playerbalancer.velocity.utils.MessageUtils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Arrays;
import java.util.Optional;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class ManageCommand implements SimpleCommand {
    private final PlayerBalancer plugin;

    public ManageCommand(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("playerbalancer.admin");
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length != 0) {
            switch (args[0].toLowerCase()) {
                case "connect": {
                    if (args.length >= 2) {
                        String input = args[1];
                        ServerSection section = plugin.getSectionManager().getByName(input);
                        if (section != null) {
                            if (args.length == 3) {
                                Optional<Player> player = plugin.getProxyServer().getPlayer(args[2]);
                                if (player.isPresent()) {
                                    ConnectionIntent.simple(plugin, player.get(), section);
                                } else {
                                    sender.sendMessage(text("There is no player with that name connected to this proxy", RED));
                                }
                            } else {
                                if (sender instanceof Player) {
                                    ConnectionIntent.simple(plugin, (Player) sender, section);
                                } else {
                                    sender.sendMessage(text("This command variant can only be executed by a player", RED));
                                }
                            }
                        } else {
                            MessageUtils.send(sender, plugin.getSettings().getMessagesProps().getUnknownSectionMessage());
                        }
                    } else {
                        sender.sendMessage(text("Usage: /section connect <section> [player]", RED));
                    }
                    break;
                }

                case "info": {
                    if (args.length == 2) {
                        String input = args[1];
                        SectionManager manager = plugin.getSectionManager();
                        ServerSection section = manager.getByName(input);

                        if (section != null) {
                            sender.sendMessage(text(Strings.repeat("-", 53), GRAY, TextDecoration.STRIKETHROUGH));

                            sender.sendMessage(text("Information of section: ", GRAY)
                                    .append(text(section.getName(), RED))
                            );

                            sender.sendMessage(text("Principal: ", GRAY)
                                    .append(
                                            text(manager.isPrincipal(section) ? "yes" : "no")
                                                    .color(manager.isPrincipal(section) ? GREEN : RED)
                                    )
                            );

                            sender.sendMessage(text("Dummy: ", GRAY)
                                    .append(
                                            text(manager.isDummy(section) ? "yes" : "no")
                                                    .color(manager.isDummy(section) ? GREEN : RED)
                                    )
                            );

                            sender.sendMessage(text("Reiterative: ", GRAY)
                                    .append(
                                            text(manager.isReiterative(section) ? "yes" : "no")
                                                    .color(manager.isReiterative(section) ? GREEN : RED))
                            );

                            if (section.getParent() != null) {
                                sender.sendMessage(text("Parent: ", GRAY)
                                        .append(text(section.getParent().getName(), AQUA))
                                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/section info %s", section.getParent().getName())))
                                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, text("Click me for info of " + section.getParent().getName(), RED)))
                                );
                            } else {
                                sender.sendMessage(text("Parent: ", GRAY)
                                        .append(text("None", AQUA))
                                );
                            }

                            if (section.getProps().getAlias() != null) {
                                sender.sendMessage(text("Alias: ", GRAY)
                                        .append(text("\"", AQUA))
                                        .append(text(section.getProps().getAlias(), RED))
                                        .append(text("\"", AQUA))

                                );
                            } else {
                                sender.sendMessage(text("Alias: ", GRAY)
                                        .append(text("None", AQUA))

                                );
                            }

                            sender.sendMessage(text("Position: ", GRAY)
                                    .append(text(String.valueOf(section.getPosition()), AQUA))

                            );

                            sender.sendMessage(text("Provider: ", GRAY)
                                    .append(text(section.getImplicitProvider().name(), AQUA))
                                    .append(text(String.format(" (%s)", section.isInherited() ? "Implicit" : "Explicit"), GRAY))

                            );

                            if (section.getServer() != null) {
                                sender.sendMessage(text("Section Server: ", GRAY)
                                        .append(text(section.getServer().getServerInfo().getName(), AQUA))
                                );
                            } else {
                                sender.sendMessage(text("Section Server: ", GRAY)
                                        .append(text("None", AQUA))
                                );
                            }

                            if (section.getCommand() != null) {
                                sender.sendMessage(text("Section Command: ", GRAY)
                                        .append(text(section.getCommand().getCommandProps().getName(), AQUA))
                                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        textOfChildren(
                                                                text("Name: ", GRAY),
                                                                text(section.getCommand().getCommandProps().getName()).color(AQUA),

                                                                newline(),

                                                                text("Permission: ", GRAY),
                                                                text("\"", AQUA),
                                                                text(section.getCommand().getCommandProps().getPermission(), RED),
                                                                text("\"", AQUA),

                                                                newline(),

                                                                text("Aliases: ", GRAY),
                                                                text(Arrays.toString(section.getCommand().getCommandProps().getAliasesArray()), AQUA)
                                                        )
                                                )
                                        )
                                );
                            } else {
                                sender.sendMessage(text("Section Command: ", GRAY)
                                        .append(text("None", AQUA))
                                );
                            }

                            if (!section.getServers().isEmpty()) {
                                sender.sendMessage(text("Section Servers: ", GRAY));

                                section.getServers().forEach(server -> {
                                    ServerStatus status = plugin.getStatusManager().getStatus(server.getServerInfo());
                                    boolean accessible = plugin.getStatusManager().isAccessible(server.getServerInfo());
                                    sender.sendMessage(
                                            text("• Server: ")
                                            .append(text(server.getServerInfo().getName()))
                                            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                    textOfChildren(
                                                        text("Online: ", GRAY),
                                                        text(status.isOnline() ? "yes" : "no", status.isOnline() ? GREEN : RED),

                                                        newline(),

                                                        text("Accessible: ", GRAY),
                                                        text(accessible ? "yes" : "no", accessible ? GREEN : RED),

                                                        newline(),

                                                        text("Description: ", GRAY),
                                                        text("\"", AQUA),
                                                        status.getDescription().color(WHITE),
                                                        text("\"", AQUA),

                                                        newline(),

                                                        text("Address: ", GRAY),
                                                        text(server.getServerInfo().getAddress().toString(), AQUA)
                                                    )
                                            ))
                                            .append(text((String.format(" (%d/%d) ",
                                                    status.getPlayers(),
                                                    status.getMaximum()))))
                                            .color(status.isOnline() ? GREEN : RED)

                                    );
                                });
                            } else {
                                sender.sendMessage(text("Section Servers: ", GRAY)
                                        .append(text("None"))
                                        .color(AQUA)

                                );
                            }

                            sender.sendMessage(text(Strings.repeat("-", 53), GRAY, TextDecoration.STRIKETHROUGH));
                        } else {
                            MessageUtils.send(sender, plugin.getSettings().getMessagesProps().getUnknownSectionMessage());
                        }
                    } else {
                        sender.sendMessage(text("Usage: /section info <section>", RED));
                    }
                    break;
                }

                case "list": {
                    if (!plugin.getSectionManager().getSections().isEmpty()) {
                        sender.sendMessage(text("These are the registered sections: ", GRAY));

                        plugin.getSectionManager().getSections().forEach((name, section) -> {
                            sender.sendMessage(text("• Section: ", GRAY)
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/section info %s", name)))
                                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, text("Click me for info of section " + name, RED)))
                                    .append(text(name, AQUA))
                            );
                        });
                    } else {
                        sender.sendMessage(text("There are no sections to list", GRAY));
                    }
                    break;
                }

                default: {
                    sender.sendMessage(text("This is not a valid argument for this command! Execute /section for help", RED));
                }
            }
        } else {
            sender.sendMessage(text(Strings.repeat("-", 53), GRAY, TextDecoration.STRIKETHROUGH));
            sender.sendMessage(text("Available commands:", GRAY));
            sender.sendMessage(text("/section", AQUA).append(text(" - ", GRAY)).append(text("Shows you this message", RED)));
            sender.sendMessage(text("/section list", AQUA).append(text(" - ", GRAY)).append(text("Tells you which sections are configured in the plugin", RED)));
            sender.sendMessage(text("/section info <section>", AQUA).append(text(" - ", GRAY)).append(text("Tells you info about the specified section", RED)));
            sender.sendMessage(text("/section connect <section> [player]", AQUA).append(text(" - ", GRAY)).append(text("Connects you or the specified player to that section", RED)));
            sender.sendMessage(text(Strings.repeat("-", 53), GRAY, TextDecoration.STRIKETHROUGH));
        }
    }
}
