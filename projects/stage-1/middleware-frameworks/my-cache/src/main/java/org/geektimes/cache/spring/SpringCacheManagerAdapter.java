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

import org.geektimes.cache.configuration.CacheConfiguration;
import org.geektimes.cache.configuration.PropertiesCacheConfiguration;
import org.geektimes.commons.function.Streams;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.cache.Caching;
import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Spring {@link CacheManager} adapter based on Java Caching
 * {@link javax.cache.CacheManager}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SpringCacheManagerAdapter implements CacheManager {

    private final javax.cache.CacheManager cacheManager;

    public SpringCacheManagerAdapter() {
        this(Caching.getCachingProvider().getCacheManager());
    }

    public SpringCacheManagerAdapter(javax.cache.CacheManager cacheManager) {
        requireNonNull(cacheManager, "The 'cacheManager' argument must not be null!");
        this.cacheManager = cacheManager;
    }

    @Override
    public Cache getCache(String name) {
        javax.cache.Cache cache = getJavaCache(name);
        return new SpringCacheAdapter(cache);
    }

    /**
     * Get Java {@link javax.cache.Cache} instance.
     *
     * @param name the name of {@link javax.cache.Cache }
     * @return non-null
     */
    private javax.cache.Cache getJavaCache(String name) {
        javax.cache.Cache cache = cacheManager.getCache(name);
        if (cache == null) {
            CacheConfiguration configuration = new PropertiesCacheConfiguration(cacheManager.getProperties());
            cache = cacheManager.createCache(name, configuration);
        }
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return Streams.stream(cacheManager.getCacheNames())
                .collect(Collectors.toList());
    }
}
