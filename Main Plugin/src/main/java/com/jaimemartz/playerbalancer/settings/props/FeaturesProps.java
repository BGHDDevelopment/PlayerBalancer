package com.jaimemartz.playerbalancer.settings.props;

import com.jaimemartz.playerbalancer.settings.props.features.*;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
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

    public BalancerProps getBalancerProps() {
        return balancerProps;
    }

    public void setBalancerProps(BalancerProps balancerProps) {
        this.balancerProps = balancerProps;
    }

    public FallbackCommandProps getFallbackCommandProps() {
        return fallbackCommandProps;
    }

    public void setFallbackCommandProps(FallbackCommandProps fallbackCommandProps) {
        this.fallbackCommandProps = fallbackCommandProps;
    }

    public ServerCheckerProps getServerCheckerProps() {
        return serverCheckerProps;
    }

    public void setServerCheckerProps(ServerCheckerProps serverCheckerProps) {
        this.serverCheckerProps = serverCheckerProps;
    }

    public KickHandlerProps getKickHandlerProps() {
        return kickHandlerProps;
    }

    public void setKickHandlerProps(KickHandlerProps kickHandlerProps) {
        this.kickHandlerProps = kickHandlerProps;
    }

    public ServerRefreshProps getServerRefreshProps() {
        return serverRefreshProps;
    }

    public void setServerRefreshProps(ServerRefreshProps serverRefreshProps) {
        this.serverRefreshProps = serverRefreshProps;
    }

    @Override
    public String toString() {
        return "FeaturesProps{" +
                "balancerProps=" + balancerProps +
                ", fallbackCommandProps=" + fallbackCommandProps +
                ", serverCheckerProps=" + serverCheckerProps +
                ", kickHandlerProps=" + kickHandlerProps +
                ", serverRefreshProps=" + serverRefreshProps +
                '}';
    }
}
