package com.jaimemartz.playerbalancer.settings.props;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MessagesProps {
    @Setting("connecting-server")
    private String connectingMessage;

    @Setting("connected-server")
    private String connectedMessage;

    @Setting("misc-failure")
    private String failureMessage;

    @Setting("unknown-section")
    private String unknownSectionMessage;

    @Setting("invalid-input")
    private String invalidInputMessage;

    @Setting("unavailable-server")
    private String unavailableServerMessage;

    @Setting("player-kicked")
    private String kickMessage;

    @Setting("player-bypass")
    private String bypassMessage;

    @Setting("same-section")
    private String sameSectionMessage;

    public MessagesProps __defaults() {
        connectingMessage = "&aConnecting to a {section} server";
        connectedMessage = "&aConnected to {server}";
        failureMessage = "&cCould not find a server to get connected to";
        unknownSectionMessage = "&aCould not find a section with that name";
        invalidInputMessage = "&cThis is an invalid input type for this command";
        unavailableServerMessage = "&cThis command cannot be executed on this server";
        kickMessage = "&cYou have been kicked from &a{from} &cand you are being moved to &a{to}, reason: &a{reason}";
        bypassMessage = "&cYou have not been moved because you have the playerbalancer.bypass permission";
        sameSectionMessage = "&cYou are already connected to a server on this section!";
        return this;
    }

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
