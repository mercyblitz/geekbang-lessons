/**
 *
 */
package org.geektimes.commons.jar.util;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.geektimes.commons.lang.util.ClassLoaderUtils;
import org.geektimes.commons.lang.util.ClassPathUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static org.geektimes.commons.constants.SystemConstants.JAVA_HOME;
import static org.geektimes.commons.constants.SystemConstants.JAVA_IO_TMPDIR;

/**
 * {@link JarUtils} {@link TestCase}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see JarUtilsTest
 * @since 1.0.0
 */
public class JarUtilsTest {

    private final static File tempDirectory = new File(JAVA_IO_TMPDIR);
    private final static File targetDirectory = new File(tempDirectory, "jar-util-extract");
    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    @Test
    public void testResolveRelativePath() {
        URL resourceURL = ClassLoaderUtils.getClassResource(classLoader, String.class);
        String relativePath = JarUtils.resolveRelativePath(resourceURL);
        String expectedPath = "java/lang/String.class";
        Assert.assertEquals(expectedPath, relativePath);
    }

    @Test
    public void testResolveJarAbsolutePath() throws Exception {
        URL resourceURL = ClassLoaderUtils.getClassResource(classLoader, String.class);
        String jarAbsolutePath = JarUtils.resolveJarAbsolutePath(resourceURL);
        File rtJarFile = new File(JAVA_HOME, "/lib/rt.jar");
        Assert.assertNotNull(jarAbsolutePath);
        Assert.assertEquals(rtJarFile.getAbsolutePath(), jarAbsolutePath);
    }

    @Test
    public void testToJarFile() throws Exception {
        URL resourceURL = ClassLoaderUtils.getClassResource(classLoader, String.class);
        JarFile jarFile = JarUtils.toJarFile(resourceURL);
        JarFile rtJarFile = new JarFile(new File(JAVA_HOME, "/lib/rt.jar"));
        Assert.assertNotNull(jarFile);
        Assert.assertEquals(rtJarFile.getName(), jarFile.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToJarFileOnException() throws Exception {
        URL url = new URL("http://www.google.com");
        JarFile jarFile = JarUtils.toJarFile(url);
    }

    @Test
    public void testFindJarEntry() throws Exception {
        URL resourceURL = ClassLoaderUtils.getClassResource(classLoader, String.class);
        JarEntry jarEntry = JarUtils.findJarEntry(resourceURL);
        Assert.assertNotNull(jarEntry);
    }

    @Before
    public void init() throws IOException {
        FileUtils.deleteDirectory(targetDirectory);
        targetDirectory.mkdirs();
    }

    @Test
    public void testExtract() throws IOException {
        Set<String> classPaths = ClassPathUtils.getBootstrapClassPaths();
        for (String classPath : classPaths) {
            File jarFile = new File(classPath);
            if (jarFile.exists()) {
                JarUtils.extract(jarFile, targetDirectory);
                break;
            }
        }
    }

    @Test
    public void testExtractWithURL() throws IOException {
        URL resourceURL = ClassLoaderUtils.getResource(classLoader, ClassLoaderUtils.ResourceType.PACKAGE, "org.apache.commons.lang3");
        JarUtils.extract(resourceURL, targetDirectory, ZipEntry::isDirectory);
    }
}
