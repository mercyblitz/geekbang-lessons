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

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.geektimes.commons.util.TimeUtils;
import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static org.geektimes.commons.reflect.util.ClassUtils.isDerived;

/**
 * The interceptor implementation for the annotation {@link CircuitBreaker} of
 * MicroProfile Fault Tolerance
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@CircuitBreaker
@Interceptor
public class CircuitBreakerInterceptor extends AnnotatedInterceptor<CircuitBreaker> {

    private final ConcurrentMap<CircuitBreaker, CountableSlidingWindow> slidingWindowsCache = new ConcurrentHashMap<>();

    public CircuitBreakerInterceptor() {
        super();
        setPriority(0);
    }

    @Override
    protected Object execute(InvocationContext context, CircuitBreaker circuitBreaker) throws Throwable {
        CountableSlidingWindow slidingWindow = getSlidingWindow(circuitBreaker);
        if (slidingWindow.isOpen()) {
            throw new CircuitBreakerOpenException(slidingWindow.toString());
        }
        if (slidingWindow.shouldReset()) {
            slidingWindow.reset();
        }
        Object result = null;
        try {
            result = context.proceed();
            slidingWindow.success();
        } catch (Throwable e) {
            Throwable failure = getFailure(e);
            slidingWindow.failure(failure);
        }
        return result;
    }

    CountableSlidingWindow getSlidingWindow(CircuitBreaker circuitBreaker) {
        return slidingWindowsCache.computeIfAbsent(circuitBreaker, key -> new CountableSlidingWindow(key))
                .calculateStatus();
    }

    static class CountableSlidingWindow {

        public static final int CLOSED_STATUS = 0;

        public static final int OPEN_STATUS = 1;

        public static final int HALF_OPEN_STATUS = 2;

        // Configuration fields

        private final Class<? extends Throwable>[] appliedFailures;

        private final Class<? extends Throwable>[] ignoredFailures;

        private final long delay;

        private final int requestVolumeThreshold;

        private final double failureRatio;

        private final int successThreshold;

        // status fields

        private volatile long createdTime;

        private volatile long openTime;

        private volatile int status;

        private final LongAdder requests = new LongAdder();

        private final LongAdder failures = new LongAdder();

        private final LongAdder successTrials = new LongAdder();

        public CountableSlidingWindow(CircuitBreaker circuitBreaker) {
            this.reset();
            this.appliedFailures = circuitBreaker.failOn();
            this.ignoredFailures = circuitBreaker.skipOn();
            this.delay = getDelay(circuitBreaker);
            this.requestVolumeThreshold = circuitBreaker.requestVolumeThreshold();
            this.failureRatio = circuitBreaker.failureRatio();
            this.successThreshold = circuitBreaker.successThreshold();
        }

        private long getDelay(CircuitBreaker circuitBreaker) {
            TimeUnit timeUnit = TimeUtils.toTimeUnit(circuitBreaker.delayUnit());
            return timeUnit.toNanos(circuitBreaker.delay());
        }

        private CountableSlidingWindow success() {
            if (isHalfOpen()) {
                successTrials.increment();
            }
            requests.increment();
            return this;
        }

        private CountableSlidingWindow failure(Throwable failure) {
            if (isOnFailure(failure)) {
                failures.increment();
            }
            requests.increment();
            return this;
        }

        private boolean isOnFailure(Throwable failure) {
            Class<? extends Throwable> failureClass = failure.getClass();
            return isDerived(failureClass, appliedFailures) && !isDerived(failureClass, ignoredFailures);
        }

        private CountableSlidingWindow calculateStatus() {
            if (isClosed()) {

                // Status : CLOSED -> OPEN
                /**
                 * If a failure occurs, the Circuit Breaker records the event.
                 * In closed state the requestVolumeThreshold and failureRatio parameters may be configured
                 * in order to specify the conditions under which the breaker will transition the circuit to open.
                 * If the failure conditions are met, the circuit will be opened.
                 */
                if (shouldOpen()) {
                    open();
                }
            } else if (isOpen()) {
                // Status : OPEN -> HALF_OPEN
                /**
                 * When the circuit is open, calls to the service operating under the circuit breaker
                 * will fail immediately.
                 * A delay may be configured for the circuit breaker.
                 * After the specified delay, the circuit transitions to half-open state.
                 */
                if (shouldHalfOpen()) {
                    halfOpen();
                }
            } else if (isHalfOpen()) {

                if (successTrials.intValue() >= successThreshold) {
                    // Status : HALF_OPEN -> CLOSE
                    close();
                } else if (shouldOpen()) {
                    open();
                }

            }

            return this;
        }

        boolean shouldOpen() {
            return !shouldClosed();
        }

        boolean shouldHalfOpen() {
            return System.nanoTime() - delay > openTime
                    && shouldClosed();
        }

        boolean shouldClosed() {
            return currentFailureRatio() < failureRatio;
        }

        boolean shouldReset() {
            return requests.intValue() >= requestVolumeThreshold;
        }

        private void close() {
            this.openTime = Long.MAX_VALUE;
            this.status = CLOSED_STATUS;
        }

        private void open() {
            this.openTime = System.nanoTime();
            this.status = OPEN_STATUS;
        }

        private void halfOpen() {
            status = HALF_OPEN_STATUS;
        }

        boolean isOpen() {
            return OPEN_STATUS == status;
        }

        boolean isClosed() {
            return CLOSED_STATUS == status;
        }

        boolean isHalfOpen() {
            return HALF_OPEN_STATUS == status;
        }

        private double currentFailureRatio() {
            return failures.doubleValue() / requests.doubleValue();
        }

        private void reset() {
            createdTime = System.nanoTime();
            close();
            requests.reset();
            failures.reset();
            successTrials.reset();
        }

        @Override
        public String toString() {
            return "CountableSlidingWindow{" +
                    "appliedFailures=" + Arrays.toString(appliedFailures) +
                    ", ignoredFailures=" + Arrays.toString(ignoredFailures) +
                    ", delay=" + delay +
                    ", requestVolumeThreshold=" + requestVolumeThreshold +
                    ", failureRatio=" + failureRatio +
                    ", successThreshold=" + successThreshold +
                    ", createdTime=" + createdTime +
                    ", openTime=" + openTime +
                    ", status=" + status +
                    ", requests=" + requests +
                    ", failures=" + failures +
                    ", successTrials=" + successTrials +
                    '}';
        }
    }
}
