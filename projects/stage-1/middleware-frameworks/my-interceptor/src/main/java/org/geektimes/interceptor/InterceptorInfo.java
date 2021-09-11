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

import org.geektimes.interceptor.util.InterceptorUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.AroundConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.String.format;
import static org.geektimes.commons.lang.util.AnnotationUtils.getAllDeclaredAnnotations;
import static org.geektimes.commons.reflect.util.ClassUtils.getAllClasses;
import static org.geektimes.interceptor.util.InterceptorUtils.validateInterceptorClass;

/**
 * Interceptor Info Metadata class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class InterceptorInfo {

    private final InterceptorManager interceptorManager;

    private final Class<?> interceptorClass;

    /**
     * If an interceptor class declared using interceptor bindings has superclasses,
     * interceptor methods declared in the interceptor class’s superclasses are
     * invoked before the interceptor method declared in the interceptor class itself,
     * most general superclass first.
     */
    private final Collection<Method> aroundInvokeMethods;

    private final Collection<Method> aroundTimeoutMethods;

    private final Collection<Method> aroundConstructMethods;

    private final Collection<Method> postConstructMethods;

    private final Collection<Method> preDestroyMethods;

    private final InterceptorBindings interceptorBindings;

    public InterceptorInfo(Class<?> interceptorClass) {
        this.interceptorManager = InterceptorManager.getInstance(interceptorClass.getClassLoader());
        this.interceptorClass = interceptorClass;
        this.aroundInvokeMethods = new LinkedList<>();
        this.aroundTimeoutMethods = new LinkedList<>();
        this.aroundConstructMethods = new LinkedList<>();
        this.postConstructMethods = new LinkedList<>();
        this.preDestroyMethods = new LinkedList<>();
        resolveInterceptionMethods();
        this.interceptorBindings = resolveInterceptorBindings();
    }

    private void resolveInterceptionMethods() throws IllegalStateException {
        Set<Class<?>> allClasses = getAllClasses(interceptorClass, true, t -> !Object.class.equals(t));

        for (Class<?> declaringClass : allClasses) {
            Map<Class<? extends Annotation>, Method> interceptionMethods = new HashMap<>();
            for (Method method : declaringClass.getDeclaredMethods()) {
                resolveInterceptionMethod(method, AroundInvoke.class, InterceptorUtils::isAroundInvokeMethod,
                        interceptionMethods, aroundInvokeMethods::add);

                resolveInterceptionMethod(method, AroundTimeout.class, InterceptorUtils::isAroundTimeoutMethod,
                        interceptionMethods, aroundTimeoutMethods::add);

                resolveInterceptionMethod(method, AroundConstruct.class, InterceptorUtils::isAroundConstructMethod,
                        interceptionMethods, aroundConstructMethods::add);

                resolveInterceptionMethod(method, PostConstruct.class, InterceptorUtils::isPostConstructMethod,
                        interceptionMethods, postConstructMethods::add);

                resolveInterceptionMethod(method, PreDestroy.class, InterceptorUtils::isPreDestroyMethod,
                        interceptionMethods, preDestroyMethods::add);
            }
            interceptionMethods.clear();
        }

    }

    private void resolveInterceptionMethod(Method method,
                                           Class<? extends Annotation> annotationType,
                                           Predicate<Method> isInterceptionMethod,
                                           Map<Class<? extends Annotation>, Method> interceptionMethods,
                                           Consumer<Method> interceptionMethodConsumer) {
        if (isInterceptionMethod.test(method)) {
            if (interceptionMethods.putIfAbsent(annotationType, method) == null) {
                interceptionMethodConsumer.accept(method);
            } else {
                throw interceptionMethodDefinitionException(method, annotationType);
            }
        }
    }

    private IllegalStateException interceptionMethodDefinitionException(Method method, Class<? extends Annotation> annotationType) {
        throw new IllegalStateException(format("There is only one @%s method[%s] is declared in the interceptor class[%s]",
                annotationType.getName(), method.toString(), method.getDeclaringClass().getName()));
    }

    private InterceptorBindings resolveInterceptorBindings() {
        return new InterceptorBindings(getAllDeclaredAnnotations(interceptorClass, interceptorManager::isInterceptorBinding));
    }

    Collection<Method> getAroundInvokeMethods() {
        return aroundInvokeMethods;
    }

    Collection<Method> getAroundTimeoutMethods() {
        return aroundTimeoutMethods;
    }

    Collection<Method> getAroundConstructMethods() {
        return aroundConstructMethods;
    }

    Collection<Method> getPostConstructMethods() {
        return postConstructMethods;
    }

    Collection<Method> getPreDestroyMethods() {
        return preDestroyMethods;
    }

    public Class<?> getInterceptorClass() {
        return interceptorClass;
    }

    public boolean hasAroundInvokeMethod() {
        return !getAroundInvokeMethods().isEmpty();
    }

    public boolean hasAroundTimeoutMethod(){
        return !getAroundTimeoutMethods().isEmpty();
    }

    public boolean hasAroundConstructMethod(){
        return !getAroundConstructMethods().isEmpty();
    }

    public boolean hasPostConstructMethod(){
        return !getPostConstructMethods().isEmpty();
    }

    public boolean hasPreDestroyMethod(){
        return !getPreDestroyMethods().isEmpty();
    }


    public InterceptorBindings getInterceptorBindings() {
        return interceptorBindings;
    }

    public Set<Class<? extends Annotation>> getInterceptorBindingTypes() {
        return interceptorBindings.getInterceptorBindingTypes();
    }

    public InterceptorManager getInterceptorRegistry() {
        return interceptorManager;
    }

}
