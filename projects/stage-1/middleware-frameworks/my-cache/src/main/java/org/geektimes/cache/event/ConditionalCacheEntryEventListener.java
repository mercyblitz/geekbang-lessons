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

import javax.cache.Cache;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.event.*;
import java.util.EventListener;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * The conditional {@link EventListener} of {@link CacheEntryEvent}
 *
 * @param <K> the type of key
 * @param <V> the type of value
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CacheEntryListener
 * @see CacheEntryEventFilter
 * @see CacheEntryListenerConfiguration
 * @since 1.0
 */
public interface ConditionalCacheEntryEventListener<K, V> extends EventListener {

    /**
     * Determines current listener supports the given {@link CacheEntryEvent} or not.
     *
     * @param event
     * @return The effect of returning true is that listener will be invoked
     * @throws CacheEntryListenerException
     * @see CacheEntryEventFilter#evaluate(CacheEntryEvent)
     */
    boolean supports(CacheEntryEvent<? extends K, ? extends V> event) throws CacheEntryListenerException;

    /**
     * Called after one entry was raised by some event.
     *
     * @param event some event
     * @see CacheEntryCreatedListener
     * @see CacheEntryUpdatedListener
     * @see CacheEntryRemovedListener
     * @see CacheEntryExpiredListener
     */
    void onEvent(CacheEntryEvent<? extends K, ? extends V> event);

    /**
     * Called after one or more entries have been created.
     *
     * @param events one or more events
     * @see CacheEntryCreatedListener
     * @see CacheEntryUpdatedListener
     * @see CacheEntryRemovedListener
     * @see CacheEntryExpiredListener
     */
    default void onEvents(Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {
        events.forEach(this::onEvent);
    }

    /**
     * Get the supported {@link EventType event types}
     *
     * @return non-null
     */
    Set<EventType> getSupportedEventTypes();

    /**
     * The {@link Executor} is used to dispatch the {@link CacheEntryEvent}
     *
     * @return non-null
     * @see CacheEntryListenerConfiguration#isSynchronous()
     */
    Executor getExecutor();

    @Override
    int hashCode();

    @Override
    boolean equals(Object object);
}
