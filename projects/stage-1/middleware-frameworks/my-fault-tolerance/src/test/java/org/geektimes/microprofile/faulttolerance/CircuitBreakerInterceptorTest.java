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
import org.geektimes.interceptor.ReflectiveMethodInvocationContext;
import org.junit.Assert;
import org.junit.Test;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

/**
 * {@link CircuitBreakerInterceptor} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class CircuitBreakerInterceptorTest {

    private CircuitBreakerInterceptor interceptor = new CircuitBreakerInterceptor();

    @Test(expected = CircuitBreakerOpenException.class)
    public void testFailOn() throws Throwable {
        Method method = getClass().getMethod("failOn");
        InvocationContext context = new ReflectiveMethodInvocationContext(this, method);
        try {
            interceptor.execute(context);
        } catch (Throwable e) {

        }

        CircuitBreaker circuitBreaker = method.getAnnotation(CircuitBreaker.class);
        CircuitBreakerInterceptor.CountableSlidingWindow slidingWindow = interceptor.getSlidingWindow(circuitBreaker);
        Assert.assertTrue(slidingWindow.shouldReset());
        Assert.assertTrue(slidingWindow.isOpen());
        Assert.assertFalse(slidingWindow.isClosed());
        Assert.assertFalse(slidingWindow.isHalfOpen());
        interceptor.execute(context);
    }

    @CircuitBreaker(failOn = RuntimeException.class, requestVolumeThreshold = 1)
    public void failOn() {
        throw new RuntimeException();
    }
}
