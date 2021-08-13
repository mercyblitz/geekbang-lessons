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

import org.geektimes.enterprise.inject.util.Qualifiers;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.Bean;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.geektimes.commons.util.AnnotationUtils.isAnnotated;
import static org.geektimes.enterprise.inject.util.Beans.getBeanTypes;
import static org.geektimes.enterprise.inject.util.Scopes.getScopeType;
import static org.geektimes.enterprise.inject.util.Stereotypes.getStereotypeTypes;

/**
 * The Standard abstract implementation  {@link Bean} based on Java Reflection.
 *
 * @param <A> The sub-type of {@link AnnotatedElement} as annotated object that may be :
 *            <ul>
 *            <li>{@link Class Bean Class}</li>
 *            <li>{@link Method Producer Method}</li>
 *            <li>{@link Field Producer Field}</li>
 *            </ul>
 * @param <T> the type of {@link Bean}
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class AbstractBean<A extends AnnotatedElement, T> implements Bean<T> {

    private final A annotatedElement;

    private final Class<?> type;

    private final Set<Type> beanTypes;

    private final Set<Annotation> qualifiers;

    private final String beanName;

    private final Class<? extends Annotation> scopeType;

    private final Set<Class<? extends Annotation>> stereotypeTypes;

    private final boolean alternative;

    public AbstractBean(A annotatedElement, Class<?> type) {
        requireNonNull(annotatedElement, "The 'annotatedSource' argument must not be null!");
        validateAnnotatedElement(annotatedElement);
        requireNonNull(type, "The 'type' argument must not be null!");
        this.annotatedElement = annotatedElement;
        this.type = type;
        this.beanTypes = getBeanTypes(getBeanClass());
        this.qualifiers = Qualifiers.getQualifiers(annotatedElement);
        this.beanName = getBeanName(annotatedElement);
        this.scopeType = getScopeType(annotatedElement);
        this.stereotypeTypes = getStereotypeTypes(annotatedElement);
        this.alternative = isAnnotated(annotatedElement, Alternative.class);
    }

    protected abstract void validateAnnotatedElement(A annotatedElement);

    @Override
    public Class<?> getBeanClass() {
        return type;
    }

    /**
     * @return As of CDI 1.1 this method is deprecated and can safely always return false.
     */
    @Override
    @Deprecated
    public boolean isNullable() {
        return false;
    }

    @Override
    public Set<Type> getTypes() {
        return beanTypes;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return scopeType;
    }

    @Override
    public String getName() {
        return beanName;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return stereotypeTypes;
    }

    @Override
    public boolean isAlternative() {
        return alternative;
    }

    public A getAnnotatedElement() {
        return annotatedElement;
    }

    // Abstract methods
    protected abstract String getBeanName(A annotatedElement);

}
