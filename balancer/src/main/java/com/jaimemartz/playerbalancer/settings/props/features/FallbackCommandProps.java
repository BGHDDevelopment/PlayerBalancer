package com.jaimemartz.playerbalancer.settings.props.features;

import com.jaimemartz.playerbalancer.settings.props.shared.CommandProps;
import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
@Data
public class FallbackCommandProps {
    @Setting
    private boolean enabled;

    @Setting
    private CommandProps command;

    @Setting(value = "excluded-sections")
    private List<String> excludedSections;

    @Setting
    private boolean restrictive;

    @Setting(value = "prevent-same-section")
    private boolean preventSameSection;

    @Setting
    private Map<String, String> rules;
}
