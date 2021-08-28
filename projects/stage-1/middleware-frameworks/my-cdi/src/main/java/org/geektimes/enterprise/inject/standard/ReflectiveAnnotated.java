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

import javax.enterprise.inject.spi.Annotated;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import static java.util.Objects.hash;
import static org.geektimes.commons.collection.util.CollectionUtils.ofSet;
import static org.geektimes.commons.reflect.util.TypeUtils.asClass;
import static org.geektimes.enterprise.inject.util.Beans.getBeanTypes;

/**
 * The abstract implementation of {@link Annotated} based on
 * Java Reflection
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class ReflectiveAnnotated<A extends AnnotatedElement> implements Annotated {

    private final A annotatedElement;

    private Set<Annotation> annotations;

    private Set<Type> beanTypes;

    private int hashCode;

    public ReflectiveAnnotated(A annotatedElement) {
        this.annotatedElement = annotatedElement;
    }

    @Override
    public Set<Type> getTypeClosure() {
        if (beanTypes == null) {
            beanTypes = getBeanTypes(asClass(getBaseType()));
        }
        return beanTypes;
    }

    @Override
    public final <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return annotatedElement.getAnnotation(annotationType);
    }

    @Override
    public final <T extends Annotation> Set<T> getAnnotations(Class<T> annotationType) {
        return ofSet(annotatedElement.getAnnotationsByType(annotationType));
    }

    @Override
    public Set<Annotation> getAnnotations() {
        if (annotations == null) {
            annotations = ofSet(annotatedElement.getAnnotations());
        }
        return annotations;
    }

    @Override
    public final boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return annotatedElement.isAnnotationPresent(annotationType);
    }

    public final A getAnnotatedElement() {
        return annotatedElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReflectiveAnnotated<?> that = (ReflectiveAnnotated<?>) o;
        return Objects.equals(getAnnotatedElement(), that.getAnnotatedElement());
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = hash(getAnnotatedElement());
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("annotatedElement=" + getAnnotatedElement())
                .add("baseType=" + getBaseType())
                .add("types=" + getTypeClosure())
                .add("annotations=" + getAnnotations())
                .toString();
    }
}
