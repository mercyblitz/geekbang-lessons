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
package org.geektimes.interceptor.util;

import org.geektimes.commons.util.PriorityComparator;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.*;
import static java.util.Objects.requireNonNull;
import static org.geektimes.commons.function.ThrowableSupplier.execute;
import static org.geektimes.commons.lang.util.AnnotationUtils.isAnnotationPresent;
import static org.geektimes.commons.lang.util.AnnotationUtils.isMetaAnnotation;
import static org.geektimes.commons.reflect.util.ConstructorUtils.hasNonPrivateConstructorWithoutParameters;

/**
 * The utilities class for {@link Interceptor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class InterceptorUtils {

    public static final Class<? extends Annotation> INTERCEPTOR_ANNOTATION_TYPE = javax.interceptor.Interceptor.class;

    public static final Class<? extends Annotation> INTERCEPTOR_BINDING_ANNOTATION_TYPE = InterceptorBinding.class;

    public static final Class<? extends Annotation> AROUND_INVOKE_ANNOTATION_TYPE = AroundInvoke.class;

    public static final Class<? extends Annotation> AROUND_TIMEOUT_ANNOTATION_TYPE = AroundTimeout.class;

    public static final Class<? extends Annotation> AROUND_CONSTRUCT_ANNOTATION_TYPE = AroundConstruct.class;

    public static final Class<? extends Annotation> POST_CONSTRUCT_ANNOTATION_TYPE = PostConstruct.class;

    public static final Class<? extends Annotation> PRE_DESTROY_ANNOTATION_TYPE = PreDestroy.class;


    public static boolean isInterceptorClass(Class<?> interceptorClass) {
        if (isAnnotationPresent(interceptorClass, INTERCEPTOR_ANNOTATION_TYPE)) {
            validateInterceptorClass(interceptorClass);
        }
        return false;
    }

    public static <T> List<T> sortInterceptors(List<T> interceptors) {
        List<T> sortedInterceptors = new LinkedList<>(interceptors);
        sortedInterceptors.sort(PriorityComparator.INSTANCE);
        return sortedInterceptors;
    }

    public static <T> T unwrap(Class<T> type) {
        return execute(type::newInstance);
    }

    public static <A extends Annotation> A searchAnnotation(Executable executable, Class<A> annotationType) {
        A annotation = executable.getAnnotation(annotationType);
        if (annotation == null) {
            annotation = searchAnnotation(executable.getDeclaringClass(), annotationType);
        }
        return annotation;
    }

    public static <A extends Annotation> A searchAnnotation(Class<?> componentClass, Class<A> annotationType) {
        A annotation = null;
        if (!componentClass.isInterface()) {
            do {
                annotation = componentClass.getAnnotation(annotationType);
                componentClass = componentClass.getSuperclass();
            } while (annotation == null && componentClass != null);
        }
        return annotation;
    }

    /**
     * Around-invoke methods may be declared in interceptor classes, in the superclasses of interceptor
     * classes, in the target class, and/or in superclasses of the target class. However, only one around-invoke
     * method may be declared in a given class.
     * <p>
     * Around-invoke methods can have public, private, protected, or package level access. An
     * around-invoke method must not be declared as abstract, final or static.
     * <p>
     * Around-invoke methods have the following signature:
     * <p>
     * Object <METHOD>(InvocationContext)
     *
     * @param method the target {@link Method method}
     * @return <code>true</code> if the given method that annotated {@link AroundInvoke} is any non-final,
     * non-static method with a single parameter of type {@link InvocationContext} and return type {@link Object},
     * <code>false</code> otherwise
     * @throws IllegalStateException If an around-invoke method must not be declared as abstract, final or static,
     *                               or if the count of method arguments is not only one or the argument type is not
     *                               {@link InvocationContext},
     *                               or if the return type of method is not <code>Object</code> or its derived type.
     */
    public static boolean isAroundInvokeMethod(Method method) {
        return isInterceptionMethod(method, AROUND_INVOKE_ANNOTATION_TYPE, Object.class);
    }

    /**
     * Around-timeout methods can have public, private, protected, or package level access. An
     * around-timeout method must not be declared as abstract, final or static.
     * <p>
     * Around-timeout methods have the following signature:
     * <p>
     * Object <METHOD>(InvocationContext)
     *
     * @param method the target {@link Method method}
     * @return <code>true</code> if the given method that annotated {@link AroundTimeout} is any non-final,
     * non-static method with a single parameter of type {@link InvocationContext} and return type {@link Object},
     * <code>false</code> otherwise
     * @throws IllegalStateException If an around-timeout method must not be declared as abstract, final or static,
     *                               or if the count of method arguments is not only one or the argument type is not
     *                               {@link InvocationContext},
     *                               or if the return type of method is not <code>Object</code> or its derived type.
     */
    public static boolean isAroundTimeoutMethod(Method method) {
        return isInterceptionMethod(method, AROUND_TIMEOUT_ANNOTATION_TYPE, Object.class);
    }

    /**
     * @param method the target {@link Method method}
     * @return <code>true</code> if the given method that annotated {@link AroundConstruct} is any non-final,
     * non-static method with a single parameter of type {@link InvocationContext} and return type {@link Object},
     * <code>false</code> otherwise
     * @throws IllegalStateException If an around-construct method must not be declared as abstract, final or static,
     *                               or if the count of method arguments is not only one or the argument type is not
     *                               {@link InvocationContext},
     *                               or if the return type of method is not <code>void</code>
     */
    public static boolean isAroundConstructMethod(Method method) {
        return isInterceptionMethod(method, AROUND_CONSTRUCT_ANNOTATION_TYPE, void.class);
    }

    /**
     * @param method the target {@link Method method}
     * @return <code>true</code> if the given method that annotated {@link PostConstruct} is any non-final,
     * non-static method with a single parameter of type {@link InvocationContext} and return type {@link Object},
     * <code>false</code> otherwise
     * @throws IllegalStateException If a post-construct method must not be declared as abstract, final or static,
     *                               or if the count of method arguments is not only one or the argument type is not
     *                               {@link InvocationContext}
     *                               or if the return type of method is not <code>void</code>
     */
    public static boolean isPostConstructMethod(Method method) {
        return isInterceptionMethod(method, POST_CONSTRUCT_ANNOTATION_TYPE, void.class);
    }

    /**
     * @param method the target {@link Method method}
     * @return <code>true</code> if the given method that annotated {@link PreDestroy} is any non-final,
     * non-static method with a single parameter of type {@link InvocationContext} and return type {@link Object},
     * <code>false</code> otherwise
     * @throws IllegalStateException If a pre-destroy method must not be declared as abstract, final or static,
     *                               or if the count of method arguments is not only one or the argument type is not
     *                               {@link InvocationContext},
     *                               or if the return type of method is not <code>void</code>
     */
    public static boolean isPreDestroyMethod(Method method) {
        return isInterceptionMethod(method, PRE_DESTROY_ANNOTATION_TYPE, void.class);
    }

    static boolean isInterceptionMethod(Method method, Class<? extends Annotation> annotationType,
                                        Class<?> validReturnType) {
        if (isAnnotationPresent(method, annotationType)) {
            validateMethodModifiers(method, annotationType);
            validateMethodReturnType(method, annotationType, validReturnType);
            validateMethodArguments(method, annotationType);
            return true;
        }
        return false;
    }

    /**
     * @param method         the given {@link Method method}
     * @param annotationType the given {@link Annotation annotation} {@link Class type}
     * @throws IllegalStateException If the given method is abstract, final or static
     */
    static void validateMethodModifiers(Method method, Class<? extends Annotation> annotationType)
            throws IllegalStateException {
        int modifiers = method.getModifiers();
        if (isAbstract(modifiers) || isFinal(modifiers) || isStatic(modifiers)) {
            throw new IllegalStateException(format("@s Method[%s] must not be abstract or final or static!",
                    annotationType.getName(), method.toString()));
        }
    }

    private static void validateMethodReturnType(Method method, Class<? extends Annotation> annotationType, Class<?> validReturnType) {
        if (!validReturnType.isAssignableFrom(method.getReturnType())) {
            throw new IllegalStateException(
                    format("The return type of @s Method[%s] must be %s or its derived type , actual type %s!",
                            annotationType.getName(), method.toString(), validReturnType.getName(),
                            method.getReturnType().getName()));
        }
    }

    /**
     * @param method         the given {@link Method method}
     * @param annotationType
     * @throws IllegalStateException If the count of method arguments is not only one or
     *                               the argument type is not {@link InvocationContext}
     */
    static void validateMethodArguments(Method method, Class<? extends Annotation> annotationType) throws IllegalStateException {
        Class[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new IllegalStateException(format("@s Method[%s] must have only one argument!",
                    annotationType.getName(), method.toString()));
        }

        if (!InvocationContext.class.equals(parameterTypes[0])) {
            throw new IllegalStateException(format("There is only one argument must be an %s instance is declared in the @%s method[%s]!",
                    annotationType.getName(), InvocationContext.class.getName(), method.toString()));
        }
    }

    /**
     * An interceptor class must not be abstract and must have a public no-arg constructor.
     *
     * @param interceptorClass the class of interceptor
     * @throws NullPointerException  If <code>interceptorClass</code> is <code>null</code>
     * @throws IllegalStateException If an interceptor class does not annotate @Interceptor or
     *                               is abstract or have not a public no-arg constructor
     */
    public static void validateInterceptorClass(Class<?> interceptorClass) throws NullPointerException, IllegalStateException {
        requireNonNull(interceptorClass, "The argument 'interceptorClass' must not be null!");
        if (!interceptorClass.isAnnotationPresent(INTERCEPTOR_ANNOTATION_TYPE)) {
            throw new IllegalStateException(format("The Interceptor class[%s] must annotate %s",
                    interceptorClass, INTERCEPTOR_ANNOTATION_TYPE));
        }

        validateInterceptorClassModifiers(interceptorClass);
        validateInterceptorClassConstructors(interceptorClass);
        validateInterceptorClassMethods(interceptorClass);
    }

    private static void validateInterceptorClassModifiers(Class<?> interceptorClass) {
        int modifies = interceptorClass.getModifiers();
        if (isAbstract(modifies)) {
            throw newIllegalStateException("The Interceptor class[%s] must not be declared abstract!",
                    interceptorClass.getName());
        }
        if (isFinal(modifies)) {
            throw newIllegalStateException("The Interceptor class[%s] must not be declared final!",
                    interceptorClass.getName());
        }
    }

    private static void validateInterceptorClassConstructors(Class<?> interceptorClass) {
        if (!hasNonPrivateConstructorWithoutParameters(interceptorClass)) {
            throw newIllegalStateException("The Interceptor class[%s] must have a public no-arg constructor!",
                    interceptorClass.getName());
        }
    }

    private static void validateInterceptorClassMethods(Class<?> interceptorClass) {
    }

    public static boolean isAnnotatedInterceptorBinding(Class<? extends Annotation> annotationType) {
        return isMetaAnnotation(annotationType, INTERCEPTOR_BINDING_ANNOTATION_TYPE);
    }

    public static <A extends Annotation> A resolveInterceptorBinding(Method method, Class<A> interceptorBindingType) {
        if (method == null) {
            return null;
        }
        return searchAnnotation(method, interceptorBindingType);
    }

    public static <A extends Annotation> A resolveInterceptorBinding(Constructor constructor, Class<A> interceptorBindingType) {
        if (constructor == null) {
            return null;
        }
        return searchAnnotation(constructor, interceptorBindingType);
    }

    public static boolean isAnnotatedInterceptorBinding(Executable executable,
                                                        Class<? extends Annotation> interceptorBindingType) {
        return searchAnnotation(executable, interceptorBindingType) != null;
    }

    private static IllegalStateException newIllegalStateException(String messagePattern, Object... args) {
        return new IllegalStateException(format(messagePattern, args));
    }

}
