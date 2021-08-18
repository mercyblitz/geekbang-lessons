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
package org.geektimes.enterprise.inject.standard;

import org.geektimes.enterprise.inject.util.Qualifiers;

import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.EventContext;
import javax.enterprise.inject.spi.ObserverMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Set;

import static org.geektimes.enterprise.inject.util.Events.getObservedParameter;

/**
 * {@link ObserverMethod} based on Java Reflection.
 *
 * @param <T>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 */
public class ReflectiveObserverMethod<T> implements ObserverMethod<T> {

    private final Method method;

    private final Parameter observedParameter;

    private final Reception reception;

    private final TransactionPhase transactionPhase;

    private final boolean async;

    public ReflectiveObserverMethod(Method method) {
        this.method = method;
        this.observedParameter = getObservedParameter(method);
        Observes observes = observedParameter.getAnnotation(Observes.class);
        if (observes != null) {
            async = false;
            this.reception = observes.notifyObserver();
            this.transactionPhase = observes.during();
        } else {
            ObservesAsync observesAsync = observedParameter.getAnnotation(ObservesAsync.class);
            async = true;
            this.reception = observesAsync.notifyObserver();
            this.transactionPhase = null;
        }
    }

    @Override
    public Class<?> getBeanClass() {
        return method.getDeclaringClass();
    }

    @Override
    public Type getObservedType() {
        return observedParameter.getParameterizedType();
    }

    @Override
    public Set<Annotation> getObservedQualifiers() {
        return Qualifiers.getQualifiers(observedParameter);
    }

    @Override
    public Reception getReception() {
        return reception;
    }

    @Override
    public TransactionPhase getTransactionPhase() {
        return transactionPhase;
    }

    @Override
    public void notify(T event) {

    }

    @Override
    public void notify(EventContext<T> eventContext) {
        notify(eventContext.getEvent());
    }

    @Override
    public boolean isAsync() {
        return async;
    }
}
