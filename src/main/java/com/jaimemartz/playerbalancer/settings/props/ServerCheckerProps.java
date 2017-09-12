package com.jaimemartz.playerbalancer.settings.props;

import com.jaimemartz.playerbalancer.ping.PingTactic;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Arrays;
import java.util.List;

@ConfigSerializable
public class ServerCheckerProps {
    @Setting
    private boolean active;

    @Setting
    private PingTactic tactic;

    @Setting
    private int attempts;

    @Setting
    private int interval;

    @Setting("marker-descs")
    private List<String> markerDescs;

    @Setting(value = "debug-info")
    private boolean debug;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public ServerCheckerProps __defaults() {
        this.active = true;
        this.tactic = PingTactic.CUSTOM;
        this.attempts = 5;
        this.interval = 10000;
        this.markerDescs = Arrays.asList("Server is not accessible", "Gamemode has already started");
        this.debug = false;
        return this;
    }

    @Override
    public String toString() {
        return "ServerCheckerProps{" +
                "active=" + active +
                ", tactic=" + tactic +
                ", attempts=" + attempts +
                ", interval=" + interval +
                ", markerDescs=" + markerDescs +
                ", debug=" + debug +
                '}';
    }
}
