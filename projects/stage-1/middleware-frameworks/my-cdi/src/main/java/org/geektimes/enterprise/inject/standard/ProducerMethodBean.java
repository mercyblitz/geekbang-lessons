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
package org.geektimes.enterprise.inject.standard;

import org.geektimes.enterprise.inject.util.Beans;

import javax.decorator.Decorator;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.spi.*;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

/**
 * Producer {@link Method} {@link Bean} based on Java Reflection
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ProducerMethodBean<T> extends AbstractBean<Method, T> {

    public ProducerMethodBean(Method producerMethod) {
        super(producerMethod, producerMethod.getReturnType());
    }

    @Override
    protected void validateAnnotatedElement(Method producerMethod) {
        validateProducerMethodProduces(producerMethod);
        validateProducerMethodSpecializes(producerMethod);
        validateProducerMethodDeclaringClass(producerMethod);
        validateProducerMethodParameters(producerMethod);
    }

    /**
     * @param producerMethod {@link Method Producer Method}
     * @throws DefinitionException if {@link Method Producer Method} does not annotate {@link Produces @Produces}
     */
    private void validateProducerMethodProduces(Method producerMethod) throws DefinitionException {
        validateProducerMethodRequiredAnnotation(producerMethod, Produces.class);
    }

    private void validateProducerMethodRequiredAnnotation(Method producerMethod,
                                                          Class<? extends Annotation> annotationType) {
        if (!producerMethod.isAnnotationPresent(annotationType)) {
            String errorMessage = format("The Producer Method[%s] must annotate @%s",
                    producerMethod.toGenericString(),
                    annotationType.getName()
            );
            throw new DefinitionException(errorMessage);
        }
    }

    /**
     * If a producer method X is annotated {@link Specializes @Specializes}, then it must be non-static and directly override
     * another producer method Y. Then X directly specializes Y, as defined in Specialization.
     * <p>
     *
     * @param producerMethod {@link Method Producer Method}
     * @throws DefinitionException If the method is static or does not directly override another producer method,
     *                             the container automatically detects the problem and treats it as a
     *                             definition error.
     */
    private void validateProducerMethodSpecializes(Method producerMethod) throws DefinitionException {
        if (producerMethod.isAnnotationPresent(Specializes.class)) {
            Class<?> declaringClass = producerMethod.getDeclaringClass();
            Class<?> superClass = declaringClass.getSuperclass();
            String methodName = producerMethod.getName();
            Class<?>[] parameterTypes = producerMethod.getParameterTypes();
            Method superProducerMethod = null;
            try {
                superProducerMethod = superClass.getMethod(methodName, parameterTypes);
                validateProducerMethodProduces(superProducerMethod);
            } catch (NoSuchMethodException e) {
                String errorMessage = format("The override Producer Method[name : %s , param-types : %s] is not found " +
                        "in the super class[%s]", methodName, asList(parameterTypes), superClass.getName());
                throw new DefinitionException(errorMessage, e);
            }
        }
    }

    /**
     * @param producerMethod Producer {@link Method}
     * @throws DefinitionException Interceptors and decorators may not declare producer methods.
     *                             If an interceptor or decorator has a method annotated {@link Produces @Produces},
     *                             the container automatically detects the problem and treats it as a definition error.
     */
    private void validateProducerMethodDeclaringClass(Method producerMethod) throws DefinitionException {
        Class<?> declaringType = producerMethod.getDeclaringClass();
        validateDeclaringType(declaringType, Interceptor.class);
        validateDeclaringType(declaringType, Decorator.class);
    }

    private void validateDeclaringType(Class<?> declaringType, Class<? extends Annotation> annotationType) throws DefinitionException {
        if (declaringType.isAnnotationPresent(annotationType)) {
            String errorMessage = format("The Producer Methods' declaring type[type : %s] must not annotate @%s",
                    declaringType.getName(),
                    annotationType.getName()
            );
            throw new DefinitionException(errorMessage);
        }
    }

    private void validateProducerMethodParameters(Method producerMethod) {
        int parameterCount = producerMethod.getParameterCount();
        Parameter[] parameters = producerMethod.getParameters();
        for (int i = 0; i < parameterCount; i++) {
            Parameter parameter = parameters[i];
            validateAnnotatedParameter(parameter, i, producerMethod);
        }
    }

    /**
     * @param parameter      {@link Parameter}
     * @param index          the index of {@link Parameter}
     * @param producerMethod {@link Method Producer Method}
     * @throws DefinitionException If a producer method is annotated {@link Inject @Inject},
     *                             has a parameter annotated {@link Disposes @Disposes},
     *                             has a parameter annotated {@link Observes @Observes}, or
     *                             has a parameter annotated {@link ObservesAsync @ObservesAsync},
     *                             the container automatically detects the problem and treats it as a definition error.
     */
    private void validateAnnotatedParameter(Parameter parameter, int index, Method producerMethod) throws DefinitionException {
        if (producerMethod.isAnnotationPresent(Inject.class)) {
            validateParameterAnnotation(parameter, index, Disposes.class);
            validateParameterAnnotation(parameter, index, Observes.class);
            validateParameterAnnotation(parameter, index, ObservesAsync.class);
        }
    }

    private void validateParameterAnnotation(Parameter parameter,
                                             int index,
                                             Class<? extends Annotation> annotationType) throws DefinitionException {
        if (parameter.isAnnotationPresent(annotationType)) {
            String errorMessage = format("The Producer Methods' parameter[type : %s , name : % , index : %d] must not annotate @%s",
                    parameter.getParameterizedType(),
                    parameter.getName(),
                    index,
                    annotationType.getName()
            );
            throw new DefinitionException(errorMessage);
        }
    }

    @Override
    protected String getBeanName(Method producerMethod) {
        return Beans.getBeanName(producerMethod);
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        // TODO
        return null;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        // TODO
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        Method producerMethod = getAnnotatedElement();
        AnnotatedMethod annotatedMethod = new ReflectiveAnnotatedMethod(producerMethod);

        List<AnnotatedParameter> annotatedParameters = annotatedMethod.getParameters();
        Set<InjectionPoint> injectionPoints = new LinkedHashSet<>();

        for (AnnotatedParameter annotatedParameter : annotatedParameters) {
            InjectionPoint injectionPoint = new MethodParameterInjectionPoint(annotatedParameter, annotatedMethod, this);
            injectionPoints.add(injectionPoint);
        }

        return unmodifiableSet(injectionPoints);
    }
}
