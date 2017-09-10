package com.jaimemartz.playerbalancer.settings.props;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class GeneralProps {
    @Setting
    private boolean active;

    @Setting
    private boolean silent;

    @Setting(value = "auto-reload")
    private boolean autoReload;

    @Setting(value = "redis-bungee")
    private boolean redisBungee;

    @Setting(value = "assign-targets")
    private boolean assignTargets;

    @Setting(value = "fallback-principal")
    private boolean fallbackPrincipal;

    @Setting(value = "auto-refresh")
    private boolean autoRefresh;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public boolean isAutoReload() {
        return autoReload;
    }

    public void setAutoReload(boolean autoReload) {
        this.autoReload = autoReload;
    }

    public boolean isRedisBungee() {
        return redisBungee;
    }

    public void setRedisBungee(boolean redisBungee) {
        this.redisBungee = redisBungee;
    }

    public boolean isAssignTargets() {
        return assignTargets;
    }

    public void setAssignTargets(boolean assignTargets) {
        this.assignTargets = assignTargets;
    }

    public boolean isFallbackPrincipal() {
        return fallbackPrincipal;
    }

    public void setFallbackPrincipal(boolean fallbackPrincipal) {
        this.fallbackPrincipal = fallbackPrincipal;
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

    public GeneralProps _defaults() {
        this.active = false;
        this.silent = false;
        this.autoReload = true;
        this.redisBungee = false;
        this.assignTargets = false;
        this.fallbackPrincipal = true;
        this.autoRefresh = false;
        return this;
    }

    @Override
    public String toString() {
        return "GeneralProps{" +
                "active=" + active +
                ", silent=" + silent +
                ", autoReload=" + autoReload +
                ", redisBungee=" + redisBungee +
                ", assignTargets=" + assignTargets +
                ", fallbackPrincipal=" + fallbackPrincipal +
                ", autoRefresh=" + autoRefresh +
                '}';
    }
}
