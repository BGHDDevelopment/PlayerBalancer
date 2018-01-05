package com.jaimemartz.playerbalancer.settings;

import com.jaimemartz.playerbalancer.settings.props.FeaturesProps;
import com.jaimemartz.playerbalancer.settings.props.GeneralProps;
import com.jaimemartz.playerbalancer.settings.props.MessagesProps;
import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
@Data
public class SettingsHolder {
    @Setting(value = "general")
    private GeneralProps generalProps;

    @Setting(value = "messages")
    private MessagesProps messagesProps;

    @Setting(value = "features")
    private FeaturesProps featuresProps;
}
