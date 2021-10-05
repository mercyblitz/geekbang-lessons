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
package org.geektimes.enterprise.inject.standard.context;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import static org.geektimes.enterprise.inject.util.Contexts.getBeanType;

/**
 * Abstract implementation {@link Context}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class AbstractContext implements Context {

    protected final BeanManager beanManager;

    protected final Class<? extends Annotation> scope;

    protected final List<Bean<?>> beans;

    protected boolean active;

    public AbstractContext(BeanManager beanManager, Class<? extends Annotation> scope) {
        this.beanManager = beanManager;
        this.scope = scope;
        this.beans = new LinkedList<>();
        active();
    }

    public final BeanManager getBeanManager() {
        return beanManager;
    }

    @Override
    public final Class<? extends Annotation> getScope() {
        return scope;
    }

    /**
     * @param contextual
     * @param creationalContext
     * @param <T>
     * @return <ul>
     * <li>return an existing instance of the given contextual type, or</li>
     * <li>if no CreationalContext is given, return a null value, or</li>
     * <li>if a CreationalContext is given, create a new instance of the given contextual type by calling
     * Contextual.create(), passing the given CreationalContext, and return the new instance.</li>
     * </ul>
     */
    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        T instance = getInstance(contextual);
        if (instance == null) {
            if (creationalContext != null) {
                instance = contextual.create(creationalContext);
            }
        }
        return instance;
//        Class<T> beanClass = getBeanClass(contextual);
//        Type requiredBeanType = getBeanType(contextual);
//        CreationalContext<T> context = creationalContext;
//        if (context == null) {
//            context = beanManager.createCreationalContext(contextual);
//        }
//        AnnotatedType<T> beanType = beanManager.createAnnotatedType(beanClass);
//        return get(contextual, context, beanType);
    }

    private <T> T getInstance(Contextual<T> contextual) {
        Type requiredBeanType = getBeanType(contextual);
        return getInstance(contextual, requiredBeanType);
    }

    protected <T> T getInstance(Contextual<T> contextual, Type requiredBeanType) {
        // TODO
        return null;
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        return get(contextual, beanManager.createCreationalContext(contextual));
    }

    public final void inactive() {
        active = false;
    }

    public final void active() {
        active = true;
    }

    @Override
    public final boolean isActive() {
        return active;
    }

    public void addBean(Bean<?> bean) {
        this.beans.add(bean);
    }
}
