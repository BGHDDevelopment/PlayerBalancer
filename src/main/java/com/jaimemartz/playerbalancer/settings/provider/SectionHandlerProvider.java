package com.jaimemartz.playerbalancer.settings.provider;

import ch.jalu.configme.SettingsManager;
import com.jaimemartz.playerbalancer.settings.beans.SectionHandler;
import com.jaimemartz.playerbalancer.settings.types.SectionsHolder;

import javax.inject.Inject;
import javax.inject.Provider;

public class SectionHandlerProvider implements Provider<SectionHandler> {
    @Inject
    private SettingsManager settings;

    @Override
    public SectionHandler get() {
        return settings.getProperty(SectionsHolder.SECTION_HOLDER);
    }
}
