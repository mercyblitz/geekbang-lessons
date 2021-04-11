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

import org.geektimes.cache.event.CacheEntryEventPublisher;
import org.geektimes.cache.integration.CompositeFallbackStorage;
import org.geektimes.cache.integration.FallbackStorage;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.Factory;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.geektimes.cache.ExpirableEntry.requireKeyNotNull;
import static org.geektimes.cache.ExpirableEntry.requireValueNotNull;
import static org.geektimes.cache.configuration.ConfigurationUtils.completeConfiguration;
import static org.geektimes.cache.event.GenericCacheEntryEvent.*;

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

    protected final ExpiryPolicy expiryPolicy;

    protected final FallbackStorage fallbackStorage;

    private final CacheEntryEventPublisher entryEventPublisher;

    private volatile boolean closed = false;

    protected AbstractCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        this.cacheManager = cacheManager;
        this.cacheName = cacheName;
        this.configuration = completeConfiguration(configuration);
        this.expiryPolicy = getExpiryPolicy(this.configuration);
        this.fallbackStorage = new CompositeFallbackStorage(cacheManager.getClassLoader());
        this.entryEventPublisher = new CacheEntryEventPublisher();
    }

    @Override
    public boolean containsKey(K key) {
        assertNotClosed();
        return containsEntry(key);
    }

    protected abstract boolean containsEntry(K key) throws CacheException, ClassCastException;

    @Override
    public V get(K key) {
        assertNotClosed();
        requireKeyNotNull(key);
        ExpirableEntry<K, V> entry = null;
        try {
            entry = getEntry(key);
            if (handleExpiryPolicyForAccess(entry)) {
                return null;
            }
        } catch (Throwable e) {
            logger.severe(e.getMessage());
        }
        return loadValueIfReadThrough(entry);
    }

    protected abstract ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException;

    private V loadValueIfReadThrough(ExpirableEntry<K, V> entry) {
        if (entry == null) {
            return null;
        }
        K key = entry.getKey();
        V value = entry.getValue();
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
    public V getAndPut(K key, V value) {
        Entry<K, V> entry = getEntry(key);
        put(key, value);
        return entry.getValue();
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
        Entry<K, V> entry = null;
        try {
            if (!containsKey(key)) {
                // Put the new Cache.Entry
                entry = createAndPutEntry(key, value);
            } else {
                entry = updateEntry(key, value);
            }
        } finally {
            writeIfWriteThrough(entry);
        }
    }

    private Entry<K, V> createAndPutEntry(K key, V value) {
        // Create Cache.Entry
        ExpirableEntry<K, V> newEntry = createEntry(key, value);
        if (handleExpiryPolicyForCreation(newEntry)) {
            // The new Cache.Entry is already expired and will not be added to the Cache.
            return null;
        }

        putEntry(newEntry);
        publishCreatedEvent(key, value);

        return newEntry;
    }

    private ExpirableEntry<K, V> createEntry(K key, V value) {
        return ExpirableEntry.of(key, value);
    }

    private Entry<K, V> updateEntry(K key, V value) {
        // Update Cache.Entry
        ExpirableEntry<K, V> oldEntry = getEntry(key);

        V oldValue = oldEntry.getValue();
        // Update the value
        oldEntry.setValue(value);
        // Rewrite oldEntry
        putEntry(oldEntry);
        publishUpdatedEvent(key, oldValue, value);

        if (handleExpiryPolicyForUpdate(oldEntry)) {
            return null;
        }

        return oldEntry;
    }

    private boolean handleExpiryPolicyForCreation(ExpirableEntry<K, V> newEntry) {
        return handleExpiryPolicy(newEntry, getExpiryForCreation(), false);
    }

    private boolean handleExpiryPolicyForAccess(ExpirableEntry<K, V> entry) {
        return handleExpiryPolicy(entry, getExpiryForAccess(), true);
    }

    private boolean handleExpiryPolicyForUpdate(ExpirableEntry<K, V> oldEntry) {
        return handleExpiryPolicy(oldEntry, getExpiryForUpdate(), true);
    }

    /**
     * Handle {@link ExpiryPolicy}
     *
     * @param entry               {@link ExpirableEntry}
     * @param duration            Creation : If a {@link Duration#ZERO} is returned the new Cache.Entry is considered
     *                            to be already expired and will not be added to the Cache.
     *                            Access or Update : If a {@link Duration#ZERO} is returned a Cache.Entry will be considered
     *                            immediately expired.
     *                            <code>null</code> will result in no change to the previously understood expiry
     *                            {@link Duration}.
     * @param removedExpiredEntry the expired {@link Cache.Entry} is removed or not.
     *                            If <code>true</code>, the {@link Cache.Entry} will be removed and publish an
     *                            {@link EventType#EXPIRED} of {@link CacheEntryEvent}.
     * @return <code>true</code> indicates the specified {@link Cache.Entry} should be expired.
     */
    private boolean handleExpiryPolicy(ExpirableEntry<K, V> entry, Duration duration, boolean removedExpiredEntry) {

        boolean expired = false;

        if (duration != null) {
            if (duration.isZero() || entry.isExpired()) {
                expired = true;
            } else {
                long timestamp = duration.getAdjustedTime(System.currentTimeMillis());
                // Update the timestamp
                entry.setTimestamp(timestamp);
            }
        }

        if (removedExpiredEntry && expired) {
            // Remove Cache.Entry
            K key = entry.getKey();
            V value = entry.getValue();
            removeEntry(key);
            publishExpiredEvent(key, value);
        }

        return expired;
    }

    /**
     * Put the {@link javax.cache.Cache.Entry} into cache.
     *
     * @param newEntry The new instance of {@link Cache.Entry<K,V>} is created by {@link Cache}
     * @throws CacheException
     * @throws ClassCastException
     */
    protected abstract void putEntry(ExpirableEntry<K, V> newEntry) throws CacheException, ClassCastException;

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
        requireKeyNotNull(key);
        boolean removed = false;
        try {
            ExpirableEntry<K, V> oldEntry = removeEntry(key);
            removed = oldEntry != null;
            if (removed) {
                publishRemovedEvent(key, oldEntry.getValue());
            }
        } finally {
            deleteIfWriteThrough(key);
        }
        return removed;
    }

    protected abstract ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException;

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
        requireValueNotNull(oldValue);
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
        return (C) completeConfiguration(configuration);
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
        entryEventPublisher.registerCacheEntryListener(cacheEntryListenerConfiguration);
    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        entryEventPublisher.deregisterCacheEntryListener(cacheEntryListenerConfiguration);
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


    protected Duration getExpiryForCreation() {
        return getDuration(expiryPolicy::getExpiryForCreation);
    }

    protected Duration getExpiryForAccess() {
        return getDuration(expiryPolicy::getExpiryForAccess);
    }

    protected Duration getExpiryForUpdate() {
        return getDuration(expiryPolicy::getExpiryForUpdate);
    }

    private Duration getDuration(Supplier<Duration> durationSupplier) {
        Duration duration = null;
        try {
            duration = durationSupplier.get();
        } catch (Throwable ignored) {
            // Default
            duration = Duration.ETERNAL;
        }
        return duration;
    }

    @Override
    public final boolean isClosed() {
        return closed;
    }

    private void publishCreatedEvent(K key, V value) {
        entryEventPublisher.publish(createdEvent(this, key, value));
    }

    private void publishUpdatedEvent(K key, V oldValue, V value) {
        entryEventPublisher.publish(updatedEvent(this, key, oldValue, value));
    }

    private void deleteIfWriteThrough(K key) {
        if (configuration.isWriteThrough()) {
            fallbackStorage.delete(key);
        }
    }

    private void publishExpiredEvent(K key, V oldValue) {
        entryEventPublisher.publish(expiredEvent(this, key, oldValue));
    }

    private void publishRemovedEvent(K key, V oldValue) {
        entryEventPublisher.publish(removedEvent(this, key, oldValue));
    }

    private void writeIfWriteThrough(Entry<K, V> entry) {
        if (entry != null && configuration.isWriteThrough()) {
            fallbackStorage.write(entry);
        }
    }

    private void assertNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("Current cache has been closed! No operation should be executed.");
        }
    }

    private static ExpiryPolicy getExpiryPolicy(CompleteConfiguration<?, ?> configuration) {
        Factory<ExpiryPolicy> expiryPolicyFactory = configuration.getExpiryPolicyFactory();
        if (expiryPolicyFactory == null) {
            expiryPolicyFactory = EternalExpiryPolicy::new;
        }
        return expiryPolicyFactory.create();
    }
}
