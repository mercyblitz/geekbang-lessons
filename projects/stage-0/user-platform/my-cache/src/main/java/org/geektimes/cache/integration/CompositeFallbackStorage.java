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
package org.geektimes.cache.integration;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * Composite multiple {@link FallbackStorage}s that instantiated by {@link ServiceLoader Java SPI}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
public class CompositeFallbackStorage extends AbstractFallbackStorage<Object, Object> {

    private static final ConcurrentMap<ClassLoader, List<FallbackStorage>> fallbackStoragesCache =
            new ConcurrentHashMap<>();

    private final List<FallbackStorage> fallbackStorages;

    public CompositeFallbackStorage(ClassLoader classLoader) {
        super(Integer.MIN_VALUE);
        this.fallbackStorages = fallbackStoragesCache.computeIfAbsent(classLoader, this::loadFallbackStorages);
    }

    private List<FallbackStorage> loadFallbackStorages(ClassLoader classLoader) {
        return stream(ServiceLoader.load(FallbackStorage.class, classLoader).spliterator(), false)
                .sorted(PRIORITY_COMPARATOR)
                .collect(toList());
    }

    public CompositeFallbackStorage() {
        this(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Object load(Object key) throws CacheLoaderException {
        Object value = null;
        for (FallbackStorage fallbackStorage : fallbackStorages) {
            value = fallbackStorage.load(key);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    @Override
    public void write(Cache.Entry entry) throws CacheWriterException {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.write(entry));
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.delete(key));
    }

    @Override
    public void destroy() {
        fallbackStorages.forEach(FallbackStorage::destroy);
    }
}
