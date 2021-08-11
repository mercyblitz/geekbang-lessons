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

import org.geektimes.commons.reflect.util.ClassUtils;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.Extension;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.geektimes.commons.reflect.util.ClassUtils.getClassLoader;

/**
 * Standard {@link SeContainerInitializer}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class StandardContainerInitializer extends SeContainerInitializer {

    ClassLoader classLoader;

    final Map<String, Object> properties;

    boolean enabledDiscovery;

    final Set<Class<?>> beanClasses;

    final Map<Package, Boolean> packagesToDiscovery;

    final Map<Class<? extends Extension>, Extension> typedExtensions;

    final Set<Class<?>> interceptorClasses;

    final Set<Class<?>> decoratorClasses;

    final Set<Class<?>> alternativeClasses;

    final Set<Class<? extends Annotation>> alternativeStereotypeClasses;

    public StandardContainerInitializer() {
        this.classLoader = getClassLoader(getClass());
        this.properties = new HashMap<>();
        this.enabledDiscovery = true;
        this.beanClasses = new LinkedHashSet<>();
        this.packagesToDiscovery = new LinkedHashMap<>();
        this.typedExtensions = new LinkedHashMap<>();
        this.interceptorClasses = new LinkedHashSet<>();
        this.decoratorClasses = new LinkedHashSet<>();
        this.alternativeClasses = new LinkedHashSet<>();
        this.alternativeStereotypeClasses = new LinkedHashSet<>();
    }

    @Override
    public SeContainerInitializer addBeanClasses(Class<?>... classes) {
        // TODO Validate the Bean Classes ?
        iterateNonNull(classes, this.beanClasses::add);
        return this;
    }

    @Override
    public SeContainerInitializer addPackages(Class<?>... packageClasses) {
        return addPackages(false, packageClasses);
    }

    @Override
    public SeContainerInitializer addPackages(boolean scanRecursively, Class<?>... packageClasses) {
        Package[] packages = Stream.of(packageClasses)
                .map(Class::getPackage)
                .toArray(Package[]::new);
        return addPackages(scanRecursively, packages);
    }

    @Override
    public SeContainerInitializer addPackages(Package... packages) {
        return addPackages(false, packages);
    }

    @Override
    public SeContainerInitializer addPackages(boolean scanRecursively, Package... packages) {
        iterateNonNull(packages, p -> packagesToDiscovery.put(p, scanRecursively));
        return this;
    }

    @Override
    public SeContainerInitializer addExtensions(Extension... extensions) {
        iterateNonNull(extensions, e -> typedExtensions.put(e.getClass(), e));
        return this;
    }

    @Override
    public SeContainerInitializer addExtensions(Class<? extends Extension>... extensions) {
        return addExtensions(Stream.of(extensions)
                .map(ClassUtils::unwrap)
                .toArray(Extension[]::new)
        );
    }

    @Override
    public SeContainerInitializer enableInterceptors(Class<?>... interceptorClasses) {
        iterateNonNull(interceptorClasses, this.interceptorClasses::add);
        return this;
    }

    @Override
    public SeContainerInitializer enableDecorators(Class<?>... decoratorClasses) {
        iterateNonNull(decoratorClasses, this.decoratorClasses::add);
        return this;
    }

    @Override
    public SeContainerInitializer selectAlternatives(Class<?>... alternativeClasses) {
        iterateNonNull(alternativeClasses, this.alternativeClasses::add);
        return this;
    }

    @Override
    public SeContainerInitializer selectAlternativeStereotypes(Class<? extends Annotation>... alternativeStereotypeClasses) {
        iterateNonNull(alternativeStereotypeClasses, this.alternativeStereotypeClasses::add);
        return this;
    }

    @Override
    public SeContainerInitializer addProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    @Override
    public SeContainerInitializer setProperties(Map<String, Object> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
        return this;
    }

    @Override
    public SeContainerInitializer disableDiscovery() {
        enabledDiscovery = false;
        return this;
    }

    @Override
    public SeContainerInitializer setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public static <T> void iterateNonNull(T[] values, Consumer<T> consumer) {
        Objects.requireNonNull(values, "The argument must not be null!");
        for (T value : values) {
            Objects.requireNonNull(value, "Any element of the argument must not be null!");
            consumer.accept(value);
        }
    }

    @Override
    public SeContainer initialize() {
        return new StandardContainer(this).initialize();
    }
}
