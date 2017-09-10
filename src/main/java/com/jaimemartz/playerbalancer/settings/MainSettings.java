package com.jaimemartz.playerbalancer.settings;

import com.jaimemartz.playerbalancer.settings.props.*;
import com.jaimemartz.playerbalancer.settings.shared.SectionProps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class MainSettings {
    @Setting(value = "general")
    private GeneralProps generalProps;

    @Setting(value = "server-checker")
    private ServerCheckerProps serverCheckerProps;

    @Setting(value = "kick-handler")
    private KickHandlerProps kickHandlerProps;

    @Setting(value = "fallback-command")
    private FallbackCommandProps fallbackCommandProps;

    @Setting(value = "messages", comment = "Effectively remove (i.e comment) a message to disable it")
    private MessagesProps messagesProps;

    @Setting
    private Map<String, SectionProps> sections;

    public MainSettings _defaults() {
        this.generalProps = new GeneralProps()._defaults();
        this.serverCheckerProps = new ServerCheckerProps()._defaults();
        this.kickHandlerProps = new KickHandlerProps()._defaults();
        this.fallbackCommandProps = new FallbackCommandProps()._defaults();
        this.messagesProps = new MessagesProps()._defaults();
        this.sections = new HashMap<>();

        SectionProps prop1 = new SectionProps();
        prop1.setDummy(true);
        prop1.setPrincipal(true);
        sections.put("test", prop1);

        SectionProps prop2 = new SectionProps();
        prop2.setDummy(false);
        prop2.setPrincipal(false);
        sections.put("other", prop2);
        return this;
    }

    public GeneralProps getGeneralProps() {
        return generalProps;
    }

    public void setGeneralProps(GeneralProps generalProps) {
        this.generalProps = generalProps;
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

    public FallbackCommandProps getFallbackCommandProps() {
        return fallbackCommandProps;
    }

    public void setFallbackCommandProps(FallbackCommandProps fallbackCommandProps) {
        this.fallbackCommandProps = fallbackCommandProps;
    }

    public MessagesProps getMessagesProps() {
        return messagesProps;
    }

    public void setMessagesProps(MessagesProps messagesProps) {
        this.messagesProps = messagesProps;
    }

    public Map<String, SectionProps> getSections() {
        return sections;
    }

    public void setSections(Map<String, SectionProps> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        return "MainSettings{" +
                "generalProps=" + generalProps +
                ", serverCheckerProps=" + serverCheckerProps +
                ", kickHandlerProps=" + kickHandlerProps +
                ", fallbackCommandProps=" + fallbackCommandProps +
                ", messagesProps=" + messagesProps +
                ", sections=" + sections +
                '}';
    }
}
