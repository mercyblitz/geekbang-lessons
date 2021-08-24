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

import org.geektimes.enterprise.inject.standard.ManagedBean;
import org.geektimes.enterprise.inject.standard.ProducerMethodBean;

import javax.decorator.Decorator;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.DefinitionException;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static org.geektimes.commons.reflect.util.MemberUtils.isStatic;
import static org.geektimes.commons.reflect.util.TypeUtils.asParameterizedType;
import static org.geektimes.enterprise.inject.util.Producers.validateProducerRequiredAnnotation;

/**
 * The utilities class {@link Method Producer Methods}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class ProducerMethods {

    public static Set<ProducerMethodBean> resolveProducerMethodBeans(ManagedBean<?> managedBean) {
        Set<ProducerMethodBean> producerMethodBeans = new LinkedHashSet<>();
        Set<AnnotatedMethod> methods = managedBean.getAnnotatedType().getMethods();
        for (AnnotatedMethod method : methods) {
            Method javaMethod = method.getJavaMember();
            if (isProduceMethod(javaMethod)) {
                ProducerMethodBean producerMethodBean = new ProducerMethodBean(javaMethod);
                producerMethodBeans.add(producerMethodBean);
            }
        }
        return unmodifiableSet(producerMethodBeans);
    }

    /**
     * A producer method must be a default-access, public, protected or private,
     * non-abstract method of a managed bean class.
     * A producer method may be either static or non-static.
     *
     * @param method {@link Method}
     * @return <code>true</code> if {@link Method} is producer
     */
    private static boolean isProduceMethod(Method method) {
        return !isStatic(method) && method.isAnnotationPresent(Produces.class);
    }

    /**
     * @param producerMethod {@link Method Producer Method}
     * @throws DefinitionException if {@link Method Producer Method} does not annotate {@link Produces @Produces}
     */
    public static void validateProducerMethodProduces(Method producerMethod) throws DefinitionException {
        validateProducerRequiredAnnotation(producerMethod, Produces.class);
    }

    /**
     * <ul>
     *     <li>
     *         If the producer method return type is a parameterized type, it must specify an actual type parameter or
     *         type variable for each type parameter.
     *     </li>
     *     <li>
     *         If a producer method return type contains a wildcard type parameter or is an array type whose component
     *         type contains a wildcard type parameter, the container automatically detects the problem and treats it as a definition error.
     *     </li>
     *     <li>
     *         If the producer method return type is a parameterized type with a type variable, it must have scope
     *         {@link Dependent @Dependent}.
     *     </li>
     *     <li>
     *         If a producer method with a parameterized return type with a type variable declares any scope other than
     *         {@link Dependent @Dependent}, the container automatically detects the problem and
     *         treats it as a definition error.
     *     </li>
     *     <li>
     *         If a producer method return type is a type variable or an array type whose component type is a type
     *         variable the container automatically detects the problem and treats it as a definition error.
     *     </li>
     * </ul>
     *
     * @param producerMethod the method annotated {@link Produces @Produces}
     */
    public static void validateProducerMethodReturnType(Method producerMethod) {
        Type returnType = producerMethod.getGenericReturnType();
        ParameterizedType returnParameterizedType = asParameterizedType(returnType);
        if (returnParameterizedType != null) {
            returnParameterizedType.getActualTypeArguments();
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
    public static void validateProducerMethodSpecializes(Method producerMethod) throws DefinitionException {
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
    public static void validateProducerMethodDeclaringClass(Method producerMethod) throws DefinitionException {
        Class<?> declaringType = producerMethod.getDeclaringClass();
        validateForbiddenAnnotation(declaringType, Interceptor.class);
        validateForbiddenAnnotation(declaringType, Decorator.class);
    }

    public static void validateProducerMethodParameters(Method producerMethod) {
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
    public static void validateAnnotatedParameter(Parameter parameter, int index, Method producerMethod) throws DefinitionException {
        if (producerMethod.isAnnotationPresent(Inject.class)) {
            validateForbiddenAnnotation(parameter, index, Disposes.class);
            validateForbiddenAnnotation(parameter, index, Observes.class);
            validateForbiddenAnnotation(parameter, index, ObservesAsync.class);
        }
    }

    private static void validateForbiddenAnnotation(Class<?> declaringType, Class<? extends Annotation> annotationType) throws DefinitionException {
        if (declaringType.isAnnotationPresent(annotationType)) {
            String errorMessage = format("The Producer Methods' declaring type[type : %s] must not annotate @%s",
                    declaringType.getName(),
                    annotationType.getName()
            );
            throw new DefinitionException(errorMessage);
        }
    }

    private static void validateForbiddenAnnotation(Parameter parameter,
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

}
