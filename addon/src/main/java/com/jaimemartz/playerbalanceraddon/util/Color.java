package com.jaimemartz.playerbalanceraddon.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Color {

    private static final Pattern HEX_PATTERN = Pattern.compile("(&#[0-9a-fA-F]{6})");

    public static String translate(String message) {
        String hexColored = message;

        if (VersionCheck.isOnePointSixteenPlus()) {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String hex = matcher.group(1).substring(1);
                matcher.appendReplacement(sb, net.md_5.bungee.api.ChatColor.of(hex) + "");
            }
            matcher.appendTail(sb);

            hexColored = sb.toString();
        }

        return ChatColor.translateAlternateColorCodes('&', hexColored);
    }
    public static List<String> translate(List<String> source) {
        return source.stream().map(Color::translate).collect(Collectors.toList());
    }
}
