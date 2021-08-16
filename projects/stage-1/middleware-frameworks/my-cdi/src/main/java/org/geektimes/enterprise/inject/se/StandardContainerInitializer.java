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
import java.util.Map;
import java.util.stream.Stream;


/**
 * Standard {@link SeContainerInitializer}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class StandardContainerInitializer extends SeContainerInitializer {

    private final StandardContainer standardContainer;

    public StandardContainerInitializer() {
        this.standardContainer = new StandardContainer();
    }

    @Override
    public SeContainerInitializer addBeanClasses(Class<?>... classes) {
        standardContainer.addBeanClasses(classes);
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
        standardContainer.addPackages(scanRecursively, packages);
        return this;
    }

    @Override
    public SeContainerInitializer addExtensions(Extension... extensions) {
        standardContainer.addExtensions(extensions);
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
        standardContainer.addInterceptors(interceptorClasses);
        return this;
    }

    @Override
    public SeContainerInitializer enableDecorators(Class<?>... decoratorClasses) {
        standardContainer.addDecorators(decoratorClasses);
        return this;
    }

    @Override
    public SeContainerInitializer selectAlternatives(Class<?>... alternativeClasses) {
        standardContainer.addAlternatives(alternativeClasses);
        return this;
    }

    @Override
    public SeContainerInitializer selectAlternativeStereotypes(Class<? extends Annotation>... alternativeStereotypeClasses) {
        standardContainer.addAlternativeStereotypes(alternativeStereotypeClasses);
        return this;
    }

    @Override
    public SeContainerInitializer addProperty(String key, Object value) {
        standardContainer.setProperty(key, value);
        return this;
    }

    @Override
    public SeContainerInitializer setProperties(Map<String, Object> properties) {
        standardContainer.setProperties(properties);
        return this;
    }

    @Override
    public SeContainerInitializer disableDiscovery() {
        standardContainer.disableDiscovery();
        return this;
    }

    @Override
    public SeContainerInitializer setClassLoader(ClassLoader classLoader) {
        standardContainer.setClassLoader(classLoader);
        return this;
    }

    @Override
    public SeContainer initialize() {
        standardContainer.initialize();
        return standardContainer;
    }
}
