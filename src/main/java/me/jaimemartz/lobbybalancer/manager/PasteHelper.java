package me.jaimemartz.lobbybalancer.manager;

import com.github.kennedyoliveira.pastebin4j.*;
import com.google.common.io.CharStreams;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public enum PasteHelper {
    PLUGIN {
        @Override
        public String paste(LobbyBalancer plugin) throws Exception {
            File file = new File(plugin.getDataFolder(), "config.yml");
            if (!file.exists()) {
                return "File does not exist";
            }

            GuestPaste paste = new GuestPaste();
            paste.setTitle("{name} ({version} on {bungee_version}) Configuration"
                    .replace("{name}", plugin.getDescription().getName())
                    .replace("{version}", plugin.getDescription().getVersion())
                    .replace("{bungee_version}", plugin.getProxy().getVersion())
            );

            paste.setExpiration(PasteExpiration.ONE_MONTH);
            paste.setVisibility(PasteVisibility.UNLISTED);
            paste.setHighLight(PasteHighLight.YAML);

            try (FileInputStream stream = new FileInputStream(file)) {
                try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                    String content = CharStreams.toString(reader);
                    paste.setContent(content);
                }
            }

            return paste.paste(credentials);
        }
    },
    BUNGEE {
        @Override
        public String paste(LobbyBalancer plugin) throws Exception {
            File file = new File("config.yml");
            if (!file.exists()) {
                return "File does not exist";
            }

            GuestPaste paste = new GuestPaste();
            paste.setTitle("{name} ({version}) Configuration"
                    .replace("{name}", plugin.getProxy().getName())
                    .replace("{version}", plugin.getProxy().getVersion())
            );

            paste.setExpiration(PasteExpiration.ONE_MONTH);
            paste.setVisibility(PasteVisibility.UNLISTED);
            paste.setHighLight(PasteHighLight.YAML);

            try (FileInputStream stream = new FileInputStream(file)) {
                try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                    String content = CharStreams.toString(reader);
                    content = content.replaceAll("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}", "X.X.X.X");
                    paste.setContent(content);
                }
            }

            return paste.paste(credentials);
        }
    };

    private String link;
    private ScheduledTask task = null;

    public void send(LobbyBalancer plugin, CommandSender sender, String message) {
        try {
            sender.sendMessage(new ComponentBuilder(message.replace("{link}", link == null ? link = paste(plugin) : link)).color(ChatColor.GREEN).create());

            if (task != null) {
                plugin.getProxy().getScheduler().cancel(task);
            }

            task = plugin.getProxy().getScheduler().schedule(plugin, () -> link = null, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            sender.sendMessage(new ComponentBuilder("An internal error occurred while attempting to perform this command").color(ChatColor.RED).create());
            e.printStackTrace();
        }
    }

    public abstract String paste(LobbyBalancer plugin) throws Exception;

    private static final AccountCredentials credentials = new AccountCredentials("e3ff18d8fb001a3ece08ae0d7d4a87bd");
}
