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
package org.geektimes.commons.reflect.util;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * {@link ReflectionUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @version 1.0.0
 * @see ReflectionUtils
 * @since 1.0.0
 */
public class ReflectionUtilsTest  {

    @Test
    public void testAssertArrayIndex() {
        int size = 10;
        Object array = Array.newInstance(int.class, size);

        for (int i = 0; i < size; i++) {
            ReflectionUtils.assertArrayIndex(array, i);
        }

        for (int i = size; i < size * 2; i++) {
            ArrayIndexOutOfBoundsException exception = null;
            try {
                ReflectionUtils.assertArrayIndex(array, i);
            } catch (ArrayIndexOutOfBoundsException e) {
                exception = e;
                System.err.println(e.getMessage());
            }
            assertNotNull(exception);
        }
    }

    @Test
    public void testAssertArrayTypeOnException() {
        IllegalArgumentException exception = null;
        try {
            ReflectionUtils.assertArrayType(new Object());
        } catch (IllegalArgumentException e) {
            exception = e;
            System.err.println(e.getMessage());
        }
        assertNotNull(exception);
    }

    @Test
    public void testAssertArrayType() {
        testAssertArrayType(long.class);
        testAssertArrayType(int.class);
        testAssertArrayType(short.class);
        testAssertArrayType(byte.class);
        testAssertArrayType(boolean.class);
        testAssertArrayType(double.class);
        testAssertArrayType(float.class);
        testAssertArrayType(char.class);
        testAssertArrayType(String.class);
        testAssertArrayType(Object.class);
    }

    private void testAssertArrayType(Class<?> type) {
        Object array = Array.newInstance(type, 0);
        ReflectionUtils.assertArrayType(array);
    }

    @Test
    public void testGetCallerClassX() throws Exception {
        Class<?> expectedClass = ReflectionUtilsTest.class;

        Class<?> callerClass = ReflectionUtils.getCallerClass();
        Class<?> callerClassInSunJVM = ReflectionUtils.getCallerClassInSunJVM();
        Class<?> callerClassInGeneralJVM = ReflectionUtils.getCallerClassInGeneralJVM();

        assertEquals(expectedClass, callerClass);
        assertEquals(callerClassInSunJVM, callerClass);
        assertEquals(callerClassInGeneralJVM, callerClass);

    }

    @Test
    public void testGetCallerClassName() {
        String expectedClassName = ReflectionUtilsTest.class.getName();
        String callerClassName = ReflectionUtils.getCallerClassName();
        String callerClassNameInSunJVM = ReflectionUtils.getCallerClassNameInSunJVM();
        String callerClassNameInGeneralJVM = ReflectionUtils.getCallerClassNameInGeneralJVM();

        assertEquals(expectedClassName, callerClassName);
        assertEquals(callerClassNameInSunJVM, callerClassName);
        assertEquals(callerClassNameInGeneralJVM, callerClassName);
    }

    @Test
    public void testGetCallerPackage() {
        Class<?> expectedClass = ReflectionUtilsTest.class;
        Package expectedPackage = expectedClass.getPackage();

        assertEquals(expectedPackage, ReflectionUtils.getCallerPackage());
    }

    @Test
    public void testGetCallerClassNamePerformance() {

        for (int i = 0; i < 6; i++) {
            int times = (int) Math.pow(10 + .0, i + .0);
            testGetCallerClassNameInSunJVMPerformance(times);
            testGetCallerClassNameInGeneralJVMPerformance(times);
            System.out.println();
        }
    }

    private void testGetCallerClassNameInSunJVMPerformance(int times) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ReflectionUtils.getCallerClassNameInSunJVM();
        }
        long costTime = System.currentTimeMillis() - startTime;
        System.out.printf("It's cost to execute ReflectionUtils.getCallerClassNameInSunJVM() %d times : %d ms！\n", times, costTime);
    }

    private void testGetCallerClassNameInGeneralJVMPerformance(int times) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ReflectionUtils.getCallerClassNameInGeneralJVM();
        }
        long costTime = System.currentTimeMillis() - startTime;
        System.out.printf("It's cost to execute ReflectionUtils.getCallerClassNameInGeneralJVM() %d times : %d ms！\n", times, costTime);
    }

    @Test
    public void testToList() {
        int[] intArray = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        List<Integer> list = ReflectionUtils.toList(intArray);
        Object expectedList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(expectedList, list);


        int[][] intIntArray = new int[][]{{1, 2, 3}, {4, 5, 6,}, {7, 8, 9}};
        list = ReflectionUtils.toList(intIntArray);
        expectedList = Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9));
        assertEquals(expectedList, list);
    }

}
