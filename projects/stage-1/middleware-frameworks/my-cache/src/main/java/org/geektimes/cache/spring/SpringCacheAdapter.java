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
package org.geektimes.cache.spring;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

import static java.util.Objects.requireNonNull;
import static org.geektimes.commons.convert.Converter.convertIfPossible;
import static org.geektimes.commons.function.ThrowableSupplier.execute;

/**
 * Spring {@link Cache} adapter based on Java Caching
 * {@link javax.cache.Cache}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SpringCacheAdapter implements Cache {

    private final javax.cache.Cache cache;

    public SpringCacheAdapter(javax.cache.Cache cache) {
        requireNonNull(cache, "The 'cache' argument must not be null!");
        this.cache = cache;
    }

    @Override
    public String getName() {
        return cache.getName();
    }

    @Override
    public Object getNativeCache() {
        return cache;
    }

    @Override
    public ValueWrapper get(Object key) {
        return () -> cache.get(key);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        Object value = cache.get(key);
        return convertIfPossible(value, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = cache.get(key);
        if (value == null) {
            value = execute(valueLoader::call);
        }
        return (T) value;
    }

    @Override
    public void put(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void evict(Object key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
