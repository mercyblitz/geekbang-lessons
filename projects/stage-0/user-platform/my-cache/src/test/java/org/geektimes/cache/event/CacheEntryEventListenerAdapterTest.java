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

import org.geektimes.cache.InMemoryCache;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.EventType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * {@link CacheEntryEventListenerAdapter} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
public class CacheEntryEventListenerAdapterTest {

    @Test
    public void testOnEvent() {
        TestCacheEntryListener listener = new TestCacheEntryListener();

        CacheEntryListenerConfiguration configuration = new MutableCacheEntryListenerConfiguration(listener, null, true, false);

        CacheEntryEventListenerAdapter listenerAdapter = new CacheEntryEventListenerAdapter(configuration);

        Cache cache = newCacheProxy();

        listenerAdapter.onEvent(new GenericCacheEntryEvent(cache, EventType.CREATED, "a", null, 1));
        listenerAdapter.onEvent(new GenericCacheEntryEvent(cache, EventType.UPDATED, "a", 1, 2));
        listenerAdapter.onEvent(new GenericCacheEntryEvent(cache, EventType.EXPIRED, "a", 1, 2));
        listenerAdapter.onEvent(new GenericCacheEntryEvent(cache, EventType.REMOVED, "a", 1, 2));
    }

    private Cache newCacheProxy() {
        return (Cache) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Cache.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("getName".equals(method.getName())) {
                    return "My Cache";
                }
                return null;
            }
        });
    }
}
