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

import org.geektimes.enterprise.inject.standard.beans.AbstractBeanAttributes;
import org.geektimes.enterprise.inject.standard.beans.GenericBeanAttributes;
import org.geektimes.enterprise.inject.standard.beans.manager.StandardBeanManager;

import javax.enterprise.inject.spi.*;
import javax.enterprise.inject.spi.configurator.BeanAttributesConfigurator;
import java.util.StringJoiner;

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

    private final AnnotatedType<T> beanType;

    private AbstractBeanAttributes beanAttributes;

    public ProcessBeanAttributesEvent(Annotated annotated, StandardBeanManager standardBeanManager) {
        this(annotated, new GenericBeanAttributes<>(resolveAnnotatedType(annotated)), standardBeanManager);
    }

    public ProcessBeanAttributesEvent(Annotated annotated, AbstractBeanAttributes beanAttributes,
                                      StandardBeanManager standardBeanManager) {
        requireNonNull(annotated, "The 'annotated' argument must not be null!");
        requireNonNull(beanAttributes, "The 'beanAttributes' argument must not be null!");
        requireNonNull(standardBeanManager, "The 'standardBeanManager' argument must not be null!");
        this.standardBeanManager = standardBeanManager;
        this.annotated = annotated;
        this.beanType = beanAttributes.getBeanType();
        this.beanAttributes = beanAttributes;
    }

    private static AnnotatedType resolveAnnotatedType(Annotated annotated) {
        if (annotated instanceof AnnotatedType) {
            return ((AnnotatedType) annotated);
        } else if (annotated instanceof AnnotatedMember) {
            return ((AnnotatedMember) annotated).getDeclaringType();
        }

        throw new IllegalArgumentException(
                format("The 'annotated' argument must be a instance of %s, or %s",
                        AnnotatedType.class.getName(),
                        AnnotatedMember.class.getName()
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
        this.beanAttributes.setBeanAttributes(beanAttributes);
    }

    @Override
    public BeanAttributesConfigurator<T> configureBeanAttributes() {
        // TODO
        return null;
    }

    @Override
    public void addDefinitionError(Throwable t) {
        standardBeanManager.addDefinitionError(t);
    }

    @Override
    public void veto() {
        beanAttributes.veto();
    }

    @Override
    public void ignoreFinalMethods() {
        // TODO
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("annotated=" + getAnnotated())
                .add("beanClass=" + beanType.getJavaClass())
                .add("beanAttributes=" + getBeanAttributes())
                .toString();
    }
}
