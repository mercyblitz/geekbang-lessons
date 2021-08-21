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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Collections.*;
import static org.geektimes.commons.function.Streams.filterAll;
import static org.geektimes.commons.lang.util.StringUtils.isNotEmpty;
import static org.geektimes.commons.reflect.util.ClassUtils.*;
import static org.geektimes.commons.reflect.util.MemberUtils.*;

/**
 * The utilities class for Java Refection {@link Method}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class MethodUtils {

    /**
     * Get all {@link Method methods} of the declared class
     *
     * @param declaringClass        the declared class
     * @param includeInheritedTypes include the inherited types, e,g. super classes or interfaces
     * @param publicOnly            only public method
     * @param methodsToFilter       (optional) the methods to be filtered
     * @return non-null read-only {@link Set}
     */
    public static Set<Method> getMethods(Class<?> declaringClass, boolean includeInheritedTypes, boolean publicOnly,
                                         Predicate<Method>... methodsToFilter) {

        if (declaringClass == null || declaringClass.isPrimitive()) {
            return emptySet();
        }

        // All declared classes
        Set<Class<?>> declaredClasses = new LinkedHashSet<>();
        // If the super classes are resolved, all them into declaredClasses
        if (includeInheritedTypes) {
            declaredClasses.addAll(getAllInheritedTypes(declaringClass));
        }
        // Add the top declaring class
        declaredClasses.add(declaringClass);

        // All methods
        Set<Method> allMethods = new LinkedHashSet<>();

        for (Class<?> classToSearch : declaredClasses) {
            Method[] methods = publicOnly ? classToSearch.getMethods() : classToSearch.getDeclaredMethods();
            // Add the declared methods or public methods
            for (Method method : methods) {
                allMethods.add(method);
            }
        }

        return unmodifiableSet(filterAll(allMethods, methodsToFilter));
    }

    /**
     * Get all declared {@link Method methods} of the declared class, excluding the inherited methods
     *
     * @param declaringClass  the declared class
     * @param methodsToFilter (optional) the methods to be filtered
     * @return non-null read-only {@link Set}
     * @see #getMethods(Class, boolean, boolean, Predicate[])
     */
    static Set<Method> getDeclaredMethods(Class<?> declaringClass, Predicate<Method>... methodsToFilter) {
        return getMethods(declaringClass, false, false, methodsToFilter);
    }

    /**
     * Get all public {@link Method methods} of the declared class, including the inherited methods.
     *
     * @param declaringClass  the declared class
     * @param methodsToFilter (optional) the methods to be filtered
     * @return non-null read-only {@link Set}
     * @see #getMethods(Class, boolean, boolean, Predicate[])
     */
    static Set<Method> getMethods(Class<?> declaringClass, Predicate<Method>... methodsToFilter) {
        return getMethods(declaringClass, false, true, methodsToFilter);
    }

    /**
     * Get all declared {@link Method methods} of the declared class, including the inherited methods.
     *
     * @param declaringClass  the declared class
     * @param methodsToFilter (optional) the methods to be filtered
     * @return non-null read-only {@link Set}
     * @see #getMethods(Class, boolean, boolean, Predicate[])
     */
    public static Set<Method> getAllDeclaredMethods(Class<?> declaringClass, Predicate<Method>... methodsToFilter) {
        return getMethods(declaringClass, true, false, methodsToFilter);
    }

    /**
     * Get all public {@link Method methods} of the declared class, including the inherited methods.
     *
     * @param declaringClass  the declared class
     * @param methodsToFilter (optional) the methods to be filtered
     * @return non-null read-only {@link Set}
     * @see #getMethods(Class, boolean, boolean, Predicate[])
     */
    static Set<Method> getAllMethods(Class<?> declaringClass, Predicate<Method>... methodsToFilter) {
        return getMethods(declaringClass, true, true, methodsToFilter);
    }

    /**
     * Find the {@link Method} by the the specified type and method name without the parameter types
     *
     * @param type       the target type
     * @param methodName the specified method name
     * @return if not found, return <code>null</code>
     */
    static Method findMethod(Class type, String methodName) {
        return findMethod(type, methodName, EMPTY_CLASS_ARRAY);
    }

    /**
     * Find the {@link Method} by the the specified type, method name and parameter types
     *
     * @param type           the target type
     * @param methodName     the method name
     * @param parameterTypes the parameter types
     * @return if not found, return <code>null</code>
     */
    static Method findMethod(Class type, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        try {
            if (type != null && isNotEmpty(methodName)) {
                method = type.getDeclaredMethod(methodName, parameterTypes);
            }
        } catch (NoSuchMethodException e) {
        }
        return method;
    }

    /**
     * Invoke the target object and method
     *
     * @param object          the target object
     * @param methodName      the method name
     * @param parameterValues the method parameters
     * @param <T>             the return type
     * @return the target method's execution result
     */
    public static <T> T invokeMethod(Object object, String methodName, Object... parameterValues) {
        Class[] parameterTypes = resolveTypes(parameterValues);
        return invokeMethod(object, methodName, parameterTypes, parameterValues);
    }

    /**
     * Invoke the target object and method
     *
     * @param object          the target object
     * @param methodName      the method name
     * @param parameterTypes  the types of parameters
     * @param parameterValues the method parameters
     * @param <T>             the return type
     * @return the target method's execution result
     */
    public static <T> T invokeMethod(Object object, String methodName, Class[] parameterTypes, Object[] parameterValues) {
        Class type = object.getClass();
        Method method = findMethod(type, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalStateException(String.format("cannot find method %s,class: %s", methodName, type.getName()));
        }
        return invokeMethod(object, method, parameterValues);
    }

    public static <T> T invokeMethod(Object object, Method method, Object... parameterValues) {
        T value = null;
        try {
            enableAccessible(method);
            value = (T) method.invoke(object, parameterValues);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return value;
    }

    /**
     * Tests whether one method, as a member of a given type,
     * overrides another method.
     *
     * @param overrider  the first method, possible overrider
     * @param overridden the second method, possibly being overridden
     * @return {@code true} if and only if the first method overrides
     * the second
     * @jls 8.4.8 Inheritance, Overriding, and Hiding
     * @jls 9.4.1 Inheritance and Overriding
     * @see Elements#overrides(ExecutableElement, ExecutableElement, TypeElement)
     */
    public static boolean overrides(Method overrider, Method overridden) {

        if (overrider == null || overridden == null) {
            return false;
        }

        // equality comparison: If two methods are same
        if (Objects.equals(overrider, overridden)) {
            return false;
        }

        // Modifiers comparison: Any method must be non-static method
        if (isStatic(overrider) || isStatic(overridden)) { //
            return false;
        }

        // Modifiers comparison: the accessibility of any method must not be private
        if (isPrivate(overrider) || isPrivate(overridden)) {
            return false;
        }

        // Inheritance comparison: The declaring class of overrider must be inherit from the overridden's
        if (!overridden.getDeclaringClass().isAssignableFrom(overrider.getDeclaringClass())) {
            return false;
        }

        // Method comparison: must not be "default" method
        if (overrider.isDefault()) {
            return false;
        }

        // Method comparison: The method name must be equal
        if (!Objects.equals(overrider.getName(), overridden.getName())) {
            return false;
        }

        // Method comparison: The count of method parameters must be equal
        if (!Objects.equals(overrider.getParameterCount(), overridden.getParameterCount())) {
            return false;
        }

        // Method comparison: Any parameter type of overrider must equal the overridden's
        for (int i = 0; i < overrider.getParameterCount(); i++) {
            if (!Objects.equals(overridden.getParameterTypes()[i], overrider.getParameterTypes()[i])) {
                return false;
            }
        }

        // Method comparison: The return type of overrider must be inherit from the overridden's
        if (!overridden.getReturnType().isAssignableFrom(overrider.getReturnType())) {
            return false;
        }

        // Throwable comparison: "throws" Throwable list will be ignored, trust the compiler verify

        return true;
    }

    /**
     * Find the nearest overridden {@link Method method} from the inherited class
     *
     * @param overrider the overrider {@link Method method}
     * @return if found, the overrider <code>method</code>, or <code>null</code>
     */
    public static Method findNearestOverriddenMethod(Method overrider) {
        Class<?> declaringClass = overrider.getDeclaringClass();
        Method overriddenMethod = null;
        for (Class<?> inheritedType : getAllInheritedTypes(declaringClass)) {
            overriddenMethod = findOverriddenMethod(overrider, inheritedType);
            if (overriddenMethod != null) {
                break;
            }
        }
        return overriddenMethod;
    }

    /**
     * Find the overridden {@link Method method} from the declaring class
     *
     * @param overrider      the overrider {@link Method method}
     * @param declaringClass the class that is declaring the overridden {@link Method method}
     * @return if found, the overrider <code>method</code>, or <code>null</code>
     */
    public static Method findOverriddenMethod(Method overrider, Class<?> declaringClass) {
        Set<Method> matchedMethods = getAllMethods(declaringClass, method -> overrides(overrider, method));
        return matchedMethods.isEmpty() ? null : matchedMethods.iterator().next();
    }

    public static void enableAccessible(Method method) {
        if (!isPublic(method) || !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    public static boolean isObjectMethod(Method method) {
        if (method != null) {
            return Objects.equals(Object.class, method.getDeclaringClass());
        }
        return false;
    }
}
