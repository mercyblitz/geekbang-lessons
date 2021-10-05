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
package org.geektimes.enterprise.inject.standard.beans;

import org.geektimes.enterprise.inject.standard.annotation.ReflectiveAnnotatedType;
import org.geektimes.enterprise.inject.standard.beans.producer.ProducerFieldBean;
import org.geektimes.enterprise.inject.standard.beans.producer.ProducerMethodBean;
import org.geektimes.enterprise.inject.util.Beans;

import javax.enterprise.context.NormalScope;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static org.geektimes.enterprise.inject.util.Beans.validateManagedBeanSpecializes;
import static org.geektimes.enterprise.inject.util.Beans.validateManagedBeanType;
import static org.geektimes.enterprise.inject.util.Producers.resolveProducerFieldBeans;
import static org.geektimes.enterprise.inject.util.Producers.resolveProducerMethodBeans;

/**
 * Managed {@link Bean} based on Java Reflection.
 *
 * @param <T> the type of bean
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ManagedBean<T> extends GenericBean<T> {

    private Set<ProducerMethodBean> producerMethodBeans;

    private Set<ProducerFieldBean> producerFieldBeans;

    private Set<Type> producerTypes;

    public ManagedBean(AnnotatedType<T> beanType, BeanManager beanManager) {
        super(beanType, beanManager);
    }

    protected ManagedBean(Class<?> beanClass, BeanManager beanManager) {
        this(new ReflectiveAnnotatedType<>(beanClass), beanManager);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        Class<? extends Annotation> scope = super.getScope();
        return scope == null ? NormalScope.class : scope;
    }

    @Override
    protected void validate(Class<T> beanClass) {
        validateManagedBeanType(beanClass);
        validateManagedBeanSpecializes(beanClass);
    }

    @Override
    public Annotated getAnnotated() {
        return getBeanType();
    }

    @Override
    protected String getBeanName(Class beanClass) {
        return Beans.getBeanName(beanClass);
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        T instance = super.create(creationalContext);
        // TODO
        return instance;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        // TODO
        creationalContext.release();
    }

    public Set<ProducerMethodBean> getProducerMethodBeans() {
        if (producerMethodBeans == null) {
            producerMethodBeans = resolveProducerMethodBeans(this);
        }
        return producerMethodBeans;
    }

    public Set<ProducerFieldBean> getProducerFieldBeans() {
        if (producerFieldBeans == null) {
            producerFieldBeans = resolveProducerFieldBeans(this);
        }
        return producerFieldBeans;
    }

    public Set<Type> getProducerTypes() {
        if (producerTypes == null) {
            producerTypes = new LinkedHashSet<>();
            producerMethodBeans.forEach(bean -> {
                producerTypes.addAll(bean.getTypes());
            });
            producerFieldBeans.forEach(bean -> {
                producerTypes.addAll(bean.getTypes());
            });
            producerTypes = unmodifiableSet(producerTypes);
        }
        return producerTypes;
    }

}
