package com.jaimemartz.playerbalancer.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.function.Function;

public final class MessageUtils {
    public static void send(CommandSender sender, String text) {
        if (text != null) {
            text = ChatColor.translateAlternateColorCodes('&', text);
            sender.sendMessage(TextComponent.fromLegacyText(text));
        }
    }

    public static void send(CommandSender sender, String text, Function<String, String> postProcess) {
        if (text != null) {
            text = postProcess.apply(text);
        }

        send(sender, text);
    }

    public static String revertColor(String string) {
        return string.replace(ChatColor.COLOR_CHAR, '&');
    }

    private MessageUtils() {}
}
