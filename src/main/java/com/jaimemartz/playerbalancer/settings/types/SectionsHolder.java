package com.jaimemartz.playerbalancer.settings.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import com.jaimemartz.playerbalancer.settings.beans.SectionHandler;

import static ch.jalu.configme.properties.PropertyInitializer.newBeanProperty;

public class SectionsHolder implements SettingsHolder {
    public static final Property<SectionHandler> SECTION_HOLDER = newBeanProperty(SectionHandler.class, "", new SectionHandler());
}
