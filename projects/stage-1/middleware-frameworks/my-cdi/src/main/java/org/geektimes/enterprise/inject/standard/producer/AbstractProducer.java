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
package org.geektimes.enterprise.inject.standard.producer;

import org.geektimes.enterprise.inject.standard.MethodParameterInjectionPoint;
import org.geektimes.enterprise.inject.standard.beans.manager.StandardBeanManager;
import org.geektimes.enterprise.inject.standard.disposer.DisposerMethodManager;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * Abstract implementation of {@link Producer}
 *
 * @param <T> The class of object produced by the producer
 * @param <X> THe class of {@link Bean}
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class AbstractProducer<T, X> implements Producer<T> {

    private final Bean<X> declaringBean;

    private final StandardBeanManager standardBeanManager;

    private Set<InjectionPoint> injectionPoints;

    public AbstractProducer(Bean<X> declaringBean, StandardBeanManager standardBeanManager) {
        this.declaringBean = declaringBean;
        this.standardBeanManager = standardBeanManager;
    }

    @Override
    public void dispose(T instance) {
        DisposerMethodManager disposerMethodManager = standardBeanManager.getDisposerMethodManager();
        disposerMethodManager.invokeDisposerMethod(instance);
    }

    @Override
    public final Set<InjectionPoint> getInjectionPoints() {
        if (injectionPoints == null) {
            injectionPoints = unmodifiableSet(resolveInjectionPoints());
        }
        return injectionPoints;
    }

    protected Bean<X> getDeclaringBean() {
        return declaringBean;
    }

    protected StandardBeanManager getStandardBeanManager() {
        return standardBeanManager;
    }

    protected Object[] resolveInjectedArguments(CreationalContext<T> ctx) {
        Set<InjectionPoint> injectionPoints = getInjectionPoints();
        Object[] injectedArguments = new Object[injectionPoints.size()];

        injectionPoints
                .stream()
                .map(MethodParameterInjectionPoint.class::cast)
                .forEach(injectionPoint -> {
                    AnnotatedParameter parameter = injectionPoint.getAnnotated();
                    injectedArguments[parameter.getPosition()] = standardBeanManager.getInjectableReference(injectionPoint, ctx);
                });

        return injectedArguments;
    }

    protected Object getDeclaringBeanInstance(CreationalContext<T> ctx) {
        Bean<X> declaringBean = getDeclaringBean();
        return standardBeanManager.getReference(declaringBean, declaringBean.getBeanClass(), ctx);
    }

    protected abstract Set<InjectionPoint> resolveInjectionPoints();

}
