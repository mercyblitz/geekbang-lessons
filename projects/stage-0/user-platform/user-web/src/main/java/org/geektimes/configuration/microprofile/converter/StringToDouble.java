package org.geektimes.configuration.microprofile.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.spi.Converter;

public class StringToDouble implements Converter<Double> {

    @Override
    public Double convert(String value) throws IllegalArgumentException, NullPointerException {
        if (StringUtils.isBlank(value)) {
            throw new NullPointerException();
        }
        return Double.valueOf(value);
    }
}
