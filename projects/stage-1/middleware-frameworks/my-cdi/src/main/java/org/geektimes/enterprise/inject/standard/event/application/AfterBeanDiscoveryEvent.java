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
package org.geektimes.enterprise.inject.standard.event.application;

import org.geektimes.enterprise.inject.standard.beans.manager.StandardBeanManager;

import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.configurator.BeanConfigurator;
import javax.enterprise.inject.spi.configurator.ObserverMethodConfigurator;

/**
 * An event was raised when it has fully completed the bean discovery process, validated that there are
 * no definition errors relating to the discovered beans, and registered Bean and ObserverMethod
 * objects for the discovered beans.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class AfterBeanDiscoveryEvent implements AfterBeanDiscovery {

    private final StandardBeanManager standardBeanManager;

    public AfterBeanDiscoveryEvent(StandardBeanManager standardBeanManager) {
        this.standardBeanManager = standardBeanManager;
    }

    @Override
    public void addDefinitionError(Throwable t) {
        standardBeanManager.addDefinitionError(t);
    }

    @Override
    public void addBean(Bean<?> bean) {
        standardBeanManager.registerBean(bean);
    }

    @Override
    public <T> BeanConfigurator<T> addBean() {
        // TODO
        return null;
    }

    @Override
    public void addObserverMethod(ObserverMethod<?> observerMethod) {
        standardBeanManager.registerObserverMethod(observerMethod);
    }

    @Override
    public <T> ObserverMethodConfigurator<T> addObserverMethod() {
        // TODO
        return null;
    }

    @Override
    public void addContext(Context context) {
        standardBeanManager.getContextManager().addContext(context);
    }

    @Override
    public <T> AnnotatedType<T> getAnnotatedType(Class<T> type, String id) {
        return standardBeanManager.getAnnotatedType(type, id);
    }

    @Override
    public <T> Iterable<AnnotatedType<T>> getAnnotatedTypes(Class<T> type) {
        return standardBeanManager.getAnnotatedTypes(type);
    }
}
