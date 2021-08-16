/**
 * Confucius commons project
 */
package org.geektimes.commons.reflect.util;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * {@link SimpleClassScanner} Test
 *
 * @author <a href="mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see SimpleClassScanner
 * @since 1.0.0
 */
public class SimpleClassScannerTest {

    private SimpleClassScanner simpleClassScanner = SimpleClassScanner.INSTANCE;

    @Test
    public void testScan() {
        ClassLoader classLoader = getClass().getClassLoader();
        Set<Class<?>> classesSet = simpleClassScanner.scan(classLoader, "org.geektimes.commons.constants");
        assertFalse(classesSet.isEmpty());

        classesSet = simpleClassScanner.scan(classLoader, "org.geektimes.commons.reflect.util");
        assertFalse(classesSet.isEmpty());
    }
}
