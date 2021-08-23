/**
 *
 */
package org.geektimes.commons.net.util;

import junit.framework.Assert;
import org.geektimes.commons.constants.FileSuffixConstants;
import org.geektimes.commons.lang.util.ClassLoaderUtils;
import org.geektimes.commons.lang.util.StringUtils;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.geektimes.commons.collection.util.MapUtils.newLinkedHashMap;
import static org.geektimes.commons.constants.SystemConstants.JAVA_HOME;
import static org.geektimes.commons.net.util.URLUtils.resolveArchiveFile;
import static org.junit.Assert.*;

/**
 * {@link URLUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see URLUtilsTest
 * @since 1.0.0
 */
public class URLUtilsTest {

    @Test
    public void testEncodeAndDecode() {
        String path = "/abc/def";

        String encodedPath = URLUtils.encode(path);
        String decodedPath = URLUtils.decode(encodedPath);
        Assert.assertEquals(path, decodedPath);

        encodedPath = URLUtils.encode(path, "GBK");
        decodedPath = URLUtils.decode(encodedPath, "GBK");
        Assert.assertEquals(path, decodedPath);
    }

    @Test
    public void testResolvePath() {
        String path = null;
        String expectedPath = null;
        String resolvedPath = null;

        resolvedPath = URLUtils.resolvePath(path);
        Assert.assertEquals(expectedPath, resolvedPath);

        path = "";
        expectedPath = "";
        resolvedPath = URLUtils.resolvePath(path);
        Assert.assertEquals(expectedPath, resolvedPath);

        path = "/abc/";
        expectedPath = "/abc/";
        resolvedPath = URLUtils.resolvePath(path);
        Assert.assertEquals(expectedPath, resolvedPath);

        path = "//abc///";
        expectedPath = "/abc/";
        resolvedPath = URLUtils.resolvePath(path);
        Assert.assertEquals(expectedPath, resolvedPath);


        path = "//\\abc///";
        expectedPath = "/abc/";
        resolvedPath = URLUtils.resolvePath(path);
        Assert.assertEquals(expectedPath, resolvedPath);
    }

    @Test
    public void testResolveRelativePath() throws MalformedURLException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL resourceURL = ClassLoaderUtils.getClassResource(classLoader, String.class);
        String expectedPath = "java/lang/String.class";
        String relativePath = URLUtils.resolveRelativePath(resourceURL);
        assertEquals(expectedPath, relativePath);

        File rtJarFile = new File(JAVA_HOME, "lib/rt.jar");
        resourceURL = rtJarFile.toURI().toURL();
        relativePath = URLUtils.resolveRelativePath(resourceURL);
        assertNull(relativePath);

    }

    @Test
    public void testResolveArchiveFile() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL resourceURL = ClassLoaderUtils.getClassResource(classLoader, String.class);
        File archiveFile = URLUtils.resolveArchiveFile(resourceURL, FileSuffixConstants.JAR);
        assertTrue(archiveFile.exists());
    }

    @Test
    public void testResolveParametersMap() {
        String url = "https://www.google.com.hk/search?q=java&oq=java&sourceid=chrome&es_sm=122&ie=UTF-8";
        Map<String, List<String>> parametersMap = URLUtils.resolveParametersMap(url);
        Map<String, List<String>> expectedParametersMap = newLinkedHashMap();
        expectedParametersMap.put("q", Arrays.asList("java"));
        expectedParametersMap.put("oq", Arrays.asList("java"));
        expectedParametersMap.put("sourceid", Arrays.asList("chrome"));
        expectedParametersMap.put("es_sm", Arrays.asList("122"));
        expectedParametersMap.put("ie", Arrays.asList("UTF-8"));

        assertEquals(expectedParametersMap, parametersMap);

        url = "https://www.google.com.hk/search";
        parametersMap = URLUtils.resolveParametersMap(url);
        expectedParametersMap = Collections.emptyMap();
        assertEquals(expectedParametersMap, parametersMap);

        url = "https://www.google.com.hk/search?";
        parametersMap = URLUtils.resolveParametersMap(url);
        expectedParametersMap = Collections.emptyMap();
        assertEquals(expectedParametersMap, parametersMap);
    }

    @Test
    public void testIsDirectoryURL() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resourceURL = ClassLoaderUtils.getClassResource(classLoader, StringUtils.class);
        assertFalse(URLUtils.isDirectoryURL(resourceURL));

        String externalForm = null;
        externalForm = StringUtils.substringBeforeLast(resourceURL.toExternalForm(), StringUtils.class.getSimpleName() + ".class");
        resourceURL = new URL(externalForm);
        assertTrue(URLUtils.isDirectoryURL(resourceURL));

        resourceURL = ClassLoaderUtils.getClassResource(classLoader, String.class);
        assertFalse(URLUtils.isDirectoryURL(resourceURL));

        resourceURL = ClassLoaderUtils.getClassResource(classLoader, getClass());
        assertFalse(URLUtils.isDirectoryURL(resourceURL));


        externalForm = StringUtils.substringBeforeLast(resourceURL.toExternalForm(), getClass().getSimpleName() + ".class");
        resourceURL = new URL(externalForm);
        assertTrue(URLUtils.isDirectoryURL(resourceURL));

    }

    @Test
    public void testResolveArchiveFile2() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL resourceURL = ClassLoaderUtils.getClassResource(classLoader, String.class);
        File archiveFile = resolveArchiveFile(resourceURL);
        assertTrue(archiveFile.isFile());

        resourceURL = ClassLoaderUtils.getClassResource(classLoader, StringUtils.class);

        archiveFile = resolveArchiveFile(resourceURL);
        assertTrue(archiveFile.isDirectory());
    }
}
