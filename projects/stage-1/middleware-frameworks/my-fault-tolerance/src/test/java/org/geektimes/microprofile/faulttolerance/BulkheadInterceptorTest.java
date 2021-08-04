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

import org.geektimes.interceptor.ReflectiveMethodInvocationContext;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * {@link BulkheadInterceptor} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class BulkheadInterceptorTest {

    private BulkheadInterceptor interceptor = new BulkheadInterceptor();

    @Test
    public void testInThreadIsolation() throws Throwable {
        EchoService echoService = new EchoService();
        Method method = EchoService.class.getMethod("echo", Object.class);
        ReflectiveMethodInvocationContext context = new ReflectiveMethodInvocationContext
                (echoService, method, "Hello,World");
        interceptor.execute(context);
    }

    @Test
    public void testInSemaphoreIsolation() throws Throwable {
        EchoService echoService = new EchoService();
        Method method = EchoService.class.getMethod("echo", String.class);
        ReflectiveMethodInvocationContext context = new ReflectiveMethodInvocationContext
                (echoService, method, "Hello,World");
        interceptor.execute(context);
    }

}

