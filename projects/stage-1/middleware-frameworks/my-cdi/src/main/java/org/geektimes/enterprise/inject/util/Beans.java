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
package org.geektimes.enterprise.inject.util;

import org.geektimes.commons.lang.util.ArrayUtils;
import org.geektimes.commons.reflect.util.MemberUtils;

import javax.decorator.Decorator;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.DefinitionException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import java.lang.reflect.*;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static java.beans.Introspector.decapitalize;
import static java.lang.Integer.compare;
import static java.lang.String.format;
import static java.util.stream.Stream.of;
import static org.geektimes.commons.collection.util.CollectionUtils.ofSet;
import static org.geektimes.commons.lang.util.AnnotationUtils.findAnnotation;
import static org.geektimes.commons.lang.util.AnnotationUtils.isAnnotationPresent;
import static org.geektimes.commons.reflect.util.ClassUtils.*;
import static org.geektimes.commons.reflect.util.FieldUtils.getAllFields;
import static org.geektimes.commons.reflect.util.TypeUtils.*;
import static org.geektimes.enterprise.inject.util.Decorators.isDecorator;
import static org.geektimes.enterprise.inject.util.Interceptors.isInterceptor;
import static org.geektimes.enterprise.inject.util.Qualifiers.findQualifier;

/**
 * Bean Utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Beans {

    /**
     * Bean Class - Constructor Cache
     * default access for testing
     */
    static final Map<Class<?>, Constructor> beanConstructorsCache = new ConcurrentHashMap<>();

    static final Predicate<Type> WILDCARD_PARAMETERIZED_TYPE_FILTER = type -> {
        ParameterizedType parameterizedType = asParameterizedType(type);
        if (parameterizedType != null) {
            for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                if (typeArgument instanceof WildcardType) {
                    return true;
                }
            }
        }
        return false;
    };

    static final Predicate<Type> ARRAY_TYPE_FILTER = type -> {
        Type componentType = getComponentType(type);
        return !TYPE_VARIABLE_FILTER.test(componentType) || WILDCARD_PARAMETERIZED_TYPE_FILTER.test(componentType);
    };

    static final Predicate<Type>[] ILLEGAL_BEAN_TYPE_FILTERS = ArrayUtils.of(
            // A type variable is not a legal bean type.
            TYPE_VARIABLE_FILTER.negate(),
            // A parameterized type that contains a wildcard type parameter is not a legal bean type.
            WILDCARD_PARAMETERIZED_TYPE_FILTER.negate(),
            // An array type whose component type is not a legal bean type.
            ARRAY_TYPE_FILTER
    );

    /**
     * Constructor's parameter count descent
     */
    private static final Comparator<Constructor> CONSTRUCTOR_PARAM_COUNT_COMPARATOR =
            (a, b) -> compare(b.getParameterCount(), a.getParameterCount());

    private Beans() {
        throw new IllegalStateException("BeanUtils should not be instantiated!");
    }

    /**
     * The unrestricted set of bean types for a managed bean contains the bean class, every superclass and all
     * interfaces it implements directly or indirectly.
     * <p>
     * The resulting set of bean types for a managed bean consists only of legal bean types, all other types are
     * removed from the set of bean types.
     * <p>
     * Note the additional restrictions upon bean types of beans with normal scopes defined in Unproxyable bean types.
     *
     * @param beanClass
     * @return
     */
    public static Set<Type> getBeanTypes(Class<?> beanClass) {
        Typed typed = findAnnotation(beanClass, Typed.class);
        if (typed != null) {
            return ofSet(Object.class, typed.value());
        } else {
            return getAllTypes(beanClass, ILLEGAL_BEAN_TYPE_FILTERS);
        }
    }

    /**
     * Get the bean name from {@link Named} annotation
     *
     * @param annotatedElement the element annotated {@link Named}
     * @return {@link Named#value()} if {@link Named} annotated, <code>null</code> otherwise.
     */
    public static String getAnnotatedBeanName(AnnotatedElement annotatedElement) {
        Named named = findQualifier(annotatedElement, Named.class);
        String name = null;
        if (named != null) {
            name = named.value().trim();
        }
        return name;
    }

    public static String getBeanName(Class<?> beanClass) {
        String name = getAnnotatedBeanName(beanClass);
        if (name == null || "".equals(name)) {
            name = decapitalize(beanClass.getSimpleName());
        }
        return name;
    }

    public static String getBeanName(Method producerMethod) {
        String name = getAnnotatedBeanName(producerMethod);
        if (name == null || "".equals(name)) {
            name = producerMethod.getName();
        }
        return name;
    }

    public static String getBeanName(Field producerField) {
        String name = getAnnotatedBeanName(producerField);
        if (name == null || "".equals(name)) {
            name = producerField.getName();
        }
        return name;
    }

    /**
     * A Java class is a managed bean if it meets all of the following conditions:
     * <p>
     * It is not an inner class.
     * <p>
     * It is a non-abstract class, or is annotated @Decorator.
     * <p>
     * It does not implement javax.enterprise.inject.spi.Extension.
     * <p>
     * It is not annotated @Vetoed or in a package annotated @Vetoed.
     * <p>
     * It has an appropriate constructor - either:
     * <p>
     * the class has a constructor with no parameters, or
     * <p>
     * the class declares a constructor annotated @Inject.
     *
     * @param beanClass the class of bean
     * @return
     */
    public static boolean isManagedBean(Class<?> beanClass) {
        if (!isTopLevelClass(beanClass)) {
            return false;
        }
        if (!isConcreteClass(beanClass)) {
            return false;
        }
        if (isDecorator(beanClass)) {
            return false;
        }
        if (isExtensionClass(beanClass)) {
            return false;
        }
        if (isAnnotatedVetoed(beanClass)) {
            return false;
        }
        if (!hasManagedBeanConstructor(beanClass)) {
            return false;
        }
        return true;
    }

    private static boolean hasManagedBeanConstructor(Class<?> beanClass) {
        boolean hasManagedBeanConstructor = false;
        for (Constructor constructor : beanClass.getConstructors()) {
            if (constructor.getParameterCount() == 0 || constructor.isAnnotationPresent(Inject.class)) {
                hasManagedBeanConstructor = true;
                break;
            }
        }
        return hasManagedBeanConstructor;
    }

    /**
     * If the bean class of a managed bean is annotated with both @Interceptor and @Decorator, the container
     * automatically detects the problem and treats it as a definition error.
     * <p>
     * If a managed bean has a non-static public field, it must have scope @Dependent. If a managed bean with a
     * non-static public field declares any scope other than @Dependent, the container automatically detects the problem
     * and treats it as a definition error.
     * <p>
     * If the managed bean class is a generic type, it must have scope @Dependent. If a managed bean with a
     * parameterized bean class declares any scope other than @Dependent, the container automatically detects the
     * problem and treats it as a definition error.
     *
     * @param managedBeanClass the class of managed bean
     * @throws DefinitionException if the bean class does not meet above conditions
     */
    public static void validateManagedBeanType(Class<?> managedBeanClass) throws DefinitionException {
        if (isInterceptor(managedBeanClass) && isDecorator(managedBeanClass)) {
            throwDefinitionException("The managed bean [class : %s] must not annotate with both %s and %s",
                    managedBeanClass.getName(), Interceptor.class.getName(), Decorator.class.getName());
        }

        Set<Field> nonStaticPublicFields = getAllFields(managedBeanClass, MemberUtils::isNonStatic);
        if (!nonStaticPublicFields.isEmpty() && !isAnnotatedDependent(managedBeanClass)) {
            throwDefinitionException("The managed bean [class : %s] has a non-static public field, it must have scope @%s!",
                    managedBeanClass.getName(), Dependent.class.getName());
        }

        if (isGenericClass(managedBeanClass) && !isAnnotatedDependent(managedBeanClass)) {
            throwDefinitionException("The managed bean [class : %s] is a generic type, it must have scope @%s!",
                    managedBeanClass.getName(),
                    Dependent.class.getName());
        }
    }

    /**
     * If a bean class of a managed bean X is annotated @Specializes, then the bean class of X must directly extend the
     * bean class of another managed bean Y. Then X directly specializes Y, as defined in Specialization.
     *
     * @param beanClass
     * @throws DefinitionException If the bean class of X does not directly extend the bean class of another managed bean,
     *                             the container automatically detects the problem and treats it as a definition error.
     */
    public static void validateManagedBeanSpecializes(Class beanClass) throws DefinitionException {
        if (beanClass.isAnnotationPresent(Specializes.class)) {
            Class<?> superClass = beanClass.getSuperclass();
            validateManagedBeanType(superClass);
        }
    }

    public static Constructor<?> findAppropriateConstructor(Class<?> beanClass) {
        return beanConstructorsCache.computeIfAbsent(beanClass, type -> of(type.getConstructors())
                .sorted(CONSTRUCTOR_PARAM_COUNT_COMPARATOR) // parameter count descent
                .filter(c -> c.getParameterCount() == 0 || c.isAnnotationPresent(Inject.class))
                .findFirst()
                .orElseThrow(() -> new DefinitionException(
                        format("The bean class[%s] does not have a constructor with no parameters, " +
                                        "or the class declares a constructor annotated @Inject",
                                beanClass.getName())))
        );
    }

    public static boolean isAnnotatedVetoed(Class<?> type) {
        return isAnnotatedVetoed((AnnotatedElement) type) || isAnnotatedVetoed(type.getPackage());
    }

    public static boolean isAnnotatedVetoed(AnnotatedElement annotatedElement) {
        return isAnnotationPresent(annotatedElement, Vetoed.class);
    }

    public static boolean isAnnotatedDependent(AnnotatedElement annotatedElement) {
        return isAnnotationPresent(annotatedElement, Dependent.class);
    }

    public static boolean isExtensionClass(Class<?> type) {
        return isAssignableFrom(Exception.class, type);
    }

    static <T> void validate(T target, Predicate<T> validator, String errorMessage) {
        if (!validator.test(target)) {
            throw new DefinitionException(errorMessage);
        }
    }

    static void throwDefinitionException(String messagePattern, Object... args) {
        String message = format(messagePattern, args);
        throw new DefinitionException(message);
    }

}
