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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.*;
import javax.inject.Inject;
import java.util.*;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static org.geektimes.commons.reflect.util.ClassUtils.unwrap;
import static org.geektimes.commons.util.CollectionUtils.newFixedSet;
import static org.geektimes.enterprise.inject.util.Beans.validateManagedBeanSpecializes;
import static org.geektimes.enterprise.inject.util.Beans.validateManagedBeanType;
import static org.geektimes.enterprise.inject.util.Injections.*;

/**
 * Managed {@link Bean} based on Java Reflection.
 *
 * @param <T> the type of bean
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ManagedBean<T> extends AbstractBean<Class, T> {

    public ManagedBean(Class<?> beanClass) {
        super(beanClass, beanClass);
    }

    @Override
    protected void validateAnnotatedElement(Class beanClass) {
        validateManagedBeanType(beanClass);
        validateManagedBeanSpecializes(beanClass);
    }

    @Override
    protected String getBeanName(Class beanClass) {
        return Beans.getBeanName(beanClass);
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        T instance = (T) unwrap(getBeanClass());
        creationalContext.push(instance);
        return instance;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        // TODO
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {

        AnnotatedType annotatedType = new ReflectiveAnnotatedType(getBeanClass());

        Set<InjectionPoint> constructorParameterInjectionPoints = getConstructorParameterInjectionPoints(annotatedType, this);

        Set<InjectionPoint> fieldInjectionPoints = getFieldInjectionPoints(annotatedType, this);

        Set<InjectionPoint> methodParameterInjectionPoints = getMethodParameterInjectionPoints(annotatedType, this);

        int size = constructorParameterInjectionPoints.size() + fieldInjectionPoints.size()
                + methodParameterInjectionPoints.size();

        Set<InjectionPoint> injectionPoints = newFixedSet(size);
        // add the InjectionPoints from Constructors' parameters
        injectionPoints.addAll(constructorParameterInjectionPoints);
        // add the InjectionPoints from Fields
        injectionPoints.addAll(fieldInjectionPoints);
        // add the InjectionPoints from Methods' parameters
        injectionPoints.addAll(methodParameterInjectionPoints);

        return unmodifiableSet(injectionPoints);
    }

}
