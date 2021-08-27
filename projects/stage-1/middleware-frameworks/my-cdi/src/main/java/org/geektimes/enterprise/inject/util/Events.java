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

import org.geektimes.enterprise.inject.standard.event.ObserverMethodParameter;

import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.DefinitionException;
import javax.enterprise.inject.spi.EventMetadata;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.geektimes.commons.lang.util.AnnotationUtils.isAnnotationPresent;
import static org.geektimes.enterprise.inject.standard.event.ObserverMethodParameter.*;

/**
 * The utilities class for CDI Events
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Events {


    private static final Map<Method, List<ObserverMethodParameter>> observerMethodParametersCache = new ConcurrentHashMap<>();

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

    public static boolean isEventMetadata(Parameter parameter) {
        return parameter != null && EventMetadata.class.equals(parameter.getType());
    }

    public static boolean validateObserverMethod(Method method) {
        if (hasObservedParameter(method)) {
            return true;
        }

        if (validateObserverMethodParameters(method)) {
            validateObserverMethodAnnotations(method, Produces.class, Inject.class);
        }
        return hasObservedParameter(method);
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
     * @return <code>true</code> if <code>method</code> is the Observer method
     * @throws DefinitionException If a method has more than one parameter annotated @Observes or @ObservesAsync
     *                             or has a parameter annotated @Disposes
     */
    private static boolean validateObserverMethodParameters(Method method) throws DefinitionException {

        List<Parameter> parameters = new LinkedList<>(asList(method.getParameters()));

        List<ObserverMethodParameter> observerMethodParameters = new ArrayList<>(parameters.size());

        ListIterator<Parameter> iterator = parameters.listIterator();

        int observedParameterCount = 0;


        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            Parameter parameter = iterator.next();

            if (observedParameterCount > 1) {
                String message = format("An observer method must not have more than one parameter annotated @%s or @%s",
                        Observes.class.getName(), ObservesAsync.class.getName());
                throw new DefinitionException(message);
            } else if (isObservedParameter(parameter)) {
                observedParameterCount++;
                observerMethodParameters.add(observedParameter(parameter, index));
                iterator.remove();
            } else if (isEventMetadata(parameter)) {
                observerMethodParameters.add(eventMetadataParameter(parameter, index));
                iterator.remove();
            }
        }

        if (observedParameterCount == 1) {
            // remaining
            for (int i = 0; i < parameters.size(); i++) {
                Parameter parameter = parameters.get(i);
                if (parameter.isAnnotationPresent(Disposes.class)) {
                    String message = format("An observer method must not annotate @%s!", Disposes.class.getName());
                    throw new DefinitionException(message);
                } else {
                    observerMethodParameters.add(injectedParameter(parameter, i));
                }
            }
            observerMethodParametersCache.put(method, observerMethodParameters);
            return true;
        }

        // else
        observerMethodParameters.clear();
        return false;

    }

    public static List<ObserverMethodParameter> getObserverMethodParameters(Method method) {
        return observerMethodParametersCache.getOrDefault(method, emptyList());
    }

    public static boolean hasObservedParameter(Method method) {
        return observerMethodParametersCache.containsKey(method);
    }

    public static ObserverMethodParameter getObservedParameter(Method method) {
        return getObservedParameter(getObserverMethodParameters(method));
    }

    public static ObserverMethodParameter getObservedParameter(List<ObserverMethodParameter> observerMethodParameters) {
        return observerMethodParameters.stream()
                .filter(ObserverMethodParameter::isObserved)
                .findFirst()
                .orElse(null);
    }


}
