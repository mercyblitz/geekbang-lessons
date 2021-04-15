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
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;
import javax.cache.expiry.Duration;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.spi.CachingProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static javax.cache.expiry.CreatedExpiryPolicy.factoryOf;
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

    CachingProvider cachingProvider;

    CacheManager cacheManager;

    MutableConfiguration<String, Integer> config;

    private TestCacheEntryListener<String, Integer> cacheEntryListener;

    Cache<String, Integer> cache;

    private String cacheName = "testCache";

    String key = "test-key";

    Integer value = 2;


    @Before
    public void init() {
        cachingProvider = Caching.getCachingProvider();
        cacheManager = cachingProvider.getCacheManager();
        cacheEntryListener = new TestCacheEntryListener();
        config = new MutableConfiguration<String, Integer>()
                // Key and Value types
                .setTypes(String.class, Integer.class)
                .setReadThrough(true)
                .setWriteThrough(true)
                .setManagementEnabled(true)
                .setStatisticsEnabled(true)
                // CacheEntryListener
                .addCacheEntryListenerConfiguration(cacheEntryListener);
        // create the cache
        this.cache = cacheManager.createCache(cacheName, config);
    }

    @After
    public void clearCache() {
        cache.removeAll();
        assertFalse(cache.isClosed());
        cacheManager.destroyCache(cacheName);
        assertTrue(cache.isClosed());
        // DO NOTHING after close() method being invoked.
        cache.close();
        assertTrue(cache.isClosed());
        // Test operation after cache closed.
        assertThrows(IllegalStateException.class, () -> cache.put(key, value));
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
    }

    @Test
    public void testGetConfiguration() {
        CompleteConfiguration<String, Integer> configuration = (CompleteConfiguration) cache.getConfiguration(Configuration.class);
        assertTrue(configuration.isReadThrough());
        assertTrue(configuration.isWriteThrough());
        assertTrue(configuration.isStatisticsEnabled());
        assertTrue(configuration.isManagementEnabled());
        assertEquals(String.class, configuration.getKeyType());
        assertEquals(Integer.class, configuration.getValueType());
        assertNotNull(configuration.getCacheEntryListenerConfigurations());

        Class<?> clazz = String.class;
        assertThrows(IllegalArgumentException.class, () -> cache.getConfiguration((Class<Configuration>) clazz));
    }

    @Test
    public void testContainsKeyAndPut() {
        // test containsKey
        assertFalse(cache.containsKey(key));
        // test put if create Cache.Entry
        cache.put(key, value);
        assertCacheEntryEvent(EventType.CREATED, key, value, null);
        assertTrue(cache.containsKey(key));
    }

    void assertCacheEntryEvent(EventType eventType, String key, Integer value, Integer oldValue) {
        CacheEntryEvent<String, Integer> event = getCacheEntryEvent();
        assertEquals(cache, event.getSource());
        assertEquals(eventType, event.getEventType());
        assertEquals(key, event.getKey());
        assertEquals(value, event.getValue());
        assertEquals(oldValue, event.getOldValue());
    }

    @Test
    public void testPutIfAbsent() {
        // test putIfAbsent
        assertTrue(cache.putIfAbsent(key, value));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);
        assertFalse(cache.putIfAbsent(key, value));
        assertNull(getCacheEntryEvent());
    }

    @Test
    public void testPutAll() {
        // test putAll
        cache.putAll(singletonMap(key, value));
        assertTrue(cache.containsKey(key));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);
        assertNull(getCacheEntryEvent());
    }

    @Test
    public void testGetOps() {
        // test get
        assertNull(cache.get(key));

        // test getAndPut
        assertNull(cache.getAndPut(key, value));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);
        assertEquals(value, cache.getAndPut(key, value));
        assertCacheEntryEvent(EventType.UPDATED, key, value, value);

        // test getAndRemove
        assertEquals(value, cache.getAndRemove(key));
        assertCacheEntryEvent(EventType.REMOVED, key, value, value);

        // test getAndReplace
        assertNull(cache.getAndReplace(key, value));
        assertNull(getCacheEntryEvent());
        assertNull(cache.getAndPut(key, value));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);
        assertEquals(value, cache.getAndReplace(key, 1));
        assertCacheEntryEvent(EventType.UPDATED, key, 1, value);
        assertEquals(Integer.valueOf(1), cache.getAndReplace(key, value));
        assertCacheEntryEvent(EventType.UPDATED, key, value, 1);

        // test getAll
        assertEquals(singletonMap(key, value), cache.getAll(singleton(key)));
    }

    @Test
    public void testRemove() {
        cache.put(key, value);
        assertCacheEntryEvent(EventType.CREATED, key, value, null);

        // test remove
        assertTrue(cache.remove(key));
        assertCacheEntryEvent(EventType.REMOVED, key, value, value);

        cache.put(key, value);
        assertCacheEntryEvent(EventType.CREATED, key, value, null);

        assertFalse(cache.remove(key, 1));
        assertNull(getCacheEntryEvent());

        assertTrue(cache.remove(key, value));
        assertCacheEntryEvent(EventType.REMOVED, key, value, value);
    }

    @Test
    public void testRemoveAll() {
        // test removeAll
        assertTrue(cache.putIfAbsent(key, value));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);
        cache.removeAll();
        assertCacheEntryEvent(EventType.REMOVED, key, value, value);
        assertFalse(cache.containsKey(key));

        assertTrue(cache.putIfAbsent(key, value));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);

        cache.removeAll(singleton(key));
        assertCacheEntryEvent(EventType.REMOVED, key, value, value);
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void testReplace() {
        cache.put(key, value);
        assertCacheEntryEvent(EventType.CREATED, key, value, null);

        // test replace
        assertFalse(cache.replace("#", 1));
        assertNull(getCacheEntryEvent());

        assertTrue(cache.replace(key, 1));
        assertCacheEntryEvent(EventType.UPDATED, key, 1, value);

        assertEquals(Integer.valueOf(1), cache.get(key));
        assertFalse(cache.replace(key, value, value));
        assertNull(getCacheEntryEvent());

        assertTrue(cache.replace(key, 1, value));
        assertCacheEntryEvent(EventType.UPDATED, key, value, 1);
    }

    @Test
    public void testIterator() {
        cache.put(key, value);
        Iterator<Cache.Entry<String, Integer>> iterator = cache.iterator();
        while (iterator.hasNext()) {
            Cache.Entry<String, Integer> entry = iterator.next();
            assertEquals(key, entry.getKey());
            assertEquals(value, entry.getValue());
        }
    }

    @Test
    public void testUnwrap() {
        assertEquals("", cache.unwrap(String.class));
    }

    @Test
    public void testLoadAll() {
        MutableConfiguration<String, Integer> config = new MutableConfiguration<>(this.config)
                .setCacheLoaderFactory(() -> new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws CacheLoaderException {
                        if (AbstractCacheTest.this.key.equals(key)) {
                            return 1;
                        }
                        return null;
                    }

                    @Override
                    public Map<String, Integer> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
                        return null;
                    }
                });

        Cache<String, Integer> cache = cacheManager.createCache("testCache1", config);

        AtomicBoolean completed = new AtomicBoolean(false);
        AtomicReference<Exception> exceptionReference = new AtomicReference();

        CompletionListener listener = new CompletionListener() {
            @Override
            public void onCompletion() {
                completed.set(true);
            }

            @Override
            public void onException(Exception e) {
                exceptionReference.set(e);
                onCompletion();
            }
        };

        // test isReadThrough == true
        boolean replaceExistingValues = true;
        cache.loadAll(singleton(key), replaceExistingValues, listener);
        while (!completed.get()) {
        }
        assertFalse(cache.containsKey(key));
        assertEquals(Integer.valueOf(1), cache.get(key)); // the value comes from CacheLoader
        assertNull(exceptionReference.get());

        // replaceExistingValues == false
        completed.set(false);
        replaceExistingValues = false;
        cache.loadAll(singleton(key), replaceExistingValues, listener);
        while (!completed.get()) {
        }
        assertTrue(cache.containsKey(key));
        assertEquals(Integer.valueOf(1), cache.get(key)); // the value comes from Cache
        assertNull(exceptionReference.get());

        // test isReadThrough == false
        cache.clear();
        completed.set(false);
        config.setReadThrough(false);
        cache.loadAll(singleton(key), replaceExistingValues, listener);
        while (!completed.get()) {
        }
        assertFalse(cache.containsKey(key));
        assertNull(cache.get(key));
        assertNull(exceptionReference.get());

        cache.clear();
        cache.close();

        // test Exception
        completed.set(false);
        config.setReadThrough(true);
        config.setCacheLoaderFactory(() -> new CacheLoader<String, Integer>() {
            @Override
            public Integer load(String key) throws CacheLoaderException {
                throw new CacheLoaderException("Testing...");
            }

            @Override
            public Map<String, Integer> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
                return null;
            }
        });

        cache = cacheManager.createCache("testCache-exception", config);

        cache.loadAll(singleton(key), replaceExistingValues, listener);
        while (!completed.get()) {
        }
        assertFalse(cache.containsKey(key));
        assertEquals("Testing...", exceptionReference.get().getMessage());

        cache.clear();
        cache.close();
    }

    @Test
    public void testInvoke() {

        // test MutableEntry(MutableEntryAdapter)
        cache.put(key, value);

        // test MutableEntry#getKey()
        Object result = cache.invoke(key, (entry, args) -> entry.getKey());
        assertEquals(key, result);

        // test MutableEntry#getValue()
        result = cache.invoke(key, (entry, args) -> entry.getValue());
        assertNull(result);
        // isReadThrough(true) -> (false)
        config.setReadThrough(false);
        result = cache.invoke(key, (entry, args) -> entry.getValue());
        assertEquals(value, result);

        result = cache.invoke(key, (entry, args) -> {
            entry.setValue(1);
            return null;
        });
        assertNull(result);
        assertEquals(Integer.valueOf(1), cache.get(key));

        // test MutableEntry#exists()
        result = cache.invoke(key, (entry, args) -> entry.exists());
        assertEquals(Boolean.TRUE, result);

        // test MutableEntry#remove()
        result = cache.invoke(key, (entry, args) -> {
            entry.remove();
            return null;
        });
        assertNull(result);
        assertFalse(cache.containsKey(key));

        // test MutableEntry#unwrap()
        result = cache.invoke(key, (entry, args) -> entry.unwrap(String.class));
        assertEquals("", result);
    }

    @Test
    public void testInvokeAll() {
        Map<String, EntryProcessorResult<Object[]>> resultMap = cache.invokeAll(singleton(key), (entry, args) -> args, "1", "2");
        assertTrue(resultMap.containsKey(key));
        assertArrayEquals(new String[]{"1", "2"}, resultMap.get(key).get());
    }

    @Test
    public void testDeregisterCacheEntryListener() {
        cache.deregisterCacheEntryListener(cacheEntryListener);
    }

    @Test
    public void testExpiryPolicy() throws Exception {
        MutableConfiguration<String, Integer> config = new MutableConfiguration<>(this.config)
                .setExpiryPolicyFactory(factoryOf(Duration.ZERO));
        cacheManager.destroyCache(cacheName);
        cache = cacheManager.createCache(cacheName, config);

        // test CreatedExpiryPolicy with Duration.ZERO
        cache.put(key, value);
        assertFalse(cache.containsKey(key));
        assertNull(cache.get(key));
        assertNull(getCacheEntryEvent());

        // test CreatedExpiryPolicy with Duration 1 second
        config = new MutableConfiguration<>(this.config)
                .setExpiryPolicyFactory(factoryOf(new Duration(TimeUnit.SECONDS, 1L)));
        cacheManager.destroyCache(cacheName);
        cache = cacheManager.createCache(cacheName, config);

        cache.put(key, value);
        assertTrue(cache.containsKey(key));
        assertEquals(value, cache.get(key));
        assertCacheEntryEvent(EventType.CREATED, key, value, null);

        Thread.sleep(TimeUnit.SECONDS.toMillis(2));

        assertTrue(cache.containsKey(key));
        assertNull(cache.get(key));
        assertCacheEntryEvent(EventType.EXPIRED, key, value, value);
    }

    private CacheEntryEvent<String, Integer> getCacheEntryEvent() {
        return cacheEntryListener.getCacheEntryEvent();
    }
}
