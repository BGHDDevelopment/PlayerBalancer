package com.jaimemartz.playerbalancer.settings.beans;

import java.util.HashMap;
import java.util.Map;

public final class MapBean {
    private final Map<String, String> map;

    public MapBean() {
        this.map = null;
    }

    public MapBean(Map<String, String> defaults) {
        this.map = new HashMap<>();
        map.putAll(defaults);
    }

    public Map<String, String> getMap() {
        return map;
    }
}
