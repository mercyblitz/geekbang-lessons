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
package org.geektimes.interceptor.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

import static org.geektimes.commons.reflect.util.ClassUtils.getTypes;
import static org.geektimes.commons.reflect.util.ClassUtils.isDerived;

/**
 * The interceptor implementation for the annotation {@link Fallback} of
 * MicroProfile Fault Tolerance
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Interceptor
public class FallbackInterceptor extends AnnotatedInterceptor<Fallback> {

    public FallbackInterceptor() {
        super();
        setPriority(200);
    }

    @Override
    protected Object execute(InvocationContext context, Fallback fallback) throws Throwable {
        Object result = null;
        try {
            result = context.proceed();
        } catch (Throwable e) {
            Throwable failure = getFailure(e);

            if (!isApplyOn(fallback, failure) || isSkipOn(fallback, failure)) {
                throw failure;
            }
            result = handleFallback(context, fallback, failure);
        }
        return result;
    }

    private Object handleFallback(InvocationContext context, Fallback fallback, Throwable e) throws Exception {
        Object result = null;
        String methodName = fallback.fallbackMethod();
        if (!"".equals(methodName)) {
            Class<?>[] parameterTypes = getTypes(context.getParameters());
            Class<?> type = context.getMethod().getDeclaringClass();
            Method method = type.getMethod(methodName, parameterTypes);
            result = method.invoke(context.getTarget(), context.getParameters());
        } else {
            Class<? extends FallbackHandler<?>> fallbackHandlerType = fallback.value();
            FallbackHandler fallbackHandler = fallbackHandlerType.newInstance();
            result = fallbackHandler.handle(new ExecutionContextAdapter(context, e));
        }
        return result;
    }

    private boolean isApplyOn(Fallback fallback, Throwable e) {
        return isDerived(e.getClass(), fallback.applyOn());
    }

    private boolean isSkipOn(Fallback fallback, Throwable e) {
        return isDerived(e.getClass(), fallback.skipOn());
    }

    private static class ExecutionContextAdapter implements ExecutionContext {

        private final InvocationContext context;

        private final Throwable e;

        private ExecutionContextAdapter(InvocationContext context, Throwable e) {
            this.context = context;
            this.e = e;
        }

        @Override
        public Method getMethod() {
            return context.getMethod();
        }

        @Override
        public Object[] getParameters() {
            return context.getParameters();
        }

        @Override
        public Throwable getFailure() {
            return e;
        }
    }
}
