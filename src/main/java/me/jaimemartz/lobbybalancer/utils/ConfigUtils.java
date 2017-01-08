package me.jaimemartz.lobbybalancer.utils;

import net.md_5.bungee.config.Configuration;

public class ConfigUtils {
    public static boolean isSet(Configuration config, String path) {
        return config.get(path) != null;
    }
}
