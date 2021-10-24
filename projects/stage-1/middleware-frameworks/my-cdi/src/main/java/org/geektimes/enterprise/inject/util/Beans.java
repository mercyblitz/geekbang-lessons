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
import org.geektimes.interceptor.InterceptorManager;

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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static java.beans.Introspector.decapitalize;
import static java.lang.Integer.compare;
import static java.lang.String.format;
import static java.util.stream.Stream.of;
import static org.geektimes.commons.collection.util.CollectionUtils.asSet;
import static org.geektimes.commons.lang.util.AnnotationUtils.findAnnotation;
import static org.geektimes.commons.lang.util.AnnotationUtils.isAnnotationPresent;
import static org.geektimes.commons.reflect.util.ClassUtils.*;
import static org.geektimes.commons.reflect.util.FieldUtils.getAllFields;
import static org.geektimes.commons.reflect.util.MemberUtils.NON_PRIVATE_METHOD_PREDICATE;
import static org.geektimes.commons.reflect.util.MemberUtils.NON_STATIC_METHOD_PREDICATE;
import static org.geektimes.commons.reflect.util.MethodUtils.getAllDeclaredMethods;
import static org.geektimes.commons.reflect.util.TypeUtils.*;
import static org.geektimes.enterprise.inject.util.Decorators.isDecorator;
import static org.geektimes.enterprise.inject.util.Qualifiers.findQualifier;
import static org.geektimes.interceptor.InterceptorManager.getInstance;

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
            return asSet(Object.class, typed.value());
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

    /**
     * The container uses proxies to provide certain functionality. Certain legal bean types cannot be
     * proxied by the container:
     * <ul>
     *     <li>classes which don’t have a non-private constructor with no parameters</li>
     *     <li>classes which are declared final</li>
     *     <li>classes which have non-static, final methods with public, protected or default visibility</li>
     *     <li>primitive types</li>
     *     <li>array types</li>
     * </ul>
     *
     * @param beanClass the type of Bean
     * @return <code>true</code> if bean type cannot be proxied by the container:
     * * <ul>
     * *     <li>classes which don’t have a non-private constructor with no parameters</li>
     * *     <li>classes which are declared final</li>
     * *     <li>classes which have non-static, final methods with public, protected or default visibility</li>
     * *     <li>primitive types</li>
     * *     <li>array types</li>
     * * </ul>
     */
    public static boolean isUnproxyable(Class<?> beanClass) {
        if (isArray(beanClass) ||
                isPrimitive(beanClass) ||
                isFinal(beanClass) ||
                hasFinalMethod(beanClass) ||
                hasNonDefaultConstructor(beanClass)) {
            return true;
        }
        return false;
    }

    private static boolean hasFinalMethod(Class<?> beanClass) {
        Set<Method> methods = getAllDeclaredMethods(beanClass, NON_STATIC_METHOD_PREDICATE, NON_PRIVATE_METHOD_PREDICATE);
        boolean hasFinalMethod = false;
        for (Method method : methods) {
            hasFinalMethod = MemberUtils.isFinal(method);
            if (hasFinalMethod) {
                break;
            }
        }
        return hasFinalMethod;
    }

    private static boolean hasNonDefaultConstructor(Class<?> beanClass) {
        // TODO
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
        InterceptorManager interceptorManager = getInstance(managedBeanClass.getClassLoader());
        if (interceptorManager.isInterceptorClass(managedBeanClass) && isDecorator(managedBeanClass)) {
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


    /**
     * Match rules:
     *
     * <ul>
     *     <li>Primitive types are considered to match their corresponding wrapper types in java.lang</li>
     *     <li>Array types are considered to match only if their element types are identical</li>
     * </ul>
     *
     * @param requiredType
     * @param beanType
     * @return
     */
    public static boolean matches(Type requiredType, Type beanType) {

        if (Objects.equals(requiredType, beanType)) {
            return true;
        }

        if (isClass(requiredType)) {
            if (isClass(beanType)) {
                return matches((Class) requiredType, (Class) beanType);
            } else if (isParameterizedType(beanType)) {
                ParameterizedType beanParameterizedType = asParameterizedType(beanType);
                Type beanRawType = beanParameterizedType.getRawType();
                return matches(requiredType, beanRawType);
            }
        } else if (isParameterizedType(requiredType)) {
            ParameterizedType requiredParameterizedType = asParameterizedType(requiredType);
            ParameterizedType beanParameterizedType = asParameterizedType(beanType);

            if (requiredParameterizedType != null && beanParameterizedType != null) {
                Type requiredRawType = requiredParameterizedType.getRawType();
                Type beanRawType = beanParameterizedType.getRawType();
                // A parameterized bean type is considered assignable to a parameterized required type
                // if they have identical raw type
                if (matches(requiredRawType, beanRawType)) {
                    /**
                     * the required type parameter and the bean type parameter are actual types with identical raw
                     * type, and, if the type is parameterized, the bean type parameter is assignable to the required
                     * type parameter according to these rules
                     */
                    Type[] requiredTypeArguments = requiredParameterizedType.getActualTypeArguments();
                    Type[] beanTypeArguments = beanParameterizedType.getActualTypeArguments();
                    int matchCount = 0;
                    if (requiredTypeArguments.length == beanTypeArguments.length) {
                        for (int i = 0; i < requiredTypeArguments.length; i++) {
                            Type requiredTypeArgument = requiredTypeArguments[i];
                            Type beanTypeArgument = beanTypeArguments[i];
                            if (matchesTypeArgument(requiredTypeArgument, beanTypeArgument)) {
                                matchCount++;
                            }
                        }
                    }
                    return matchCount == requiredTypeArguments.length;
                }
            }
        }

        return false;
    }

    private static boolean matchesTypeArgument(Type requiredTypeArgument, Type beanTypeArgument) {
        if (isWildcardType(requiredTypeArgument)) { // the required type parameter is a wildcard
            /**
             * the bean type parameter is an actual type and the
             * actual type is assignable to the upper bound
             */
            WildcardType requiredWildcardType = asWildcardType(requiredTypeArgument);
            if (isClass(beanTypeArgument)) {
                return matchesBounds(beanTypeArgument, requiredWildcardType.getUpperBounds());
            } else if (isTypeVariable(beanTypeArgument)) {
                /**
                 *  the bean type parameter is a type variable and the upper bound of the type variable is assignable
                 *  to or assignable from the upper bound, if any, of the wildcard and
                 *  assignable from the lower bound, if any, of the wildcard
                 */
                TypeVariable beanTypeVariable = asTypeVariable(beanTypeArgument);
                Type[] upperBounds = beanTypeVariable.getBounds();
                if (matchesBounds(upperBounds, requiredWildcardType.getUpperBounds())
                        || matchesBounds(upperBounds, requiredWildcardType.getLowerBounds())) {
                    return true;
                }
            }
            return false;
        } else if (isTypeVariable(beanTypeArgument)) {
            if (isClass(requiredTypeArgument)) {
                /**
                 * the required type parameter is an actual type, the bean type parameter is a type variable and
                 * the actual type is assignable to the upper bound, if any, of the type variable
                 */
                TypeVariable beanTypeVariable = asTypeVariable(beanTypeArgument);
                return matchesBounds(requiredTypeArgument, beanTypeVariable.getBounds());
            } else if (isTypeVariable(requiredTypeArgument)) {
                /**
                 * the required type parameter and the bean type parameter are both type variables and the
                 * upper bound of the required type parameter is assignable to the upper bound, if any, of the
                 * bean type parameter.
                 */
                TypeVariable requiredTypeVariable = asTypeVariable(requiredTypeArgument);
                TypeVariable beanTypeVariable = asTypeVariable(beanTypeArgument);
                return matchesBounds(requiredTypeVariable.getBounds(), beanTypeVariable.getBounds());
            }
        }
        return false;
    }

    private static boolean matchesBounds(Type[] types, Type[] bounds) {
        int typesLength = types.length;
        int matchCount = 0;
        for (int i = 0; i < typesLength; i++) {
            Type type = types[i];
            if (matchesBounds(type, bounds)) {
                matchCount++;
            }
        }
        return matchCount == typesLength;
    }

    private static boolean matchesBounds(Type type, Type[] bounds) {
        int boundsLength = bounds.length;
        int matchCount = 0;
        for (int i = 0; i < boundsLength; i++) {
            Type bound = bounds[i];
            if (matches(type, bound)) {
                matchCount++;
            }
        }
        return matchCount == boundsLength;
    }

    private static boolean matches(Class<?> requiredClass, Class<?> beanClass) {
        if (isPrimitive(requiredClass)) {
            return matchesPrimitiveType(requiredClass, beanClass);
        } else if (isArray(requiredClass)) {
            return arrayTypeEquals(requiredClass, beanClass);
        } else {
            return isAssignableFrom(requiredClass, beanClass);
        }
    }

    private static boolean matchesPrimitiveType(Class<?> requiredClass, Type beanType) {
        Class<?> requiredWrapperType = resolveWrapperType(requiredClass);
        return Objects.equals(requiredWrapperType, beanType);
    }

}
