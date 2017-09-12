package com.jaimemartz.playerbalancer.settings.props.features;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class KickHandlerProps {
    @Setting
    private boolean enabled;

    @Setting
    private boolean inverted;

    @Setting
    private List<String> reasons;

    @Setting(value = "excluded-sections")
    private List<String> excludedSections;

    @Setting
    private boolean restricted;

    @Setting(value = "force-principal")
    private boolean forcePrincipal;

    @Setting
    private Map<String, String> rules;

    @Setting(value = "debug-info")
    private boolean debug;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public List<String> getExcludedSections() {
        return excludedSections;
    }

    public void setExcludedSections(List<String> excludedSections) {
        this.excludedSections = excludedSections;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public boolean isForcePrincipal() {
        return forcePrincipal;
    }

    public void setForcePrincipal(boolean forcePrincipal) {
        this.forcePrincipal = forcePrincipal;
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public void setRules(Map<String, String> rules) {
        this.rules = rules;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public String toString() {
        return "KickHandlerProps{" +
                "enabled=" + enabled +
                ", inverted=" + inverted +
                ", reasons=" + reasons +
                ", excludedSections=" + excludedSections +
                ", restricted=" + restricted +
                ", forcePrincipal=" + forcePrincipal +
                ", rules=" + rules +
                ", debug=" + debug +
                '}';
    }
}
