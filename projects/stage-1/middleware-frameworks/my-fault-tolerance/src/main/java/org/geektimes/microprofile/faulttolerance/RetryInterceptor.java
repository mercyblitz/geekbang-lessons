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

import org.eclipse.microprofile.faulttolerance.Retry;
import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.Optional;
import java.util.concurrent.*;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static org.geektimes.commons.reflect.util.ClassUtils.isDerived;
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
public class RetryInterceptor extends AnnotatedInterceptor<Retry> {

    private final ScheduledExecutorService executorService = newScheduledThreadPool(2);


    public RetryInterceptor() {
        super();
        setPriority(300);
    }

    @Override
    protected Object execute(InvocationContext context, Retry retry) throws Throwable {

        long maxRetries = retry.maxRetries();
        if (maxRetries < 1) { // No retry
            return context.proceed();
        }

        // Invoke first
        InvocationResult result = action(retry, context);

        if (result.isSuccess()) {
            return result.getResult();
        } else if (result.getFailure() != null) { // if the abort or no-retry failure found
            throw result.getFailure();
        }

        Callable<InvocationResult> retryAction = () -> action(retry, context);

        Optional<Long> delay = getDelay(retry);

        Callable<InvocationResult> maxRetriesAction = () -> {
            InvocationResult retryActionResult = null;
            for (int i = 0; i < maxRetries; i++) {
                if (delay.isPresent()) { // Schedule
                    Optional<Long> jitter = getJitter(retry);
                    long actualDelay = delay.get() + jitter.get();
                    ScheduledFuture<InvocationResult> future = executorService.schedule(retryAction,
                            actualDelay, TimeUnit.NANOSECONDS);
                    retryActionResult = future.get();
                } else { // Synchronization
                    retryActionResult = retryAction.call();
                }
                if (retryActionResult.isSuccess()) {
                    break;
                }
            }
            return retryActionResult;
        };

        Optional<Long> maxDuration = getMaxDuration(retry, delay);

        if (maxDuration.isPresent()) { // with max duration execution
            Future<InvocationResult> future = executorService.submit(maxRetriesAction);
            result = future.get(maxDuration.get(), TimeUnit.NANOSECONDS);
        } else {
            result = maxRetriesAction.call();
        }

        return result.getResult();
    }

    private InvocationResult action(Retry retry, InvocationContext context) {
        InvocationResult invocationResult = new InvocationResult();
        try {
            invocationResult.setResult(context.proceed());
            invocationResult.setSuccess(true);
        } catch (Throwable e) {
            Throwable failure = getFailure(e);
            invocationResult.setSuccess(false);
            if (isAbortOn(retry, failure) || !isRetryOn(retry, failure)) {
                invocationResult.setFailure(failure);
            }

        }
        return invocationResult;
    }

    private boolean isRetryOn(Retry retry, Throwable e) {
        return isDerived(e.getClass(), retry.retryOn());
    }

    private boolean isAbortOn(Retry retry, Throwable e) {
        return isDerived(e.getClass(), retry.abortOn());
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

    private static class InvocationResult {

        private Object result;

        private boolean success;

        /**
         * Holds the abort or no-retry failure
         */
        private Throwable failure;

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

        public Throwable getFailure() {
            return failure;
        }

        public void setFailure(Throwable failure) {
            this.failure = failure;
            this.setSuccess(false);
        }
    }
}


