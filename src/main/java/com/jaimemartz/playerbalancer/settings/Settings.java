package com.jaimemartz.playerbalancer.settings;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.resource.PropertyResource;

public class Settings extends SettingsManager {
    public Settings(PropertyResource resource, MigrationService migrationService, ConfigurationData configurationData) {
        super(resource, migrationService, configurationData);
    }
}
