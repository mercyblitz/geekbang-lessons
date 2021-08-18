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

import org.geektimes.enterprise.inject.se.StandardBeanManager;
import org.geektimes.enterprise.inject.se.StandardContainer;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator;
import java.lang.annotation.Annotation;
import java.util.EventObject;

/**
 * {@link BeforeBeanDiscovery} Event implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class BeforeBeanDiscoveryEvent extends EventObject implements BeforeBeanDiscovery {

    /**
     * Constructs a prototypical Event.
     *
     * @param source The instance of {@link StandardContainer}
     * @throws IllegalArgumentException if source is null.
     */
    public BeforeBeanDiscoveryEvent(StandardBeanManager source) {
        super(source);
    }

    @Override
    public StandardBeanManager getSource() {
        return (StandardBeanManager) super.getSource();
    }

    @Override
    public void addQualifier(Class<? extends Annotation> qualifier) {

    }

    @Override
    public void addQualifier(AnnotatedType<? extends Annotation> qualifier) {

    }

    @Override
    public void addScope(Class<? extends Annotation> scopeType, boolean normal, boolean passivating) {

    }

    @Override
    public void addStereotype(Class<? extends Annotation> stereotype, Annotation... stereotypeDef) {

    }

    @Override
    public void addInterceptorBinding(AnnotatedType<? extends Annotation> bindingType) {

    }

    @Override
    public void addInterceptorBinding(Class<? extends Annotation> bindingType, Annotation... bindingTypeDef) {

    }

    @Override
    public void addAnnotatedType(AnnotatedType<?> type) {

    }

    @Override
    public void addAnnotatedType(AnnotatedType<?> type, String id) {

    }

    @Override
    public <T> AnnotatedTypeConfigurator<T> addAnnotatedType(Class<T> type, String id) {
        return null;
    }

    @Override
    public <T extends Annotation> AnnotatedTypeConfigurator<T> configureQualifier(Class<T> qualifier) {
        return null;
    }

    @Override
    public <T extends Annotation> AnnotatedTypeConfigurator<T> configureInterceptorBinding(Class<T> bindingType) {
        return null;
    }
}
