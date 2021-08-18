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


import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.enterprise.inject.spi.EventContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.geektimes.commons.reflect.util.TypeUtils.getAllTypes;

/**
 * {@link Event} Dispatcher
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class EventDispatcher implements Event<Object> {

    private final InjectionPoint injectionPoint;

    private final ObserverMethodRepository repository;

    public EventDispatcher(ObserverMethodRepository repository) {
        this(repository, null);
    }

    public EventDispatcher(ObserverMethodRepository repository,
                           InjectionPoint injectionPoint) {
        this.repository = repository;
        this.injectionPoint = injectionPoint;
    }

    @Override
    public void fire(Object event) {
        fire(event, false);
    }

    @Override
    public <U> CompletionStage<U> fireAsync(U event) {
        return fireAsync(event, ImmutableAsyncNotificationOptions.INSTANCE);
    }

    @Override
    public <U> CompletionStage<U> fireAsync(U event, NotificationOptions options) {
        return CompletableFuture.supplyAsync(() -> event, options.getExecutor())
                .thenApplyAsync(e -> {
                    fire(e, true);
                    return e;
                }); // TODO Exception Handler
    }

    protected void fire(Object event, boolean async) {
        repository.resolveObserverMethods(event)
                .stream()
                .filter(m -> async == m.isAsync())
                .forEach(observerMethod -> {
                    EventContext eventContext = new DefaultEventContext(
                            event,
                            observerMethod.getObservedType(),
                            observerMethod.getObservedQualifiers(),
                            injectionPoint);
                    observerMethod.notify(eventContext);
                });
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
        Map<Type, Set<ObserverMethod>> subRepository = new LinkedHashMap<>();
        getAllTypes(eventType).forEach(type -> {
            subRepository.put(type, repository.resolveObserverMethods(eventType, qualifiers));
        });
        return (Event<U>) new EventDispatcher(new ObserverMethodRepository(subRepository), injectionPoint);
    }

}
