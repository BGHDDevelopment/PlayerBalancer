package com.jaimemartz.playerbalancer.velocity.settings.props.features;

import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
@Data
public class ServerRefreshProps {
    @Setting
    private boolean enabled;

    @Setting
    private int delay;

    @Setting
    private int interval;
}
