package com.jaimemartz.playerbalancer.settings.props.features;

import com.jaimemartz.playerbalancer.settings.props.shared.CommandProps;
import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
@Data
public class CustomServerCommandProps {
    @Setting
    private boolean enabled;

    @Setting
    private CommandProps command;

    @Setting
    private Formats formats;

    @ConfigSerializable
    @Data
    private static class Formats {

    }
}
