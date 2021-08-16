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

import org.geektimes.commons.lang.util.ClassLoaderUtils;
import org.geektimes.enterprise.inject.standard.StandardBeanManager;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.TypeLiteral;
import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;


/**
 * Standard {@link SeContainer} implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class StandardContainer implements SeContainer {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Map<String, Object> properties;

    private final Set<Class<?>> beanClasses;

    private final Map<Package, Boolean> packagesToDiscovery;

    private final Map<Class<? extends Extension>, Extension> typedExtensions;

    private final Set<Class<?>> interceptorClasses;

    private final Set<Class<?>> decoratorClasses;

    private final Set<Class<?>> alternativeClasses;

    private final Set<Class<? extends Annotation>> alternativeStereotypeClasses;

    private ClassLoader classLoader;

    private boolean enabledDiscovery;

    private boolean running;

    private StandardBeanManager standardBeanManager;

    public StandardContainer() {
        this.classLoader = ClassLoaderUtils.getClassLoader(getClass());
        this.properties = new HashMap<>();
        this.enabledDiscovery = true;
        this.beanClasses = new LinkedHashSet<>();
        this.packagesToDiscovery = new LinkedHashMap<>();
        this.typedExtensions = new LinkedHashMap<>();
        this.interceptorClasses = new LinkedHashSet<>();
        this.decoratorClasses = new LinkedHashSet<>();
        this.alternativeClasses = new LinkedHashSet<>();
        this.alternativeStereotypeClasses = new LinkedHashSet<>();
        this.running = false;
    }


    void addBeanClasses(Class<?>... classes) {
        // TODO Validate the Bean Classes ?
        iterateNonNull(classes, this.beanClasses::add);
    }

    void addPackages(boolean scanRecursively, Package... packages) {
        iterateNonNull(packages, p -> packagesToDiscovery.put(p, scanRecursively));
    }

    void addExtensions(Extension... extensions) {
        iterateNonNull(extensions, e -> typedExtensions.put(e.getClass(), e));
    }

    void addInterceptors(Class<?>... interceptorClasses) {
        iterateNonNull(interceptorClasses, this.interceptorClasses::add);
    }

    void addDecorators(Class<?>... decoratorClasses) {
        iterateNonNull(decoratorClasses, this.decoratorClasses::add);
    }

    void addAlternatives(Class<?>... alternativeClasses) {
        iterateNonNull(alternativeClasses, this.alternativeClasses::add);
    }

    void addAlternativeStereotypes(Class<? extends Annotation>... alternativeStereotypeClasses) {
        iterateNonNull(alternativeStereotypeClasses, this.alternativeStereotypeClasses::add);
    }

    void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    void setProperties(Map<String, Object> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    void disableDiscovery() {
        enabledDiscovery = false;
    }

    void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public static <T> void iterateNonNull(T[] values, Consumer<T> consumer) {
        Objects.requireNonNull(values, "The argument must not be null!");
        for (T value : values) {
            Objects.requireNonNull(value, "Any element of the argument must not be null!");
            consumer.accept(value);
        }
    }

    public void initialize() {
        running = true;
        // Create BeanManager
        standardBeanManager = new StandardBeanManager(this);

        discover();
        // TODO
    }

    private void discover() {
        if (!enabledDiscovery) {
            // TODO log
            return;
        }
        Set<Class<?>> classes = scanClasses();
        discoverTypes();
        discoverBeans();
    }

    private Set<Class<?>> scanClasses() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        ClassLoader classLoader = getClassLoader();
        for (Map.Entry<Package, Boolean> packageEntry : packagesToDiscovery.entrySet()) {
            Package packageToDiscovery = packageEntry.getKey();
            boolean scanRecursively = Boolean.TRUE.equals(packageEntry.getValue());
            scanClasses(classes, classLoader, packageToDiscovery, scanRecursively);
        }
        return classes;
    }

    private void scanClasses(Set<Class<?>> classes, ClassLoader classLoader,
                             Package packageToDiscovery, boolean scanRecursively) {
        String resourcePath = packageToDiscovery.getName().replace('.', '/');
        URL packageResource = classLoader.getResource(resourcePath);
        String protocol = packageResource.getProtocol();
        switch (protocol) {
            case "file":
                scanClassesInDirectory(classes, packageResource.getPath(), scanRecursively);
                break;
            case "jar":
                scanClassesInJar(classes, classLoader, packageResource, scanRecursively);
                break;
        }
    }

    private void scanClassesInDirectory(Set<Class<?>> classes, String directoryPath, boolean scanRecursively) {
        File directory = new File(directoryPath);
    }

    private void scanClassesInJar(Set<Class<?>> classes, ClassLoader classLoader, URL packageResource,
                                  boolean scanRecursively) {

    }

    private void discoverTypes() {
    }

    private void discoverBeans() {
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
        return standardBeanManager;
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

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public boolean isEnabledDiscovery() {
        return enabledDiscovery;
    }

    public Set<Class<?>> getBeanClasses() {
        return beanClasses;
    }

    public Map<Package, Boolean> getPackagesToDiscovery() {
        return packagesToDiscovery;
    }

    public Map<Class<? extends Extension>, Extension> getTypedExtensions() {
        return typedExtensions;
    }

    public Set<Class<?>> getInterceptorClasses() {
        return interceptorClasses;
    }

    public Set<Class<?>> getDecoratorClasses() {
        return decoratorClasses;
    }

    public Set<Class<?>> getAlternativeClasses() {
        return alternativeClasses;
    }

    public Set<Class<? extends Annotation>> getAlternativeStereotypeClasses() {
        return alternativeStereotypeClasses;
    }
}
