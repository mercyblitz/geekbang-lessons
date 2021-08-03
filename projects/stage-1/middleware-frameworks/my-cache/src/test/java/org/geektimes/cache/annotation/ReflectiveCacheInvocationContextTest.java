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
import org.geektimes.cache.InMemoryDataRepository;
import org.junit.Test;

import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CachePut;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * {@link ReflectiveCacheInvocationContext} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveCacheInvocationContextTest {

    @Test
    public void test() throws Throwable {
        DataRepository dataRepository = new InMemoryDataRepository();
        Method method = DataRepository.class.getMethod("create", String.class, Object.class);
        ReflectiveCacheInvocationContext context = new ReflectiveCacheInvocationContext(dataRepository, method, "A", 1);

        assertEquals(dataRepository, context.getTarget());
        assertEquals(method, context.getMethod());
        assertEquals("simpleCache", context.getCacheName());
        assertEquals(CachePut.class, context.getCacheAnnotation().annotationType());
        assertEquals(1, context.getAnnotations().size());
        assertEquals(2, context.getAllParameters().length);

        CacheInvocationParameter[] allParameters = context.getAllParameters();

        CacheInvocationParameter p1 = allParameters[0];
        assertEquals(0, p1.getParameterPosition());
        assertEquals(String.class, p1.getRawType());
        assertEquals("A", p1.getValue());
        assertEquals(1, p1.getAnnotations().size());

        CacheInvocationParameter p2 = allParameters[1];
        assertEquals(1, p2.getParameterPosition());
        assertEquals(Object.class, p2.getRawType());
        assertEquals(1, p2.getValue());
        assertEquals(1, p2.getAnnotations().size());


    }
}
