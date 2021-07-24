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

import org.eclipse.microprofile.faulttolerance.Retry;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static org.geektimes.commons.util.AnnotationUtils.findAnnotation;
import static org.geektimes.commons.util.TimeUtils.toTimeUnit;

/**
 * The interceptor implementation for the annotation {@link Retry} of
 * MicroProfile Fault Tolerance
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Retry
@Interceptor
public class RetryInterceptor {

    private final ScheduledExecutorService executorService = newScheduledThreadPool(2);

    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        Retry retry = findRetry(method);
        if (retry == null) { // No @Retry Found
            return context.proceed();
        }

        long maxRetries = retry.maxRetries();
        if (maxRetries < 1) { // No retry
            return context.proceed();
        }

        // Invoke first
        Object result = null;

        boolean success = false;

        try {
            result = context.proceed();
            success = true;
        } catch (Throwable e) {
            if (isAbortOn(retry, e)) {
                throw e;
            } else if (!isRetryOn(retry, e)) {
                throw e;
            }
            success = false;
        }

        if (success) { // successful
            return result;
        }

        Supplier<InvocationResult> retryActionResult = () -> {
            InvocationResult invocationResult = new InvocationResult();
            try {
                invocationResult.setResult(context.proceed());
                invocationResult.setSuccess(true);
            } catch (Throwable e) {
                invocationResult.setSuccess(false);
                if (!isAbortOn(retry, e) && isRetryOn(retry, e)) {
                    invocationResult.setException(e);
                } // else isAbortOn == true || isRetryOn == false
            }
            return invocationResult;
        };


        Optional<Long> delay = getDelay(retry);

        Callable<InvocationResult> maxRetriesActionResult = () -> {
            InvocationResult value = null;
            for (int i = 0; i < maxRetries; i++) {
                if (delay.isPresent()) { // Schedule
                    Optional<Long> jitter = getJitter(retry);
                    long actualDelay = delay.get() + jitter.get();
                    ScheduledFuture<InvocationResult> future =
                            executorService.schedule(() -> retryActionResult.get(), actualDelay, TimeUnit.NANOSECONDS);
                    value = future.get();
                } else { // Synchronization
                    value = retryActionResult.get();
                }
                if (value.isSuccess() || value.getException() == null) {
                    break;
                }
            }
            return value;
        };

        Optional<Long> maxDuration = getMaxDuration(retry, delay);

        if (maxDuration.isPresent()) { // with max duration execution
            Future<InvocationResult> future = executorService.submit(maxRetriesActionResult);
            InvocationResult value = future.get(maxDuration.get(), TimeUnit.NANOSECONDS);
            result = value.getResult();
        } else {
            InvocationResult value = maxRetriesActionResult.call();
            result = value.getResult();
        }

        return result;
    }

    private boolean isRetryOn(Retry retry, Throwable e) {
        boolean retryOn = false;
        for (Class<? extends Throwable> retryType : retry.retryOn()) {
            if (retryType.isInstance(e.getCause())) {
                retryOn = true;
                break;
            }
        }
        return retryOn;
    }

    private boolean isAbortOn(Retry retry, Throwable e) {
        boolean abort = false;
        for (Class<? extends Throwable> abortType : retry.abortOn()) {
            if (abortType.isInstance(e.getCause())) {
                abort = true;
                break;
            }
        }
        return abort;
    }

    private Optional<Long> getMaxDuration(Retry retry, Optional<Long> delay) {
        long maxDuration = retry.maxDuration();
        if (maxDuration < 1) {
            return empty();
        }
        TimeUnit timeUnit = toTimeUnit(retry.durationUnit());
        long maxDurationInNanos = timeUnit.toNanos(maxDuration);
        delay.ifPresent(delayInNanos -> {
            if (delayInNanos >= maxDurationInNanos) {
                throw new IllegalArgumentException(
                        format("The max duration[%d ns] must be greater than the delay duration[%d ns] if set.",
                                maxDurationInNanos, delayInNanos));
            }
        });

        return of(maxDurationInNanos);
    }

    private Optional<Long> getDelay(Retry retry) {
        long delay = retry.delay();
        if (delay < 1) {
            return empty();
        }
        TimeUnit timeUnit = toTimeUnit(retry.delayUnit());
        return of(timeUnit.toNanos(delay));
    }

    private Optional<Long> getJitter(Retry retry) {
        long jitter = retry.jitter();
        if (jitter < 1) {
            return empty();
        }
        TimeUnit timeUnit = toTimeUnit(retry.jitterDelayUnit());
        long origin = Math.negateExact(jitter);
        long bound = jitter;
        long value = ThreadLocalRandom.current().nextLong(origin, bound);
        return of(timeUnit.toNanos(value));
    }

    private Retry findRetry(Method method) {
        Retry timeout = findAnnotation(method, Retry.class);
        if (timeout == null) {
            timeout = method.getDeclaringClass().getAnnotation(Retry.class);
        }
        return timeout;
    }

    private static class InvocationResult {

        private Object result;

        private boolean success;

        private Throwable exception;

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public Throwable getException() {
            return exception;
        }

        public void setException(Throwable exception) {
            this.exception = exception;
        }
    }
}


