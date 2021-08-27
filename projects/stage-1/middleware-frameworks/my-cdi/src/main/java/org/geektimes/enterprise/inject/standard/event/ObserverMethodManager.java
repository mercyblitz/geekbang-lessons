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
import org.geektimes.commons.util.PriorityComparator;
import org.geektimes.enterprise.inject.standard.MethodParameterInjectionPoint;
import org.geektimes.enterprise.inject.standard.ReflectiveAnnotatedMethod;
import org.geektimes.enterprise.inject.standard.ReflectiveAnnotatedParameter;
import org.geektimes.enterprise.inject.standard.ReflectiveObserverMethod;
import org.geektimes.enterprise.inject.standard.beans.StandardBeanManager;

import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.enterprise.inject.spi.*;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
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
public class ObserverMethodManager implements Event<Object> {

    /**
     * Key is Event type
     */
    private final Map<Type, Set<ObserverMethod>> storage;

    private final StandardBeanManager standardBeanManager;

    private final ObserverMethodDiscoverer observerMethodDiscoverer;

    public ObserverMethodManager(StandardBeanManager standardBeanManager) {
        this(new ConcurrentHashMap<>(), standardBeanManager);
    }

    protected ObserverMethodManager(Map<Type, Set<ObserverMethod>> storage, StandardBeanManager standardBeanManager) {
        this.storage = storage;
        this.standardBeanManager = standardBeanManager;
        this.observerMethodDiscoverer = new ReflectiveObserverMethodDiscoverer(standardBeanManager);
    }

    public void registerObserverMethods(Object beanInstance) {
        observerMethodDiscoverer.getObserverMethods(beanInstance, beanInstance.getClass())
                .forEach(this::registerObserverMethod);
    }

    public void registerObserverMethods(Bean bean) {
        observerMethodDiscoverer.getObserverMethods(bean, bean.getBeanClass())
                .forEach(this::registerObserverMethod);
    }

    public void registerObserverMethod(ObserverMethod observerMethod) {
        Type observerType = observerMethod.getObservedType();
        Set<Type> eventTypes = getAllTypes(observerType);
        fireProcessInjectionPointEvents(observerMethod);
        fireProcessObserverMethodEvent(observerMethod);
        eventTypes.forEach(eventType -> {
            Set<ObserverMethod> observerMethods = storage.computeIfAbsent(eventType,
                    k -> new TreeSet<>(PriorityComparator.INSTANCE));
            observerMethods.add(observerMethod);
        });
    }

    private void fireProcessInjectionPointEvents(ObserverMethod observerMethod) {
        List<InjectionPoint> injectionPoints = createInjectionPoints(observerMethod);
        injectionPoints.forEach(this::fireProcessInjectionPointEvent);
    }

    private void fireProcessInjectionPointEvent(InjectionPoint injectionPoint) {
        fireProcessInjectionPointEvent(new ProcessInjectionPointEvent(injectionPoint, standardBeanManager));
    }

    public void fireProcessInjectionPointEvent(ProcessInjectionPoint processInjectionPoint) {
        fire(processInjectionPoint);
    }

    private void fireProcessObserverMethodEvent(ObserverMethod observerMethod) {
        fire(new ProcessObserverMethodEvent(observerMethod, standardBeanManager));
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
        resolveObserverMethods(event)
                .stream()
                .filter(m -> async == m.isAsync())
                .forEach(observerMethod -> {
                    EventContext eventContext = new DefaultEventContext(
                            event,
                            observerMethod.getObservedType(),
                            observerMethod.getObservedQualifiers(),
                            createObservedInjectionPoint(observerMethod));
                    observerMethod.notify(eventContext);
                });
    }

    private InjectionPoint createObservedInjectionPoint(ObserverMethod observerMethod) {
        if (observerMethod instanceof ReflectiveObserverMethod) {
            ReflectiveObserverMethod reflectiveObserverMethod = (ReflectiveObserverMethod) observerMethod;
            ObserverMethodParameter observedParameter = reflectiveObserverMethod.getObservedParameter();
            return createInjectionPoint(observedParameter, reflectiveObserverMethod);
        }
        return null;
    }

    private List<InjectionPoint> createInjectionPoints(ObserverMethod observerMethod) {
        List<InjectionPoint> injectionPoints = new LinkedList<>();
        if (observerMethod instanceof ReflectiveObserverMethod) {
            ReflectiveObserverMethod method = (ReflectiveObserverMethod) observerMethod;
            List<ObserverMethodParameter> parameters = method.getObserverMethodParameters();
            for (ObserverMethodParameter parameter : parameters) {
                injectionPoints.add(createInjectionPoint(parameter, method));
            }
        }
        return injectionPoints;
    }

    private InjectionPoint createInjectionPoint(ObserverMethodParameter observedParameter, ReflectiveObserverMethod observerMethod) {
        Parameter parameter = observedParameter.getParameter();
        int index = observedParameter.getIndex();
        AnnotatedMethod method = new ReflectiveAnnotatedMethod(observerMethod.getMethod());
        AnnotatedParameter annotatedParameter = new ReflectiveAnnotatedParameter(parameter, index, method);
        return new MethodParameterInjectionPoint(annotatedParameter);
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
            subRepository.put(type, resolveObserverMethods(eventType, qualifiers));
        });
        return (Event<U>) new ObserverMethodManager(subRepository, standardBeanManager);
    }
}
