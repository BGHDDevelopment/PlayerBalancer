package com.jaimemartz.playerbalancer.settings.provider;

import com.jaimemartz.playerbalancer.settings.Settings;
import com.jaimemartz.playerbalancer.settings.beans.SectionsHandler;

import javax.inject.Inject;
import javax.inject.Provider;

public class SectionHandlerProvider implements Provider<SectionsHandler> {
    @Inject
    private Settings settings;

    @Override
    public SectionsHandler get() {
        return new SectionsHandler();
        //return settings.getProperty(SectionsHolder.MAP);
    }
}
