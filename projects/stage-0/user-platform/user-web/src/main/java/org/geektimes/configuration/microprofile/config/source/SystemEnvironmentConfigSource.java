package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 系统环境变量配置源
 */
public class SystemEnvironmentConfigSource implements ConfigSource {

    private static final String ORDINAL = "100";

    private final Map<String, String> properties;

    public SystemEnvironmentConfigSource() {
        Map environmentProperties = System.getenv();
        this.properties = new HashMap<>(environmentProperties);
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
