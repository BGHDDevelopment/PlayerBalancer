package com.jaimemartz.playerbalancer.settings.props;

import com.jaimemartz.playerbalancer.settings.props.features.*;
import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
@Data
public class FeaturesProps {
    @Setting(value = "balancer")
    private BalancerProps balancerProps;

    @Setting(value = "fallback-command")
    private FallbackCommandProps fallbackCommandProps;

    @Setting(value = "server-checker")
    private ServerCheckerProps serverCheckerProps;

    @Setting(value = "kick-handler")
    private KickHandlerProps kickHandlerProps;

    @Setting(value = "server-refresh")
    private ServerRefreshProps serverRefreshProps;

    @Setting(value = "custom-find-command")
    private CustomFindCommandProps customFindCommandProps;

    @Setting(value = "custom-list-command")
    private CustomListCommandProps customListCommandProps;

    @Setting(value = "custom-server-command")
    private CustomServerCommandProps customServerCommandProps;
}
