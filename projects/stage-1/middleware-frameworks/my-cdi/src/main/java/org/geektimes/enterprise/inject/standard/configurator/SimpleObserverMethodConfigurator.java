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
package org.geektimes.enterprise.inject.standard.configurator;

import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.configurator.ObserverMethodConfigurator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Simple {@link ObserverMethodConfigurator} implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SimpleObserverMethodConfigurator<T> implements ObserverMethodConfigurator<T> {

    @Override
    public ObserverMethodConfigurator<T> read(Method method) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> read(AnnotatedMethod<?> method) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> read(ObserverMethod<T> method) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> beanClass(Class<?> type) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> observedType(Type type) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> addQualifier(Annotation qualifier) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> addQualifiers(Annotation... qualifiers) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> addQualifiers(Set<Annotation> qualifiers) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> qualifiers(Annotation... qualifiers) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> qualifiers(Set<Annotation> qualifiers) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> reception(Reception reception) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> transactionPhase(TransactionPhase transactionPhase) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> priority(int priority) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> notifyWith(EventConsumer<T> callback) {
        return null;
    }

    @Override
    public ObserverMethodConfigurator<T> async(boolean async) {
        return null;
    }
}
