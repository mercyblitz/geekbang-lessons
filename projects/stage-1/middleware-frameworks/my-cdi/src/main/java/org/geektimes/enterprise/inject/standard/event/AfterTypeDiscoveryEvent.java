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

import org.geektimes.enterprise.inject.standard.beans.BeanArchiveManager;
import org.geektimes.enterprise.inject.standard.beans.StandardBeanManager;

import javax.enterprise.inject.spi.AfterTypeDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator;
import java.util.List;

/**
 * {@link AfterTypeDiscovery} Event is fired by container when it has fully completed the type discovery
 * process and before it begins the bean discovery process.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class AfterTypeDiscoveryEvent extends ContainerEvent implements AfterTypeDiscovery {

    private final StandardBeanManager standardBeanManager;

    private final BeanArchiveManager beanArchiveManager;

    public AfterTypeDiscoveryEvent(StandardBeanManager standardBeanManager) {
        super(standardBeanManager);
        this.standardBeanManager = standardBeanManager;
        this.beanArchiveManager = standardBeanManager.getBeanArchiveManager();
    }

    @Override
    public List<Class<?>> getAlternatives() {
        getCallerExtension();
        return beanArchiveManager.getAlternativeClasses();
    }

    /**
     * {@inheritDoc}
     * <p>
     * the ability to override the interceptor order using the portable extension SPI,
     * defined in AfterTypeDiscovery event.
     *
     * @return non-null mutable {@link List}
     */
    @Override
    public List<Class<?>> getInterceptors() {
        getCallerExtension();
        return beanArchiveManager.getInterceptorClasses();
    }

    @Override
    public List<Class<?>> getDecorators() {
        getCallerExtension();
        return beanArchiveManager.getDecoratorClasses();
    }

    @Override
    public void addAnnotatedType(AnnotatedType<?> type, String id) {
        this.standardBeanManager.addSyntheticAnnotatedType(id, type, getCallerExtension());
    }

    @Override
    public <T> AnnotatedTypeConfigurator<T> addAnnotatedType(Class<T> type, String id) {
        return null;
    }

    @Override
    public String toString() {
        return "AfterTypeDiscoveryEvent{" +
                "alternatives=" + getAlternatives() +
                " , interceptors=" + getInterceptors() +
                " , decorators=" + getDecorators() +
                '}';
    }
}
