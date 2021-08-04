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
 * The {@link Interceptor @Interceptor} class for Java Caching annotation {@link CachePut}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Interceptor
public class CachePutInterceptor extends CacheOperationInterceptor<CachePut> {

    @Override
    protected CacheOperationAnnotationInfo getCacheOperationAnnotationInfo(CachePut cacheOperationAnnotation,
                                                                           CacheDefaults cacheDefaults) {
        return new CacheOperationAnnotationInfo(cacheOperationAnnotation, cacheDefaults);
    }

    @Override
    protected Object beforeExecute(CachePut cacheOperationAnnotation,
                                   CacheKeyInvocationContext<CachePut> cacheKeyInvocationContext,
                                   CacheOperationAnnotationInfo cacheOperationAnnotationInfo, Cache cache,
                                   Optional<GeneratedCacheKey> cacheKey) {
        if (!cacheOperationAnnotationInfo.isAfterInvocation()) {
            manipulateCache(cacheKeyInvocationContext, cache, cacheKey);
        }
        return null;
    }

    @Override
    protected void afterExecute(CachePut cacheOperationAnnotation,
                                CacheKeyInvocationContext<CachePut> cacheKeyInvocationContext,
                                CacheOperationAnnotationInfo cacheOperationAnnotationInfo, Cache cache,
                                Optional<GeneratedCacheKey> cacheKey, Object result) {
        if (cacheOperationAnnotationInfo.isAfterInvocation()) {
            manipulateCache(cacheKeyInvocationContext, cache, cacheKey);
        }
    }

    private void manipulateCache(CacheKeyInvocationContext<CachePut> cacheKeyInvocationContext,
                                 Cache cache, Optional<GeneratedCacheKey> cacheKey) {
        cacheKey.ifPresent(key -> {
            CacheInvocationParameter valueParameter = cacheKeyInvocationContext.getValueParameter();
            if (valueParameter != null) {
                cache.put(key, valueParameter.getValue());
            }
        });
    }

    @Override
    protected void handleFailure(CachePut cacheOperationAnnotation,
                                 CacheKeyInvocationContext<CachePut> cacheKeyInvocationContext,
                                 CacheOperationAnnotationInfo cacheOperationAnnotationInfo, Cache cache,
                                 Optional<GeneratedCacheKey> cacheKey, Throwable failure) {
        cacheKey.ifPresent(key -> {
            cache.put(key, failure);
        });
    }
}
