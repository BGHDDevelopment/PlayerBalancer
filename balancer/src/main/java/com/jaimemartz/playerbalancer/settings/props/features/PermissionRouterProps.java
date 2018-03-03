package com.jaimemartz.playerbalancer.settings.props.features;

import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Map;

@ConfigSerializable
@Data
public class PermissionRouterProps {
    @Setting
    private boolean enabled;

    @Setting
    private Map<String, Map<String, String>> rules;
}
