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

import org.geektimes.enterprise.inject.standard.ConstructorParameterInjectionPoint;
import org.geektimes.enterprise.inject.standard.FieldInjectionPoint;
import org.geektimes.enterprise.inject.standard.MethodParameterInjectionPoint;

import javax.enterprise.inject.spi.*;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;

import static java.lang.String.format;
import static java.util.Collections.*;

/**
 * The utilities class for injection.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Injections {

    public static void validateForbiddenAnnotation(Method initializerMethod,
                                                   Class<? extends Annotation> annotationType) {
        if (initializerMethod.isAnnotationPresent(annotationType)) {
            String errorMessage = format("The Initializer Method[%s] must not annotate @%s!",
                    initializerMethod,
                    annotationType.getName()
            );
            throw new DefinitionException(errorMessage);
        }
    }

    public static void validateForbiddenAnnotation(InjectionPoint injectionPoint,
                                                   Class<? extends Annotation> annotationType) {
        if (injectionPoint.getAnnotated().isAnnotationPresent(annotationType)) {
            Member member = injectionPoint.getMember();
            String errorMessage = format("The @Inject %s[%s] must not annotate @%s!",
                    member.getClass().getSimpleName(),
                    member,
                    annotationType.getName()
            );
            throw new DefinitionException(errorMessage);
        }
    }

    public static Map<AnnotatedConstructor, List<ConstructorParameterInjectionPoint>> getConstructorParameterInjectionPointsMap(AnnotatedType annotatedType, Bean<?> bean) {
        if (annotatedType == null) {
            return emptyMap();
        }

        Set<AnnotatedConstructor> annotatedConstructors = annotatedType.getConstructors();

        if (annotatedConstructors.isEmpty()) {
            return emptyMap();
        }

        Map<AnnotatedConstructor, List<ConstructorParameterInjectionPoint>> injectionPointsMap =
                new LinkedHashMap<>(annotatedConstructors.size());

        for (AnnotatedConstructor annotatedConstructor : annotatedConstructors) {
            if (annotatedConstructor.isAnnotationPresent(Inject.class)) {
                List<AnnotatedParameter> annotatedParameters = annotatedConstructor.getParameters();
                List<ConstructorParameterInjectionPoint> injectionPoints = new ArrayList<>(annotatedParameters.size());
                for (AnnotatedParameter annotatedParameter : annotatedParameters) {
                    injectionPoints.add(new ConstructorParameterInjectionPoint(annotatedParameter,
                            annotatedConstructor, bean));
                }
                injectionPointsMap.put(annotatedConstructor, unmodifiableList(injectionPoints));
                break;
            }
        }

        return unmodifiableMap(injectionPointsMap);
    }

    public static Set<FieldInjectionPoint> getFieldInjectionPoints(AnnotatedType annotatedType, Bean<?> bean) {
        if (annotatedType == null) {
            return emptySet();
        }

        Set<AnnotatedField> annotatedFields = annotatedType.getFields();

        if (annotatedFields.isEmpty()) {
            return emptySet();
        }

        Set<FieldInjectionPoint> injectionPoints = new LinkedHashSet<>(annotatedFields.size());

        for (AnnotatedField annotatedField : annotatedFields) {
            if (annotatedField.isAnnotationPresent(Inject.class)) {
                FieldInjectionPoint injectionPoint = new FieldInjectionPoint(annotatedField, bean);
                injectionPoints.add(injectionPoint);
            }
        }

        return injectionPoints;
    }

    public static Map<AnnotatedMethod, List<MethodParameterInjectionPoint>> getMethodParameterInjectionPoints(AnnotatedType annotatedType, Bean<?> bean) {
        if (annotatedType == null) {
            return emptyMap();
        }

        Set<AnnotatedMethod> annotatedMethods = annotatedType.getMethods();

        if (annotatedMethods.isEmpty()) {
            return emptyMap();
        }

        Map<AnnotatedMethod, List<MethodParameterInjectionPoint>> injectionPointsMap =
                new LinkedHashMap<>(annotatedMethods.size());

        for (AnnotatedMethod annotatedMethod : annotatedMethods) {
            if (annotatedMethod.isAnnotationPresent(Inject.class)) {
                List<AnnotatedParameter> annotatedParameters = annotatedMethod.getParameters();
                List<MethodParameterInjectionPoint> injectionPoints = new ArrayList<>(annotatedParameters.size());
                for (AnnotatedParameter annotatedParameter : annotatedParameters) {
                    injectionPoints.add(new MethodParameterInjectionPoint(annotatedParameter, annotatedMethod, bean));
                }
                injectionPointsMap.put(annotatedMethod, unmodifiableList(injectionPoints));
            }
        }

        return injectionPointsMap;
    }
}
