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
package org.geektimes.enterprise.inject;

import org.apache.commons.lang.StringUtils;
import org.geektimes.commons.reflect.util.ClassUtils;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

import static java.beans.Introspector.decapitalize;
import static java.util.Objects.requireNonNull;
import static org.geektimes.commons.reflect.util.ClassUtils.unwrap;
import static org.geektimes.commons.util.AnnotationUtils.*;
import static org.geektimes.enterprise.inject.util.Beans.getBeanTypes;
import static org.geektimes.enterprise.inject.util.Qualifiers.getAllQualifiers;
import static org.geektimes.enterprise.inject.util.Scopes.getScopeType;
import static org.geektimes.enterprise.inject.util.Stereotypes.getAllStereotypeTypes;
import static org.geektimes.enterprise.inject.util.Stereotypes.getAllStereotypes;

/**
 * Standard Managed {@link Bean} based on Java Reflection.
 *
 * @param <T> the type of bean
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class StandardManagedBean<T> implements Bean<T> {

    private final Class<T> beanClass;

    private final Set<Type> types;

    private final Set<Annotation> qualifiers;

    private final Optional<Named> named;

    private final String beanName;

    private final Class<? extends Annotation> scopeType;

    private final Set<Class<? extends Annotation>> stereotypeTypes;

    private final boolean alternative;

    public StandardManagedBean(Class<T> beanClass) {
        requireNonNull(beanClass, "The 'beanClass' argument must not be null!");
        this.beanClass = beanClass;
        this.types = getBeanTypes(beanClass);
        this.qualifiers = getAllQualifiers(beanClass);
        this.named = findNamed();
        this.beanName = resolveBeanName();
        this.scopeType = getScopeType(beanClass);
        this.stereotypeTypes = getAllStereotypeTypes(beanClass);
        this.alternative = isAnnotated(beanClass, Alternative.class);
    }

    private Optional<Named> findNamed() {
        return qualifiers.stream()
                .filter(annotation -> Named.class.equals(annotation.annotationType()))
                .map(Named.class::cast)
                .findFirst();
    }

    private String resolveBeanName() {
        String beanName = named.map(Named::value)
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> decapitalize(beanClass.getSimpleName()));
        return beanName;
    }


    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        // TODO
        return null;
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
    public T create(CreationalContext<T> creationalContext) {
        T instance = unwrap(beanClass);
        creationalContext.push(instance);
        return instance;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        // TODO
    }

    @Override
    public Set<Type> getTypes() {
        return types;
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
}
