package com.jaimemartz.playerbalancer.settings.props;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class GeneralProps {
    @Setting
    private boolean enabled;

    @Setting
    private boolean silent;

    @Setting(value = "auto-reload")
    private boolean autoReload;

    @Setting(value = "redis-bungee")
    private boolean redisBungee;

    @Setting(value = "fallback-principal")
    private boolean fallbackPrincipal;

    @Setting
    private String version;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public boolean isFallbackPrincipal() {
        return fallbackPrincipal;
    }

    public void setFallbackPrincipal(boolean fallbackPrincipal) {
        this.fallbackPrincipal = fallbackPrincipal;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "GeneralProps{" +
                "enabled=" + enabled +
                ", silent=" + silent +
                ", autoReload=" + autoReload +
                ", redisBungee=" + redisBungee +
                ", fallbackPrincipal=" + fallbackPrincipal +
                ", version='" + version + '\'' +
                '}';
    }
}
