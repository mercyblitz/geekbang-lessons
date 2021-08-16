/**
 *
 */
package org.geektimes.commons.lang.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static org.geektimes.commons.constants.SystemConstants.PATH_SEPARATOR;

/**
 * {@link ClassPathUtils}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see ClassPathUtils
 * @since 1.0.0
 */
public abstract class ClassPathUtils {

    protected static final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

    private static final Set<String> bootstrapClassPaths = initBootstrapClassPaths();

    private static final Set<String> classPaths = initClassPaths();


    private static Set<String> initBootstrapClassPaths() {
        Set<String> bootstrapClassPaths = Collections.emptySet();
        if (runtimeMXBean.isBootClassPathSupported()) {
            bootstrapClassPaths = resolveClassPaths(runtimeMXBean.getBootClassPath());
        }
        return unmodifiableSet(bootstrapClassPaths);
    }

    private static Set<String> initClassPaths() {
        return resolveClassPaths(runtimeMXBean.getClassPath());
    }

    private static Set<String> resolveClassPaths(String classPath) {
        Set<String> classPaths = new LinkedHashSet<>();
        String[] classPathsArray = StringUtils.split(classPath, PATH_SEPARATOR);
        classPaths.addAll(Arrays.asList(classPathsArray));
        return unmodifiableSet(classPaths);
    }


    /**
     * Get Bootstrap Class Paths {@link Set}
     *
     * @return If {@link RuntimeMXBean#isBootClassPathSupported()} == <code>false</code>, will return empty set.
     * @version 1.0.0
     * @since 1.0.0
     **/
    
    public static Set<String> getBootstrapClassPaths() {
        return bootstrapClassPaths;
    }

    /**
     * Get {@link #classPaths}
     *
     * @return Class Paths {@link Set}
     * @version 1.0.0
     * @since 1.0.0
     **/
    
    public static Set<String> getClassPaths() {
        return classPaths;
    }

    /**
     * Get Class Location URL from specified class name at runtime
     *
     * @param className
     *         class name
     * @return If <code>className</code> associated class is loaded on {@link Thread#getContextClassLoader() Thread
     * context ClassLoader} , return class location URL, or return <code>null</code>
     * @see #getRuntimeClassLocation(Class)
     */
    public static URL getRuntimeClassLocation(String className) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL location = null;
        if (classLoader != null) {
            if (ClassLoaderUtils.isLoadedClass(classLoader, className)) {
                try {
                    location = getRuntimeClassLocation(classLoader.loadClass(className));
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
        return location;
    }

    /**
     * Get Class Location URL from specified {@link Class} at runtime
     *
     * @param type
     *         {@link Class}
     * @return If <code>type</code> is <code>{@link Class#isPrimitive() primitive type}</code>, <code>{@link
     * Class#isArray() array type}</code>, <code>{@link Class#isSynthetic() synthetic type}</code> or {a security
     * manager exists and its <code>checkPermission</code> method doesn't allow getting the ProtectionDomain., return
     * <code>null</code>
     */
    public static URL getRuntimeClassLocation(Class<?> type) {
        ClassLoader classLoader = type.getClassLoader();
        URL location = null;
        if (classLoader != null) { // Non-Bootstrap
            try {
                ProtectionDomain protectionDomain = type.getProtectionDomain();
                CodeSource codeSource = protectionDomain.getCodeSource();
                location = codeSource == null ? null : codeSource.getLocation();
            } catch (SecurityException exception) {

            }
        } else if (!type.isPrimitive() && !type.isArray() && !type.isSynthetic()) { // Bootstrap ClassLoader
            // Class was loaded by Bootstrap ClassLoader
            location = ClassLoaderUtils.getClassResource(ClassLoader.getSystemClassLoader(), type.getName());
        }
        return location;
    }
}
