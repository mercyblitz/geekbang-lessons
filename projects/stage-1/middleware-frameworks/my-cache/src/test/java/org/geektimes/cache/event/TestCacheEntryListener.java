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
package org.geektimes.cache.event;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.event.*;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class TestCacheEntryListener<K, V> implements CacheEntryCreatedListener<K, V>, CacheEntryUpdatedListener<K, V>,
        CacheEntryExpiredListener<K, V>, CacheEntryRemovedListener<K, V>, CacheEntryListenerConfiguration<K, V> {

    private CacheEntryEvent<K, V> cacheEntryEvent;

    private boolean oldValueRequired = true;

    private boolean synchronous = true;

    public TestCacheEntryListener() {
    }

    @Override
    public void onCreated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        handleEvents("onCreated", cacheEntryEvents);
    }

    @Override
    public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        handleEvents("onExpired", cacheEntryEvents);
    }

    @Override
    public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        handleEvents("onRemoved", cacheEntryEvents);
    }

    @Override
    public void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        handleEvents("onUpdated", cacheEntryEvents);
    }

    private void handleEvents(String source, Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) {
        cacheEntryEvents.forEach(event -> handleEvent(source, event));
    }

    private void handleEvent(String source, CacheEntryEvent<? extends K, ? extends V> event) {
        this.cacheEntryEvent = (CacheEntryEvent<K, V>) event;
        System.out.printf("[Thread : %s] %s - %s\n", Thread.currentThread().getName(), source, event);
    }

    public CacheEntryEvent<K, V> getCacheEntryEvent() {
        CacheEntryEvent<K, V> event = cacheEntryEvent;
        this.cacheEntryEvent = null;
        return event;
    }

    @Override
    public Factory<CacheEntryListener<? super K, ? super V>> getCacheEntryListenerFactory() {
        return () -> this;
    }

    @Override
    public boolean isOldValueRequired() {
        return oldValueRequired;
    }

    @Override
    public Factory<CacheEntryEventFilter<? super K, ? super V>> getCacheEntryEventFilterFactory() {
        return () -> e -> true;
    }

    @Override
    public boolean isSynchronous() {
        return synchronous;
    }

    public void setOldValueRequired(boolean oldValueRequired) {
        this.oldValueRequired = oldValueRequired;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }
}
