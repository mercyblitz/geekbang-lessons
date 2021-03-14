package org.geektimes.configuration.microprofile.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.spi.Converter;

public class StringToFloat implements Converter<Float> {

    @Override
    public Float convert(String value) throws IllegalArgumentException, NullPointerException {
        if (StringUtils.isBlank(value)) {
            throw new NullPointerException();
        }
        return Float.valueOf(value);
    }
}
