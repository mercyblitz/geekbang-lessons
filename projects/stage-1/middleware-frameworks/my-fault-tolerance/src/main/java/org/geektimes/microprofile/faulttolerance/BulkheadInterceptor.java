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
package org.geektimes.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException;
import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

/**
 * The interceptor implementation for the annotation {@link Bulkhead} of
 * MicroProfile Fault Tolerance
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Bulkhead
@Interceptor
public class BulkheadInterceptor extends AnnotatedInterceptor<Bulkhead> {

    private final ConcurrentMap<Bulkhead, ExecutorService> executorsCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Bulkhead, Semaphore> semaphoresCache = new ConcurrentHashMap<>();

    public BulkheadInterceptor() {
        super();
        setPriority(100);
    }

    @Override
    protected Object execute(InvocationContext context, Bulkhead bulkhead) throws Exception {
        Method method = context.getMethod();
        if (isThreadIsolation(method)) {
            return executeInThreadIsolation(context, bulkhead);
        } else {
            return executeInSemaphoreIsolation(context, bulkhead);
        }
    }

    private Object executeInThreadIsolation(InvocationContext context, Bulkhead bulkhead) throws Exception {
        ExecutorService executorService = executorsCache.computeIfAbsent(bulkhead, key -> {
            int fixedThreadPoolSize = bulkhead.value();
            int waitingTaskQueueSize = bulkhead.waitingTaskQueue();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(fixedThreadPoolSize, fixedThreadPoolSize,
                    0, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(waitingTaskQueueSize),
                    new BulkheadThreadFactory(),
                    new BulkheadExceptionRejectedExecutionHandler()
            );
            return executor;
        });

        Future<Object> future = executorService.submit(context::proceed);
        return future.get();
    }

    private Object executeInSemaphoreIsolation(InvocationContext context, Bulkhead bulkhead) throws Exception {

        int maxConcurrentRequests = bulkhead.value();

        Semaphore semaphore = semaphoresCache.computeIfAbsent(bulkhead,
                key -> new Semaphore(maxConcurrentRequests));

        Object result = null;
        if (!semaphore.tryAcquire()) { // No semaphore available
            throw new BulkheadException(
                    format("The concurrent requests exceed the threshold[%d] under thread isolation",
                            maxConcurrentRequests));
        }
        try {
            result = context.proceed();
        } finally {
            semaphore.release();
        }

        return result;
    }

    private boolean isThreadIsolation(Method method) {
        return method.isAnnotationPresent(Asynchronous.class);
    }

    private static class BulkheadThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        private BulkheadThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "Bulkhead-pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement());
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }

    }

    private static class BulkheadExceptionRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            int fixedThreadPoolSize = executor.getPoolSize();
            int waitingTaskQueueSize = executor.getQueue().size();
            throw new BulkheadException(
                    format("The concurrent request was rejected by the ThreadPoolExecutor[size: %d , queue: d] under semaphore isolation",
                            fixedThreadPoolSize, waitingTaskQueueSize));
        }
    }

}
