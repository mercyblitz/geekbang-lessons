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
 * The {@link Interceptor @Interceptor} class for Java Caching annotation {@link CacheRemove}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Interceptor
public class CacheRemoveInterceptor extends CacheOperationInterceptor<CacheRemove> {

    @Override
    protected CacheOperationAnnotationInfo getCacheOperationAnnotationInfo(CacheRemove cacheOperationAnnotation,
                                                                           CacheDefaults cacheDefaults) {
        return new CacheOperationAnnotationInfo(cacheOperationAnnotation, cacheDefaults);
    }

    @Override
    protected Object beforeExecute(CacheRemove cacheOperationAnnotation,
                                   CacheKeyInvocationContext<CacheRemove> cacheKeyInvocationContext,
                                   CacheOperationAnnotationInfo cacheOperationAnnotationInfo, Cache cache,
                                   Optional<GeneratedCacheKey> cacheKey) {
        if (!cacheOperationAnnotationInfo.isAfterInvocation()) {
            manipulateCache(cacheKeyInvocationContext, cache, cacheKey);
        }
        return null;
    }

    @Override
    protected void afterExecute(CacheRemove cacheOperationAnnotation,
                                CacheKeyInvocationContext<CacheRemove> cacheKeyInvocationContext,
                                CacheOperationAnnotationInfo cacheOperationAnnotationInfo, Cache cache,
                                Optional<GeneratedCacheKey> cacheKey, Object result) {
        if (cacheOperationAnnotationInfo.isAfterInvocation()) {
            manipulateCache(cacheKeyInvocationContext, cache, cacheKey);
        }
    }

    private void manipulateCache(CacheKeyInvocationContext<CacheRemove> cacheKeyInvocationContext,
                                 Cache cache, Optional<GeneratedCacheKey> cacheKey) {
        cacheKey.ifPresent(key -> {
            CacheInvocationParameter valueParameter = cacheKeyInvocationContext.getValueParameter();
            if (valueParameter != null) {
                cache.remove(key, valueParameter.getValue());
            } else {
                cache.remove(key);
            }
        });
    }

    @Override
    protected void handleFailure(CacheRemove cacheOperationAnnotation,
                                 CacheKeyInvocationContext<CacheRemove> cacheKeyInvocationContext,
                                 CacheOperationAnnotationInfo cacheOperationAnnotationInfo, Cache cache,
                                 Optional<GeneratedCacheKey> cacheKey, Throwable failure) {
        cacheKey.ifPresent(key -> {
            cache.remove(key, failure);
        });
    }
}
