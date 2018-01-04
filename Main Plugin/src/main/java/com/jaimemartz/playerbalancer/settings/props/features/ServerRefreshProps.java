package com.jaimemartz.playerbalancer.settings.props.features;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ServerRefreshProps {
    @Setting
    private boolean enabled;

    @Setting
    private int delay;

    @Setting
    private int interval;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "ServerRefreshProps{" +
                "enabled=" + enabled +
                ", delay=" + delay +
                ", interval=" + interval +
                '}';
    }
}
