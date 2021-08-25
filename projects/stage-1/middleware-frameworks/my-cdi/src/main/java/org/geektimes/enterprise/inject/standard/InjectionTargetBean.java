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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * {@link InjectionTarget} {@link Bean} delegate implementation
 *
 * @param <T> the class of the bean instance
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class InjectionTargetBean<T> implements Bean<T> {

    private final BeanAttributes<T> beanAttributes;

    private final Class<?> beanClass;

    private final InjectionTarget<T> injectionTarget;

    public InjectionTargetBean(BeanAttributes<T> beanAttributes, Class<?> beanClass,
                               InjectionTargetFactory<T> injectionTargetFactory) {
        this.beanAttributes = beanAttributes;
        this.beanClass = beanClass;
        this.injectionTarget = injectionTargetFactory.createInjectionTarget(this);
    }


    @Override
    public Set<Type> getTypes() {
        return beanAttributes.getTypes();
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return beanAttributes.getQualifiers();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return beanAttributes.getScope();
    }

    @Override
    public String getName() {
        return beanAttributes.getName();
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return beanAttributes.getStereotypes();
    }

    @Override
    public boolean isAlternative() {
        return beanAttributes.isAlternative();
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return injectionTarget.getInjectionPoints();
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        // Instantiation
        T instance = injectionTarget.produce(creationalContext);
        // Initialization
        injectionTarget.postConstruct(instance);
        // Injection
        injectionTarget.inject(instance, creationalContext);
        return instance;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        // Pre-Destroy
        injectionTarget.preDestroy(instance);
        // Dispose
        injectionTarget.dispose(instance);
        // TODO : destroy other dependent objects
    }
}
