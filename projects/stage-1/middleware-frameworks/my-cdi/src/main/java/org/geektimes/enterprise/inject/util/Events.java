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

import com.sun.xml.internal.rngom.digested.DDataPattern;

import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.DefinitionException;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;
import static org.geektimes.commons.lang.util.AnnotationUtils.isAnnotationPresent;

/**
 * The utilities class for CDI Events
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Events {


    private static final Map<Method, Parameter> observedParametersCache = new ConcurrentHashMap<>();

    /**
     * Check the specified {@link Parameter parameter} annotated {@link Observes}
     * or {@link ObservesAsync}
     *
     * @param parameter {@link Parameter}
     * @return <code>true</code> if annotated  {@link Observes} or {@link ObservesAsync},
     * <code>false</code> otherwise.
     */
    public static boolean isObservedParameter(Parameter parameter) {
        return isAnnotationPresent(parameter, Observes.class)
                || isAnnotationPresent(parameter, ObservesAsync.class);
    }

    public static boolean validateObserverMethod(Method method) {
        if (hasObservedParameter(method)) {
            return true;
        }

        validateObserverMethodAnnotations(method, Produces.class, Inject.class);
        return validateObserverMethodParameters(method);
    }

    /**
     * @param method                   {@link Method}
     * @param forbiddenAnnotationTypes
     * @throws DefinitionException If an observer method is annotated {@link Produces @Produces} or
     *                             {@link Inject @Inject}
     */
    private static void validateObserverMethodAnnotations(Method method,
                                                          Class<? extends Annotation>... forbiddenAnnotationTypes) throws DefinitionException {
        for (Class<? extends Annotation> forbiddenAnnotationType : forbiddenAnnotationTypes) {
            if (method.isAnnotationPresent(forbiddenAnnotationType)) {
                String message = format("An observer method must not annotate %s!", forbiddenAnnotationType.getName());
                throw new DefinitionException(message);
            }
        }
    }

    /**
     * @param method
     * @return
     * @throws DefinitionException If a method has more than one parameter annotated @Observes or @ObservesAsync
     *                             or has a parameter annotated @Disposes
     */
    private static boolean validateObserverMethodParameters(Method method) throws DefinitionException {

        Parameter[] parameters = method.getParameters();

        Parameter observedParameter = null;
        for (Parameter parameter : parameters) {
            if (observedParameter != null) {
                String message = format("An observer method must not have more than one parameter annotated @%s or @%s",
                        Observes.class.getName(), ObservesAsync.class.getName());
                throw new DefinitionException(message);
            }

            if (isObservedParameter(parameter)) {
                observedParameter = parameter;
            }
            if (parameter.isAnnotationPresent(Disposes.class)) {
                String message = format("An observer method must not annotate @%s!", Disposes.class.getName());
                throw new DefinitionException(message);
            }

        }

        if (observedParameter != null) {
            observedParametersCache.put(method, observedParameter);
            return true;
        }

        return false;
    }

    public static Parameter getObservedParameter(Method method) {
        return observedParametersCache.get(method);
    }

    public static boolean hasObservedParameter(Method method){
        return observedParametersCache.containsKey(method);
    }

}
