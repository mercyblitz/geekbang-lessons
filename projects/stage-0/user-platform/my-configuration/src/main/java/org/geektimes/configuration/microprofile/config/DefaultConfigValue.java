package org.geektimes.configuration.microprofile.config;

import org.eclipse.microprofile.config.ConfigValue;

/**
 * 默认实现 {@link ConfigValue}
 */
class DefaultConfigValue implements ConfigValue {

    private final String name;

    private final String value;

    private final String rawValue;

    private final String sourceName;

    private final int sourceOrdinal;

    DefaultConfigValue(String name, String value, String rawValue, String sourceName, int sourceOrdinal) {
        this.name = name;
        this.value = value;
        this.rawValue = rawValue;
        this.sourceName = sourceName;
        this.sourceOrdinal = sourceOrdinal;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getRawValue() {
        return rawValue;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public int getSourceOrdinal() {
        return sourceOrdinal;
    }
}
