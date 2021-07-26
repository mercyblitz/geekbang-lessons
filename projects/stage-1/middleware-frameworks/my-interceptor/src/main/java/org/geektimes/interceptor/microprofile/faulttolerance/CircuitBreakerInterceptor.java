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

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

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

    public CircuitBreakerInterceptor() {
        super();
        setPriority(0);
    }

    @Override
    protected Object execute(InvocationContext context, CircuitBreaker bindingAnnotation) throws Throwable {
        return null;
    }

    private enum Status {

        CLOSED,

        OPEN,

        HALF_OPEN;
    }
}
