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
package org.geektimes.commons.util;

import org.junit.Test;

import javax.annotation.Priority;

import static org.junit.Assert.assertEquals;

/**
 * {@link PriorityComparator} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class PriorityComparatorTest {

    @Test
    public void testCompare() {
        assertEquals(0, PriorityComparator.compare(A.class, A.class));
        assertEquals(0, PriorityComparator.compare(A.class, B.class));
        assertEquals(0, PriorityComparator.compare(A.class, D.class));
        assertEquals(-1, PriorityComparator.compare(A.class, C.class));
        assertEquals(-1, PriorityComparator.compare(D.class, getClass()));
        assertEquals(1, PriorityComparator.compare(getClass(), C.class));
    }

    @Priority(100)
    static interface A {

    }

    @Priority(100)
    static class B {

    }

    @Priority(200)
    static class C extends B {
    }

    static class D extends B implements A {
    }


}
