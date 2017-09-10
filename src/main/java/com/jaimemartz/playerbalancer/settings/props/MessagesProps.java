package com.jaimemartz.playerbalancer.settings.props;

import ninja.leaping.configurate.objectmapping.Setting;

import java.util.Optional;

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

    public MessagesProps _defaults() {
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

    public Optional<String> getConnectingMessage() {
        return Optional.ofNullable(connectingMessage);
    }

    public void setConnectingMessage(String connectingMessage) {
        this.connectingMessage = connectingMessage;
    }

    public Optional<String> getConnectedMessage() {
        return Optional.ofNullable(connectedMessage);
    }

    public void setConnectedMessage(String connectedMessage) {
        this.connectedMessage = connectedMessage;
    }

    public Optional<String> getFailureMessage() {
        return Optional.ofNullable(failureMessage);
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public Optional<String> getUnknownSectionMessage() {
        return Optional.ofNullable(unknownSectionMessage);
    }

    public void setUnknownSectionMessage(String unknownSectionMessage) {
        this.unknownSectionMessage = unknownSectionMessage;
    }

    public Optional<String> getInvalidInputMessage() {
        return Optional.ofNullable(invalidInputMessage);
    }

    public void setInvalidInputMessage(String invalidInputMessage) {
        this.invalidInputMessage = invalidInputMessage;
    }

    public Optional<String> getUnavailableServerMessage() {
        return Optional.ofNullable(unavailableServerMessage);
    }

    public void setUnavailableServerMessage(String unavailableServerMessage) {
        this.unavailableServerMessage = unavailableServerMessage;
    }

    public Optional<String> getKickMessage() {
        return Optional.ofNullable(kickMessage);
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }

    public Optional<String> getBypassMessage() {
        return Optional.ofNullable(bypassMessage);
    }

    public void setBypassMessage(String bypassMessage) {
        this.bypassMessage = bypassMessage;
    }

    public Optional<String> getSameSectionMessage() {
        return Optional.ofNullable(sameSectionMessage);
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
