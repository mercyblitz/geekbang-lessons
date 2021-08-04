package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableSet;

public class DefaultResourceConfigSources implements ConfigSource {

    private static final String configFileLocation = "META-INF/microprofile-config.properties";

    private static final String CONFIG_ORDINAL_PROPERTY_NAME = "config_ordinal";

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final List<ConfigSource> configSources = new LinkedList<>();

    public DefaultResourceConfigSources() {
        initializeConfigSources();
    }

    protected void initializeConfigSources() {
        ClassLoader classLoader = getClass().getClassLoader();
        try {

            Enumeration<URL> resources = classLoader.getResources(configFileLocation);

            // the order or resources by ClassPath
            while (resources.hasMoreElements()) {
                URL resource = classLoader.getResource(configFileLocation);
                if (resource == null) {
                    logger.info("The default config file can't be found in the classpath : " + configFileLocation);
                    return;
                }
                try (InputStream inputStream = resource.openStream()) {
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    String ordinalProperty = properties.getProperty(CONFIG_ORDINAL_PROPERTY_NAME);
                    int ordinal = ordinalProperty == null ? getOrdinal() : Integer.decode(ordinalProperty);
                    MapConfigSource mapConfigSource = new MapConfigSource(resource.toString(), ordinal, properties);
                    configSources.add(mapConfigSource);
                }
            }

            // Sort
            sort(this.configSources, ConfigSourceOrdinalComparator.INSTANCE);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Set<String> getPropertyNames() {
        Set<String> propertyNames = new LinkedHashSet<>();
        // sorted by ordinal
        for (ConfigSource configSource : configSources) {
            Set<String> subPropertyNames = configSource.getPropertyNames();
            for (String subPropertyName : subPropertyNames) {
                if (!propertyNames.contains(subPropertyName)) {
                    propertyNames.add(subPropertyName);
                }
            }
        }
        return unmodifiableSet(propertyNames);
    }

    @Override
    public String getValue(String propertyName) {
        // sorted by ordinal
        String value = null;
        for (ConfigSource configSource : configSources) {
            value = configSource.getValue(propertyName);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    @Override
    public int getOrdinal() {
        return 100;
    }

    @Override
    public String getName() {
        return "Default Config File";
    }
}
