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
package org.geektimes.cache.annotation.util;

import org.geektimes.cache.annotation.DefaultCacheKeyGenerator;
import org.geektimes.cache.annotation.DefaultCacheResolverFactory;

import javax.cache.annotation.*;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;

/**
 * Cache Operation Annotation Info
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CachePut
 * @see CacheRemove
 * @see CacheRemoveAll
 * @see CacheResult
 * @see CacheDefaults
 * @since 1.0.0
 */
public class CacheOperationAnnotationInfo {

    private static Class<? extends Throwable>[] EMPTY_FAILURE = new Class[0];

    private final String cacheName;

    private final Boolean afterInvocation;

    private final Class<? extends CacheResolverFactory> cacheResolverFactoryClass;

    private final Class<? extends CacheKeyGenerator> cacheKeyGeneratorClass;

    private final Class<? extends Throwable>[] appliedFailures;

    private final Class<? extends Throwable>[] nonAppliedFailures;

    private final Boolean skipGet;

    private final String exceptionCacheName;

    public CacheOperationAnnotationInfo(CacheOperationAnnotationInfo that) {
        this.cacheName = that.cacheName;
        this.afterInvocation = that.afterInvocation;
        this.cacheResolverFactoryClass = that.cacheResolverFactoryClass;
        this.cacheKeyGeneratorClass = that.cacheKeyGeneratorClass;
        this.appliedFailures = that.appliedFailures;
        this.nonAppliedFailures = that.nonAppliedFailures;
        this.skipGet = that.skipGet;
        this.exceptionCacheName = null;
    }

    public CacheOperationAnnotationInfo(CachePut cachePut, CacheDefaults cacheDefaults) {
        this.cacheName = getCacheName(cachePut::cacheName, cacheDefaults::cacheName);
        this.afterInvocation = cachePut.afterInvocation();
        this.cacheResolverFactoryClass = getCacheResolverFactoryClass(cachePut::cacheResolverFactory, cacheDefaults::cacheResolverFactory);
        this.cacheKeyGeneratorClass = getCacheKeyGeneratorClass(cachePut::cacheKeyGenerator, cacheDefaults::cacheKeyGenerator);
        this.appliedFailures = afterInvocation ? cachePut.cacheFor() : EMPTY_FAILURE;
        this.nonAppliedFailures = afterInvocation ? cachePut.noCacheFor() : EMPTY_FAILURE;
        this.skipGet = null;
        this.exceptionCacheName = null;
    }

    public CacheOperationAnnotationInfo(CacheRemove cacheRemove, CacheDefaults cacheDefaults) {
        this.cacheName = getCacheName(cacheRemove::cacheName, cacheDefaults::cacheName);
        this.afterInvocation = cacheRemove.afterInvocation();
        this.cacheResolverFactoryClass = getCacheResolverFactoryClass(cacheRemove::cacheResolverFactory, cacheDefaults::cacheResolverFactory);
        this.cacheKeyGeneratorClass = getCacheKeyGeneratorClass(cacheRemove::cacheKeyGenerator, cacheDefaults::cacheKeyGenerator);
        this.appliedFailures = afterInvocation ? cacheRemove.evictFor() : EMPTY_FAILURE;
        this.nonAppliedFailures = afterInvocation ? cacheRemove.noEvictFor() : EMPTY_FAILURE;
        this.skipGet = null;
        this.exceptionCacheName = null;
    }

    public CacheOperationAnnotationInfo(CacheRemoveAll cacheRemoveAll, CacheDefaults cacheDefaults) {
        this.cacheName = getCacheName(cacheRemoveAll::cacheName, cacheDefaults::cacheName);
        this.afterInvocation = cacheRemoveAll.afterInvocation();
        this.cacheResolverFactoryClass = getCacheResolverFactoryClass(cacheRemoveAll::cacheResolverFactory, cacheDefaults::cacheResolverFactory);
        this.cacheKeyGeneratorClass = null;
        this.appliedFailures = afterInvocation ? cacheRemoveAll.evictFor() : EMPTY_FAILURE;
        this.nonAppliedFailures = afterInvocation ? cacheRemoveAll.noEvictFor() : EMPTY_FAILURE;
        this.skipGet = null;
        this.exceptionCacheName = null;
    }

    public CacheOperationAnnotationInfo(CacheResult cacheResult, CacheDefaults cacheDefaults) {
        this.cacheName = getCacheName(cacheResult::cacheName, cacheDefaults::cacheName);
        this.afterInvocation = null;
        this.cacheResolverFactoryClass = getCacheResolverFactoryClass(cacheResult::cacheResolverFactory, cacheDefaults::cacheResolverFactory);
        this.cacheKeyGeneratorClass = getCacheKeyGeneratorClass(cacheResult::cacheKeyGenerator, cacheDefaults::cacheKeyGenerator);
        this.appliedFailures = cacheResult.cachedExceptions();
        this.nonAppliedFailures = cacheResult.nonCachedExceptions();
        this.skipGet = cacheResult.skipGet();
        this.exceptionCacheName = cacheResult.exceptionCacheName();
    }

    public String getCacheName() {
        return cacheName;
    }

    public Boolean getAfterInvocation() {
        return afterInvocation;
    }

    public boolean isAfterInvocation() {
        return TRUE.equals(getAfterInvocation());
    }

    public Class<? extends CacheResolverFactory> getCacheResolverFactoryClass() {
        return cacheResolverFactoryClass;
    }

    public Class<? extends CacheKeyGenerator> getCacheKeyGeneratorClass() {
        return cacheKeyGeneratorClass;
    }

    public Class<? extends Throwable>[] getAppliedFailures() {
        return appliedFailures;
    }

    public Class<? extends Throwable>[] getNonAppliedFailures() {
        return nonAppliedFailures;
    }

    public Boolean getSkipGet() {
        return skipGet;
    }

    public boolean isSkipGet() {
        return TRUE.equals(getSkipGet());
    }

    public String getExceptionCacheName() {
        return exceptionCacheName;
    }


    private String getCacheName(Supplier<String> cacheNameSupplier, Supplier<String> defaultCacheNameSupplier) {
        String cacheName = cacheNameSupplier.get();
        if ("".equals(cacheName)) {
            cacheName = defaultCacheNameSupplier.get();
        }
        return cacheName;
    }

    private Class<? extends CacheKeyGenerator> getCacheKeyGeneratorClass(
            Supplier<Class<? extends CacheKeyGenerator>> cacheKeyGeneratorClassSupplier,
            Supplier<Class<? extends CacheKeyGenerator>> defaultCacheKeyGeneratorClassSupplier) {
        Class<? extends CacheKeyGenerator> cacheKeyGeneratorClass = cacheKeyGeneratorClassSupplier.get();
        if (CacheKeyGenerator.class.equals(cacheKeyGeneratorClass)) {
            cacheKeyGeneratorClass = defaultCacheKeyGeneratorClassSupplier.get();
        }

        if (CacheKeyGenerator.class.equals(cacheKeyGeneratorClass)) { // Default value as well
            cacheKeyGeneratorClass = DefaultCacheKeyGenerator.class;
        }

        return cacheKeyGeneratorClass;
    }

    private Class<? extends CacheResolverFactory> getCacheResolverFactoryClass(
            Supplier<Class<? extends CacheResolverFactory>> cacheResolverFactoryClassSupplier,
            Supplier<Class<? extends CacheResolverFactory>> defaultCacheResolverFactoryClassSupplier) {
        Class<? extends CacheResolverFactory> cacheResolverFactoryClass = cacheResolverFactoryClassSupplier.get();
        if (CacheResolverFactory.class.equals(cacheResolverFactoryClass)) {
            cacheResolverFactoryClass = defaultCacheResolverFactoryClassSupplier.get();
        }

        if (CacheResolverFactory.class.equals(cacheResolverFactoryClass)) { // Default value as well
            cacheResolverFactoryClass = DefaultCacheResolverFactory.class;
        }

        return cacheResolverFactoryClass;
    }
}
