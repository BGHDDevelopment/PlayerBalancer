package com.jaimemartz.playerbalancer.velocity.settings.props;

import com.jaimemartz.playerbalancer.velocity.settings.props.features.BalancerProps;
import com.jaimemartz.playerbalancer.velocity.settings.props.features.FallbackCommandProps;
import com.jaimemartz.playerbalancer.velocity.settings.props.features.KickHandlerProps;
import com.jaimemartz.playerbalancer.velocity.settings.props.features.PermissionRouterProps;
import com.jaimemartz.playerbalancer.velocity.settings.props.features.ServerCheckerProps;
import com.jaimemartz.playerbalancer.velocity.settings.props.features.ServerRefreshProps;
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

    @Setting(value = "permission-router")
    private PermissionRouterProps permissionRouterProps;
}
