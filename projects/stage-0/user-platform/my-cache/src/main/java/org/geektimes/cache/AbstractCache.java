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

import org.geektimes.cache.configuration.ConfigurationUtils;
import org.geektimes.cache.integration.CompositeFallbackStorage;
import org.geektimes.cache.integration.FallbackStorage;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.util.*;
import java.util.logging.Logger;

/**
 * The abstract non-thread-safe implementation of {@link Cache}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    protected final CacheManager cacheManager;

    protected final String cacheName;

    protected final CompleteConfiguration<K, V> configuration;

    protected final FallbackStorage fallbackStorage;

    private volatile boolean closed = false;

    protected AbstractCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        this.cacheManager = cacheManager;
        this.cacheName = cacheName;
        this.configuration = ConfigurationUtils.valueOf(configuration);
        this.fallbackStorage = new CompositeFallbackStorage(cacheManager.getClassLoader());
    }

    @Override
    public V get(K key) {
        assertNotClosed();
        assertNotNull(key, "key");
        V value = null;
        try {
            value = doGet(key);
        } catch (Throwable e) {
            logger.severe(e.getMessage());
        }
        return loadValueIfReadThrough(key, value);
    }

    private V loadValueIfReadThrough(K key, V value) {
        V result = value;
        if (value == null && configuration.isReadThrough()) {
            result = (V) fallbackStorage.load(key);
            // re-write into cache
            if (result != null) {
                put(key, result);
            }
        }
        return result;
    }

    protected abstract V doGet(K key) throws CacheException, ClassCastException;

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        // Keep the order of keys
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    @Override
    public boolean containsKey(K key) {
        assertNotClosed();
        assertNotNull(key, "key");
        return get(key) != null;
    }

    @Override
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues,
                        CompletionListener completionListener) {
        assertNotClosed();
        // TODO
        throw new UnsupportedOperationException("This feature will be supported in the future");
    }

    @Override
    public void put(K key, V value) {
        assertNotClosed();
        assertNotNull(key, "key");
        assertNotNull(value, "value");
        try {
            doPut(key, value);
        } finally {
            writeIfWriteThrough(key, value);
        }
    }

    private void writeIfWriteThrough(K key, V value) {
        if (configuration.isWriteThrough()) {
            fallbackStorage.write(EntryAdapter.of(key, value));
        }
    }

    protected abstract void doPut(K key, V value) throws CacheException, ClassCastException;

    @Override
    public V getAndPut(K key, V value) {
        V oldValue = get(key);
        put(key, value);
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            put(key, value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(K key) {
        assertNotClosed();
        assertNotNull(key, "key");
        try {
            return doRemove(key);
        } finally {
            deleteIfWriteThrough(key);
        }
    }

    private void deleteIfWriteThrough(K key) {
        if (configuration.isWriteThrough()) {
            fallbackStorage.delete(key);
        }
    }

    protected abstract boolean doRemove(K key) throws CacheException, ClassCastException;

    @Override
    public boolean remove(K key, V oldValue) {
        if (containsKey(key) && Objects.equals(get(key), oldValue)) {
            remove(key);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public V getAndRemove(K key) {
        V oldValue = get(key);
        remove(key);
        return oldValue;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        assertNotNull(key, "oldValue");
        if (Objects.equals(get(key), oldValue)) {
            put(key, newValue);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean replace(K key, V value) {
        if (containsKey(key)) {
            put(key, value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public V getAndReplace(K key, V value) {
        V oldValue = get(key);
        if (oldValue != null) {
            put(key, value);
        }
        return oldValue;
    }

    @Override
    public void removeAll(Set<? extends K> keys) {
        for (K key : keys) {
            remove(key);
        }
    }

    @Override
    public void removeAll() {
        Iterator<Entry<K, V>> iterator = iterator();
        while (iterator.hasNext()) {
            Entry<K, V> entry = iterator.next();
            K key = entry.getKey();
            remove(key);
        }
    }

    @Override
    public void clear() {
        assertNotClosed();
        doClear();
    }

    protected abstract void doClear() throws CacheException;

    @Override
    public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz) {
        Configuration<K, V> configuration = unwrap(clazz);
        return (C) ConfigurationUtils.valueOf(configuration);
    }

    @Override
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments) throws EntryProcessorException {
        // TODO
        throw new UnsupportedOperationException("This feature will be supported in the future");
    }

    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object... arguments) {
        // TODO
        throw new UnsupportedOperationException("This feature will be supported in the future");
    }


    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        // TODO
        throw new UnsupportedOperationException("This feature will be supported in the future");
    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        // TODO
        throw new UnsupportedOperationException("This feature will be supported in the future");
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        assertNotClosed();
        return newIterator();
    }

    protected abstract Iterator<Entry<K, V>> newIterator();

    @Override
    public <T> T unwrap(Class<T> clazz) {
        T value = null;
        try {
            value = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    @Override
    public final String getName() {
        return cacheName;
    }

    @Override
    public final CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public final void close() {
        if (isClosed()) {
            return;
        }
        doClose();
        closed = true;
    }

    /**
     * Subclass could override this method.
     */
    protected void doClose() {
    }

    @Override
    public final boolean isClosed() {
        return closed;
    }

    private void assertNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("Current cache has been closed! No operation should be executed.");
        }
    }

    protected static void assertNotNull(Object object, String source) {
        if (object == null) {
            throw new NullPointerException(source + " must not be null!");
        }
    }
}
