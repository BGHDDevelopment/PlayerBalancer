package me.jaimemartz.lobbybalancer.commands;

import me.jaimemartz.lobbybalancer.PlayerBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.connection.ConnectionIntent;
import me.jaimemartz.lobbybalancer.ping.ServerStatus;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import me.jaimemartz.lobbybalancer.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Set;

public class ManageCommand extends Command {
    private final PlayerBalancer plugin;

    public ManageCommand(PlayerBalancer plugin) {
        super("section");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("lobbybalancer.admin")) {
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
                                        sender.sendMessage(new ComponentBuilder("This command can only be executed by a player").color(ChatColor.RED).create());
                                    }
                                }
                            } else {
                                MessageUtils.send(sender, ConfigEntries.UNKNOWN_SECTION_MESSAGE.get());
                            }
                        } else {
                            sender.sendMessage(new ComponentBuilder("Usage: /balancer connect <section> [player]").color(ChatColor.RED).create());
                        }
                        break;
                    }

                    case "info": {
                        if (args.length == 2) {
                            String input = args[1];
                            ServerSection section = plugin.getSectionManager().getByName(input);
                            if (section != null) {
                                sender.sendMessage(new ComponentBuilder(StringUtils.repeat('-', 53)).strikethrough(true).color(ChatColor.GRAY).create());

                                sender.sendMessage(new ComponentBuilder("Information of section: ")
                                        .color(ChatColor.GRAY)
                                        .append(section.getName())
                                        .color(ChatColor.RED)
                                        .create());

                                sender.sendMessage(new ComponentBuilder("Principal: ")
                                        .color(ChatColor.GRAY)
                                        .append(section.isPrincipal() ? "yes" : "no")
                                        .color(section.isPrincipal() ? ChatColor.GREEN : ChatColor.RED)
                                        .create());

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

                                sender.sendMessage(new ComponentBuilder("Position: ")
                                        .color(ChatColor.GRAY)
                                        .append(String.valueOf(section.getPosition()))
                                        .color(ChatColor.AQUA)
                                        .create()
                                );

                                sender.sendMessage(new ComponentBuilder("Provider: ")
                                        .color(ChatColor.GRAY)
                                        .append(section.getProvider().name())
                                        .color(ChatColor.AQUA)
                                        .append(String.format("(%s)", section.isInherited() ? "Inherited" : "Specified"))
                                        .color(ChatColor.GRAY)
                                        .create()
                                );

                                sender.sendMessage(new ComponentBuilder("Dummy: ")
                                        .color(ChatColor.GRAY)
                                        .append(section.isDummy() ? "yes" : "no")
                                        .color(section.isDummy() ? ChatColor.GREEN : ChatColor.RED)
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
                                                    new ComponentBuilder("Extra Information: ") //todo implement this
                                                            .create()
                                                    )
                                            )
                                            .create()
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

                                    //todo show status when hovering over server
                                    section.getServers().forEach(server -> {
                                        ServerStatus status = plugin.getStatusManager().getStatus(server);
                                        sender.sendMessage(new ComponentBuilder("|> Server: ")
                                                .color(ChatColor.GRAY)
                                                .append(server.getName())
                                                .color(ChatColor.AQUA)
                                                .append(String.format(" (%d/%d) ",
                                                        status.getOnline(),
                                                        status.getMaximum()))
                                                .color(ChatColor.RED)
                                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("This is a test\nThis is a test").create()))
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

                                sender.sendMessage(new ComponentBuilder(StringUtils.repeat('-', 53)).strikethrough(true).color(ChatColor.GRAY).create());
                            } else {
                                MessageUtils.send(sender, ConfigEntries.UNKNOWN_SECTION_MESSAGE.get());
                            }
                        } else {
                            sender.sendMessage(new ComponentBuilder("Usage: /balancer info <section>").color(ChatColor.RED).create());
                        }
                        break;
                    }

                    case "list": {
                        Set<String> keys = plugin.getSectionManager().getSections().keySet();
                        Iterator<String> iterator = keys.iterator();
                        TextComponent message = new TextComponent("There are ");
                        message.addExtra(new TextComponent(new ComponentBuilder(String.valueOf(keys.size())).color(ChatColor.AQUA).create()));
                        message.addExtra(" configured sections:\n");
                        message.setColor(ChatColor.GRAY);

                        if (iterator.hasNext()) {
                            while (iterator.hasNext()) {
                                String name = iterator.next();
                                TextComponent extra = new TextComponent(name);
                                extra.setColor(ChatColor.GREEN);
                                extra.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/section info %s", name)));
                                extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click me for info").color(ChatColor.RED).create()));

                                if (iterator.hasNext()) {
                                    TextComponent sep = new TextComponent(", ");
                                    sep.setColor(ChatColor.GRAY);
                                    extra.addExtra(sep);
                                }

                                message.addExtra(extra);
                            }
                        } else {
                            TextComponent extra = new TextComponent("There are no sections to list");
                            extra.setColor(ChatColor.RED);
                            message.addExtra(extra);
                        }

                        sender.sendMessage(message);
                        break;
                    }

                    default: {
                        sender.sendMessage(new ComponentBuilder("This is not a valid argument for this command!").color(ChatColor.RED).create());
                    }
                }
            } else {
                sender.sendMessage(new ComponentBuilder(StringUtils.repeat('-', 53)).strikethrough(true).color(ChatColor.GRAY).create());
                sender.sendMessage(new ComponentBuilder("Available commands:").color(ChatColor.GRAY).create());
                sender.sendMessage(new ComponentBuilder("/section").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Shows you this message").color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder("/section list").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Tells you which sections are configured in the plugin").color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder("/section info <section>").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Tells you info about the specified section").color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder("/section connect <section> [player]").color(ChatColor.AQUA).append(" - ").color(ChatColor.GRAY).append("Connects you or the specified player to that section").color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder(StringUtils.repeat('-', 53)).strikethrough(true).color(ChatColor.GRAY).create());
            }
        } else {
            sender.sendMessage(new ComponentBuilder("You do not have permission to execute this command!").color(ChatColor.RED).create());
        }
    }
}
