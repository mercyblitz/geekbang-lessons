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

import org.geektimes.cache.annotation.ReflectiveCacheKeyInvocationContext;
import org.geektimes.cache.annotation.util.CacheOperationAnnotationInfo;
import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.cache.Cache;
import javax.cache.annotation.*;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.geektimes.cache.annotation.util.CacheAnnotationUtils.findCacheDefaults;
import static org.geektimes.commons.reflect.util.ClassUtils.isDerived;

/**
 * The abstract {@link Interceptor @Interceptor} class for Cache Annotation Operation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CachePut
 * @see CacheRemove
 * @see CacheRemoveAll
 * @see CacheResult
 * @see CacheDefaults
 * @since 1.0.0
 */
public abstract class CacheOperationInterceptor<A extends Annotation> extends AnnotatedInterceptor<A> {

    private final ConcurrentMap<A, CacheResolverFactory> cacheResolverFactoryCache = new ConcurrentHashMap<>();

    private final ConcurrentMap<A, CacheKeyGenerator> cacheKeyGeneratorCache = new ConcurrentHashMap<>();

    protected Object execute(InvocationContext context, A cacheOperationAnnotation) throws Throwable {
        Object target = context.getTarget();
        Method method = context.getMethod();
        Object[] parameters = context.getParameters();

        CacheKeyInvocationContext<A> cacheKeyInvocationContext = new ReflectiveCacheKeyInvocationContext<>(target, method, parameters);

        CacheDefaults cacheDefaults = findCacheDefaults(method, target);

        CacheOperationAnnotationInfo cacheOperationAnnotationInfo = getCacheOperationAnnotationInfo(cacheOperationAnnotation, cacheDefaults);

        Object result = null;

        Cache cache = resolveCache(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo);

        Optional<GeneratedCacheKey> cacheKey = generateCacheKey(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo);

        try {
            result = beforeExecute(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo, cache, cacheKey);
            if (result == null) {
                result = context.proceed();
                afterExecute(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo, cache, cacheKey, result);
            }
        } catch (Throwable e) {
            Throwable failure = getFailure(e);
            if (shouldHandleFailure(failure, cacheOperationAnnotationInfo)) {
                handleFailure(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo, cache, cacheKey, failure);
            }
        }

        return result;
    }

    protected abstract CacheOperationAnnotationInfo getCacheOperationAnnotationInfo(A cacheOperationAnnotation, CacheDefaults cacheDefaults);

    protected abstract Object beforeExecute(A cacheOperationAnnotation, CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                            CacheOperationAnnotationInfo cacheOperationAnnotationInfo,
                                            Cache cache, Optional<GeneratedCacheKey> cacheKey);

    protected abstract void afterExecute(A cacheOperationAnnotation, CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                         CacheOperationAnnotationInfo cacheOperationAnnotationInfo,
                                         Cache cache, Optional<GeneratedCacheKey> cacheKey, Object result);

    protected abstract void handleFailure(A cacheOperationAnnotation, CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                          CacheOperationAnnotationInfo cacheOperationAnnotationInfo,
                                          Cache cache, Optional<GeneratedCacheKey> cacheKey, Throwable failure);

    private Cache resolveCache(A cacheOperationAnnotation, CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                               CacheOperationAnnotationInfo cacheOperationAnnotationInfo) {
        CacheResolverFactory cacheResolverFactory = getCacheResolverFactory(cacheOperationAnnotation,
                cacheKeyInvocationContext, cacheOperationAnnotationInfo);
        CacheResolver cacheResolver = cacheResolverFactory.getCacheResolver(cacheKeyInvocationContext);
        return cacheResolver.resolveCache(cacheKeyInvocationContext);
    }

    protected CacheResolverFactory getCacheResolverFactory(A cacheOperationAnnotation,
                                                           CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                                           CacheOperationAnnotationInfo cacheOperationAnnotationInfo) {
        return cacheResolverFactoryCache.computeIfAbsent(cacheOperationAnnotation, key -> {
            Class<? extends CacheResolverFactory> cacheResolverFactoryClass = cacheOperationAnnotationInfo.getCacheResolverFactoryClass();
            return cacheKeyInvocationContext.unwrap(cacheResolverFactoryClass);
        });
    }

    private Optional<GeneratedCacheKey> generateCacheKey(A cacheOperationAnnotation,
                                                         CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                                         CacheOperationAnnotationInfo cacheOperationAnnotationInfo) {
        CacheKeyGenerator cacheKeyGenerator = getCacheKeyGenerator(cacheOperationAnnotation, cacheKeyInvocationContext, cacheOperationAnnotationInfo);

        if (cacheKeyGenerator == null) {
            return Optional.empty();
        }

        return Optional.of(cacheKeyGenerator.generateCacheKey(cacheKeyInvocationContext));
    }

    private CacheKeyGenerator getCacheKeyGenerator(A cacheOperationAnnotation,
                                                   CacheKeyInvocationContext<A> cacheKeyInvocationContext,
                                                   CacheOperationAnnotationInfo cacheOperationAnnotationInfo) {

        Class<? extends CacheKeyGenerator> cacheKeyGeneratorClass = cacheOperationAnnotationInfo.getCacheKeyGeneratorClass();

        if (cacheKeyGeneratorClass == null) {
            return null;
        }

        return cacheKeyGeneratorCache.computeIfAbsent(cacheOperationAnnotation, key ->
                cacheKeyInvocationContext.unwrap(cacheKeyGeneratorClass)
        );
    }

    private boolean shouldHandleFailure(Throwable failure, CacheOperationAnnotationInfo cacheOperationAnnotationInfo) {
        Class<? extends Throwable>[] appliedFailures = cacheOperationAnnotationInfo.getAppliedFailures();
        Class<? extends Throwable>[] nonAppliedFailures = cacheOperationAnnotationInfo.getNonAppliedFailures();

        boolean hasAppliedFailures = appliedFailures.length > 0;
        boolean hasNonAppliedFailures = nonAppliedFailures.length > 0;

        if (!hasAppliedFailures && !hasNonAppliedFailures) {
            return true;
        }

        Class<? extends Throwable> failureType = failure.getClass();

        if (hasAppliedFailures && !hasNonAppliedFailures) {
            return isDerived(failureType, appliedFailures);
        } else if (!hasAppliedFailures && hasNonAppliedFailures) {
            return !isDerived(failureType, nonAppliedFailures);
        } else if (hasAppliedFailures && hasNonAppliedFailures) {
            return isDerived(failureType, appliedFailures) && !isDerived(failureType, nonAppliedFailures);
        }

        return false;
    }
}
