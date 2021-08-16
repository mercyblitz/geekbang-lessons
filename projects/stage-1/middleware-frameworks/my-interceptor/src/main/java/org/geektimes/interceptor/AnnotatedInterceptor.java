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
import org.geektimes.commons.reflect.util.TypeUtils;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InterceptorBinding;
import javax.interceptor.InvocationContext;
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
import static org.geektimes.commons.function.Streams.stream;
import static org.geektimes.commons.lang.util.AnnotationUtils.findAnnotation;

/**
 * The abstract annotated {@link javax.interceptor.Interceptor @Interceptor} class
 *
 * @param <A> the type of {@link Annotation}
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class AnnotatedInterceptor<A extends Annotation> implements Interceptor, Prioritized {

    private static final Class<? extends Annotation> INTERCEPTOR_ANNOTATION_TYPE = javax.interceptor.Interceptor.class;

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Class<A> bindingAnnotationType;

    private int priority = Prioritized.NORMAL_PRIORITY;

    /**
     * @throws IllegalArgumentException If the implementation does not annotate {@link Interceptor @Interceptor} or
     *                                  the generic parameter type does not be specified.
     */
    public AnnotatedInterceptor() throws IllegalArgumentException {
        if (!getClass().isAnnotationPresent(INTERCEPTOR_ANNOTATION_TYPE)) {
            throw new IllegalArgumentException(
                    format("The Interceptor class[%s] must annotate %s", getClass(), INTERCEPTOR_ANNOTATION_TYPE));
        }
        this.bindingAnnotationType = resolveInterceptorBindingAnnotationType();
    }

    @Override
    public final int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @AroundInvoke
    public final Object execute(InvocationContext context) throws Throwable {
        A bindingAnnotation = findInterceptorBindingAnnotation(context.getMethod());
        if (bindingAnnotation == null) { // try to find the Constructor
            bindingAnnotation = findInterceptorBindingAnnotation(context.getConstructor());
        }
        if (bindingAnnotation == null) { // bindingAnnotation not found
            return context.proceed();
        } else {
            return execute(context, bindingAnnotation);
        }
    }

    /**
     * Executes
     *
     * @param context           {@link InvocationContext}
     * @param bindingAnnotation the instance of {@link Annotation} annotated by {@link InterceptorBinding}
     * @return the result of {@link InvocationContext#proceed()} method
     * @throws Throwable any exception if found
     */
    protected abstract Object execute(InvocationContext context, A bindingAnnotation) throws Throwable;

    /**
     * Get the type of {@link Annotation} annotated by {@link InterceptorBinding}
     *
     * @return non-null
     */
    protected Class<A> resolveInterceptorBindingAnnotationType() {
        List<Class<?>> typeArguments = TypeUtils.resolveTypeArguments(getClass());
        Class<A> annotationType = null;
        for (Class<?> typeArgument : typeArguments) {
            if (typeArgument.isAnnotation()) {
                annotationType = (Class<A>) typeArgument;
                if (!annotationType.isAnnotationPresent(InterceptorBinding.class)) {
                    if (logger.isLoggable(Level.SEVERE)) {
                        logger.severe(format("The annotationType[%s] should annotate %s",
                                typeArgument.getName(),
                                InterceptorBinding.class.getName()));
                    }
                } else {
                    annotationType = (Class<A>) typeArgument;
                    assertInterceptorBindingAnnotationType(annotationType);
                }
                break;
            }
        }

        return annotationType;
    }

    protected void assertInterceptorBindingAnnotationType(Class<A> annotationType) {
        if (annotationType == null) {
            throw new IllegalArgumentException(format("There is no @InterceptorBinding annotation type " +
                    "as the generic parameter argument in the Interceptor class[%s]!", getClass().getName()));
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

    protected A findInterceptorBindingAnnotation(Method method) {
        A annotation = findAnnotation(method, bindingAnnotationType);
        if (annotation == null && method != null) {
            annotation = method.getDeclaringClass().getAnnotation(bindingAnnotationType);
        }
        return annotation;
    }

    protected A findInterceptorBindingAnnotation(Constructor<?> constructor) {
        A annotation = findAnnotation(constructor, bindingAnnotationType);
        if (annotation == null && constructor != null) {
            annotation = constructor.getDeclaringClass().getAnnotation(bindingAnnotationType);
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
}
