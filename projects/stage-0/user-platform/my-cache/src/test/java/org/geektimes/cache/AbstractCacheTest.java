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

import org.geektimes.cache.event.TestCacheEntryListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.geektimes.cache.configuration.ConfigurationUtils.cacheEntryListenerConfiguration;
import static org.geektimes.cache.configuration.ConfigurationUtils.immutableConfiguration;
import static org.junit.Assert.*;

/**
 * {@link AbstractCache} Test cases
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Cache
 * @since 1.0.0
 * Date : 2021-04-12
 */
public class AbstractCacheTest {

    Cache<String, Integer> cache;

    Configuration<String, Integer> configuration;

    @Before
    public void init() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        MutableConfiguration<String, Integer> config = new MutableConfiguration<String, Integer>()
                // Key and Value types
                .setTypes(String.class, Integer.class)
                // CacheEntryListener
                .addCacheEntryListenerConfiguration(cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));
        // create the cache
        this.cache = cacheManager.createCache("testCache", config);
        this.configuration = config;
    }

    @After
    public void clearCache() {
        cache.removeAll();
    }

    /**
     * Test Meta-data methods :
     * <ul>
     *     <li>{@link Cache#getName()}</li>
     *     <li>{@link Cache#getCacheManager()}</li>
     *     <li>{@link Cache#getConfiguration(Class)}</li>
     * </ul>
     */
    @Test
    public void testGetMetadata() {
        assertEquals("testCache", cache.getName());
        assertEquals(Caching.getCachingProvider().getCacheManager(), cache.getCacheManager());
        assertEquals(immutableConfiguration(configuration), cache.getConfiguration(Configuration.class));
    }

    @Test
    public void testBasicOps() {
        String key = "test-key";
        Integer value = 1;

        // test containsKey
        assertFalse(cache.containsKey(key));

        // test put if create Cache.Entry
        cache.put(key, value);
        assertTrue(cache.containsKey(key));

        // test get
        assertEquals(value, cache.get(key));

        // test getAll
        assertEquals(singletonMap(key, value), cache.getAll(singleton(key)));

        // test getAndPut
        assertEquals(Integer.valueOf(2), cache.getAndPut(key, 2));

        // test getAndRemove
        assertEquals(Integer.valueOf(2), cache.getAndRemove(key));

        // test putIfAbsent
        assertTrue(cache.putIfAbsent(key, value));
        assertFalse(cache.putIfAbsent(key, value));


        // test replace
        value = 2;
        assertTrue(cache.replace(key, 1, value));
        assertTrue(cache.replace(key, value));
    }
}
