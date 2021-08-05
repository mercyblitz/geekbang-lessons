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
import javax.cache.Caching;
import javax.cache.annotation.*;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * {@link CacheResolverFactory} implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DefaultCacheResolverFactory implements CacheResolverFactory {

    @Override
    public CacheResolver getCacheResolver(CacheMethodDetails<? extends Annotation> cacheMethodDetails) {
        CacheManager cacheManager = getCacheManager(cacheMethodDetails);
        return new DefaultCacheResolver(cacheManager);
    }

    @Override
    public CacheResolver getExceptionCacheResolver(CacheMethodDetails<CacheResult> cacheMethodDetails) {
        CacheManager cacheManager = getCacheManager(cacheMethodDetails);
        CacheResult cacheResult = cacheMethodDetails.getCacheAnnotation();
        String exceptionCacheName = cacheResult.exceptionCacheName();
        return new CacheResolver() {

            @Override
            public <K, V> Cache<K, V> resolveCache(CacheInvocationContext<? extends Annotation> cacheInvocationContext) {
                Cache exceptionCache = cacheManager.getCache(exceptionCacheName);
                if (exceptionCache == null) {
                    exceptionCache = cacheManager.createCache(exceptionCacheName, new MutableConfiguration<>());
                }
                return exceptionCache;
            }
        };
    }

    private CacheManager getCacheManager(CacheMethodDetails<? extends Annotation> cacheMethodDetails) {
        Method method = cacheMethodDetails.getMethod();
        Class<?> declaringClass = method.getDeclaringClass();
        ClassLoader classLoader = declaringClass.getClassLoader();
        CachingProvider cachingProvider = Caching.getCachingProvider(classLoader);
        return cachingProvider.getCacheManager(cachingProvider.getDefaultURI(), classLoader);
    }
}
