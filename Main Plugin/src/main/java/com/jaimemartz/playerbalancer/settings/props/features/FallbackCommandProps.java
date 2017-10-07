package com.jaimemartz.playerbalancer.settings.props.features;

import com.jaimemartz.playerbalancer.settings.props.shared.CommandProps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class FallbackCommandProps {
    @Setting
    private boolean enabled;

    @Setting
    private CommandProps command;

    @Setting(value = "excluded-sections")
    private List<String> excludedSections;

    @Setting
    private boolean restrictive;

    @Setting
    private Map<String, String> rules;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    @Override
    public String toString() {
        return "FallbackCommandProps{" +
                "enabled=" + enabled +
                ", command=" + command +
                ", excludedSections=" + excludedSections +
                ", restrictive=" + restrictive +
                ", rules=" + rules +
                '}';
    }
}
