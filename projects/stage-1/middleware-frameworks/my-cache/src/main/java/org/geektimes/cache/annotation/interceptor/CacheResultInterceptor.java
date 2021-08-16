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
package org.geektimes.cache.annotation.interceptor;

import org.geektimes.cache.annotation.util.CacheOperationAnnotationInfo;

import javax.cache.Cache;
import javax.cache.annotation.*;
import javax.interceptor.Interceptor;
import java.util.Optional;

/**
 * The {@link Interceptor @Interceptor} class for Java Caching annotation {@link CacheResult}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Interceptor
public class CacheResultInterceptor extends CacheOperationInterceptor<CacheResult> {

    @Override
    protected CacheOperationAnnotationInfo getCacheOperationAnnotationInfo(CacheResult cacheOperationAnnotation,
                                                                           CacheDefaults cacheDefaults) {
        return new CacheOperationAnnotationInfo(cacheOperationAnnotation, cacheDefaults);
    }

    @Override
    protected Object beforeExecute(CacheResult cacheOperationAnnotation,
                                   CacheKeyInvocationContext<CacheResult> cacheKeyInvocationContext,
                                   CacheOperationAnnotationInfo cacheOperationAnnotationInfo, Cache cache,
                                   Optional<GeneratedCacheKey> cacheKey) {

        /**
         * If set to true the pre-invocation {@link Cache#get(Object)} is
         * skipped and the annotated method is always executed with the returned value
         * being cached as normal. This is useful for create or update methods that
         * should always be executed and have their returned value placed in the cache.
         */
        if (cacheOperationAnnotationInfo.isSkipGet()) {
            return null;
        }

        return cacheKey.map(key -> cache.get(key)).orElse(null);
    }

    @Override
    protected void afterExecute(CacheResult cacheOperationAnnotation,
                                CacheKeyInvocationContext<CacheResult> cacheKeyInvocationContext,
                                CacheOperationAnnotationInfo cacheOperationAnnotationInfo, Cache cache,
                                Optional<GeneratedCacheKey> cacheKey, Object result) {
        if (result != null) {
            cacheKey.ifPresent(key -> cache.put(key, result));
        }
    }

    @Override
    protected void handleFailure(CacheResult cacheOperationAnnotation,
                                 CacheKeyInvocationContext<CacheResult> cacheKeyInvocationContext,
                                 CacheOperationAnnotationInfo cacheOperationAnnotationInfo, Cache cache,
                                 Optional<GeneratedCacheKey> cacheKey, Throwable failure) {
        if (cacheOperationAnnotationInfo.isSkipGet()) {
            cacheKey.ifPresent(key -> {
                CacheResolverFactory cacheResolverFactory = getCacheResolverFactory(cacheOperationAnnotation,
                        cacheKeyInvocationContext, cacheOperationAnnotationInfo);
                CacheResolver exceptionCacheResolver = cacheResolverFactory.getExceptionCacheResolver(cacheKeyInvocationContext);
                Cache exceptionCache = exceptionCacheResolver.resolveCache(cacheKeyInvocationContext);
                exceptionCache.put(key, failure);
            });
        }
    }
}
