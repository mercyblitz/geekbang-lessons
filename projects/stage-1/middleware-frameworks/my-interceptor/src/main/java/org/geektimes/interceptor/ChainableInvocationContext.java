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
import javax.interceptor.InvocationContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.sort;
import static org.geektimes.interceptor.InterceptorRegistry.getInstance;

/**
 * Chainable {@link InvocationContext}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ChainableInvocationContext implements InvocationContext {

    private final InvocationContext delegateContext;

    private final int length;

    private final Object[] interceptors; // @Interceptor class instances

    private final InterceptorRegistry interceptorRegistry;

    private int pos; // position

    public ChainableInvocationContext(InvocationContext delegateContext, Object... interceptors) {
        this.delegateContext = delegateContext;
        this.length = interceptors.length;
        this.interceptors = copyOf(interceptors, length);
        // sort
        sort(this.interceptors, PriorityComparator.INSTANCE);
        this.interceptorRegistry = getInstance(resolveClassLoader(interceptors));
        this.interceptorRegistry.registerInterceptors(this.interceptors);
        this.pos = 0;
    }

    private ClassLoader resolveClassLoader(Object[] interceptors) {
        Object target = interceptors.length > 0 ? interceptors[0] : this;
        return target.getClass().getClassLoader();
    }

    @Override
    public Object getTarget() {
        return delegateContext.getTarget();
    }

    @Override
    public Object getTimer() {
        return delegateContext.getTimer();
    }

    @Override
    public Method getMethod() {
        return delegateContext.getMethod();
    }

    @Override
    public Constructor<?> getConstructor() {
        return delegateContext.getConstructor();
    }

    @Override
    public Object[] getParameters() {
        return delegateContext.getParameters();
    }

    @Override
    public void setParameters(Object[] params) {
        delegateContext.setParameters(params);
    }

    @Override
    public Map<String, Object> getContextData() {
        return delegateContext.getContextData();
    }

    @Override
    public Object proceed() throws Exception {
        if (pos < length) {
            int currentPos = pos++;
            Object interceptor = interceptors[currentPos];
            Method interceptionMethod = resolveInterceptionMethod(interceptor);
            return interceptionMethod.invoke(interceptor, this);
        } else {
            return delegateContext.proceed();
        }
    }

    private Method resolveInterceptionMethod(Object interceptor) {
        InterceptorInfo interceptorInfo = interceptorRegistry.getInterceptorInfo(interceptor.getClass());

        final Method interceptionMethod;  // nerver null

        if (getTimer() != null) { // If the "Timer" is present
            interceptionMethod = interceptorInfo.getAroundTimeoutMethod();
        } else if (getConstructor() != null) { // If the "Constructor" should be intercepted
            interceptionMethod = interceptorInfo.getAroundConstructMethod();
        } else {
            Method method = getMethod();
            if (method.isAnnotationPresent(PostConstruct.class)) {
                interceptionMethod = interceptorInfo.getPostConstructMethod();
            } else if (method.isAnnotationPresent(PreDestroy.class)) {
                interceptionMethod = interceptorInfo.getPreDestroyMethod();
            } else {
                interceptionMethod = interceptorInfo.getAroundInvokeMethod();
            }
        }

        return interceptionMethod;
    }
}
