package com.jaimemartz.playerbalanceraddon;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerBalancerPlaceholderExpansion extends PlaceholderExpansion {
    private final Map<String, Integer> sectionPlayerCounts = new LinkedHashMap<>();
    private final PlayerBalancerAddon plugin;

    public PlayerBalancerPlaceholderExpansion(PlayerBalancerAddon plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.startsWith("pc")) {
            String section = identifier.split("pc_")[1];

            if (section == null)
                return "Invalid Section";

            // For the first call this placeholder will return 0
            // For the next one, the result of the previous one
            plugin.getManager().getSectionPlayerCount(section, (count) -> sectionPlayerCounts.put(section, count));

            return String.valueOf(sectionPlayerCounts.getOrDefault(section, 0));
        }

        return null;
    }

    @Override
    public String getIdentifier() {
        return "pb";
    }

    @Override
    public String getAuthor() {
        return "BGHDDevelopmentLLC";
    }

    @Override
    public String getVersion() {
        return "bundled";
    }
}
