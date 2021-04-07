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
package org.geektimes.cache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Abstract {@link CacheManager} class, all instances of {@link CacheManager} that are generated by
 * {@link ConfigurableCachingProvider#getCacheManager(URI, ClassLoader, Properties)} must extend current class.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConfigurableCachingProvider
 * @since 1.0
 */
public abstract class AbstractCacheManager implements CacheManager {

    private static final Consumer<Cache> CLEAR_CACHE_OPERATION = Cache::clear;

    private static final Consumer<Cache> CLOSE_CACHE_OPERATION = Cache::close;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final CachingProvider cachingProvider;

    private final URI uri;

    private final ClassLoader classLoader;

    private final Properties properties;

    private volatile boolean closed;

    private ConcurrentMap<String, Map<KeyValueTypePair, Cache>> cacheRepository = new ConcurrentHashMap<>();

    public AbstractCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        this.cachingProvider = cachingProvider;
        this.uri = uri == null ? cachingProvider.getDefaultURI() : uri;
        this.properties = properties == null ? cachingProvider.getDefaultProperties() : properties;
        this.classLoader = classLoader == null ? cachingProvider.getDefaultClassLoader() : classLoader;
    }

    @Override
    public final CachingProvider getCachingProvider() {
        return cachingProvider;
    }

    @Override
    public final URI getURI() {
        return uri;
    }

    @Override
    public final ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public final Properties getProperties() {
        return properties;
    }

    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException {
        return getOrCreateCache(cacheName, configuration, true);
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        MutableConfiguration<K, V> configuration = new MutableConfiguration<K, V>().setTypes(keyType, valueType);
        return getOrCreateCache(cacheName, configuration, false);
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return getCache(cacheName, (Class<K>) Object.class, (Class<V>) Object.class);
    }

    protected <K, V, C extends Configuration<K, V>> Cache<K, V> getOrCreateCache(String cacheName, C configuration,
                                                                                 boolean created) throws IllegalArgumentException, IllegalStateException {
        assertNotClosed();

        Map<KeyValueTypePair, Cache> cacheMap = cacheRepository.computeIfAbsent(cacheName, n -> new ConcurrentHashMap<>());

        return cacheMap.computeIfAbsent(new KeyValueTypePair(configuration.getKeyType(), configuration.getValueType()),
                key -> created ? doCreateCache(cacheName, configuration) : null);
    }

    protected abstract <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration);

    @Override
    public Iterable<String> getCacheNames() {
        assertNotClosed();
        return cacheRepository.keySet();
    }

    @Override
    public void destroyCache(String cacheName) {
        assertNotClosed();
        Map<KeyValueTypePair, Cache> cacheMap = cacheRepository.get(cacheName);
        if (cacheMap != null) {
            iterateCaches(cacheMap.values(), CLEAR_CACHE_OPERATION, CLOSE_CACHE_OPERATION);
        }
    }

    @Override
    public void enableManagement(String cacheName, boolean enabled) {
        assertNotClosed();
        // TODO
        throw new UnsupportedOperationException("TO support in the future!");
    }

    @Override
    public void enableStatistics(String cacheName, boolean enabled) {
        assertNotClosed();
        // TODO
        throw new UnsupportedOperationException("TO support in the future!");
    }

    private void assertNotClosed() throws IllegalStateException {
        if (isClosed()) {
            throw new IllegalStateException("The CacheManager has been closed, current operation should not be invoked!");
        }
    }

    @Override
    public void close() {
        if (isClosed()) {
            logger.warning("The CacheManager has been closed, current close operation will be ignored!");
            return;
        }
        for (Map<KeyValueTypePair, Cache> cacheMap : cacheRepository.values()) {
            iterateCaches(cacheMap.values(), CLOSE_CACHE_OPERATION);
        }
        this.closed = true;
    }

    protected void iterateCaches(Iterable<Cache> caches, Consumer<Cache>... cacheOperations) {
        for (Cache cache : caches) {
            for (Consumer<Cache> cacheOperation : cacheOperations) {
                cacheOperation.accept(cache);
            }
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return null;
    }
}
