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
import org.geektimes.commons.lang.util.ArrayUtils;
import org.junit.Test;

import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CachePut;
import java.lang.reflect.Method;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * {@link ReflectiveCacheKeyInvocationContext} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveCacheKeyInvocationContextTest {

    private DataRepository dataRepository = new InMemoryDataRepository();

    @Test
    public void testCreate() throws Throwable {
        Method method = DataRepository.class.getMethod("create", String.class, Object.class);
        ReflectiveCacheKeyInvocationContext context = new ReflectiveCacheKeyInvocationContext(dataRepository, method, "A", 1);

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
        assertArrayEquals(ArrayUtils.of(p1), context.getKeyParameters());

        CacheInvocationParameter p2 = allParameters[1];
        assertEquals(1, p2.getParameterPosition());
        assertEquals(Object.class, p2.getRawType());
        assertEquals(1, p2.getValue());
        assertEquals(1, p2.getAnnotations().size());
        assertEquals(p2, context.getValueParameter());

    }

    @Test
    public void testSave() throws Throwable {
        Method method = DataRepository.class.getMethod("save", String.class, String.class, Object.class);
        ReflectiveCacheKeyInvocationContext context = new ReflectiveCacheKeyInvocationContext(dataRepository, method, "A", "a", 1);

        assertEquals(dataRepository, context.getTarget());
        assertEquals(method, context.getMethod());
        assertEquals("defaultCache", context.getCacheName());
        assertEquals(CachePut.class, context.getCacheAnnotation().annotationType());
        assertEquals(1, context.getAnnotations().size());
        assertEquals(3, context.getAllParameters().length);

        CacheInvocationParameter[] allParameters = context.getAllParameters();

        CacheInvocationParameter p1 = allParameters[0];
        assertEquals(0, p1.getParameterPosition());
        assertEquals(String.class, p1.getRawType());
        assertEquals("A", p1.getValue());
        assertEquals(1, p1.getAnnotations().size());

        CacheInvocationParameter p2 = allParameters[1];
        assertEquals(1, p2.getParameterPosition());
        assertEquals(String.class, p2.getRawType());
        assertEquals("a", p2.getValue());
        assertEquals(1, p2.getAnnotations().size());

        assertArrayEquals(ArrayUtils.of(p1, p2), context.getKeyParameters());

        CacheInvocationParameter p3 = allParameters[2];
        assertEquals(2, p3.getParameterPosition());
        assertEquals(Object.class, p3.getRawType());
        assertEquals(1, p3.getValue());
        assertEquals(1, p3.getAnnotations().size());
        assertEquals(p3, context.getValueParameter());
    }
}
