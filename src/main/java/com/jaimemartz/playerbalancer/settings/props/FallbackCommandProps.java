package com.jaimemartz.playerbalancer.settings.props;

import com.google.common.collect.ImmutableMap;
import com.jaimemartz.playerbalancer.settings.shared.CommandProps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class FallbackCommandProps {
    @Setting
    private boolean active;

    @Setting
    private CommandProps command;

    @Setting("excluded-sections")
    private List<String> excludedSections;

    @Setting
    private boolean restrictive;

    @Setting
    private Map<String, String> rules;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public CommandProps getCommand() {
        return command;
    }

    public void setCommand(CommandProps command) {
        this.command = command;
    }

    public List<String> getExcludedSections() {
        return excludedSections;
    }

    public void setExcludedSections(List<String> excludedSections) {
        this.excludedSections = excludedSections;
    }

    public boolean isRestrictive() {
        return restrictive;
    }

    public void setRestrictive(boolean restrictive) {
        this.restrictive = restrictive;
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public void setRules(Map<String, String> rules) {
        this.rules = rules;
    }

    public FallbackCommandProps _defaults() {
        this.active = true;
        this.command = new CommandProps();
        command.setName("fallback");
        command.setPermission("");
        command.setAliases(Arrays.asList("lobby", "hub", "back"));
        this.excludedSections = Collections.emptyList();
        this.restrictive = true;
        this.rules = ImmutableMap.of("section-from", "section-to");
        return this;
    }

    @Override
    public String toString() {
        return "FallbackCommandProps{" +
                "active=" + active +
                ", command=" + command +
                ", excludedSections=" + excludedSections +
                ", restrictive=" + restrictive +
                ", rules=" + rules +
                '}';
    }
}
