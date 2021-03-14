package org.geektimes.configuration.microprofile.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.spi.Converter;

public class StringToByte implements Converter<Byte> {

    @Override
    public Byte convert(String value) throws IllegalArgumentException, NullPointerException {
        if (StringUtils.isBlank(value)) {
            throw new NullPointerException();
        }
        return Byte.valueOf(value);
    }
}
