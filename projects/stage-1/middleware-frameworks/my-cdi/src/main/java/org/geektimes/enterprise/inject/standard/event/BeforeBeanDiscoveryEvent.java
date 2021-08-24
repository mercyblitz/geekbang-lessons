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

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator;
import java.lang.annotation.Annotation;

/**
 * {@link BeforeBeanDiscovery} Event is fired by container before it begins the type discovery process.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class BeforeBeanDiscoveryEvent extends ContainerEvent implements BeforeBeanDiscovery {

    private final StandardBeanManager standardBeanManager;

    private final BeanArchiveManager beanArchiveManager;

    public BeforeBeanDiscoveryEvent(StandardBeanManager standardBeanManager) {
        super(standardBeanManager);
        this.standardBeanManager = standardBeanManager;
        this.beanArchiveManager = standardBeanManager.getBeanArchiveManager();
    }

    @Override
    public void addQualifier(Class<? extends Annotation> qualifier) {
        getCallerExtension();
        beanArchiveManager.addSyntheticQualifier(qualifier);
    }

    @Override
    public void addQualifier(AnnotatedType<? extends Annotation> qualifier) {
        getCallerExtension();
        addQualifier(qualifier.getJavaClass());
    }

    @Override
    public void addScope(Class<? extends Annotation> scopeType, boolean normal, boolean passivating) {
        getCallerExtension();
        beanArchiveManager.addSyntheticScope(scopeType, normal, passivating);
    }

    @Override
    public void addStereotype(Class<? extends Annotation> stereotype, Annotation... stereotypeDef) {
        getCallerExtension();
        beanArchiveManager.addSyntheticStereotype(stereotype, stereotypeDef);
    }

    @Override
    public void addInterceptorBinding(AnnotatedType<? extends Annotation> bindingType) {
        getCallerExtension();
        addInterceptorBinding(bindingType.getJavaClass());
    }

    @Override
    public void addInterceptorBinding(Class<? extends Annotation> bindingType, Annotation... bindingTypeDef) {
        getCallerExtension();
        beanArchiveManager.addSyntheticInterceptorBinding(bindingType, bindingTypeDef);
    }

    @Override
    @Deprecated
    public void addAnnotatedType(AnnotatedType<?> type) {
        getCallerExtension();
        addAnnotatedType(type, type.getJavaClass().getName());
    }

    @Override
    public void addAnnotatedType(AnnotatedType<?> type, String id) {
        Extension source = getCallerExtension();
        standardBeanManager.addSyntheticAnnotatedType(id, type, source);
    }

    @Override
    public <T> AnnotatedTypeConfigurator<T> addAnnotatedType(Class<T> type, String id) {
        getCallerExtension();
        // TODO
        return null;
    }

    @Override
    public <T extends Annotation> AnnotatedTypeConfigurator<T> configureQualifier(Class<T> qualifier) {
        getCallerExtension();
        // TODO
        return null;
    }

    @Override
    public <T extends Annotation> AnnotatedTypeConfigurator<T> configureInterceptorBinding(Class<T> bindingType) {
        getCallerExtension();
        // TODO
        return null;
    }


}
