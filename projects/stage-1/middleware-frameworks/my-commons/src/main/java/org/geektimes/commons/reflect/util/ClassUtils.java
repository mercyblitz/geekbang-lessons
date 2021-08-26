/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geektimes.commons.reflect.util;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.geektimes.commons.collection.util.CollectionUtils;
import org.geektimes.commons.constants.Constants;
import org.geektimes.commons.constants.FileSuffixConstants;
import org.geektimes.commons.constants.PathConstants;
import org.geektimes.commons.filter.ClassFileJarEntryFilter;
import org.geektimes.commons.io.util.FileUtils;
import org.geektimes.commons.io.util.SimpleFileScanner;
import org.geektimes.commons.jar.SimpleJarEntryScanner;
import org.geektimes.commons.lang.util.ClassPathUtils;
import org.geektimes.commons.lang.util.StringUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isInterface;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static org.geektimes.commons.collection.util.CollectionUtils.newLinkedHashSet;
import static org.geektimes.commons.collection.util.CollectionUtils.ofSet;
import static org.geektimes.commons.function.Streams.filterAll;
import static org.geektimes.commons.function.ThrowableFunction.execute;
import static org.geektimes.commons.lang.util.ArrayUtils.isEmpty;
import static org.geektimes.commons.lang.util.ArrayUtils.isNotEmpty;
import static org.geektimes.commons.lang.util.ClassLoaderUtils.getClassLoader;

/**
 * {@link Class} Utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class ClassUtils {


    /**
     * Suffix for array class names: "[]"
     */
    public static final String ARRAY_SUFFIX = "[]";

    /**
     * @see {@link Class#ANNOTATION}
     */
    private static final int ANNOTATION = 0x00002000;

    /**
     * @see {@link Class#ENUM}
     */
    private static final int ENUM = 0x00004000;

    /**
     * @see {@link Class#SYNTHETIC}
     */
    private static final int SYNTHETIC = 0x00001000;

    public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    /**
     * Simple Types including:
     * <ul>
     *     <li>{@link Void}</li>
     *     <li>{@link Boolean}</li>
     *     <li>{@link Character}</li>
     *     <li>{@link Byte}</li>
     *     <li>{@link Integer}</li>
     *     <li>{@link Float}</li>
     *     <li>{@link Double}</li>
     *     <li>{@link String}</li>
     *     <li>{@link BigDecimal}</li>
     *     <li>{@link BigInteger}</li>
     *     <li>{@link Date}</li>
     *     <li>{@link Object}</li>
     * </ul>
     *
     * @see javax.management.openmbean.SimpleType
     * @since 1.0.0
     */
    public static final Set<Class<?>> SIMPLE_TYPES = ofSet(
            Void.class,
            Boolean.class,
            Character.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            String.class,
            BigDecimal.class,
            BigInteger.class,
            Date.class,
            Object.class
    );
    /**
     * Prefix for internal array class names: "[L"
     */
    private static final String INTERNAL_ARRAY_PREFIX = "[L";
    /**
     * Map with primitive type name as key and corresponding primitive type as
     * value, for example: "int" -> "int.class".
     */
    private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP = new HashMap<String, Class<?>>(32);
    /**
     * Map with primitive wrapper type as key and corresponding primitive type
     * as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_TYPE_MAP = new HashMap<Class<?>, Class<?>>(16);

    private static final char PACKAGE_SEPARATOR_CHAR = '.';

    static final Map<Class<?>, Boolean> concreteClassCache = new WeakHashMap<>();

    private static final Map<String, Set<String>> classPathToClassNamesMap = initClassPathToClassNamesMap();

    private static final Map<String, String> classNameToClassPathsMap = initClassNameToClassPathsMap();

    private static final Map<String, Set<String>> packageNameToClassNamesMap = initPackageNameToClassNamesMap();

    static {
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Character.class, char.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Double.class, double.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Float.class, float.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Long.class, long.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Short.class, short.class);

        Set<Class<?>> primitiveTypeNames = new HashSet<>(32);
        primitiveTypeNames.addAll(PRIMITIVE_WRAPPER_TYPE_MAP.values());
        primitiveTypeNames.addAll(asList(boolean[].class, byte[].class, char[].class, double[].class,
                float[].class, int[].class, long[].class, short[].class));
        for (Class<?> primitiveTypeName : primitiveTypeNames) {
            PRIMITIVE_TYPE_NAME_MAP.put(primitiveTypeName.getName(), primitiveTypeName);
        }
    }

    private ClassUtils() {
    }

    private static Map<String, Set<String>> initClassPathToClassNamesMap() {
        Map<String, Set<String>> classPathToClassNamesMap = new LinkedHashMap<>();
        Set<String> classPaths = new LinkedHashSet<>();
        classPaths.addAll(ClassPathUtils.getBootstrapClassPaths());
        classPaths.addAll(ClassPathUtils.getClassPaths());
        for (String classPath : classPaths) {
            Set<String> classNames = findClassNamesInClassPath(classPath, true);
            classPathToClassNamesMap.put(classPath, classNames);
        }
        return Collections.unmodifiableMap(classPathToClassNamesMap);
    }

    private static Map<String, String> initClassNameToClassPathsMap() {
        Map<String, String> classNameToClassPathsMap = new LinkedHashMap<>();

        for (Map.Entry<String, Set<String>> entry : classPathToClassNamesMap.entrySet()) {
            String classPath = entry.getKey();
            Set<String> classNames = entry.getValue();
            for (String className : classNames) {
                classNameToClassPathsMap.put(className, classPath);
            }
        }

        return Collections.unmodifiableMap(classNameToClassPathsMap);
    }

    private static Map<String, Set<String>> initPackageNameToClassNamesMap() {
        Map<String, Set<String>> packageNameToClassNamesMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : classNameToClassPathsMap.entrySet()) {
            String className = entry.getKey();
            String packageName = resolvePackageName(className);
            Set<String> classNamesInPackage = packageNameToClassNamesMap.get(packageName);
            if (classNamesInPackage == null) {
                classNamesInPackage = new LinkedHashSet<>();
                packageNameToClassNamesMap.put(packageName, classNamesInPackage);
            }
            classNamesInPackage.add(className);
        }

        return Collections.unmodifiableMap(packageNameToClassNamesMap);
    }

    public static Class<?> forNameWithThreadContextClassLoader(String name)
            throws ClassNotFoundException {
        return forName(name, Thread.currentThread().getContextClassLoader());
    }

    public static Class<?> forNameWithCallerClassLoader(String name, Class<?> caller)
            throws ClassNotFoundException {
        return forName(name, caller.getClassLoader());
    }

    public static ClassLoader getCallerClassLoader(Class<?> caller) {
        return caller.getClassLoader();
    }

    /**
     * Same as <code>Class.forName()</code>, except that it works for primitive
     * types.
     */
    public static Class<?> forName(String name) throws ClassNotFoundException {
        return forName(name, getClassLoader());
    }

    /**
     * Replacement for <code>Class.forName()</code> that also returns Class
     * instances for primitives (like "int") and array class names (like
     * "String[]").
     *
     * @param name        the name of the Class
     * @param classLoader the class loader to use (may be <code>null</code>,
     *                    which indicates the default class loader)
     * @return Class instance for the supplied name
     * @throws ClassNotFoundException if the class was not found
     * @throws LinkageError           if the class file could not be loaded
     * @see Class#forName(String, boolean, ClassLoader)
     */
    public static Class<?> forName(String name, ClassLoader classLoader)
            throws ClassNotFoundException, LinkageError {

        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz != null) {
            return clazz;
        }

        // "java.lang.String[]" style arrays
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[Ljava.lang.String;" style arrays
        int internalArrayMarker = name.indexOf(INTERNAL_ARRAY_PREFIX);
        if (internalArrayMarker != -1 && name.endsWith(";")) {
            String elementClassName = null;
            if (internalArrayMarker == 0) {
                elementClassName = name
                        .substring(INTERNAL_ARRAY_PREFIX.length(), name.length() - 1);
            } else if (name.startsWith("[")) {
                elementClassName = name.substring(1);
            }
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = getClassLoader();
        }
        return classLoaderToUse.loadClass(name);
    }

    /**
     * Resolve the primitive class from the specified type
     *
     * @param type the specified type
     * @return <code>null</code> if not found
     */
    public static Class<?> resolvePrimitiveType(Class<?> type) {
        if (type == null) {
            return null;
        }
        if (type.isPrimitive()) {
            return type;
        }
        return PRIMITIVE_WRAPPER_TYPE_MAP.get(type);
    }

    /**
     * Resolve the given class name as primitive class, if appropriate,
     * according to the JVM's naming rules for primitive classes.
     * <p>
     * Also supports the JVM's internal class names for primitive arrays. Does
     * <i>not</i> support the "[]" suffix notation for primitive arrays; this is
     * only supported by {@link #forName}.
     *
     * @param name the name of the potentially primitive class
     * @return the primitive class, or <code>null</code> if the name does not
     * denote a primitive class or primitive array class
     */
    public static Class<?> resolvePrimitiveClassName(String name) {
        Class<?> result = null;
        // Most class names will be quite long, considering that they
        // SHOULD sit in a package, so a length check is worthwhile.
        if (name != null && name.length() <= 8) {
            // Could be a primitive - likely.
            result = (Class<?>) PRIMITIVE_TYPE_NAME_MAP.get(name);
        }
        return result;
    }

    public static String toShortString(Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj);

    }

    public static String simpleClassName(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        String className = clazz.getName();
        final int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        if (lastDotIdx > -1) {
            return className.substring(lastDotIdx + 1);
        }
        return className;
    }


    /**
     * The specified type is primitive type or simple type
     *
     * @param type the type to test
     * @return
     * @deprecated as 1.0.0, use {@link Class#isPrimitive()} plus {@link #isSimpleType(Class)} instead
     */
    public static boolean isPrimitive(Class<?> type) {
        return type != null && (type.isPrimitive() || isSimpleType(type));
    }

    /**
     * The specified type is simple type or not
     *
     * @param type the type to test
     * @return if <code>type</code> is one element of {@link #SIMPLE_TYPES}, return <code>true</code>, or <code>false</code>
     * @see #SIMPLE_TYPES
     * @since 1.0.0
     */
    public static boolean isSimpleType(Class<?> type) {
        return SIMPLE_TYPES.contains(type);
    }

    public static Object convertPrimitive(Class<?> type, String value) {
        if (value == null) {
            return null;
        } else if (type == char.class || type == Character.class) {
            return value.length() > 0 ? value.charAt(0) : '\0';
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.valueOf(value);
        }
        try {
            if (type == byte.class || type == Byte.class) {
                return Byte.valueOf(value);
            } else if (type == short.class || type == Short.class) {
                return Short.valueOf(value);
            } else if (type == int.class || type == Integer.class) {
                return Integer.valueOf(value);
            } else if (type == long.class || type == Long.class) {
                return Long.valueOf(value);
            } else if (type == float.class || type == Float.class) {
                return Float.valueOf(value);
            } else if (type == double.class || type == Double.class) {
                return Double.valueOf(value);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return value;
    }


    /**
     * We only check boolean value at this moment.
     *
     * @param type
     * @param value
     * @return
     */
    public static boolean isTypeMatch(Class<?> type, String value) {
        if ((type == boolean.class || type == Boolean.class)
                && !("true".equals(value) || "false".equals(value))) {
            return false;
        }
        return true;
    }

    /**
     * Get all classes from the specified type with filters
     *
     * @param type         the specified type
     * @param classFilters class filters
     * @return non-null read-only {@link Set}
     */
    public static Set<Class<?>> getAllClasses(Class<?> type, Predicate<Class<?>>... classFilters) {
        return getAllClasses(type, true, classFilters);
    }

    /**
     * Get all classes(may include self type) from the specified type with filters
     *
     * @param type         the specified type
     * @param includedSelf included self type or not
     * @param classFilters class filters
     * @return non-null read-only {@link Set}
     */
    public static Set<Class<?>> getAllClasses(Class<?> type, boolean includedSelf, Predicate<Class<?>>... classFilters) {
        if (type == null || type.isPrimitive()) {
            return emptySet();
        }

        List<Class<?>> allClasses = new LinkedList<>();

        Class<?> superClass = type.getSuperclass();
        while (superClass != null) {
            // add current super class
            allClasses.add(superClass);
            superClass = superClass.getSuperclass();
        }

        // FIFO -> FILO
        Collections.reverse(allClasses);

        if (includedSelf) {
            allClasses.add(type);
        }

        // Keep the same order from List
        return ofSet(filterAll(allClasses, classFilters));
    }

    /**
     * Get all super classes from the specified type
     *
     * @param type         the specified type
     * @param classFilters the filters for classes
     * @return non-null read-only {@link Set}
     * @since 1.0.0
     */
    public static Set<Class<?>> getAllSuperClasses(Class<?> type, Predicate<Class<?>>... classFilters) {
        return getAllClasses(type, false, classFilters);
    }

    /**
     * Get all interfaces from the specified type
     *
     * @param type             the specified type
     * @param interfaceFilters the filters for interfaces
     * @return non-null read-only {@link Set}
     * @since 1.0.0
     */
    public static Set<Class<?>> getAllInterfaces(Class<?> type, Predicate<Class<?>>... interfaceFilters) {
        if (type == null || type.isPrimitive()) {
            return emptySet();
        }

        List<Class<?>> allInterfaces = new LinkedList<>();
        Set<Class<?>> resolved = new LinkedHashSet<>();
        Queue<Class<?>> waitResolve = new LinkedList<>();

        resolved.add(type);
        Class<?> clazz = type;
        while (clazz != null) {

            Class<?>[] interfaces = clazz.getInterfaces();

            if (isNotEmpty(interfaces)) {
                // add current interfaces
                Arrays.stream(interfaces)
                        .filter(resolved::add)
                        .forEach(cls -> {
                            allInterfaces.add(cls);
                            waitResolve.add(cls);
                        });
            }

            // add all super classes to waitResolve
            getAllSuperClasses(clazz)
                    .stream()
                    .filter(resolved::add)
                    .forEach(waitResolve::add);

            clazz = waitResolve.poll();
        }

        // FIFO -> FILO
        Collections.reverse(allInterfaces);

        return ofSet(filterAll(allInterfaces, interfaceFilters));
    }

    /**
     * Get all inherited types from the specified type
     *
     * @param type        the specified type
     * @param typeFilters the filters for types
     * @return non-null read-only {@link Set}
     * @since 1.0.0
     */
    public static Set<Class<?>> getAllInheritedTypes(Class<?> type, Predicate<Class<?>>... typeFilters) {
        // Add all super classes
        Set<Class<?>> types = new LinkedHashSet<>(getAllSuperClasses(type, typeFilters));
        // Add all interface classes
        types.addAll(getAllInterfaces(type, typeFilters));
        return unmodifiableSet(types);
    }

    /**
     * the semantics is same as {@link Class#isAssignableFrom(Class)}
     *
     * @param superType the super type
     * @param target    the target object
     * @return see {@link Class#isAssignableFrom(Class)}
     * @since 1.0.0
     */
    public static boolean isAssignableFrom(Class<?> superType, Object target) {
        if (target == null) {
            return false;
        }
        return isAssignableFrom(superType, target.getClass());
    }

    /**
     * the semantics is same as {@link Class#isAssignableFrom(Class)}
     *
     * @param superType  the super type
     * @param targetType the target type
     * @return see {@link Class#isAssignableFrom(Class)}
     * @since 1.0.0
     */
    public static boolean isAssignableFrom(Class<?> superType, Class<?> targetType) {
        // any argument is null
        if (superType == null || targetType == null) {
            return false;
        }
        // equals
        if (Objects.equals(superType, targetType)) {
            return true;
        }
        // isAssignableFrom
        return superType.isAssignableFrom(targetType);
    }

    /**
     * the semantics is same as {@link Class#isAssignableFrom(Class)}
     *
     * @param targetType the target type
     * @param superTypes the super types
     * @return see {@link Class#isAssignableFrom(Class)}
     * @since 1.0.0
     */
    public static boolean isDerived(Class<?> targetType, Class<?>... superTypes) {
        // any argument is null
        if (superTypes == null || superTypes.length == 0 || targetType == null) {
            return false;
        }
        boolean derived = false;
        for (Class<?> superType : superTypes) {
            if (isAssignableFrom(superType, targetType)) {
                derived = true;
                break;
            }
        }
        return derived;
    }

    public static Class<?>[] getTypes(Object... args) {
        int size = args == null ? 0 : args.length;
        Class[] types = new Class[size];
        for (int i = 0; i < size; i++) {
            types[i] = args[i].getClass();
        }
        return types;
    }

    /**
     * Test the specified class name is present in the {@link ClassLoader}
     *
     * @param className   the name of {@link Class}
     * @param classLoader {@link ClassLoader}
     * @return If found, return <code>true</code>
     * @since 1.0.0
     */
    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            forName(className, classLoader);
        } catch (Throwable ignored) { // Ignored
            return false;
        }
        return true;
    }

    /**
     * Resolve the {@link Class} by the specified name and {@link ClassLoader}
     *
     * @param className   the name of {@link Class}
     * @param classLoader {@link ClassLoader}
     * @return If can't be resolved , return <code>null</code>
     * @since 1.0.0
     */
    public static Class<?> resolveClass(String className, ClassLoader classLoader) {
        Class<?> targetClass = null;
        try {
            targetClass = forName(className, classLoader);
        } catch (Throwable ignored) { // Ignored
        }
        return targetClass;
    }

    /**
     * Is generic class or not?
     *
     * @param type the target type
     * @return <code>true</code> if the target type is generic class, <code>false</code> otherwise.
     * @since 1.0.0
     */
    public static boolean isGenericClass(Class<?> type) {
        return type != null && type.getTypeParameters().length > 0;
    }

    public static <T> T unwrap(Class<T> type) {
        return execute(type, Class::newInstance);
    }

    /**
     * Is the specified type a concrete class or not?
     *
     * @param type type to check
     * @return <code>true</code> if concrete class, <code>false</code> otherwise.
     */
    public static boolean isConcreteClass(Class<?> type) {

        if (type == null) {
            return false;
        }

        if (concreteClassCache.containsKey(type)) {
            return true;
        }

        if (isGeneralClass(type, Boolean.FALSE)) {
            concreteClassCache.put(type, Boolean.TRUE);
            return true;
        }

        return false;
    }

    /**
     * Is the specified type a abstract class or not?
     * <p>
     *
     * @param type the type
     * @return true if type is a abstract class, false otherwise.
     */
    public static boolean isAbstractClass(Class<?> type) {
        return isGeneralClass(type, Boolean.TRUE);
    }

    /**
     * Is the specified type a general class or not?
     * <p>
     *
     * @param type the type
     * @return true if type is a general class, false otherwise.
     */
    public static boolean isGeneralClass(Class<?> type) {
        return isGeneralClass(type, null);
    }

    /**
     * Is the specified type a general class or not?
     * <p>
     * If <code>isAbstract</code> == <code>null</code>,  it will not check <code>type</code> is abstract or not.
     *
     * @param type       the type
     * @param isAbstract optional abstract flag
     * @return true if type is a general (abstract) class, false otherwise.
     */
    protected static boolean isGeneralClass(Class<?> type, Boolean isAbstract) {

        if (type == null) {
            return false;
        }

        int mod = type.getModifiers();

        if (isInterface(mod)
                || isAnnotation(mod)
                || isEnum(mod)
                || isSynthetic(mod)
                || type.isPrimitive()
                || type.isArray()) {
            return false;
        }

        if (isAbstract != null) {
            return isAbstract(mod) == isAbstract.booleanValue();
        }

        return true;
    }

    public static boolean isTopLevelClass(Class<?> type) {
        if (type == null) {
            return false;
        }

        if (type.isLocalClass() || type.isMemberClass()) {
            return false;
        }

        return true;
    }

    /**
     * @param modifiers {@link Class#getModifiers()}
     * @return true if this class's modifiers represents an annotation type; false otherwise
     * @see Class#isAnnotation()
     */
    public static boolean isAnnotation(int modifiers) {
        return (modifiers & ANNOTATION) != 0;
    }

    /**
     * @param modifiers {@link Class#getModifiers()}
     * @return true if this class's modifiers represents an enumeration type; false otherwise
     * @see Class#isEnum()
     */
    public static boolean isEnum(int modifiers) {
        return (modifiers & ENUM) != 0;
    }

    /**
     * @param modifiers {@link Class#getModifiers()}
     * @return true if this class's modifiers represents a synthetic type; false otherwise
     * @see Class#isSynthetic()
     */
    public static boolean isSynthetic(int modifiers) {
        return (modifiers & SYNTHETIC) != 0;
    }

    /**
     * Get all package names in {@link ClassPathUtils#getClassPaths() class paths}
     *
     * @return all package names in class paths
     */
    public static Set<String> getAllPackageNamesInClassPaths() {
        return packageNameToClassNamesMap.keySet();
    }

    /**
     * Resolve package name under specified class name
     *
     * @param className class name
     * @return package name
     */
    public static String resolvePackageName(String className) {
        return StringUtils.substringBeforeLast(className, ".");
    }

    /**
     * Find all class names in class path
     *
     * @param classPath class path
     * @param recursive is recursive on sub directories
     * @return all class names in class path
     */
    public static Set<String> findClassNamesInClassPath(String classPath, boolean recursive) {
        File archiveFile = new File(classPath); // JarFile or Directory
        return findClassNamesInClassPath(archiveFile, recursive);
    }

    /**
     * Find all class names in class path
     *
     * @param archiveFile JarFile or class patch directory
     * @param recursive   is recursive on sub directories
     * @return all class names in class path
     */
    public static Set<String> findClassNamesInClassPath(File archiveFile, boolean recursive) {
        if (archiveFile==null || !archiveFile.exists()) {
            return emptySet();
        }
        if (archiveFile.isDirectory()) { // Directory
            return findClassNamesInArchiveDirectory(archiveFile, recursive);
        } else if (archiveFile.isFile() && archiveFile.getName().endsWith(FileSuffixConstants.JAR)) { //JarFile
            return findClassNamesInArchiveFile(archiveFile, recursive);
        }
        return emptySet();
    }

    /**
     * Find class path under specified class name
     *
     * @param type class
     * @return class path
     */
    public static String findClassPath(Class<?> type) {
        return findClassPath(type.getName());
    }

    /**
     * Find class path under specified class name
     *
     * @param className class name
     * @return class path
     */
    public static String findClassPath(String className) {
        return classNameToClassPathsMap.get(className);
    }

    /**
     * Gets class name {@link Set} under specified class path
     *
     * @param classPath class path
     * @param recursive is recursive on sub directories
     * @return non-null {@link Set}
     */

    public static Set<String> getClassNamesInClassPath(String classPath, boolean recursive) {
        Set<String> classNames = classPathToClassNamesMap.get(classPath);
        if (CollectionUtils.isEmpty(classNames)) {
            classNames = findClassNamesInClassPath(classPath, recursive);
        }
        return classNames;
    }

    /**
     * Gets class name {@link Set} under specified package
     *
     * @param onePackage one package
     * @return non-null {@link Set}
     */

    public static Set<String> getClassNamesInPackage(Package onePackage) {
        return getClassNamesInPackage(onePackage.getName());
    }

    /**
     * Gets class name {@link Set} under specified package name
     *
     * @param packageName package name
     * @return non-null {@link Set}
     */

    public static Set<String> getClassNamesInPackage(String packageName) {
        Set<String> classNames = packageNameToClassNamesMap.get(packageName);
        return classNames == null ? Collections.<String>emptySet() : classNames;
    }


    protected static Set<String> findClassNamesInArchiveDirectory(File classesDirectory, boolean recursive) {
        Set<String> classNames = new LinkedHashSet<>();
        SimpleFileScanner simpleFileScanner = SimpleFileScanner.INSTANCE;
        Set<File> classFiles = simpleFileScanner.scan(classesDirectory, recursive, new SuffixFileFilter(FileSuffixConstants.CLASS));
        for (File classFile : classFiles) {
            String className = resolveClassName(classesDirectory, classFile);
            classNames.add(className);
        }
        return classNames;
    }

    protected static Set<String> findClassNamesInArchiveFile(File jarFile, boolean recursive) {
        Set<String> classNames = new LinkedHashSet<>();
        SimpleJarEntryScanner simpleJarEntryScanner = SimpleJarEntryScanner.INSTANCE;
        try {
            JarFile jarFile_ = new JarFile(jarFile);
            Set<JarEntry> jarEntries = simpleJarEntryScanner.scan(jarFile_, recursive, ClassFileJarEntryFilter.INSTANCE);
            for (JarEntry jarEntry : jarEntries) {
                String jarEntryName = jarEntry.getName();
                String className = resolveClassName(jarEntryName);
                if (StringUtils.isNotBlank(className)) {
                    classNames.add(className);
                }
            }
        } catch (Exception ignored) {
        }
        return classNames;
    }


    protected static String resolveClassName(File classesDirectory, File classFile) {
        String classFileRelativePath = FileUtils.resolveRelativePath(classesDirectory, classFile);
        return resolveClassName(classFileRelativePath);
    }

    /**
     * Resolve resource name to class name
     *
     * @param resourceName resource name
     * @return class name
     */
    public static String resolveClassName(String resourceName) {
        String className = StringUtils.replace(resourceName, PathConstants.SLASH, Constants.DOT);
        className = StringUtils.substringBefore(className, FileSuffixConstants.CLASS);
        while (StringUtils.startsWith(className, Constants.DOT)) {
            className = StringUtils.substringAfter(className, Constants.DOT);
        }
        return className;
    }

    /**
     * The map of all class names in {@link ClassPathUtils#getClassPaths() class path} , the class path for one {@link
     * JarFile} or classes directory as key , the class names set as value
     *
     * @return Read-only
     */
    public static Map<String, Set<String>> getClassPathToClassNamesMap() {
        return classPathToClassNamesMap;
    }

    /**
     * The set of all class names in {@link ClassPathUtils#getClassPaths() class path}
     *
     * @return Read-only
     */

    public static Set<String> getAllClassNamesInClassPaths() {
        Set<String> allClassNames = new LinkedHashSet<>();
        for (Set<String> classNames : classPathToClassNamesMap.values()) {
            allClassNames.addAll(classNames);
        }
        return Collections.unmodifiableSet(allClassNames);
    }


    /**
     * Get {@link Class}'s code source location URL
     *
     * @param type
     * @return If , return <code>null</code>.
     * @throws NullPointerException If <code>type</code> is <code>null</code> , {@link NullPointerException} will be thrown.
     */
    public static URL getCodeSourceLocation(Class<?> type) throws NullPointerException {

        URL codeSourceLocation = null;
        ClassLoader classLoader = type.getClassLoader();

        if (classLoader == null) { // Bootstrap ClassLoader or type is primitive or void
            String path = findClassPath(type);
            if (StringUtils.isNotBlank(path)) {
                try {
                    codeSourceLocation = new File(path).toURI().toURL();
                } catch (MalformedURLException ignored) {
                    codeSourceLocation = null;
                }
            }
        } else {
            ProtectionDomain protectionDomain = type.getProtectionDomain();
            CodeSource codeSource = protectionDomain == null ? null : protectionDomain.getCodeSource();
            codeSourceLocation = codeSource == null ? null : codeSource.getLocation();
        }
        return codeSourceLocation;
    }

    /**
     * Resolve the types of the specified values
     *
     * @param values the values
     * @return If can't be resolved, return {@link #EMPTY_CLASS_ARRAY empty class array}
     */
    public static Class[] resolveTypes(Object... values) {

        if (isEmpty(values)) {
            return EMPTY_CLASS_ARRAY;
        }

        int size = values.length;

        Class[] types = new Class[size];

        for (int i = 0; i < size; i++) {
            Object value = values[i];
            types[i] = value == null ? null : value.getClass();
        }

        return types;
    }

}
