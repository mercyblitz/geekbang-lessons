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
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.geektimes.commons.reflect.util.ClassUtils.unwrap;

/**
 * Managed {@link Bean} based on Java Reflection.
 * <p>
 * TODO features:
 * <ul>
 *     <li>{@link javax.enterprise.inject.Specializes}</li>
 *     <li>{@link #getInjectionPoints}</li>
 * </ul>
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
        // TODO
        return null;
    }
}
