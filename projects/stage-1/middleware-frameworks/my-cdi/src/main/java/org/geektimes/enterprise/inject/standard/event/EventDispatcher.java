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
package org.geektimes.enterprise.inject.standard.event;


import org.geektimes.commons.function.Streams;

import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.geektimes.commons.reflect.util.TypeUtils.getAllTypes;

/**
 * {@link Event} Dispatcher
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class EventDispatcher implements Event<Object> {

    /**
     * Key is Event type
     */
    private final ConcurrentMap<Type, Set<ObserverMethod>> storage;

    public EventDispatcher() {
        this(new ConcurrentHashMap<>());
    }

    EventDispatcher(ConcurrentMap<Type, Set<ObserverMethod>> storage) {
        this.storage = storage;
    }

    @Override
    public void fire(Object event) {
        Set<ObserverMethod> observerMethods = resolveObserverMethods(event);
        observerMethods.forEach(observerMethod -> observerMethod.notify(event));
    }

    @Override
    public <U> CompletionStage<U> fireAsync(U event) {
        return null;
    }

    @Override
    public <U> CompletionStage<U> fireAsync(U event, NotificationOptions options) {
        return null;
    }

    @Override
    public Event<Object> select(Annotation... qualifiers) {
        return select(Object.class, qualifiers);
    }

    @Override
    public <U> Event<U> select(Class<U> subtype, Annotation... qualifiers) {
        return select((Type) subtype, qualifiers);
    }

    @Override
    public <U> Event<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
        return select(subtype.getRawType(), qualifiers);
    }

    public <U> Event<U> select(Type eventType, Annotation... qualifiers) {
        ConcurrentMap<Type, Set<ObserverMethod>> subRepository = new ConcurrentHashMap<>();
        getAllTypes(eventType).forEach(type -> {
            subRepository.put(type, resolveObserverMethods(eventType, qualifiers));
        });
        return (Event<U>) new EventDispatcher(subRepository);
    }

    public EventDispatcher addObserverMethod(ObserverMethod observerMethod) {
        Type observerType = observerMethod.getObservedType();
        Set<Type> eventTypes = getAllTypes(observerType);
        eventTypes.forEach(eventType -> {
            Set<ObserverMethod> observerMethods = storage.computeIfAbsent(eventType, k -> new LinkedHashSet<>());
            observerMethods.add(observerMethod);
        });
        return this;
    }

    public <T> Set<ObserverMethod> resolveObserverMethods(T event, Annotation... qualifiers) {
        return resolveObserverMethods(event.getClass(), qualifiers);
    }

    public <T> Set<ObserverMethod> resolveObserverMethods(Class<T> eventType, Annotation... qualifiers) {
        List<Annotation> qualifiersList = asList(qualifiers);
        Set<ObserverMethod> observerMethods = storage.getOrDefault(eventType, emptySet());
        return Streams.filterSet(observerMethods, observerMethod -> {
            Set<Annotation> observedQualifiers = observerMethod.getObservedQualifiers();
            return observedQualifiers.containsAll(qualifiersList);
        });
    }
}
