package com.jaimemartz.playerbalancer.settings;

import com.jaimemartz.playerbalancer.settings.props.FeaturesProps;
import com.jaimemartz.playerbalancer.settings.props.GeneralProps;
import com.jaimemartz.playerbalancer.settings.props.MessagesProps;
import com.jaimemartz.playerbalancer.settings.props.features.*;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class SettingsHolder {
    @Setting(value = "general")
    private GeneralProps generalProps;

    @Setting(value = "messages")
    private MessagesProps messagesProps;

    @Setting(value = "features")
    private FeaturesProps featuresProps;

    public GeneralProps getGeneralProps() {
        return generalProps;
    }

    public void setGeneralProps(GeneralProps generalProps) {
        this.generalProps = generalProps;
    }

    public MessagesProps getMessagesProps() {
        return messagesProps;
    }

    public void setMessagesProps(MessagesProps messagesProps) {
        this.messagesProps = messagesProps;
    }

    public FeaturesProps getFeaturesProps() {
        return featuresProps;
    }

    public void setFeaturesProps(FeaturesProps featuresProps) {
        this.featuresProps = featuresProps;
    }

    public BalancerProps getBalancerProps() {
        return featuresProps.getBalancerProps();
    }

    public void setBalancerProps(BalancerProps balancerProps) {
        featuresProps.setBalancerProps(balancerProps);
    }

    public FallbackCommandProps getFallbackCommandProps() {
        return featuresProps.getFallbackCommandProps();
    }

    public void setFallbackCommandProps(FallbackCommandProps fallbackCommandProps) {
        featuresProps.setFallbackCommandProps(fallbackCommandProps);
    }

    public ServerCheckerProps getServerCheckerProps() {
        return featuresProps.getServerCheckerProps();
    }

    public void setServerCheckerProps(ServerCheckerProps serverCheckerProps) {
        featuresProps.setServerCheckerProps(serverCheckerProps);
    }

    public KickHandlerProps getKickHandlerProps() {
        return featuresProps.getKickHandlerProps();
    }

    public void setKickHandlerProps(KickHandlerProps kickHandlerProps) {
        featuresProps.setKickHandlerProps(kickHandlerProps);
    }

    public ServerRefreshProps getServerRefreshProps() {
        return featuresProps.getServerRefreshProps();
    }

    public void setKickHandlerProps(ServerRefreshProps serverRefreshProps) {
        featuresProps.setServerRefreshProps(serverRefreshProps);
    }

    public ForcedEntrySectionProps getForcedEntrySectionProps() {
        return featuresProps.getForcedEntrySectionProps();
    }

    public void setForcedEntrySectionProps(ForcedEntrySectionProps forcedEntrySectionProps) {
        featuresProps.setForcedEntrySectionProps(forcedEntrySectionProps);
    }

    @Override
    public String toString() {
        return "SettingsHolder{" +
                "generalProps=" + generalProps +
                ", messagesProps=" + messagesProps +
                ", featuresProps=" + featuresProps +
                '}';
    }
}
