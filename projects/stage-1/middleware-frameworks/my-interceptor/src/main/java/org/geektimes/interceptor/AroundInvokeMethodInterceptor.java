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

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The annotated {@link AroundInvoke} {@link MethodInterceptor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class AroundInvokeMethodInterceptor implements MethodInterceptor {

    private final Object interceptor;

    private final Optional<Method> aroundInvokeMethod;

    public AroundInvokeMethodInterceptor(Object interceptor) {
        this.interceptor = interceptor;
        this.aroundInvokeMethod = findAroundInvokeMethod(interceptor);
    }

    private Optional<Method> findAroundInvokeMethod(Object interceptor) {
        return Stream.of(interceptor.getClass().getMethods())
                .filter(method ->
                        method.isAnnotationPresent(AroundInvoke.class) &&
                                method.getParameterCount() == 1 &&
                                InvocationContext.class.isAssignableFrom(method.getParameterTypes()[0])
                ).findFirst();
    }

    @Override
    public Object intercept(Object target, Method method, Object... args) throws Exception {
        if (aroundInvokeMethod.isPresent()) {
            InvocationContext context = new ReflectiveMethodInvocationContext(target, method, args);
            return aroundInvokeMethod.get().invoke(interceptor, context);
        }
        // no @AroundInvoke method found
        return method.invoke(target, args);
    }
}
