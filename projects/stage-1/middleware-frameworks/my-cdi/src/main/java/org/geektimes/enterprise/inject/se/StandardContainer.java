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
package org.geektimes.enterprise.inject.se;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Standard {@link SeContainer} implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class StandardContainer implements SeContainer {

    private final ClassLoader classLoader;

    private final Map<String, Object> properties;

    private final boolean enabledDiscovery;

    private final Set<Class<?>> beanClasses;

    private final Map<Package, Boolean> packagesToDiscovery;

    private final Map<Class<? extends Extension>, Extension> typedExtensions;

    private final Set<Class<?>> interceptorClasses;

    private final Set<Class<?>> decoratorClasses;

    private final Set<Class<?>> alternativeClasses;

    private final Set<Class<? extends Annotation>> alternativeStereotypeClasses;

    private boolean running;

    public StandardContainer(StandardContainerInitializer initializer) {
        this.classLoader = initializer.classLoader;
        this.properties = initializer.properties;
        this.enabledDiscovery = initializer.enabledDiscovery;
        this.beanClasses = initializer.beanClasses;
        this.packagesToDiscovery = initializer.packagesToDiscovery;
        this.typedExtensions = initializer.typedExtensions;
        this.interceptorClasses = initializer.interceptorClasses;
        this.decoratorClasses = initializer.decoratorClasses;
        this.alternativeClasses = initializer.alternativeClasses;
        this.alternativeStereotypeClasses = initializer.alternativeStereotypeClasses;
    }

    public StandardContainer initialize() {
        running = true;
        return this;
    }

    @Override
    public void close() {
        if (!running) {
            throw new IllegalStateException("The container is already shutdown!");
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public BeanManager getBeanManager() {
        return null;
    }

    @Override
    public Instance<Object> select(Annotation... qualifiers) {
        return null;
    }

    @Override
    public <U> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
        return null;
    }

    @Override
    public <U> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
        return null;
    }

    @Override
    public boolean isUnsatisfied() {
        return false;
    }

    @Override
    public boolean isAmbiguous() {
        return false;
    }

    @Override
    public void destroy(Object instance) {

    }

    @Override
    public Iterator<Object> iterator() {
        return null;
    }

    @Override
    public Object get() {
        return null;
    }
}
