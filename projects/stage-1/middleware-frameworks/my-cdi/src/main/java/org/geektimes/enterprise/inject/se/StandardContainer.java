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

import org.geektimes.enterprise.inject.standard.beans.BeanArchiveManager;
import org.geektimes.enterprise.inject.standard.beans.StandardBeanManager;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import static org.geektimes.commons.lang.util.ArrayUtils.iterate;


/**
 * Standard {@link SeContainer} implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class StandardContainer implements SeContainer {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private boolean running;

    private final StandardBeanManager standardBeanManager;

    private final BeanArchiveManager beanArchiveManager;

    public StandardContainer() {
        this.running = false;
        // Create BeanManager
        this.standardBeanManager = new StandardBeanManager();
        this.beanArchiveManager = standardBeanManager.getBeanArchiveManager();
    }

    void addBeanClasses(Class<?>... beanClasses) {
        iterate(beanClasses, beanArchiveManager::addSyntheticBeanClass);
    }

    void addPackages(boolean scanRecursively, Package... packages) {
        iterate(packages, packageToScan -> beanArchiveManager.addSyntheticPackage(packageToScan, scanRecursively));
    }

    void addExtensions(Extension... extensions) {
        standardBeanManager.extensions(extensions);
    }

    void addInterceptors(Class<?>... interceptorClasses) {
        standardBeanManager.interceptorClasses(interceptorClasses);
    }

    void addDecorators(Class<?>... decoratorClasses) {
        standardBeanManager.decoratorClasses(decoratorClasses);
    }

    void addAlternatives(Class<?>... alternativeClasses) {
        standardBeanManager.alternativeClasses(alternativeClasses);
    }

    void addAlternativeStereotypes(Class<? extends Annotation>... alternativeStereotypeClasses) {
        standardBeanManager.alternativeStereotypeClasses(alternativeStereotypeClasses);
    }

    void setProperty(String key, Object value) {
        standardBeanManager.property(key, value);
    }

    void setProperties(Map<String, Object> properties) {
        standardBeanManager.properties(properties);
    }

    void disableDiscovery() {
        beanArchiveManager.disableDiscovery();
    }

    void setClassLoader(ClassLoader classLoader) {
        standardBeanManager.classLoader(classLoader);
    }

    public void initialize() {
        if (isRunning()) {
            return;
        }
        standardBeanManager.initialize();
        running = true;
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
        return standardBeanManager.select(qualifiers);
    }

    @Override
    public <U> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
        return standardBeanManager.select(subtype, qualifiers);
    }

    @Override
    public <U> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
        return standardBeanManager.select(subtype, qualifiers);
    }

    @Override
    public boolean isUnsatisfied() {
        return standardBeanManager.isUnsatisfied();
    }

    @Override
    public boolean isAmbiguous() {
        return standardBeanManager.isAmbiguous();
    }

    @Override
    public void destroy(Object instance) {
        standardBeanManager.destroy(instance);
    }

    @Override
    public Iterator<Object> iterator() {
        return standardBeanManager.iterator();
    }

    @Override
    public Object get() {
        return standardBeanManager.get();
    }
}
