package org.geektimes.configuration.microprofile.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.spi.Converter;

public class StringToBoolean implements Converter<Boolean> {

    @Override
    public Boolean convert(String value) throws IllegalArgumentException, NullPointerException {
        if (StringUtils.isBlank(value)) {
            throw new NullPointerException();
        }
        return Boolean.valueOf(value);
    }
}
