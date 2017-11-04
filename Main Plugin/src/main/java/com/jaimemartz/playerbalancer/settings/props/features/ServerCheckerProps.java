package com.jaimemartz.playerbalancer.settings.props.features;

import com.jaimemartz.playerbalancer.ping.PingTactic;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ServerCheckerProps {
    @Setting
    private boolean enabled;

    @Setting
    private PingTactic tactic;

    @Setting
    private int attempts;

    @Setting
    private int interval;

    @Setting
    private int timeout;

    @Setting(value = "marker-descs")
    private List<String> markerDescs;

    @Setting(value = "debug-info")
    private boolean debug;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public PingTactic getTactic() {
        return tactic;
    }

    public void setTactic(PingTactic tactic) {
        this.tactic = tactic;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public List<String> getMarkerDescs() {
        return markerDescs;
    }

    public void setMarkerDescs(List<String> markerDescs) {
        this.markerDescs = markerDescs;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public String toString() {
        return "ServerCheckerProps{" +
                "enabled=" + enabled +
                ", tactic=" + tactic +
                ", attempts=" + attempts +
                ", interval=" + interval +
                ", timeout=" + timeout +
                ", markerDescs=" + markerDescs +
                ", debug=" + debug +
                '}';
    }
}
