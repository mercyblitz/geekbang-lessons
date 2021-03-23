package org.geektimes.configuration.microprofile.config.converter;

import java.math.BigDecimal;

public class BigDecimalConverter extends AbstractConverter<BigDecimal> {

    @Override
    protected BigDecimal doConvert(String value) {
        return new BigDecimal(value);
    }
}
