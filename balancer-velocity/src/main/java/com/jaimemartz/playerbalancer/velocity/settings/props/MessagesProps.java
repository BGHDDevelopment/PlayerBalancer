package com.jaimemartz.playerbalancer.velocity.settings.props;

import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
@Data
public class MessagesProps {
    @Setting(value = "connecting-server")
    private String connectingMessage;

    @Setting(value = "connected-server")
    private String connectedMessage;

    @Setting(value = "misc-failure")
    private String failureMessage;

    @Setting(value = "unknown-section")
    private String unknownSectionMessage;

    @Setting(value = "invalid-input")
    private String invalidInputMessage;

    @Setting(value = "unavailable-server")
    private String unavailableServerMessage;

    @Setting(value = "player-kicked")
    private String kickMessage;

    @Setting(value = "player-bypass")
    private String bypassMessage;

    @Setting(value = "same-section")
    private String sameSectionMessage;
}
