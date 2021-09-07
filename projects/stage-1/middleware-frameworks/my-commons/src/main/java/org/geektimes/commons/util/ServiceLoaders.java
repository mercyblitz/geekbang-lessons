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
package org.geektimes.commons.util;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static org.geektimes.commons.function.Streams.stream;
import static org.geektimes.commons.lang.util.ArrayUtils.asArray;
import static org.geektimes.commons.lang.util.ClassLoaderUtils.getClassLoader;

/**
 * {@link ServiceLoader} Utilities Class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class ServiceLoaders {

    private static final Map<ClassLoader, Map<Class<?>, ServiceLoader<?>>> serviceLoadersCache = new ConcurrentHashMap<>();

    public static <T> Stream<T> loadAsStream(Class<T> serviceClass) {
        return loadAsStream(serviceClass, getClassLoader(serviceClass));
    }

    public static <T> Stream<T> loadAsStream(Class<T> serviceClass, ClassLoader classLoader) {
        return stream(load(serviceClass, classLoader));
    }

    public static <T> T loadSpi(Class<T> serviceClass) {
        return loadSpi(serviceClass, getClassLoader(serviceClass));
    }

    public static <T> T loadSpi(Class<T> serviceClass, ClassLoader classLoader) {
        return load(serviceClass, classLoader).iterator().next();
    }

    public static <T> T[] loadAsArray(Class<T> serviceClass) {
        return loadAsArray(serviceClass, getClassLoader(serviceClass));
    }

    public static <T> T[] loadAsArray(Class<T> serviceClass, ClassLoader classLoader) {
        return asArray(load(serviceClass, classLoader), serviceClass);
    }

    public static <T> ServiceLoader<T> load(Class<T> serviceClass) {
        return load(serviceClass, getClassLoader(serviceClass));
    }

    public static <T> ServiceLoader<T> load(Class<T> serviceClass, ClassLoader classLoader) {
        Map<Class<?>, ServiceLoader<?>> serviceLoadersMap = serviceLoadersCache.computeIfAbsent(classLoader, cl -> new ConcurrentHashMap<>());
        ServiceLoader<T> serviceLoader = (ServiceLoader<T>) serviceLoadersMap.computeIfAbsent(serviceClass,
                service -> ServiceLoader.load(service, classLoader));
        return serviceLoader;
    }
}
