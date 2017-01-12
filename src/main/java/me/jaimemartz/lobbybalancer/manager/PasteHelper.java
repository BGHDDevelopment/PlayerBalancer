package me.jaimemartz.lobbybalancer.manager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.apache.commons.io.IOUtils;
import org.jpaste.exceptions.PasteException;
import org.jpaste.pastebin.PasteExpireDate;
import org.jpaste.pastebin.PastebinLink;
import org.jpaste.pastebin.PastebinPaste;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public enum PasteHelper {
    PLUGIN {
        @Override
        public String paste(Plugin plugin) throws Exception {
            File file = new File(plugin.getDataFolder(), "config.yml");
            PastebinPaste paste = new PastebinPaste();
            paste.setPasteTitle("{name} ({version} on {bungee_version}) Configuration"
                    .replace("{name}", plugin.getDescription().getName())
                    .replace("{version}", plugin.getDescription().getVersion())
                    .replace("{bungee_version}", plugin.getProxy().getVersion())
            );
            paste.setDeveloperKey(DEVELOPER_KEY);
            paste.setPasteExpireDate(PasteExpireDate.ONE_MONTH);
            paste.setVisibility(PastebinPaste.VISIBILITY_UNLISTED);
            paste.setPasteFormat("yaml");
            try (FileInputStream stream = new FileInputStream(file)) {
                paste.setContents(IOUtils.toString(stream, Charset.forName("UTF-8")));
            }
            PastebinLink link = paste.paste();
            return link.getLink().toString();
        }
    },
    BUNGEE {
        @Override
        public String paste(Plugin plugin) throws Exception {
            File file = new File(plugin.getDataFolder().getParentFile().getParentFile(), "config.yml");
            PastebinPaste paste = new PastebinPaste();
            paste.setPasteTitle("{name} ({version}) Configuration"
                    .replace("{name}", plugin.getProxy().getName())
                    .replace("{version}", plugin.getProxy().getVersion())
            );
            paste.setDeveloperKey(DEVELOPER_KEY);
            paste.setPasteExpireDate(PasteExpireDate.ONE_MONTH);
            paste.setVisibility(PastebinPaste.VISIBILITY_UNLISTED);
            paste.setPasteFormat("yaml");
            try (FileInputStream stream = new FileInputStream(file)) {
                paste.setContents(IOUtils.toString(stream, Charset.forName("UTF-8")));
            }
            PastebinLink link = paste.paste();
            return link.getLink().toString();
        }
    },
    LOGS {
        @Override
        public String paste(Plugin plugin) throws Exception {
            File file = new File(plugin.getDataFolder().getParentFile().getParentFile(), "proxy.log.0");
            PastebinPaste paste = new PastebinPaste();
            paste.setPasteTitle("{name} ({version}) Last Logs"
                    .replace("{name}", plugin.getProxy().getName())
                    .replace("{version}", plugin.getProxy().getVersion())
            );
            paste.setDeveloperKey(DEVELOPER_KEY);
            paste.setPasteExpireDate(PasteExpireDate.ONE_MONTH);
            paste.setVisibility(PastebinPaste.VISIBILITY_UNLISTED);
            paste.setPasteFormat("text");
            try (FileInputStream stream = new FileInputStream(file)) {
                paste.setContents(IOUtils.toString(stream, Charset.forName("UTF-8")));
            }
            PastebinLink link = paste.paste();
            return link.getLink().toString();
        }
    };

    private String link;

    private ScheduledTask task = null;
    public void send(Plugin plugin, CommandSender sender, String message) {
        try {
            sender.sendMessage(new ComponentBuilder(message.replace("{link}", link == null ? link = paste(plugin) : link)
            ).color(ChatColor.GREEN).create());
            if (task != null) {
                plugin.getProxy().getScheduler().cancel(task);
            }
            task = plugin.getProxy().getScheduler().schedule(plugin, () -> link = null, 5, TimeUnit.MINUTES);
        } catch (PasteException e) {
            if (e.getMessage().equals("Failed to generate paste: Post limit, maximum pastes per 24h reached")) {
                sender.sendMessage(new ComponentBuilder("The file could not be pasted, your ip has reached the 10 pastes per day limit").color(ChatColor.RED).create());
            } else {
                sender.sendMessage(new ComponentBuilder("An unexpected error occurred while pasting the file").color(ChatColor.RED).create());
            }
            e.printStackTrace();
        } catch(Exception e) {
            sender.sendMessage(new ComponentBuilder("An internal error occurred while attempting to perform this command").color(ChatColor.RED).create());
            e.printStackTrace();
        }
    }

    public abstract String paste(Plugin plugin) throws Exception;
    public static final String DEVELOPER_KEY = "e3ff18d8fb001a3ece08ae0d7d4a87bd";
}
