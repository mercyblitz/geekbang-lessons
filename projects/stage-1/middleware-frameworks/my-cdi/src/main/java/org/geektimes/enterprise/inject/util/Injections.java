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

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.*;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.emptySet;

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

    public static Set<InjectionPoint> getConstructorParameterInjectionPoints(AnnotatedType annotatedType, Bean<?> bean) {
        if (annotatedType == null) {
            return emptySet();
        }

        Set<AnnotatedConstructor> annotatedConstructors = annotatedType.getConstructors();

        if (annotatedConstructors.isEmpty()) {
            return emptySet();
        }

        Set<InjectionPoint> injectionPoints = new LinkedHashSet<>();

        for (AnnotatedConstructor annotatedConstructor : annotatedConstructors) {
            if (annotatedConstructor.isAnnotationPresent(Inject.class)) {
                List<AnnotatedParameter> annotatedParameters = annotatedConstructor.getParameters();
                for (AnnotatedParameter annotatedParameter : annotatedParameters) {
                    InjectionPoint injectionPoint = new ConstructorParameterInjectionPoint(annotatedParameter,
                            annotatedConstructor, bean);
                    injectionPoints.add(injectionPoint);
                }
                break;
            }
        }

        return injectionPoints;
    }

    public static Set<InjectionPoint> getFieldInjectionPoints(AnnotatedType annotatedType, Bean<?> bean) {
        if (annotatedType == null) {
            return emptySet();
        }

        Set<AnnotatedField> annotatedFields = annotatedType.getFields();

        if (annotatedFields.isEmpty()) {
            return emptySet();
        }

        Set<InjectionPoint> injectionPoints = new LinkedHashSet<>();

        for (AnnotatedField annotatedField : annotatedFields) {
            if (annotatedField.isAnnotationPresent(Inject.class)) {
                InjectionPoint injectionPoint = new FieldInjectionPoint(annotatedField, bean);
                injectionPoints.add(injectionPoint);
            }
        }

        return injectionPoints;
    }

    public static Set<InjectionPoint> getMethodParameterInjectionPoints(AnnotatedType annotatedType, Bean<?> bean) {
        if (annotatedType == null) {
            return emptySet();
        }

        Set<AnnotatedMethod> annotatedMethods = annotatedType.getMethods();

        if (annotatedMethods.isEmpty()) {
            return emptySet();
        }

        Set<InjectionPoint> injectionPoints = new LinkedHashSet<>();

        for (AnnotatedMethod annotatedMethod : annotatedMethods) {
            if (annotatedMethod.isAnnotationPresent(Inject.class)) {
                List<AnnotatedParameter> annotatedParameters = annotatedMethod.getParameters();
                for (AnnotatedParameter annotatedParameter : annotatedParameters) {
                    InjectionPoint injectionPoint = new MethodParameterInjectionPoint(annotatedParameter, annotatedMethod, bean);
                    injectionPoints.add(injectionPoint);
                }
            }
        }

        return injectionPoints;
    }
}
