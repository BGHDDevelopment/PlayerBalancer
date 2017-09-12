package com.jaimemartz.playerbalancer.settings.props;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
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

    public String getConnectingMessage() {
        return connectingMessage;
    }

    public void setConnectingMessage(String connectingMessage) {
        this.connectingMessage = connectingMessage;
    }

    public String getConnectedMessage() {
        return connectedMessage;
    }

    public void setConnectedMessage(String connectedMessage) {
        this.connectedMessage = connectedMessage;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public String getUnknownSectionMessage() {
        return unknownSectionMessage;
    }

    public void setUnknownSectionMessage(String unknownSectionMessage) {
        this.unknownSectionMessage = unknownSectionMessage;
    }

    public String getInvalidInputMessage() {
        return invalidInputMessage;
    }

    public void setInvalidInputMessage(String invalidInputMessage) {
        this.invalidInputMessage = invalidInputMessage;
    }

    public String getUnavailableServerMessage() {
        return unavailableServerMessage;
    }

    public void setUnavailableServerMessage(String unavailableServerMessage) {
        this.unavailableServerMessage = unavailableServerMessage;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }

    public String getBypassMessage() {
        return bypassMessage;
    }

    public void setBypassMessage(String bypassMessage) {
        this.bypassMessage = bypassMessage;
    }

    public String getSameSectionMessage() {
        return sameSectionMessage;
    }

    public void setSameSectionMessage(String sameSectionMessage) {
        this.sameSectionMessage = sameSectionMessage;
    }

    @Override
    public String toString() {
        return "MessagesProps{" +
                "connectingMessage=" + connectingMessage +
                ", connectedMessage=" + connectedMessage +
                ", failureMessage=" + failureMessage +
                ", unknownSectionMessage=" + unknownSectionMessage +
                ", invalidInputMessage=" + invalidInputMessage +
                ", unavailableServerMessage=" + unavailableServerMessage +
                ", kickMessage=" + kickMessage +
                ", bypassMessage=" + bypassMessage +
                ", sameSectionMessage=" + sameSectionMessage +
                '}';
    }
}
