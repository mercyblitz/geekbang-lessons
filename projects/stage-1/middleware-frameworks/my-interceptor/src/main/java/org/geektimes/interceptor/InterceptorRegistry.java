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
package org.geektimes.interceptor;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Annotation;
import java.util.ServiceLoader;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * The registry of {@link Interceptor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface InterceptorRegistry {

    void registerInterceptorClass(Class<?> interceptorClass);

    default void registerInterceptorClasses(Class<?> interceptorClass, Class<?>... otherInterceptorClasses) {
        registerInterceptorClass(interceptorClass);
        registerInterceptorClasses(otherInterceptorClasses);
    }

    default void registerInterceptorClasses(Class<?>[] interceptorClasses) {
        registerInterceptorClasses(asList(interceptorClasses));
    }

    default void registerInterceptorClasses(Iterable<Class<?>> interceptorClasses) {
        interceptorClasses.forEach(this::registerInterceptorClass);
    }

    void registerInterceptor(Object interceptor);

    default void registerInterceptors(Object interceptor, Object... otherInterceptors) {
        registerInterceptor(interceptor);
        registerInterceptors(otherInterceptors);
    }

    default void registerInterceptors(Object[] interceptors) {
        registerInterceptors(asList(interceptors));
    }

    default void registerInterceptors(Iterable<?> interceptors) {
        interceptors.forEach(this::registerInterceptor);
    }

    default void registerDiscoveredInterceptors() {
        registerInterceptors(ServiceLoader.load(Interceptor.class));
    }

    /**
     * Gets the {@linkplain InterceptorBinding interceptor bindings} of the interceptor.
     *
     * @return the set of {@linkplain InterceptorBinding interceptor bindings}
     */
    Set<Annotation> getInterceptorBindings(Class<?> interceptorClass);

    void registerSyntheticInterceptorBinding(Class<? extends Annotation> interceptorBindingType);
}
