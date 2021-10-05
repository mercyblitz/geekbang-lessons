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
package org.geektimes.enterprise.inject.standard.beans.interceptor;

import org.geektimes.enterprise.inject.standard.beans.GenericBean;
import org.geektimes.interceptor.InterceptorInfo;
import org.geektimes.interceptor.InterceptorManager;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.*;
import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;
import java.util.Set;

import static java.lang.String.format;
import static org.geektimes.enterprise.inject.util.Exceptions.newDefinitionException;
import static org.geektimes.interceptor.InterceptorManager.getInstance;

/**
 * {@link Interceptor} Bean Implementation
 *
 * @param <T> the interceptor bean class
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class InterceptorBean<T> extends GenericBean<T> implements Interceptor<T> {

    private final AnnotatedType<?> interceptorType;

    private final Class<?> interceptorClass;

    private final InterceptorManager interceptorManager;

    private final InterceptorInfo interceptorInfo;

    private final Set<Annotation> interceptorBindings;

    public InterceptorBean(AnnotatedType<T> interceptorType, BeanManager beanManager) {
        super(interceptorType, beanManager);
        this.interceptorType = interceptorType;
        this.interceptorClass = interceptorType.getJavaClass();
        this.interceptorManager = getInstance(interceptorClass.getClassLoader());
        this.interceptorManager.registerInterceptorClass(interceptorClass);
        this.interceptorInfo = interceptorManager.getInterceptorInfo(interceptorClass);
        this.interceptorBindings = resolveInterceptorBindings();
    }

    private Set<Annotation> resolveInterceptorBindings() {
        return interceptorInfo.getInterceptorBindings().getDeclaredAnnotations();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        Class<? extends Annotation> scope = super.getScope();
        if (scope == null) {
            scope = Dependent.class;
        } else if (scope != null && !Dependent.class.equals(scope)) {
            throw newDefinitionException("The scope of interceptor must be declared as @%s!", Dependent.class.getName());
        }
        return scope;
    }

    @Override
    public Set<Annotation> getInterceptorBindings() {
        return interceptorBindings;
    }

    @Override
    public boolean intercepts(InterceptionType type) {
        boolean supported = false;
        switch (type) {
            case AROUND_INVOKE:
                supported = interceptorInfo.hasAroundInvokeMethod();
                break;
            case AROUND_TIMEOUT:
                supported = interceptorInfo.hasAroundTimeoutMethod();
                break;
            case AROUND_CONSTRUCT:
                supported = interceptorInfo.hasAroundConstructMethod();
                break;
            case POST_CONSTRUCT:
                supported = interceptorInfo.hasPostConstructMethod();
                break;
            case PRE_DESTROY:
                supported = interceptorInfo.hasPreDestroyMethod();
                break;
            case PRE_PASSIVATE:
                // TODO
                break;
            case POST_ACTIVATE:
                // TODO
                break;
        }
        return supported;
    }

    @Override
    public Object intercept(InterceptionType type, T instance, InvocationContext ctx) throws Exception {
        if (!intercepts(type)) {
            throw new UnsupportedOperationException(format(
                    "The interceptor[type:%s] does not support the interception type[%s]!",
                    interceptorClass.getName(),
                    type.name()));
        }
        // TODO
        return null;
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        return super.create(creationalContext);
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        super.destroy(instance, creationalContext);
    }

    @Override
    protected void validate(Class interceptorClass) {
        this.interceptorManager.validateInterceptorClass(interceptorClass);
    }

    @Override
    public Annotated getAnnotated() {
        return interceptorType;
    }
}
