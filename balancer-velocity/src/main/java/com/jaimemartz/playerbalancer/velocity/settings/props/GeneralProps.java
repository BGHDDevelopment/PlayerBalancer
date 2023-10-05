package com.jaimemartz.playerbalancer.velocity.settings.props;

import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
@Data
public class GeneralProps {
    @Setting
    private boolean enabled;

    @Setting
    private boolean silent;

    @Setting(value = "auto-reload")
    private boolean autoReload;

    @Setting(value = "plugin-messaging")
    private boolean pluginMessaging;

    @Setting(value = "redis-bungee")
    private boolean redisBungee;

    @Setting
    private String version;
}
