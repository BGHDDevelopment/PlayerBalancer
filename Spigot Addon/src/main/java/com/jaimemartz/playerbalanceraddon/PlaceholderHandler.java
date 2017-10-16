package com.jaimemartz.playerbalanceraddon;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlaceholderHandler extends EZPlaceholderHook {
    private final Map<String, Integer> sectionPlayerCounts = new LinkedHashMap<>();
    private final PlayerBalancerAddon plugin;

    public PlaceholderHandler(PlayerBalancerAddon plugin) {
        super(plugin, "balancer");
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.startsWith("playercount_")) {
            String section = identifier.split("playercount_")[1];

            if (section == null)
                return null;

            //For the first call this placeholder will return 0
            //For the next one, the result of the previous one
            plugin.getManager().getSectionPlayerCount(section, (count) -> {
                sectionPlayerCounts.put(section, count);
            });

            return String.valueOf(sectionPlayerCounts.get(section));
        }

        return null;
    }
}
