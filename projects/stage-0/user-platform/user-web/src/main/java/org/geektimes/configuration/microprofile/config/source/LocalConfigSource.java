package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 本地文件配置源
 */
public class LocalConfigSource implements ConfigSource {

    private static final String ORDINAL = "300";

    private final Map<String, String> properties;

    public LocalConfigSource() {
        URL propertiesFileURL = this.getClass().getClassLoader().getResource("local.properties");
        Properties localProperties = new Properties();
        try {
            localProperties.load(propertiesFileURL.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.properties = new HashMap<>(localProperties.size());
        for (String propertyName: localProperties.stringPropertyNames()) {
            this.properties.put(propertyName, localProperties.getProperty(propertyName));
        }
        this.properties.put(CONFIG_ORDINAL, ORDINAL);
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public String getName() {
        return "Java System Properties";
    }
}
