package com.jaimemartz.playerbalancer.settings.props.features;

import com.jaimemartz.playerbalancer.section.ServerSection;
import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Map;
import java.util.Optional;

@ConfigSerializable
@Data
public class PermissionRouterProps {
    @Setting
    private boolean enabled;

    @Setting
    private Map<String, Map<String, String>> rules;

    public Optional<String> getRouteBind(ProxiedPlayer player, ServerSection section) {
        Map<String, String> routes = rules.get(section.getName());

        if (routes != null) {
            for (Map.Entry<String, String> route : routes.entrySet()) {
                if (player.hasPermission(route.getKey())) {
                    return Optional.of(route.getValue());
                }
            }
        }

        return Optional.empty();
    }
}
