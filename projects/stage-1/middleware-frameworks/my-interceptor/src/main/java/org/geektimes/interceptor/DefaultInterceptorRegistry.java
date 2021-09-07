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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.Interceptor;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.*;
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
     * The {@link InterceptorInfo} Repository
     */
    private final Map<Class<?>, InterceptorInfo> interceptorInfoRepository;

    /**
     * The interceptor binding types map the sorted {@link Interceptor @Interceptor} instances
     */
    private final Map<InterceptorBindings, List<Object>> bindingInterceptors;

    public DefaultInterceptorRegistry() {
        this.interceptorBindingTypes = new LinkedHashSet<>();
        this.interceptorInfoRepository = new LinkedHashMap<>();
        this.bindingInterceptors = new LinkedHashMap<>();
        registerDefaultInterceptorBindingType();
    }

    @Override
    public void registerInterceptorClass(Class<?> interceptorClass) {
        interceptorInfoRepository.computeIfAbsent(interceptorClass, InterceptorInfo::new);
    }

    @Override
    public void registerInterceptor(Object interceptor) {
        Class<?> interceptorClass = interceptor.getClass();
        registerInterceptorClass(interceptorClass);
        InterceptorBindings interceptorBindings = getInterceptorBindings(interceptorClass);
        List<Object> interceptors = bindingInterceptors.computeIfAbsent(interceptorBindings, t -> new LinkedList<>());
        if (!interceptors.contains(interceptor)) {
            interceptors.add(interceptor);
            interceptors.sort(PriorityComparator.INSTANCE);
        }
    }

    @Override
    public InterceptorInfo getInterceptorInfo(Class<?> interceptorClass) throws IllegalStateException {
        return interceptorInfoRepository.get(interceptorClass);
    }

    @Override
    public List<Object> getInterceptors(AnnotatedElement interceptedElement) {
        InterceptorBindings interceptorBindings = resolveInterceptorBindings(interceptedElement);
        return unmodifiableList(bindingInterceptors.getOrDefault(interceptorBindings, emptyList()));
    }

    @Override
    public void registerInterceptorBindingType(Class<? extends Annotation> interceptorBindingType) {
        this.interceptorBindingTypes.add(interceptorBindingType);
    }

    @Override
    public boolean isInterceptorBindingType(Class<? extends Annotation> annotationType) {
        return isMetaAnnotation(annotationType, InterceptorBinding.class) ||
                interceptorBindingTypes.contains(annotationType);
    }

    public Set<Class<? extends Annotation>> getInterceptorBindingTypes() {
        return unmodifiableSet(interceptorBindingTypes);
    }

    private void registerDefaultInterceptorBindingType() {
        registerInterceptorBindingType(PostConstruct.class);
        registerInterceptorBindingType(PreDestroy.class);
    }

    private InterceptorBindings resolveInterceptorBindings(AnnotatedElement interceptedElement) {
        Set<Annotation> interceptorBindings = Stream.of(interceptedElement.getAnnotations())
                .filter(this::isInterceptorBinding)
                .collect(Collectors.toSet());
        return new InterceptorBindings(interceptorBindings);
    }
}
