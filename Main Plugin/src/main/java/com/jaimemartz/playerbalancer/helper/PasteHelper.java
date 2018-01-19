package com.jaimemartz.playerbalancer.helper;

import com.google.common.io.CharStreams;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.utils.GuestPaste.PasteException;
import com.jaimemartz.playerbalancer.utils.HastebinPaste;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.BiConsumer;

public enum PasteHelper {
    PLUGIN((sender, address) -> {
        if (sender instanceof ProxiedPlayer) {
            sender.sendMessage(new ComponentBuilder("Click me for the PlayerBalancer configuration")
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, address.toString()))
                    .color(ChatColor.GREEN)
                    .create()
            );
        } else {
            sender.sendMessage(new ComponentBuilder("PlayerBalancer configuration link: " + address.toString()).create());
        }
    }, true) {
        @Override
        public URL paste(PlayerBalancer plugin) throws Exception {
            File file = new File(plugin.getDataFolder(), "plugin.conf");
            try (FileInputStream stream = new FileInputStream(file)) {
                try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                    String content = CharStreams.toString(reader);
                    HastebinPaste paste = new HastebinPaste("https://file.properties/paste/", content);
                    return paste.paste();
                }
            }
        }
    },

    BUNGEE((sender, address) -> {
        if (sender instanceof ProxiedPlayer) {
            sender.sendMessage(new ComponentBuilder("Click me for the BungeeCord configuration")
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, address.toString()))
                    .color(ChatColor.GREEN)
                    .create()
            );
        } else {
            sender.sendMessage(new ComponentBuilder("BungeeCord configuration link: " + address.toString()).create());
        }
    }, true) {
        @Override
        public URL paste(PlayerBalancer plugin) throws Exception {
            File file = new File("config.yml");
            try (FileInputStream stream = new FileInputStream(file)) {
                try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                    String content = CharStreams.toString(reader);
                    content = content.replaceAll("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}", "?.?.?.?");
                    HastebinPaste paste = new HastebinPaste("https://file.properties/paste/", content);
                    return paste.paste();
                }
            }
        }
    },

    LOGS((sender, address) -> {
        if (sender instanceof ProxiedPlayer) {
            sender.sendMessage(new ComponentBuilder("Click me for the plugin logs")
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, address.toString()))
                    .color(ChatColor.GREEN)
                    .create()
            );
        } else {
            sender.sendMessage(new ComponentBuilder("Plugin logs link: " + address.toString()).create());
        }
    }, false) {
        @Override
        public URL paste(PlayerBalancer plugin) throws Exception {
            HastebinPaste paste = new HastebinPaste("https://file.properties/paste/",
                    plugin.getLogsBuilder().toString()
            );

            return paste.paste();
        }
    };

    private URL url;

    private final BiConsumer<CommandSender, URL> consumer;
    private final boolean cache;

    PasteHelper(BiConsumer<CommandSender, URL> consumer, boolean cache) {
        this.consumer = consumer;
        this.cache = cache;
    }

    public void send(PlayerBalancer plugin, CommandSender sender) {
        if (url == null || !cache) {
            try {
                url = paste(plugin);
            } catch (PasteException e) {
                sender.sendMessage(new ComponentBuilder("An pastebin exception occurred: " + e.getMessage())
                        .color(ChatColor.RED)
                        .create()
                );
                e.printStackTrace();
            } catch (Exception e) {
                sender.sendMessage(new ComponentBuilder("An internal error occurred while attempting to perform this command")
                        .color(ChatColor.RED)
                        .create()
                );
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(new ComponentBuilder("This is a cached link, reload the plugin for it to refresh!")
                    .color(ChatColor.RED)
                    .create()
            );
        }

        if (url != null) {
            consumer.accept(sender, url);
        } else {
            sender.sendMessage(new ComponentBuilder("Could not create the paste, try again...")
                    .color(ChatColor.RED)
                    .create()
            );
        }
    }

    public URL getURL() {
        return url;
    }

    public abstract URL paste(PlayerBalancer plugin) throws Exception;

    public static void reset() {
        for (PasteHelper helper : values()) {
            helper.url = null;
        }
    }
}
