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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In Memory no-thread-safe {@link Cache}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
public class InMemoryCache<K, V> extends AbstractCache<K, V> {

    private final Map<K, V> cache;

    public InMemoryCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        super(cacheManager, cacheName, configuration);
        this.cache = new HashMap<>();
    }

    @Override
    protected V doGet(K key) throws CacheException, ClassCastException {
        return cache.get(key);
    }

    @Override
    protected void doPut(K key, V value) throws CacheException, ClassCastException {
        cache.put(key, value);
    }

    @Override
    protected boolean doRemove(K key) throws CacheException, ClassCastException {
        return cache.remove(key) != null;
    }

    @Override
    protected void doClear() throws CacheException {
        cache.clear();
    }

    @Override
    protected Iterator<Entry<K, V>> newIterator() {
        return cache.entrySet().stream().map(EntryAdapter::of).iterator();
    }

}
