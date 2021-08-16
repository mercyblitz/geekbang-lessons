/**
 *
 */
package org.geektimes.commons.lang.util;

import org.geektimes.commons.constants.FileSuffixConstants;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

/**
 * {@link ClassLoaderUtils} {@link Test}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see ClassLoaderUtils
 * @since 1.0.0
 */
public class ClassLoaderUtilsTest  {

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    @Test
    public void testResolve() {
        String resourceName = "META-INF/abc/def";
        String expectedResourceName = "META-INF/abc/def";
        String resolvedResourceName = ClassLoaderUtils.ResourceType.DEFAULT.resolve(resourceName);
        assertEquals(expectedResourceName, resolvedResourceName);

        resourceName = "///////META-INF//abc\\/def";
        resolvedResourceName = ClassLoaderUtils.ResourceType.DEFAULT.resolve(resourceName);
        assertEquals(expectedResourceName, resolvedResourceName);

        resourceName = "java.lang.String.class";

        expectedResourceName = "java/lang/String.class";
        resolvedResourceName = ClassLoaderUtils.ResourceType.CLASS.resolve(resourceName);
        assertEquals(expectedResourceName, resolvedResourceName);

        resourceName = "java.lang";
        expectedResourceName = "java/lang/";
        resolvedResourceName = ClassLoaderUtils.ResourceType.PACKAGE.resolve(resourceName);
        assertEquals(expectedResourceName, resolvedResourceName);

    }

    @Test
    public void testGetClassResource() {
        URL classResourceURL = ClassLoaderUtils.getClassResource(classLoader, ClassLoaderUtilsTest.class);
        assertNotNull(classResourceURL);

        classResourceURL = ClassLoaderUtils.getClassResource(classLoader, String.class.getName());
        assertNotNull(classResourceURL);
    }

    @Test
    public void testGetResource() {
        URL resourceURL = ClassLoaderUtils.getResource(classLoader, ClassLoaderUtilsTest.class.getName() + FileSuffixConstants.CLASS);
        assertNotNull(resourceURL);

        resourceURL = ClassLoaderUtils.getResource(classLoader, "///java/lang/CharSequence.class");
        assertNotNull(resourceURL);

        resourceURL = ClassLoaderUtils.getResource(classLoader, "//META-INF/services/java.lang.CharSequence");
        assertNotNull(resourceURL);
    }

    @Test
    public void testGetResources() throws IOException {
        Set<URL> resourceURLs = ClassLoaderUtils.getResources(classLoader, ClassLoaderUtilsTest.class.getName() + FileSuffixConstants.CLASS);
        assertNotNull(resourceURLs);
        assertEquals(1, resourceURLs.size());

        resourceURLs = ClassLoaderUtils.getResources(classLoader, "///java/lang/CharSequence.class");
        assertNotNull(resourceURLs);
        assertEquals(1, resourceURLs.size());

        resourceURLs = ClassLoaderUtils.getResources(classLoader, "//META-INF/services/java.lang.CharSequence");
        assertNotNull(resourceURLs);
        assertEquals(1, resourceURLs.size());
    }

    @Test
    public void testClassLoadingMXBean() {
        ClassLoadingMXBean classLoadingMXBean = ClassLoaderUtils.classLoadingMXBean;
        assertEquals(classLoadingMXBean.getTotalLoadedClassCount(), ClassLoaderUtils.getTotalLoadedClassCount());
        assertEquals(classLoadingMXBean.getLoadedClassCount(), ClassLoaderUtils.getLoadedClassCount());
        assertEquals(classLoadingMXBean.getUnloadedClassCount(), ClassLoaderUtils.getUnloadedClassCount());
        assertEquals(classLoadingMXBean.isVerbose(), ClassLoaderUtils.isVerbose());

        ClassLoaderUtils.setVerbose(true);
        assertTrue(ClassLoaderUtils.isVerbose());
    }

    @Test
    public void testGetInheritableClassLoaders() {
        Set<ClassLoader> classLoaders = ClassLoaderUtils.getInheritableClassLoaders(classLoader);
        assertNotNull(classLoaders);
        assertTrue(classLoaders.size() > 1);
    }

    @Test
    public void testGetLoadedClasses() {
        Set<Class<?>> classesSet = ClassLoaderUtils.getLoadedClasses(classLoader);
        assertNotNull(classesSet);
        Assert.assertFalse(classesSet.isEmpty());


        classesSet = ClassLoaderUtils.getLoadedClasses(ClassLoader.getSystemClassLoader());
        assertNotNull(classesSet);
        Assert.assertFalse(classesSet.isEmpty());
    }

    @Test
    public void testGetAllLoadedClasses() {
        Set<Class<?>> classesSet = ClassLoaderUtils.getAllLoadedClasses(classLoader);
        assertNotNull(classesSet);
        Assert.assertFalse(classesSet.isEmpty());


        classesSet = ClassLoaderUtils.getAllLoadedClasses(ClassLoader.getSystemClassLoader());
        assertNotNull(classesSet);
        Assert.assertFalse(classesSet.isEmpty());
    }

    @Test
    public void testGetAllLoadedClassesMap() {
        Map<ClassLoader, Set<Class<?>>> allLoadedClassesMap = ClassLoaderUtils.getAllLoadedClassesMap(classLoader);
        assertNotNull(allLoadedClassesMap);
        assertFalse(allLoadedClassesMap.isEmpty());
    }

    @Test
    public void testFindLoadedClass() {

        Class<?> type = null;
        for (Class<?> class_ : ClassLoaderUtils.getAllLoadedClasses(classLoader)) {
            type = ClassLoaderUtils.findLoadedClass(classLoader, class_.getName());
            assertEquals(class_, type);
        }

        type = ClassLoaderUtils.findLoadedClass(classLoader, String.class.getName());
        assertEquals(String.class, type);

        type = ClassLoaderUtils.findLoadedClass(classLoader, Double.class.getName());
        assertEquals(Double.class, type);
    }

    @Test
    public void testIsLoadedClass() {
        assertTrue(ClassLoaderUtils.isLoadedClass(classLoader, String.class));
        assertTrue(ClassLoaderUtils.isLoadedClass(classLoader, Double.class));
        assertTrue(ClassLoaderUtils.isLoadedClass(classLoader, Double.class.getName()));
    }


    @Test
    public void testFindLoadedClassesInClassPath() {
        Double d = null;
        Set<Class<?>> allLoadedClasses = ClassLoaderUtils.findLoadedClassesInClassPath(classLoader);

        Set<Class<?>> classesSet = ClassLoaderUtils.getAllLoadedClasses(classLoader);

        Set<Class<?>> remainingClasses = new LinkedHashSet<>(allLoadedClasses);

        remainingClasses.addAll(classesSet);

        Set<Class<?>> sortedClasses = new TreeSet<>(new ClassComparator());
        sortedClasses.addAll(remainingClasses);

        int loadedClassesSize = allLoadedClasses.size() + classesSet.size();

        int loadedClassCount = ClassLoaderUtils.getLoadedClassCount();

    }

    @Test
    public void testGetCount() {
        long count = ClassLoaderUtils.getTotalLoadedClassCount();
        assertTrue(count > 0);

        count = ClassLoaderUtils.getLoadedClassCount();
        assertTrue(count > 0);

        count = ClassLoaderUtils.getUnloadedClassCount();
        assertTrue(count > -1);
    }

    @Test
    public void testFindLoadedClassesInClassPaths() {
        Set<Class<?>> allLoadedClasses = ClassLoaderUtils.findLoadedClassesInClassPaths(classLoader, ClassPathUtils.getClassPaths());
        Assert.assertFalse(allLoadedClasses.isEmpty());
    }


    private static class ClassComparator implements Comparator<Class<?>> {

        @Override
        public int compare(Class<?> o1, Class<?> o2) {
            String cn1 = o1.getName();
            String cn2 = o2.getName();
            return cn1.compareTo(cn2);
        }
    }

}
