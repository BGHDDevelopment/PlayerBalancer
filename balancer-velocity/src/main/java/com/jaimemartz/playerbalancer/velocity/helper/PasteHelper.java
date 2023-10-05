package com.jaimemartz.playerbalancer.velocity.helper;

import com.google.common.io.CharStreams;
import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.utils.GuestPaste.PasteException;
import com.jaimemartz.playerbalancer.velocity.utils.HastebinPaste;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.event.ClickEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public enum PasteHelper {
    PLUGIN((sender, address) -> {
        if (sender instanceof Player) {
            sender.sendMessage(text("Click me for the PlayerBalancer configuration", GREEN)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, address.toString()))
            );
        } else {
            sender.sendMessage(text("PlayerBalancer configuration link: " + address.toString()));
        }
    }, true) {
        @Override
        public URL paste(PlayerBalancer plugin) throws Exception {
            File file = new File(plugin.getDataDirectory().toFile(), "plugin.conf");
            try (FileInputStream stream = new FileInputStream(file)) {
                try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    String content = CharStreams.toString(reader);
                    HastebinPaste paste = new HastebinPaste(HASTEBIN_HOST, content);
                    return paste.paste();
                }
            }
        }
    },

    VELOCITY((sender, address) -> {
        if (sender instanceof Player) {
            sender.sendMessage(text("Click me for the Velocity configuration", GREEN)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, address.toString()))
            );
        } else {
            sender.sendMessage(text("Velocity configuration link: " + address.toString()));
        }
    }, true) {
        @Override
        public URL paste(PlayerBalancer plugin) throws Exception {
            File file = new File("velocity.toml");
            try (FileInputStream stream = new FileInputStream(file)) {
                try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    String content = CharStreams.toString(reader);
                    content = content.replaceAll("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}", "?.?.?.?");
                    HastebinPaste paste = new HastebinPaste(HASTEBIN_HOST, content);
                    return paste.paste();
                }
            }
        }
    },

    LOGS((sender, address) -> {
        if (sender instanceof Player) {
            sender.sendMessage(text("Click me for the server logs", GREEN)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, address.toString()))
            );
        } else {
            sender.sendMessage(text("Server logs link: " + address.toString()));
        }
    }, false) {
        @Override
        public URL paste(PlayerBalancer plugin) throws Exception {
            File file = new File("logs/latest.log");
            try (FileInputStream stream = new FileInputStream(file)) {
                try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    String content = CharStreams.toString(reader);
                    HastebinPaste paste = new HastebinPaste(HASTEBIN_HOST, content);
                    return paste.paste();
                }
            }
        }
    };

    private static final String HASTEBIN_HOST = "https://haste.zneix.eu/";

    private URL lastPasteUrl;

    private final BiConsumer<CommandSource, URL> consumer;
    private final boolean cache;

    PasteHelper(BiConsumer<CommandSource, URL> consumer, boolean cache) {
        this.consumer = consumer;
        this.cache = cache;
    }

    public void send(PlayerBalancer plugin, CommandSource sender) {
        if (lastPasteUrl == null || !cache) {
            try {
                lastPasteUrl = paste(plugin);
            } catch (PasteException e) {
                sender.sendMessage(text("An exception occurred while trying to send the paste: " + e.getMessage(), RED));

            } catch (Exception e) {
                sender.sendMessage(text("An internal error occurred while attempting to perform this command", RED));
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(text("This is a cached link, reload the plugin for it to refresh!", RED));
        }

        if (lastPasteUrl != null) {
            consumer.accept(sender, lastPasteUrl);
        } else {
            sender.sendMessage(text("Could not create the paste, try again...", RED));
        }
    }

    public URL getLastPasteURL() {
        return lastPasteUrl;
    }

    public abstract URL paste(PlayerBalancer plugin) throws Exception;

    public static void reset() {
        for (PasteHelper helper : values()) {
            helper.lastPasteUrl = null;
        }
    }
}
