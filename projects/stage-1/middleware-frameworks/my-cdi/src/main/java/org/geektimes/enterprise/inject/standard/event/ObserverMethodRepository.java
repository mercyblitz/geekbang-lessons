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
import org.geektimes.commons.reflect.util.ClassUtils;
import org.geektimes.commons.reflect.util.TypeUtils;

import javax.enterprise.inject.spi.ObserverMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.geektimes.commons.reflect.util.TypeUtils.getAllTypes;

/**
 * The repository of {@link ObserverMethod}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ObserverMethodRepository {

    /**
     * Key is Event type
     */
    private final Map<Type, Set<ObserverMethod>> storage;

    public ObserverMethodRepository() {
        this(new ConcurrentHashMap<>());
    }

    public ObserverMethodRepository(Map<Type, Set<ObserverMethod>> storage) {
        this.storage = storage;
    }

    public ObserverMethodRepository addObserverMethod(ObserverMethod observerMethod) {
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
        Set<ObserverMethod> allObserverMethods = new LinkedHashSet<>();
        Set<Type> subEventTypes = getAllTypes(eventType);
        subEventTypes.forEach(subEventType -> {
            Set<ObserverMethod> observerMethods = storage.getOrDefault(subEventType, emptySet());
            Set<ObserverMethod> matchedObserverMethods = Streams.filter(observerMethods,
                    observerMethod -> {
                        Class<?> observedClass = TypeUtils.asClass(observerMethod.getObservedType());
                        if (ClassUtils.isAssignableFrom(observedClass, eventType)) {
                            Set<Annotation> observedQualifiers = observerMethod.getObservedQualifiers();
                            return observedQualifiers.containsAll(qualifiersList);
                        }
                        return false;
                    });
            allObserverMethods.addAll(matchedObserverMethods);
        });
        return allObserverMethods;
    }
}
