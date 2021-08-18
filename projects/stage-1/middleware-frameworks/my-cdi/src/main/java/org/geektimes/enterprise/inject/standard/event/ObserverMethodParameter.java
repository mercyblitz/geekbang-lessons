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
package org.geektimes.enterprise.inject.standard.event;

import javax.enterprise.inject.spi.ObserverMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * The {@link ObserverMethod} Parameter
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ObserverMethodParameter implements AnnotatedElement {

    private static final int OBSERVED_PARAM_TYPE = 1;

    private static final int EVENT_METADATA_PARAM_TYPE = 2;

    private static final int INJECTED_PARAM_TYPE = 3;

    private final Parameter parameter;

    private final int index;

    private final int type;

    public ObserverMethodParameter(Parameter parameter, int index, int type) {
        this.parameter = parameter;
        this.index = index;
        this.type = type;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public int getIndex() {
        return index;
    }

    public boolean isObserved() {
        return OBSERVED_PARAM_TYPE == type;
    }

    public boolean isMetadata() {
        return EVENT_METADATA_PARAM_TYPE == type;
    }

    public boolean isInjected() {
        return INJECTED_PARAM_TYPE == type;
    }


    public boolean isNamePresent() {
        return parameter.isNamePresent();
    }

    public Executable getDeclaringExecutable() {
        return parameter.getDeclaringExecutable();
    }

    public int getModifiers() {
        return parameter.getModifiers();
    }

    public String getName() {
        return parameter.getName();
    }

    public Type getParameterizedType() {
        return parameter.getParameterizedType();
    }

    public Class<?> getType() {
        return parameter.getType();
    }

    public AnnotatedType getAnnotatedType() {
        return parameter.getAnnotatedType();
    }

    public boolean isImplicit() {
        return parameter.isImplicit();
    }

    public boolean isSynthetic() {
        return parameter.isSynthetic();
    }

    public boolean isVarArgs() {
        return parameter.isVarArgs();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return parameter.getAnnotation(annotationClass);
    }

    @Override
    public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        return parameter.getAnnotationsByType(annotationClass);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return parameter.getDeclaredAnnotations();
    }

    @Override
    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        return parameter.getDeclaredAnnotation(annotationClass);
    }

    @Override
    public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        return parameter.getDeclaredAnnotationsByType(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return parameter.getAnnotations();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return parameter.isAnnotationPresent(annotationClass);
    }

    public static ObserverMethodParameter observedParameter(Parameter parameter, int index) {
        return new ObserverMethodParameter(parameter, index, OBSERVED_PARAM_TYPE);
    }

    public static ObserverMethodParameter eventMetadataParameter(Parameter parameter, int index) {
        return new ObserverMethodParameter(parameter, index, EVENT_METADATA_PARAM_TYPE);
    }

    public static ObserverMethodParameter injectedParameter(Parameter parameter, int index) {
        return new ObserverMethodParameter(parameter, index, INJECTED_PARAM_TYPE);
    }

    @Override
    public String toString() {
        return "ObserverMethodParameter{" +
                "parameter=" + parameter +
                ", index=" + index +
                ", type=" + type +
                '}';
    }
}
