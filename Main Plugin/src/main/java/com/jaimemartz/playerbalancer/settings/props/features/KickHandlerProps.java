package com.jaimemartz.playerbalancer.settings.props.features;

import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
@Data
public class KickHandlerProps {
    @Setting
    private boolean enabled;

    @Setting
    private boolean inverted;

    @Setting
    private List<String> reasons;

    @Setting(value = "excluded-sections")
    private List<String> excludedSections;

    @Setting
    private boolean restrictive;

    @Setting(value = "force-principal")
    private boolean forcePrincipal;

    @Setting
    private Map<String, String> rules;

    @Setting(value = "debug-info")
    private boolean debug;
}
