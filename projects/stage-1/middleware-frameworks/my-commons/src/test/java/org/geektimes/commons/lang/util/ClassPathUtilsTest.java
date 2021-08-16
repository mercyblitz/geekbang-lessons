/**
 *
 */
package org.geektimes.commons.lang.util;

import junit.framework.Assert;
import org.geektimes.commons.reflect.util.ClassUtils;
import org.junit.Test;

import java.net.URL;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * {@link ClassPathUtils} {@link Test}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see ClassPathUtilsTest
 * @since 1.0.0
 */
public class ClassPathUtilsTest {

    @Test
    public void testGetBootstrapClassPaths() {
        Set<String> bootstrapClassPaths = ClassPathUtils.getBootstrapClassPaths();
        assertNotNull(bootstrapClassPaths);
        assertFalse(bootstrapClassPaths.isEmpty());
    }

    @Test
    public void testGetClassPaths() {
        Set<String> classPaths = ClassPathUtils.getClassPaths();
        assertNotNull(classPaths);
        Assert.assertFalse(classPaths.isEmpty());
    }

    @Test
    public void getRuntimeClassLocation() {
        URL location = null;
        location = ClassPathUtils.getRuntimeClassLocation(String.class);
        assertNotNull(location);

        location = ClassPathUtils.getRuntimeClassLocation(getClass());
        assertNotNull(location);

        //Primitive type
        location = ClassPathUtils.getRuntimeClassLocation(int.class);
        assertNull(location);

        //Array type
        location = ClassPathUtils.getRuntimeClassLocation(int[].class);
        assertNull(location);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Set<String> classNames = ClassUtils.getAllClassNamesInClassPaths();
        for (String className : classNames) {
            if (!ClassLoaderUtils.isLoadedClass(classLoader, className)) {
                location = ClassPathUtils.getRuntimeClassLocation(className);
                Assert.assertNull(location);
            }
        }

    }
}
