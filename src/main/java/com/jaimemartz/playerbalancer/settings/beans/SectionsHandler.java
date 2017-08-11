package com.jaimemartz.playerbalancer.settings.beans;

import java.util.HashMap;
import java.util.Map;

public class SectionsHandler {
    public Map<String, SectionProperties> sections;

    public SectionsHandler() {

    }

    public SectionsHandler(boolean defaults) {
        if (defaults) {
            sections = new HashMap<>();
            sections.put("test", new SectionProperties());
            sections.put("test31", new SectionProperties());
            sections.put("test321", new SectionProperties());
        }
    }
}
