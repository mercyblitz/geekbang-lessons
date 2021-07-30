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
package org.geektimes.interceptor.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.geektimes.interceptor.ChainableInvocationContext;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

/**
 * {@link MethodInterceptor} -> @Interceptor chain
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
class MethodInterceptorAdapter implements MethodInterceptor {

    private final Object target;

    private final Object[] interceptors;

    public MethodInterceptorAdapter(Object target, Object[] interceptors) {
        this.target = target;
        this.interceptors = interceptors;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        InvocationContext delegateContext = new CglibMethodInvocationContext(obj, method, proxy, args);
        ChainableInvocationContext context = new ChainableInvocationContext(delegateContext, interceptors);
        return context.proceed();
    }
}
