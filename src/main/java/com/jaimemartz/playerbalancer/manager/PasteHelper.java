package com.jaimemartz.playerbalancer.manager;

import com.google.common.io.CharStreams;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.utils.GuestPaste;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public enum PasteHelper {
    PLUGIN {
        @Override
        public String paste(PlayerBalancer plugin) throws Exception {
            File file = new File(plugin.getDataFolder(), "config.yml");
            if (!file.exists()) {
                return "File does not exist";
            }

            try (FileInputStream stream = new FileInputStream(file)) {
                try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                    String content = CharStreams.toString(reader);
                    GuestPaste paste = new GuestPaste("e3ff18d8fb001a3ece08ae0d7d4a87bd", content);

                    paste.setName("{name} ({version} on {bungee_version}) Configuration"
                            .replace("{name}", plugin.getDescription().getName())
                            .replace("{version}", plugin.getDescription().getVersion())
                            .replace("{bungee_version}", plugin.getProxy().getVersion())
                    );

                    paste.setExpiration(GuestPaste.Expiration.ONE_MONTH);
                    paste.setExposure(GuestPaste.Exposure.UNLISTED);
                    paste.setFormat("yaml");

                    return paste.paste();
                }
            }
        }
    },
    BUNGEE {
        @Override
        public String paste(PlayerBalancer plugin) throws Exception {
            File file = new File("config.yml");
            if (!file.exists()) {
                return "File does not exist";
            }

            try (FileInputStream stream = new FileInputStream(file)) {
                try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                    String content = CharStreams.toString(reader);
                    content = content.replaceAll("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}", "X.X.X.X");
                    GuestPaste paste = new GuestPaste("e3ff18d8fb001a3ece08ae0d7d4a87bd", content);

                    paste.setName("{name} ({version}) Configuration"
                            .replace("{name}", plugin.getProxy().getName())
                            .replace("{version}", plugin.getProxy().getVersion())
                    );

                    paste.setExpiration(GuestPaste.Expiration.ONE_MONTH);
                    paste.setExposure(GuestPaste.Exposure.UNLISTED);
                    paste.setFormat("yaml");

                    return paste.paste();
                }
            }
        }
    };

    //Cached link of the paste
    private String response;
    private ScheduledTask task = null;

    public void send(PlayerBalancer plugin, CommandSender sender, String message) {
        try {
            sender.sendMessage(new ComponentBuilder(message.replace("{response}", response == null ? response = paste(plugin) : response)).color(ChatColor.GREEN).create());

            if (task != null) {
                plugin.getProxy().getScheduler().cancel(task);
            }

            task = plugin.getProxy().getScheduler().schedule(plugin, () -> response = null, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            sender.sendMessage(new ComponentBuilder("An internal error occurred while attempting to perform this command").color(ChatColor.RED).create());
            e.printStackTrace();
        }
    }

    public abstract String paste(PlayerBalancer plugin) throws Exception;
}
