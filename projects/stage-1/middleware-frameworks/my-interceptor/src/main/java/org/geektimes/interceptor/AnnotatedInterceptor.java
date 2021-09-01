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
package org.geektimes.interceptor;

import org.geektimes.commons.lang.Prioritized;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.*;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.ServiceLoader.load;
import static java.util.stream.Collectors.toList;
import static org.geektimes.commons.function.Streams.stream;
import static org.geektimes.commons.lang.util.AnnotationUtils.findAnnotation;
import static org.geektimes.commons.lang.util.AnnotationUtils.getDeclaredAnnotations;
import static org.geektimes.commons.reflect.util.TypeUtils.resolveTypeArguments;
import static org.geektimes.interceptor.InterceptorRegistry.getInstance;
import static org.geektimes.interceptor.util.Interceptors.INTERCEPTOR_ANNOTATION_TYPE;

/**
 * The abstract annotated {@link javax.interceptor.Interceptor @Interceptor} class
 *
 * @param <A> the type of {@link Annotation}
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class AnnotatedInterceptor<A extends Annotation> implements Interceptor, Prioritized {


    private final Logger logger = Logger.getLogger(getClass().getName());

    private final InterceptorRegistry interceptorRegistry;

    private final Class<A> interceptorBindingType;

    private int priority = Prioritized.NORMAL_PRIORITY;

    /**
     * @throws IllegalArgumentException If the implementation does not annotate {@link Interceptor @Interceptor} or
     *                                  the generic parameter type does not be specified.
     */
    public AnnotatedInterceptor() throws IllegalArgumentException {
        Class<?> interceptorClass = getClass();
        this.interceptorRegistry = getInstance(interceptorClass.getClassLoader());
        this.interceptorRegistry.registerInterceptorClass(interceptorClass);
        this.interceptorBindingType = resolveInterceptorBindingType(interceptorClass);
        validateInterceptorBindingType(interceptorBindingType);
        this.interceptorRegistry.registerInterceptor(this);
    }

    @Override
    public final int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Throwable {
        A interceptorBinding = findInterceptorBinding(context.getMethod());
        return interceptorBinding == null ? context.proceed() : intercept(context, interceptorBinding);
    }

    @AroundTimeout
    public Object interceptTimeout(InvocationContext context) throws Throwable {
        A interceptorBinding = findInterceptorBinding(context.getMethod());
        return interceptorBinding == null ? context.proceed() : interceptTimeout(context, interceptorBinding);
    }

    @AroundConstruct
    public void interceptConstruct(InvocationContext context) throws Throwable {
        A interceptorBinding = findInterceptorBinding(context.getConstructor());
        if(interceptorBinding == null){
            context.proceed();
        }else {
            interceptConstruct(context, interceptorBinding);
        }
    }

    @PostConstruct
    public void interceptPostConstruct(InvocationContext context) throws Throwable {
        beforePostConstruct(context.getTarget(), context.getMethod());
        context.proceed();
        afterPostConstruct(context.getTarget(), context.getMethod());
    }

    @PreDestroy
    public void interceptPreDestroy(InvocationContext context) throws Throwable {
        beforePreDestroy(context.getTarget(), context.getMethod());
        context.proceed();
        afterPreDestroy(context.getTarget(), context.getMethod());
    }

    /**
     * Executes {@link AroundInvoke @AroundInvoke} method
     *
     * @param context            {@link InvocationContext}
     * @param interceptorBinding the instance of {@link Annotation} annotated by {@link InterceptorBinding}
     * @return the result of {@link InvocationContext#proceed()} method
     * @throws Throwable any exception if occurs
     */
    protected abstract Object intercept(InvocationContext context, A interceptorBinding) throws Throwable;

    /**
     * Executes {@link AroundTimeout @AroundTimeout} method
     *
     * @param context            {@link InvocationContext}
     * @param interceptorBinding the instance of {@link Annotation} annotated by {@link InterceptorBinding}
     * @return the result of {@link InvocationContext#proceed()} method
     * @throws Throwable any exception if occurs
     */
    protected Object interceptTimeout(InvocationContext context, A interceptorBinding) throws Throwable {
        return null;
    }

    /**
     * Executes {@link AroundConstruct @AroundConstruct} method
     *
     * @param context            {@link InvocationContext}
     * @param interceptorBinding the instance of {@link Annotation} annotated by {@link InterceptorBinding}
     * @throws Throwable any exception if occurs
     */
    protected void interceptConstruct(InvocationContext context, A interceptorBinding) throws Throwable {
    }

    /**
     * Before {@link PostConstruct @PostConstruct} interception
     *
     * @param target the intercepted target object
     * @param method the intercepted {@link Method}
     * @throws Throwable any exception if occurs
     */
    protected void beforePostConstruct(Object target, Method method) throws Throwable {
    }

    /**
     * After {@link PostConstruct @PostConstruct} interception
     *
     * @param target the intercepted target object
     * @param method the intercepted {@link Method}
     * @throws Throwable any exception if occurs
     */
    protected void afterPostConstruct(Object target, Method method) throws Throwable {
    }

    /**
     * Before {@link PreDestroy @PreDestroy} interception
     *
     * @param target the intercepted target object
     * @param method the intercepted {@link Method}
     * @throws Throwable any exception if occurs
     */
    protected void beforePreDestroy(Object target, Method method) throws Throwable {
    }

    /**
     * After {@link PreDestroy @PreDestroy} interception
     *
     * @param target the intercepted target object
     * @param method the intercepted {@link Method}
     * @throws Throwable any exception if occurs
     */
    protected void afterPreDestroy(Object target, Method method) throws Throwable {
    }

    /**
     * Get the type of {@link Annotation} annotated by {@link InterceptorBinding}
     *
     * @param interceptorClass
     * @return non-null
     */
    protected Class<A> resolveInterceptorBindingType(Class<?> interceptorClass) {
        List<Annotation> interceptorBindings = getDeclaredAnnotations(interceptorClass, this::excludeInterceptorAnnotation);
        List<Class<? extends Annotation>> interceptorBindingTypes = interceptorBindings.stream().map(Annotation::annotationType).collect(toList());
        Class<A> annotationType = null;

        for (Class<?> typeArgument : resolveTypeArguments(getClass())) {
            if (typeArgument.isAnnotation() && typeArgument.isAnnotationPresent(InterceptorBinding.class)) {
                annotationType = (Class<A>) typeArgument;
            } else if (interceptorBindingTypes.contains(typeArgument)) {
                annotationType = (Class<A>) typeArgument;
                if (shouldRegisterSyntheticInterceptorBindingType()) {
                    interceptorRegistry.registerInterceptorBindingType(annotationType);
                }
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.severe(format("The annotationType[%s] should annotate %s",
                            typeArgument.getName(),
                            InterceptorBinding.class.getName()));
                }
            }
        }

        if (annotationType == null) {
            throw new IllegalArgumentException(format("There is no interceptor binding annotation found in the " +
                            "@%s Class[%s], whose type should be same as the generic argument type!",
                    INTERCEPTOR_ANNOTATION_TYPE.getName(), interceptorClass.getName()));
        }

        return annotationType;
    }

    protected boolean shouldRegisterSyntheticInterceptorBindingType() {
        return false;
    }

    protected void validateInterceptorBindingType(Class<A> annotationType) {
        if (!interceptorRegistry.isInterceptorBindingType(annotationType)) {
            String message = format("There interceptor binding annotation is invalid in the Interceptor class[%s]! " +
                            "Please check it that should annotate @%s or be registered as a synthetic interceptor " +
                            "binding type by InterceptorRegistry#registerInterceptorBindingType method!",
                    getClass().getName(), INTERCEPTOR_ANNOTATION_TYPE.getName());
            throw new IllegalArgumentException(message);
        }

        Target target = annotationType.getAnnotation(Target.class);
        ElementType[] elementTypes = target.value();
        if (Arrays.stream(elementTypes).anyMatch(ElementType.TYPE::equals)) {
            if (!getClass().isAnnotationPresent(annotationType)) {
                throw new IllegalArgumentException(format("The @%s must be annotated on the type[%s]!",
                        annotationType.getName(), getClass().getName()));
            }
        }
    }

    protected A findInterceptorBinding(Method method) {
        A annotation = findAnnotation(method, interceptorBindingType);
        if (annotation == null && method != null) {
            annotation = method.getDeclaringClass().getAnnotation(interceptorBindingType);
        }
        return annotation;
    }

    protected A findInterceptorBinding(Constructor<?> constructor) {
        A annotation = findAnnotation(constructor, interceptorBindingType);
        if (annotation == null && constructor != null) {
            annotation = constructor.getDeclaringClass().getAnnotation(interceptorBindingType);
        }
        return annotation;
    }

    protected Throwable getFailure(Throwable e) {
        Throwable failure = e instanceof InvocationTargetException ? e.getCause() : e;
        while (failure instanceof InvocationTargetException) {
            failure = getFailure(failure);
        }
        return failure;
    }

    /**
     * Load the sorted instances of {@link AnnotatedInterceptor} via Java Standard SPI.
     *
     * @return non-null
     */
    public static AnnotatedInterceptor<?>[] loadInterceptors() {
        return stream(load(AnnotatedInterceptor.class))
                .sorted()
                .toArray(AnnotatedInterceptor[]::new);
    }

    public InterceptorRegistry getInterceptorRegistry() {
        return interceptorRegistry;
    }

    private boolean excludeInterceptorAnnotation(Annotation annotation) {
        return !INTERCEPTOR_ANNOTATION_TYPE.equals(annotation.annotationType());
    }
}
