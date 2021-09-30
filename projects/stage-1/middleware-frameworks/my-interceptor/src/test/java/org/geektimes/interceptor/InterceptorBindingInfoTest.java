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
package org.geektimes.interceptor;

import org.junit.Test;

import static org.geektimes.interceptor.InterceptorBindingInfo.valueOf;
import static org.junit.Assert.*;

/**
 * {@link InterceptorBindingInfo} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Logging(name = "InterceptorBindingInfoTest")
public class InterceptorBindingInfoTest {

    @Test
    @Logging(name = "test")
    public void test() throws Throwable {
        Logging logging = this.getClass().getAnnotation(Logging.class);
        InterceptorBindingInfo info = valueOf(logging);
        assertEquals(Logging.class, info.getDeclaredAnnotationType());
        assertFalse(info.isSynthetic());
        assertTrue(info.getAttributes().isEmpty());

        InterceptorBindingInfo info2 = new InterceptorBindingInfo(getClass().getMethod("test").getAnnotation(Logging.class));
        assertEquals(Logging.class, info.getDeclaredAnnotationType());
        assertEquals(Logging.class, info2.getDeclaredAnnotationType());
        assertFalse(info2.isSynthetic());
        assertTrue(info2.getAttributes().isEmpty());

        assertEquals(info, info2);
    }
}
