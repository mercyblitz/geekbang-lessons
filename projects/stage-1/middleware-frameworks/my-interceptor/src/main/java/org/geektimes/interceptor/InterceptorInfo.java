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

import org.geektimes.interceptor.util.Interceptors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.AroundConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static java.lang.String.format;
import static org.geektimes.commons.collection.util.CollectionUtils.ofSet;
import static org.geektimes.commons.lang.util.AnnotationUtils.getAllDeclaredAnnotations;
import static org.geektimes.commons.reflect.util.MethodUtils.getAllDeclaredMethods;

/**
 * Interceptor Info Metadata class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class InterceptorInfo {

    private final Class<?> interceptorClass;

    private final Method aroundInvokeMethod;

    private final Method aroundTimeoutMethod;

    private final Method aroundConstructMethod;

    private final Method postConstructMethod;

    private final Method preDestroyMethod;

    private final Set<Annotation> interceptorBindings;

    private final InterceptorRegistry interceptorRegistry;

    public InterceptorInfo(Class<?> interceptorClass) {
        this.interceptorClass = interceptorClass;
        Map<Class<? extends Annotation>, Method> interceptionMethods = resolveInterceptionMethods();
        this.aroundInvokeMethod = interceptionMethods.remove(AroundInvoke.class);
        this.aroundTimeoutMethod = interceptionMethods.remove(AroundTimeout.class);
        this.aroundConstructMethod = interceptionMethods.remove(AroundConstruct.class);
        this.postConstructMethod = interceptionMethods.remove(PostConstruct.class);
        this.preDestroyMethod = interceptionMethods.remove(PreDestroy.class);
        this.interceptorBindings = resolveInterceptorBindings();
        this.interceptorRegistry = InterceptorRegistry.getInstance(interceptorClass.getClassLoader());
    }

    private Map<Class<? extends Annotation>, Method> resolveInterceptionMethods() throws IllegalStateException {
        Set<Method> methods = getAllDeclaredMethods(interceptorClass);
        Map<Class<? extends Annotation>, Method> interceptionMethods = new HashMap<>();

        for (Method method : methods) {
            resolveInterceptionMethod(method, AroundInvoke.class, Interceptors::isAroundInvokeMethod, interceptionMethods);
            resolveInterceptionMethod(method, AroundTimeout.class, Interceptors::isAroundTimeoutMethod, interceptionMethods);
            resolveInterceptionMethod(method, AroundConstruct.class, Interceptors::isAroundConstructMethod, interceptionMethods);
            resolveInterceptionMethod(method, PostConstruct.class, Interceptors::isPostConstructMethod, interceptionMethods);
            resolveInterceptionMethod(method, PreDestroy.class, Interceptors::isPreDestroyMethod, interceptionMethods);
        }

        return interceptionMethods;
    }

    private void resolveInterceptionMethod(Method method, Class<? extends Annotation> annotationType,
                                           Predicate<Method> isInterceptionMethod,
                                           Map<Class<? extends Annotation>, Method> interceptionMethods) {
        if (isInterceptionMethod.test(method)) {
            if (interceptionMethods.putIfAbsent(annotationType, method) != null) {
                throw interceptionMethodDefinitionException(annotationType);
            }
        }
    }

    private IllegalStateException interceptionMethodDefinitionException(Class<? extends Annotation> annotationType) {
        throw new IllegalStateException(format("There is only one @%s method is declared in the interceptor class[%s]",
                annotationType.getName(), interceptorClass.getName()));
    }

    private Set<Annotation> resolveInterceptorBindings() {
        return ofSet(getAllDeclaredAnnotations(interceptorClass, interceptorRegistry::isInterceptorBinding));
    }

    public Class<?> getInterceptorClass() {
        return interceptorClass;
    }

    public Method getAroundInvokeMethod() {
        return aroundInvokeMethod;
    }

    public Method getAroundTimeoutMethod() {
        return aroundTimeoutMethod;
    }

    public Method getAroundConstructMethod() {
        return aroundConstructMethod;
    }

    public Method getPostConstructMethod() {
        return postConstructMethod;
    }

    public Method getPreDestroyMethod() {
        return preDestroyMethod;
    }

    public Set<Annotation> getInterceptorBindings() {
        return interceptorBindings;
    }

    public InterceptorRegistry getInterceptorRegistry() {
        return interceptorRegistry;
    }
}
