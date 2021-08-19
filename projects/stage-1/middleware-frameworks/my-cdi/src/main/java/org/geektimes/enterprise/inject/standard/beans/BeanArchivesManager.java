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
package org.geektimes.enterprise.inject.standard.beans;

import org.geektimes.commons.util.ServiceLoaders;
import org.geektimes.enterprise.beans.xml.BeansReader;
import org.geektimes.enterprise.beans.xml.bind.Beans;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import static java.util.Objects.requireNonNull;
import static org.geektimes.commons.reflect.util.PackageUtils.PACKAGE_NAME_COMPARATOR;

/**
 * Bean Archives Manager
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class BeanArchivesManager {

    private final ClassLoader classLoader;

    private final BeansReader beansReader;

    private final Map<Package, Boolean> packagesToScan;

    public BeanArchivesManager(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.beansReader = ServiceLoaders.loadSpi(BeansReader.class);
        this.packagesToScan = new ConcurrentSkipListMap<>(PACKAGE_NAME_COMPARATOR);
    }

    public BeanArchivesManager addPackage(Package packageToScan, boolean scanRecursively) {
        requireNonNull(packageToScan, "The 'packageToScan' argument must not be null!");
        this.packagesToScan.put(packageToScan, scanRecursively);
        return this;
    }

    public BeanArchivesManager excludePackage(Package packageToScan, boolean scanRecursively) {
        requireNonNull(packageToScan, "The 'packageToScan' argument must not be null!");
        if (this.packagesToScan.remove(packageToScan, scanRecursively)) {
            this.packagesToScan.remove(packageToScan);
        }
        return this;
    }

    public BeanArchivesManager performTypeDiscovery() {
        List<Beans> beansList = beansReader.readAllBeans(classLoader);
        return this;
    }
}
