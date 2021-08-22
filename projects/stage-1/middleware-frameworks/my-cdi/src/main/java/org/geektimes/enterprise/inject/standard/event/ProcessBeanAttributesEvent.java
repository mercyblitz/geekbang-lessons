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

import org.geektimes.enterprise.inject.standard.GenericBeanAttributes;
import org.geektimes.enterprise.inject.standard.ReflectiveAnnotatedType;
import org.geektimes.enterprise.inject.standard.beans.StandardBeanManager;

import javax.enterprise.inject.spi.*;
import javax.enterprise.inject.spi.configurator.BeanAttributesConfigurator;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * {@inheritDoc}
 * <p>
 * {@link ProcessBeanAttributes} Event is fired by container in the Bean discovery
 * <p>
 *
 * @param <T> The class of the bean
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ProcessBeanAttributesEvent<T> implements ProcessBeanAttributes<T> {

    private final StandardBeanManager standardBeanManager;

    private final Annotated annotated;

    private final Class<?> beanClass;

    public ProcessBeanAttributesEvent(Annotated annotated, StandardBeanManager standardBeanManager) {
        this(annotated, new GenericBeanAttributes<>(resolveBeanClass(annotated)), standardBeanManager);
    }

    public ProcessBeanAttributesEvent(Annotated annotated, BeanAttributes<T> beanAttributes,
                                      StandardBeanManager standardBeanManager) {
        requireNonNull(annotated, "The 'annotated' argument must not be null!");
        requireNonNull(beanAttributes, "The 'beanAttributes' argument must not be null!");
        requireNonNull(standardBeanManager, "The 'standardBeanManager' argument must not be null!");
        this.standardBeanManager = standardBeanManager;
        this.annotated = annotated;
        this.beanClass = resolveBeanClass(annotated);
        this.beanAttributes = beanAttributes;
    }

    private BeanAttributes<T> beanAttributes;

    private static Class<?> resolveBeanClass(Annotated annotated) {
        if (annotated instanceof AnnotatedType) {
            return ((AnnotatedType) annotated).getJavaClass();
        } else if (annotated instanceof AnnotatedField) {
            return ((AnnotatedField) annotated).getJavaMember().getDeclaringClass();
        } else if (annotated instanceof AnnotatedMethod) {
            return ((AnnotatedMethod) annotated).getJavaMember().getDeclaringClass();
        }

        throw new IllegalArgumentException(
                format("The 'annotated' argument must be a instance of %s, %s or %s",
                        AnnotatedType.class.getName(),
                        AnnotatedField.class.getName(),
                        AnnotatedMethod.class.getName()
                ));
    }

    @Override
    public Annotated getAnnotated() {
        return annotated;
    }

    @Override
    public BeanAttributes<T> getBeanAttributes() {
        return beanAttributes;
    }

    @Override
    public void setBeanAttributes(BeanAttributes<T> beanAttributes) {
        this.beanAttributes = beanAttributes;
    }

    @Override
    public BeanAttributesConfigurator<T> configureBeanAttributes() {
        // TODO
        return null;
    }

    @Override
    public void addDefinitionError(Throwable t) {
        standardBeanManager.addBeanDiscoveryDefinitionError(t);
    }

    @Override
    public void veto() {
        standardBeanManager.removeAnnotatedType(new ReflectiveAnnotatedType<>(beanClass));
    }

    @Override
    public void ignoreFinalMethods() {
        // TODO
    }
}
