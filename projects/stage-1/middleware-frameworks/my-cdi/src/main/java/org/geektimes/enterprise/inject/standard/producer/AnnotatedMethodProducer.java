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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.CreationException;
import javax.enterprise.inject.spi.*;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static org.geektimes.commons.reflect.util.MemberUtils.isStatic;

/**
 * {@link Producer} implementation for Producer {@link AnnotatedMethod Method}
 *
 * @param <T> The class of object produced by the producer
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class AnnotatedMethodProducer<T, X> implements Producer<T> {

    private final AnnotatedMethod<T> producerMethod;

    private final Bean<X> declaringBean;

    private final BeanManager beanManager;

    private Set<InjectionPoint> injectionPoints;

    public AnnotatedMethodProducer(AnnotatedMethod<T> producerMethod, Bean<X> declaringBean, BeanManager beanManager) {
        this.producerMethod = producerMethod;
        this.declaringBean = declaringBean;
        this.beanManager = beanManager;
    }

    @Override
    public T produce(CreationalContext<T> ctx) {
        Method method = producerMethod.getJavaMember();

        Object[] injectedArguments = resolveInjectedArguments(ctx);

        Object instance = null;

        final T beanInstance;
        try {
            if (!isStatic(method)) {
                instance = beanManager.getReference(declaringBean, declaringBean.getBeanClass(), ctx);
            }
            beanInstance = (T) method.invoke(instance, injectedArguments);
        } catch (Throwable e) {
            throw new CreationException(e);
        }
        return beanInstance;
    }

    @Override
    public void dispose(T instance) {
        // TODO
    }

    private Object[] resolveInjectedArguments(CreationalContext<T> ctx) {
        Object[] injectedArguments = new Object[injectionPoints.size()];

        getInjectionPoints()
                .stream()
                .map(MethodParameterInjectionPoint.class::cast)
                .forEach(injectionPoint -> {
                    AnnotatedParameter parameter = injectionPoint.getAnnotated();
                    injectedArguments[parameter.getPosition()] = beanManager.getInjectableReference(injectionPoint, ctx);
                });

        return injectedArguments;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        if (injectionPoints == null) {
            List<AnnotatedParameter<T>> annotatedParameters = producerMethod.getParameters();
            Set<InjectionPoint> injectionPoints = new LinkedHashSet<>();

            for (AnnotatedParameter annotatedParameter : annotatedParameters) {
                injectionPoints.add(new MethodParameterInjectionPoint(annotatedParameter, producerMethod, declaringBean));
            }
            this.injectionPoints = unmodifiableSet(injectionPoints);
        }
        return injectionPoints;
    }
}
