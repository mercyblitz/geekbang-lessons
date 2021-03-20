package org.geektimes.configuration.microprofile.config.converter;

public class BooleanConverter extends AbstractConverter<Boolean> {

    @Override
    protected Boolean doConvert(String value) {
        return Boolean.parseBoolean(value);
    }
}
