package com.jaimemartz.playerbalancer.settings.provider;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import com.jaimemartz.playerbalancer.settings.types.*;
import net.md_5.bungee.api.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.io.IOException;

public class SettingsProvider implements Provider<SettingsManager> {
    @Inject
    private Plugin plugin;

    @Override
    public SettingsManager get() {
        plugin.getDataFolder().mkdir();

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                boolean result = configFile.createNewFile();
                if (!result) {
                    throw new IllegalStateException("Could not create a new file");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PropertyResource resource = new YamlFileResource(configFile);
        ConfigurationData configurationData = ConfigurationDataBuilder.collectData(
                GeneralProperties.class, CheckerProperties.class, ReconnectorProperties.class,
                CommandProperties.class, MessageProperties.class, SectionsHolder.class
        );

        return new SettingsManager(resource, new PlainMigrationService(), configurationData);
    }
}
