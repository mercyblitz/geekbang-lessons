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

import javax.cache.configuration.Factory;
import javax.cache.event.*;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class TestCacheEntryListener<K, V> implements CacheEntryCreatedListener<K, V>, CacheEntryUpdatedListener<K, V>,
        CacheEntryExpiredListener<K, V>, CacheEntryRemovedListener<K, V>, Factory<CacheEntryListener<K, V>> {

    @Override
    public void onCreated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        println("onCreated", cacheEntryEvents);
    }

    @Override
    public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        println("onExpired", cacheEntryEvents);
    }

    @Override
    public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        println("onRemoved", cacheEntryEvents);
    }

    @Override
    public void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        println("onUpdated", cacheEntryEvents);
    }

    @Override
    public CacheEntryListener<K, V> create() {
        return this;
    }

    private void println(String source, Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) {
        System.out.printf("[Thread : %s] %s - %s\n", Thread.currentThread().getName(), source, cacheEntryEvents);
    }
}
