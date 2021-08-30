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

import org.geektimes.commons.util.PriorityComparator;

import javax.interceptor.Interceptor;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Annotation;
import java.util.*;

import static java.util.Collections.*;
import static org.geektimes.commons.collection.util.CollectionUtils.ofSet;
import static org.geektimes.commons.lang.util.AnnotationUtils.getAllDeclaredAnnotations;
import static org.geektimes.commons.lang.util.AnnotationUtils.isMetaAnnotation;

/**
 * Default {@link InterceptorRegistry}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DefaultInterceptorRegistry implements InterceptorRegistry {

    /**
     * The supported annotation types of interceptor binding.
     */
    private final Set<Class<? extends Annotation>> interceptorBindingTypes;

    /**
     * The interceptors map interceptor bindings
     */
    private final Map<Class<?>, Set<Annotation>> interceptorBindingsMapping;

    /**
     * The interceptor binding types map the sorted {@link Interceptor @Interceptor} instances
     */
    private final Map<Class<? extends Annotation>, List<Object>> bindingInterceptors;

    public DefaultInterceptorRegistry() {
        this.interceptorBindingTypes = new LinkedHashSet<>();
        this.interceptorBindingsMapping = new LinkedHashMap<>();
        this.bindingInterceptors = new LinkedHashMap<>();
        registerDefaultInterceptorBindingType();
    }

    @Override
    public void registerInterceptorClass(Class<?> interceptorClass) {
        interceptorBindingsMapping.computeIfAbsent(interceptorClass, this::resolveInterceptorBindings);
    }

    @Override
    public void registerInterceptor(Object interceptor) {
        Class<?> interceptorClass = interceptor.getClass();
        registerInterceptorClass(interceptorClass);
        Set<Annotation> interceptorBindings = getInterceptorBindings(interceptorClass);
        interceptorBindings.stream()
                .map(Annotation::annotationType)
                .forEach(interceptorBindingType -> {
                    List<Object> interceptors = bindingInterceptors.computeIfAbsent(interceptorBindingType,
                            t -> new LinkedList<>());
                    if (!interceptors.contains(interceptor)) {
                        interceptors.add(interceptor);
                        interceptors.sort(PriorityComparator.INSTANCE);
                    }
                });
    }

    @Override
    public Set<Annotation> getInterceptorBindings(Class<?> interceptorClass) {
        return interceptorBindingsMapping.getOrDefault(interceptorClass, emptySet());
    }

    @Override
    public List<Object> getInterceptors(Class<? extends Annotation> interceptorBindingType) {
        return unmodifiableList(bindingInterceptors.getOrDefault(interceptorBindingType, emptyList()));
    }

    @Override
    public void registerInterceptorBindingType(Class<? extends Annotation> interceptorBindingType) {
        this.interceptorBindingTypes.add(interceptorBindingType);
    }

    private void registerDefaultInterceptorBindingType() {
        registerInterceptorBindingType(InterceptorBinding.class);
    }

    private Set<Annotation> resolveInterceptorBindings(Class<?> interceptorClass) {
        return ofSet(getAllDeclaredAnnotations(interceptorClass, this::isInterceptorBinding));
    }

    private boolean isInterceptorBinding(Annotation annotation) {
        return isMetaAnnotation(annotation, interceptorBindingTypes);
    }
}
