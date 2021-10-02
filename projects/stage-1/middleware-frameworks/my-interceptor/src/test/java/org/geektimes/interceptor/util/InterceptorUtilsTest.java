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
package org.geektimes.interceptor.util;

import org.geektimes.interceptor.ExternalInterceptor;
import org.geektimes.interceptor.Logging;
import org.geektimes.interceptor.Monitored;
import org.junit.Test;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.*;

import static org.geektimes.interceptor.util.InterceptorUtils.*;
import static org.junit.Assert.*;

/**
 * {@link InterceptorUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class InterceptorUtilsTest {

    @Test
    public void testConstants() {
        assertEquals(javax.interceptor.Interceptor.class, INTERCEPTOR_ANNOTATION_TYPE);
        assertEquals(InterceptorBinding.class, INTERCEPTOR_BINDING_ANNOTATION_TYPE);
        assertEquals(AroundInvoke.class, AROUND_INVOKE_ANNOTATION_TYPE);
        assertEquals(AroundTimeout.class, AROUND_TIMEOUT_ANNOTATION_TYPE);
        assertEquals(AroundConstruct.class, AROUND_CONSTRUCT_ANNOTATION_TYPE);
        assertEquals(PostConstruct.class, POST_CONSTRUCT_ANNOTATION_TYPE);
        assertEquals(PreDestroy.class, PRE_DESTROY_ANNOTATION_TYPE);
    }

    @Test
    public void testIsInterceptorClass() {
        isInterceptorClass(ExternalInterceptor.class);
    }

    @Test
    public void testValidateInterceptorClass() {
        validateInterceptorClass(ExternalInterceptor.class);
        testOnError(() -> validateInterceptorClass(String.class), IllegalStateException.class);
        testOnError(() -> validateInterceptorClass(A.class), IllegalStateException.class);
        testOnError(() -> validateInterceptorClass(B.class), IllegalStateException.class);
    }

    protected void testOnError(Runnable runnable, Class<? extends Throwable> errorClass) {
        Throwable throwable = null;
        try {
            runnable.run();
        } catch (Throwable e) {
            throwable = e;
        }
        assertTrue(errorClass.isAssignableFrom(throwable.getClass()));
    }

    @Test
    public void testIsAnnotatedInterceptorBinding() {
        assertTrue(isAnnotatedInterceptorBinding(Logging.class));
        assertTrue(isAnnotatedInterceptorBinding(Monitored.class));
        assertFalse(isAnnotatedInterceptorBinding(Override.class));
    }

    @Interceptor
    abstract class A {
    }

    @Interceptor
    final class B {
    }
}
