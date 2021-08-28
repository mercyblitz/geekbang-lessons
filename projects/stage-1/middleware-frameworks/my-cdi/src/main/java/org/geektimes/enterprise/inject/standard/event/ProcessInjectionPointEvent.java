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

import org.geektimes.enterprise.inject.standard.beans.StandardBeanManager;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.configurator.InjectionPointConfigurator;
import java.util.StringJoiner;

/**
 * {@link ProcessInjectionPoint} Event is fired by container for every injection point of every bean, interceptor
 * or decorator.
 *
 * @param <X> the declared type of the injection point.
 * @param <T> the bean class of the bean that declares the injection point
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ProcessInjectionPointEvent<T, X> implements ProcessInjectionPoint<T, X> {

    private InjectionPoint injectionPoint;

    private final StandardBeanManager standardBeanManager;

    public ProcessInjectionPointEvent(InjectionPoint injectionPoint, StandardBeanManager standardBeanManager) {
        this.injectionPoint = injectionPoint;
        this.standardBeanManager = standardBeanManager;
    }

    @Override
    public InjectionPoint getInjectionPoint() {
        return injectionPoint;
    }

    @Override
    public void setInjectionPoint(InjectionPoint injectionPoint) {
        this.injectionPoint = injectionPoint;
    }

    @Override
    public InjectionPointConfigurator configureInjectionPoint() {
        // TODO
        return null;
    }

    @Override
    public void addDefinitionError(Throwable t) {
        standardBeanManager.addBeanDiscoveryDefinitionError(t);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("injectionPoint=" + getInjectionPoint())
                .toString();
    }
}
