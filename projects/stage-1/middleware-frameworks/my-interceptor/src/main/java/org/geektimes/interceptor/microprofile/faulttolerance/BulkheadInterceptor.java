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

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.concurrent.*;

import static org.geektimes.commons.util.AnnotationUtils.findAnnotation;

/**
 * The interceptor implementation for the annotation {@link Bulkhead} of
 * MicroProfile Fault Tolerance
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Bulkhead
@Interceptor
public class BulkheadInterceptor {

    private final ConcurrentMap<Bulkhead, ExecutorService> executorsCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Bulkhead, Semaphore> semaphoresCache = new ConcurrentHashMap<>();

    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        Bulkhead bulkhead = findBulkhead(method);
        if (bulkhead == null) { // No @Bulkhead found
            return context.proceed();
        }

        if (isThreadIsolation(method)) {
            return executeInThreadIsolation(context, bulkhead);
        } else {
            return executeInSemaphoreIsolation(context, bulkhead);
        }
    }

    private Object executeInThreadIsolation(InvocationContext context, Bulkhead bulkhead) throws Exception {
        ExecutorService executorService = executorsCache.computeIfAbsent(bulkhead, key -> {
            int fixedSize = bulkhead.value();
            int waitingTaskQueue = bulkhead.waitingTaskQueue();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(fixedSize, fixedSize,
                    0, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(waitingTaskQueue)
            );
            return executor;
        });

        Future<Object> future = executorService.submit(context::proceed);
        return future.get();
    }

    private Object executeInSemaphoreIsolation(InvocationContext context, Bulkhead bulkhead) throws Exception {
        Semaphore semaphore = semaphoresCache.computeIfAbsent(bulkhead, key -> {
            int maxConcurrentRequests = bulkhead.value();
            return new Semaphore(maxConcurrentRequests);
        });

        Object result = null;
        try {
            semaphore.acquire();
            result = context.proceed();
        } finally {
            semaphore.release();
        }

        return result;
    }

    private Bulkhead findBulkhead(Method method) {
        Bulkhead bulkhead = findAnnotation(method, Bulkhead.class);
        if (bulkhead == null) { // try to find in the declaring class
            bulkhead = method.getDeclaringClass().getAnnotation(Bulkhead.class);
        }
        return bulkhead;
    }

    private boolean isThreadIsolation(Method method) {
        return method.isAnnotationPresent(Asynchronous.class);
    }

}
