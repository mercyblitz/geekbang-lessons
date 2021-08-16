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
package org.geektimes.cache.annotation;

import org.geektimes.cache.DataRepository;
import org.junit.Test;

import javax.cache.annotation.CachePut;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * {@link ReflectiveCacheMethodDetails} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveCacheMethodDetailsTest {

    @Test
    public void test() throws Throwable {
        Method method = DataRepository.class.getMethod("create", String.class, Object.class);
        ReflectiveCacheMethodDetails details = new ReflectiveCacheMethodDetails(method);

        assertEquals(method, details.getMethod());
        assertEquals("simpleCache", details.getCacheName());
        assertEquals(CachePut.class, details.getCacheAnnotation().annotationType());
        assertEquals(1, details.getAnnotations().size());
    }
}
