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

import org.geektimes.enterprise.inject.standard.ReflectiveObserverMethod;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Stream.of;
import static org.geektimes.enterprise.inject.util.Events.validateObserverMethod;

/**
 * {@link ObserverMethodDiscoverer} Implementation based on Java Reflection.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveObserverMethodDiscoverer implements ObserverMethodDiscoverer {

    private final BeanManager beanManager;

    public ReflectiveObserverMethodDiscoverer(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public List<ObserverMethod> getObserverMethods(Object bean, Class<?> beanType) {
        return unmodifiableList(of(beanType.getDeclaredMethods())
                .filter(this::isObserverMethod)
                .map(method -> createObserverMethod(bean, method))
                .collect(Collectors.toList()));
    }

    private ObserverMethod<?> createObserverMethod(Object beanInstance, Method method) {
        return new ReflectiveObserverMethod(beanInstance, method, beanManager);
    }

    /**
     * An observer method is a non-abstract method of a managed bean class or of an extension,
     * as defined in Container lifecycle events. An observer method may be either static or non-static.
     * <p>
     * Each observer method must have exactly one event parameter, of the same type as the event type it observes.
     *
     * @param method {@link Method}
     * @return <code>true</code> if observer method, <code>false</code> otherwise
     */
    private boolean isObserverMethod(Method method) {
        return validateObserverMethod(method);
    }

}
