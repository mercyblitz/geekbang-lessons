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
import org.geektimes.interceptor.util.InterceptorUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.ExcludeClassInterceptors;
import javax.interceptor.ExcludeDefaultInterceptors;
import javax.interceptor.Interceptor;
import javax.interceptor.Interceptors;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.geektimes.commons.lang.util.AnnotationUtils.findAnnotation;
import static org.geektimes.commons.lang.util.AnnotationUtils.isMetaAnnotation;
import static org.geektimes.interceptor.util.InterceptorUtils.*;

/**
 * Default {@link InterceptorManager}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DefaultInterceptorManager implements InterceptorManager {

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
    private final Map<InterceptorBindings, SortedSet<Object>> bindingInterceptors;

    /**
     * The cache for {@link Method} or {@link Constructor} mapping the prioritized {@link Interceptor @Interceptor}
     * {@link Class classes}
     */
    private final Map<Executable, List<Class<?>>> executableInterceptorClasses;

    /**
     * The repository for {@link Interceptor @Interceptor} classes and instances
     */
    private final Map<Class<?>, Object> interceptorRepository;

    public DefaultInterceptorManager() {
        this.interceptorBindingTypes = new HashSet<>();
        this.interceptorInfoRepository = new TreeMap<>(PriorityComparator.INSTANCE);
        this.bindingInterceptors = new HashMap<>();
        this.executableInterceptorClasses = new HashMap<>();
        this.interceptorRepository = new HashMap<>();
        registerDefaultInterceptorBindingType();
    }

    @Override
    public void registerInterceptorClass(Class<?> interceptorClass) {
        validateInterceptorClass(interceptorClass);
        interceptorInfoRepository.computeIfAbsent(interceptorClass, InterceptorInfo::new);
    }

    @Override
    public void registerInterceptor(Object interceptor) {
        Class<?> interceptorClass = interceptor.getClass();
        registerInterceptorClass(interceptorClass);
        InterceptorInfo interceptorInfo = getInterceptorInfo(interceptorClass);
        registerRegularInterceptor(interceptorInfo, interceptor);
        registerLifecycleEventInterceptor(interceptorInfo, interceptor);
    }

    private void registerRegularInterceptor(InterceptorInfo interceptorInfo, Object interceptor) {
        InterceptorBindings interceptorBindings = interceptorInfo.getInterceptorBindings();
        registerInterceptor(interceptorBindings, interceptor);
    }

    private void registerLifecycleEventInterceptor(InterceptorInfo interceptorInfo, Object interceptor) {
        for (Method method : interceptorInfo.getPostConstructMethods()) {
            registerLifecycleEventInterceptor(method, PostConstruct.class, interceptor);
        }

        for (Method method : interceptorInfo.getPreDestroyMethods()) {
            registerLifecycleEventInterceptor(method, PreDestroy.class, interceptor);
        }
    }

    private void registerLifecycleEventInterceptor(Method method, Class<? extends Annotation> lifecycleAnnotationType, Object interceptor) {
        Annotation lifecycleAnnotation = method.getAnnotation(lifecycleAnnotationType);
        if (lifecycleAnnotation != null) {
            InterceptorBindings interceptorBindings = new InterceptorBindings(singleton(lifecycleAnnotation));
            registerInterceptor(interceptorBindings, interceptor);
        }
    }

    private void registerInterceptor(InterceptorBindings interceptorBindings, Object interceptor) {
        SortedSet<Object> interceptors = bindingInterceptors.computeIfAbsent(interceptorBindings, t -> new TreeSet<>(PriorityComparator.INSTANCE));
        interceptors.add(interceptor);
    }

    @Override
    public InterceptorInfo getInterceptorInfo(Class<?> interceptorClass) throws IllegalStateException {
        return interceptorInfoRepository.get(interceptorClass);
    }

    @Override
    public List<Object> resolveInterceptors(Executable executable, Object... defaultInterceptors) {
        List<Object> interceptors = new LinkedList<>();

        if (!isExcludedDefaultInterceptors(executable)) {
            // 1. Default interceptors are invoked first
            interceptors.addAll(asList(defaultInterceptors));
        }

        for (Class<?> interceptorClass : resolveInterceptorClasses(executable)) {
            Object interceptor = interceptorRepository.computeIfAbsent(interceptorClass, this::unwrap);
            interceptors.add(interceptor);
        }

        return interceptors;
    }

    @Override
    public List<Class<?>> resolveInterceptorClasses(Executable executable, Class<?>... defaultInterceptorClasses) {
        return executableInterceptorClasses.computeIfAbsent(executable, e -> {

            List<Class<?>> interceptorClasses = new LinkedList<>();

            if (!isExcludedDefaultInterceptors(executable)) {
                // 1. Default interceptors are invoked first
                interceptorClasses.addAll(asList(defaultInterceptorClasses));
            }

            // Resolve interceptors using @Interceptors
            // 2. Interceptors declared by applying the Interceptors annotation at class-level to the target
            // class are invoked next.
            // 3. Interceptors declared by applying the Interceptors annotation at method- or constructor-level
            // are invoked next.
            interceptorClasses.addAll(resolveAnnotatedInterceptorClasses(executable));

            // Resolve interceptors using Interceptor Bindings
            // 4. Interceptors declared using interceptor bindings are invoked next.
            interceptorClasses.addAll(resolveBindingInterceptorClasses(executable));

            return unmodifiableList(interceptorClasses);
        });
    }

    /**
     * The set of interceptor bindings for a method or constructor are those applied to the target class
     * combined with those applied at method level or constructor level.
     * Note that the interceptor bindings applied to the target class may include those inherited from
     * its superclasses.
     *
     * @param executable {@link Executable}
     * @return
     */
    private List<Class<?>> resolveBindingInterceptorClasses(Executable executable) {
        List<Class<?>> bindingInterceptorClasses = new LinkedList<>();

        for (InterceptorInfo interceptorInfo : interceptorInfoRepository.values()) {
            InterceptorBindings interceptorBindings = interceptorInfo.getInterceptorBindings();

            boolean matched = !interceptorBindings.getInterceptorBindingTypes().isEmpty();
            for (InterceptorBindingInfo interceptorBindingInfo : interceptorBindings) {
                Class<? extends Annotation> interceptorBindingType = interceptorBindingInfo.getDeclaredAnnotationType();
                Annotation annotation = searchAnnotation(executable, interceptorBindingType);
                matched &= interceptorBindingInfo.equals(annotation);
            }

            if (matched) {
                bindingInterceptorClasses.add(interceptorInfo.getInterceptorClass());
            }
        }

        // 5.2.1 Use of the Priority Annotation in Ordering Interceptors
        sortInterceptors(bindingInterceptorClasses);

        return bindingInterceptorClasses;
    }


    @Override
    public void registerInterceptorBindingType(Class<? extends Annotation> interceptorBindingType) {
        this.interceptorBindingTypes.add(interceptorBindingType);
    }

    @Override
    public void registerInterceptorBinding(Class<? extends Annotation> interceptorBindingType, Annotation... interceptorBindingDef) {
        // TODO
    }

    @Override
    public boolean isInterceptorBindingType(Class<? extends Annotation> annotationType) {
        boolean valid = false;
        if (interceptorBindingTypes.contains(annotationType)) {
            valid = true;
        } else if (isAnnotatedInterceptorBinding(annotationType)) {
            registerInterceptorBindingType(annotationType);
            valid = true;
        } else {
            for (Class<? extends Annotation> interceptorBindingType : interceptorBindingTypes) {
                if (isMetaAnnotation(annotationType, interceptorBindingType)) {
                    registerInterceptorBindingType(annotationType);
                    valid = true;
                }
            }
        }
        return valid;
    }

    @Override
    public Set<Class<?>> getInterceptorClasses() {
        return interceptorInfoRepository.keySet();
    }

    @Override
    public Set<Class<? extends Annotation>> getInterceptorBindingTypes() {
        return unmodifiableSet(interceptorBindingTypes);
    }

    @Override
    public boolean isInterceptorClass(Class<?> interceptorClass) {
        if (interceptorInfoRepository.containsKey(interceptorClass)) {
            return true;
        }
        return InterceptorUtils.isInterceptorClass(interceptorClass);
    }

    @Override
    public void validateInterceptorClass(Class<?> interceptorClass) throws NullPointerException, IllegalStateException {
        if (!interceptorInfoRepository.containsKey(interceptorClass)) {
            InterceptorUtils.validateInterceptorClass(interceptorClass);
        }
    }

    private void registerDefaultInterceptorBindingType() {
        registerInterceptorBindingType(PostConstruct.class);
        registerInterceptorBindingType(PreDestroy.class);
    }

    private boolean isExcludedDefaultInterceptors(Executable executable) {
        if (executable != null && !executable.isAnnotationPresent(ExcludeDefaultInterceptors.class)) {
            return findAnnotation(executable.getDeclaringClass(), ExcludeDefaultInterceptors.class) != null;
        }
        return false;
    }

    /**
     * Interceptors declared by applying the Interceptors annotation at class-level to the target
     * class are invoked next.
     * <p>
     * Interceptors declared by applying the Interceptors annotation at method- or constructor-level are invoked next.
     *
     * @param executable the intercepted of {@linkplain Method method} or {@linkplain Constructor constructor}
     * @return non-null
     * @see Interceptors
     * @see ExcludeClassInterceptors
     */
    private List<Class<?>> resolveAnnotatedInterceptorClasses(Executable executable) {
        Class<?> componentClass = executable.getDeclaringClass();

        List<Class<?>> interceptorClasses = new LinkedList<>();

        if (!executable.isAnnotationPresent(ExcludeClassInterceptors.class)) {
            Interceptors classInterceptors = searchAnnotation(componentClass, Interceptors.class);
            if (classInterceptors != null) {
                for (Class interceptorClass : classInterceptors.value()) {
                    interceptorClasses.add(interceptorClass);
                }
            }
        }

        Interceptors executableInterceptors = searchAnnotation(executable, Interceptors.class);
        if (executableInterceptors != null) {
            for (Class interceptorClass : executableInterceptors.value()) {
                interceptorClasses.add(interceptorClass);
            }
        }

        return interceptorClasses;
    }

}
