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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableMap;

/**
 * The adapter of {@link ConditionalCacheEntryEventListener} based on {@link CacheEntryListenerConfiguration}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CacheEntryListenerConfiguration
 * @since 1.0
 */
public class CacheEntryEventListenerAdapter<K, V> implements ConditionalCacheEntryEventListener<K, V> {

    private static List<Object> eventTypesAndHandleMethodNames = asList(
            EventType.CREATED, "onCreated",
            EventType.UPDATED, "onUpdated",
            EventType.EXPIRED, "onExpired",
            EventType.REMOVED, "onRemoved"
    );

    private final CacheEntryListenerConfiguration<K, V> configuration;

    private final CacheEntryEventFilter<? super K, ? super V> cacheEntryEventFilter;

    private final CacheEntryListener<? super K, ? super V> cacheEntryListener;

    private final Map<EventType, Method> eventTypeMethods;

    private final Executor executor;

    public CacheEntryEventListenerAdapter(CacheEntryListenerConfiguration<K, V> configuration) {
        this.configuration = configuration;
        this.cacheEntryEventFilter = getCacheEntryEventFilter(configuration);
        this.cacheEntryListener = configuration.getCacheEntryListenerFactory().create();
        this.eventTypeMethods = determineEventTypeMethods(cacheEntryListener);
        this.executor = getExecutor(configuration);
    }

    @Override
    public boolean supports(CacheEntryEvent<? extends K, ? extends V> event) {
        return supportsEventType(event) && cacheEntryEventFilter.evaluate(event);
    }

    private boolean supportsEventType(CacheEntryEvent<? extends K, ? extends V> event) {
        return getSupportedEventTypes().contains(event.getEventType());
    }

    @Override
    public void onEvent(CacheEntryEvent<? extends K, ? extends V> event) {
        if (!supports(event)) {
            return;
        }

        EventType eventType = event.getEventType();
        Method handleMethod = eventTypeMethods.get(eventType);

        executor.execute(() -> {
            try {
                handleMethod.invoke(cacheEntryListener, singleton(event));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new CacheEntryListenerException(e);
            }
        });

    }

    @Override
    public Set<EventType> getSupportedEventTypes() {
        return eventTypeMethods.keySet();
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public int hashCode() {
        return configuration.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CacheEntryEventListenerAdapter)) {
            return false;
        }
        CacheEntryEventListenerAdapter another = (CacheEntryEventListenerAdapter) object;
        return this.configuration.equals(another.configuration);
    }

    private CacheEntryEventFilter<? super K, ? super V> getCacheEntryEventFilter(CacheEntryListenerConfiguration<K, V> configuration) {
        Factory<CacheEntryEventFilter<? super K, ? super V>> factory = configuration.getCacheEntryEventFilterFactory();
        CacheEntryEventFilter<? super K, ? super V> filter = null;

        if (factory != null) {
            filter = factory.create();
        }

        if (filter == null) {
            // When null no filtering is applied and all appropriate events are notified.
            filter = e -> true;
        }

        return filter;
    }

    private Map<EventType, Method> determineEventTypeMethods(CacheEntryListener<? super K, ? super V> cacheEntryListener) {
        Map<EventType, Method> eventTypeMethods = new HashMap<>(EventType.values().length);
        Class<?> cacheEntryListenerClass = cacheEntryListener.getClass();
        for (int i = 0; i < eventTypesAndHandleMethodNames.size(); ) {
            EventType eventType = (EventType) eventTypesAndHandleMethodNames.get(i++);
            String handleMethodName = (String) eventTypesAndHandleMethodNames.get(i++);
            try {
                Method handleMethod = cacheEntryListenerClass.getMethod(handleMethodName, Iterable.class);
                if (handleMethod != null) {
                    eventTypeMethods.put(eventType, handleMethod);
                }
            } catch (NoSuchMethodException ignored) {
            }

        }
        return unmodifiableMap(eventTypeMethods);
    }
    
    private Executor getExecutor(CacheEntryListenerConfiguration<K, V> configuration) {
        Executor executor = null;
        if (configuration.isSynchronous()) {
            executor = Runnable::run;
        } else {
            executor = ForkJoinPool.commonPool();
        }
        return executor;
    }
}
