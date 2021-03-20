package org.geektimes.configuration.microprofile.config.converter;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * {@link Converters} Test
 */
public class ConvertersTest {

    private Converters converters;

    @BeforeClass
    public static void prepare() {

    }

    @Before
    public void init() {
        converters = new Converters();
    }

    @Test
    public void testResolveConvertedType() {
        assertEquals(Byte.class, converters.resolveConvertedType(new ByteConverter()));
        assertEquals(Short.class, converters.resolveConvertedType(new ShortConverter()));
        assertEquals(Integer.class, converters.resolveConvertedType(new IntegerConverter()));
        assertEquals(Long.class, converters.resolveConvertedType(new LongConverter()));
        assertEquals(Float.class, converters.resolveConvertedType(new FloatConverter()));
        assertEquals(Double.class, converters.resolveConvertedType(new DoubleConverter()));
        assertEquals(String.class, converters.resolveConvertedType(new StringConverter()));
    }
}
