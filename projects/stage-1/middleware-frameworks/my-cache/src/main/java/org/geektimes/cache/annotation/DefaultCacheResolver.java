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
package org.geektimes.cache.annotation;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheResolver;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import java.lang.annotation.Annotation;

import static java.util.Objects.requireNonNull;

/**
 * Default {@link CacheResolver}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DefaultCacheResolver implements CacheResolver {

    private final CacheManager cacheManager;

    public DefaultCacheResolver(CacheManager cacheManager) throws NullPointerException {
        requireNonNull(cacheManager, "The 'cacheManager' argument must be not null!");
        this.cacheManager = cacheManager;
    }

    @Override
    public <K, V> Cache<K, V> resolveCache(CacheInvocationContext<? extends Annotation> cacheInvocationContext) {
        String cacheName = cacheInvocationContext.getCacheName();
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            Configuration<K, V> configuration = new MutableConfiguration<>();
            cache = cacheManager.createCache(cacheName, configuration);
        }
        return cache;
    }
}
