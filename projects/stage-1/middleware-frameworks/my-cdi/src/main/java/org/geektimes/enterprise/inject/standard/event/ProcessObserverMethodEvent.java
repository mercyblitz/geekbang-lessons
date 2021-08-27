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

import org.geektimes.enterprise.inject.standard.ReflectiveAnnotatedMethod;
import org.geektimes.enterprise.inject.standard.ReflectiveObserverMethod;
import org.geektimes.enterprise.inject.standard.beans.StandardBeanManager;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.ProcessObserverMethod;
import javax.enterprise.inject.spi.configurator.ObserverMethodConfigurator;

/**
 * {@link ProcessObserverMethod}
 *
 * @param <T> The type of the event being observed
 * @param <X> The bean type containing the observer method
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ProcessObserverMethodEvent<T, X> implements ProcessObserverMethod<T, X> {

    private AnnotatedMethod<X> annotatedMethod;

    private ObserverMethod<T> observerMethod;

    private final StandardBeanManager standardBeanManager;

    public ProcessObserverMethodEvent(ObserverMethod<T> observerMethod, StandardBeanManager standardBeanManager) {
        setObserverMethod(observerMethod);
        this.standardBeanManager = standardBeanManager;
    }

    @Override
    public AnnotatedMethod<X> getAnnotatedMethod() {
        return annotatedMethod;
    }

    @Override
    public ObserverMethod<T> getObserverMethod() {
        return observerMethod;
    }

    @Override
    public void addDefinitionError(Throwable t) {
        standardBeanManager.addBeanDiscoveryDefinitionError(t);
    }

    @Override
    public void setObserverMethod(ObserverMethod<T> observerMethod) {
        this.observerMethod = observerMethod;
        if (observerMethod instanceof ReflectiveObserverMethod) {
            ReflectiveObserverMethod method = (ReflectiveObserverMethod) observerMethod;
            this.annotatedMethod = new ReflectiveAnnotatedMethod<>(method.getMethod());
        }
    }

    @Override
    public ObserverMethodConfigurator<T> configureObserverMethod() {
        // TODO
        return null;
    }

    @Override
    public void veto() {
        // TODO
    }
}
