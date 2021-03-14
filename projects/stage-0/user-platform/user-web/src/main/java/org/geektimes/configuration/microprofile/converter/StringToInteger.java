package org.geektimes.configuration.microprofile.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.spi.Converter;

public class StringToInteger implements Converter<Integer> {

    @Override
    public Integer convert(String value) throws IllegalArgumentException, NullPointerException {
        if (StringUtils.isBlank(value)) {
            throw new NullPointerException();
        }
        return Integer.valueOf(value);
    }
}
