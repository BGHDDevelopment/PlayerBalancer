package me.jaimemartz.playerbalancer.utils;

import ch.jalu.configme.properties.Property;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.function.Function;

public class MessageUtils {
    public static void send(CommandSender sender, String text) {
        if (text != null) {
            text = ChatColor.translateAlternateColorCodes('&', text);
            sender.sendMessage(TextComponent.fromLegacyText(text));
        }
    }

    public static void send(CommandSender sender, String text, Function<String, String> after) {
        if (text != null) {
            text = after.apply(text);
        }

        send(sender, text);
    }

    public static void send(CommandSender sender, Property<String> property) {
        throw new UnsupportedOperationException();
    }
}
