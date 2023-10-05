package com.jaimemartz.playerbalancer.velocity.utils;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.function.Function;

public final class MessageUtils {
    public static void send(CommandSource sender, String text) {
        if (text != null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(text));
        }
    }

    public static void send(CommandSource sender, String text, Function<String, String> postProcess) {
        if (text != null) {
            text = postProcess.apply(text);
        }

        send(sender, text);
    }

    public static String revertColor(String string) {
        return string.replace('§', '&');
    }

    public static String safeNull(String string) {
        if (string == null) {
            return "Undefined";
        }
        return string;
    }

    private MessageUtils() {}
}
