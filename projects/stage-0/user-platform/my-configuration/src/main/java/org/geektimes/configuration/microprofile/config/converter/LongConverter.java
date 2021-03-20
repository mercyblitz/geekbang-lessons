package org.geektimes.configuration.microprofile.config.converter;

public class LongConverter extends AbstractConverter<Long> {

    @Override
    protected Long doConvert(String value) {
        return Long.valueOf(value);
    }
}
