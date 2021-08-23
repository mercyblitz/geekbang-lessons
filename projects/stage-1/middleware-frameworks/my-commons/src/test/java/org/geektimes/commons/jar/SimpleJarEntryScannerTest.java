/**
 *
 */
package org.geektimes.commons.jar;

import junit.framework.Assert;
import org.geektimes.commons.jar.util.JarUtils;
import org.geektimes.commons.lang.util.ClassLoaderUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * {@link SimpleJarEntryScanner} {@link Test}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see SimpleJarEntryScannerTest
 * @since 1.0.0
 */
public class SimpleJarEntryScannerTest {

    private SimpleJarEntryScanner simpleJarEntryScanner = SimpleJarEntryScanner.INSTANCE;

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    @Test
    public void testScan() throws IOException {
        URL resourceURL = ClassLoaderUtils.getClassResource(classLoader, String.class);
        Set<JarEntry> jarEntrySet = simpleJarEntryScanner.scan(resourceURL, true);
        Assert.assertEquals(1, jarEntrySet.size());

        JarFile jarFile = JarUtils.toJarFile(resourceURL);
        jarEntrySet = simpleJarEntryScanner.scan(jarFile, true);
        assertTrue(jarEntrySet.size() > 1000);


        jarEntrySet = simpleJarEntryScanner.scan(jarFile, true, jarEntry ->
                jarEntry.getName().equals("java/lang/String.class")
        );

        assertEquals(1, jarEntrySet.size());

    }
}
