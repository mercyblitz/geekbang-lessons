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

import org.geektimes.commons.collection.util.MapUtils;
import org.geektimes.commons.lang.util.ClassPathUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.AbstractCollection;
import java.util.Map;
import java.util.Set;

import static org.geektimes.commons.reflect.util.ClassUtils.*;
import static org.junit.Assert.*;

/**
 * {@link ClassUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ClassUtilsTest {

    @Test
    public void testIsConcreteClass() {
        assertTrue(isConcreteClass(Object.class));
        assertTrue(isConcreteClass(String.class));
        assertTrue(concreteClassCache.containsKey(Object.class));
        assertTrue(concreteClassCache.containsKey(String.class));
        assertEquals(2, concreteClassCache.size());

        assertFalse(isConcreteClass(CharSequence.class));
        assertFalse(isConcreteClass(AbstractCollection.class));
        assertFalse(isConcreteClass(int.class));
        assertFalse(isConcreteClass(int[].class));
        assertFalse(isConcreteClass(Object[].class));

    }

    @Test
    public void testIsTopLevelClass() {
        assertTrue(isTopLevelClass(Object.class));
        assertTrue(isTopLevelClass(String.class));
        assertFalse(isTopLevelClass(Map.Entry.class));

        class A {

        }

        assertFalse(isTopLevelClass(A.class));
    }

    @Test
    public void testGetClassNamesInClassPath() {
        Set<String> classPaths = ClassPathUtils.getClassPaths();
        for (String classPath : classPaths) {
            Set<String> classNames = ClassUtils.getClassNamesInClassPath(classPath, true);
            assertNotNull(classNames);
        }
    }

    @Test
    public void testGetClassNamesInPackage() {
        Set<String> packageNames = ClassUtils.getAllPackageNamesInClassPaths();
        for (String packageName : packageNames) {
            Set<String> classNames = ClassUtils.getClassNamesInPackage(packageName);
            assertFalse(classNames.isEmpty());
            assertNotNull(classNames);
        }
    }


    @Test
    public void testGetAllPackageNamesInClassPaths() {
        Set<String> packageNames = ClassUtils.getAllPackageNamesInClassPaths();
        assertNotNull(packageNames);
    }

    @Test
    public void testFindClassPath() {
        String classPath = ClassUtils.findClassPath(MapUtils.class);
        assertNotNull(classPath);

        classPath = ClassUtils.findClassPath(String.class);
        assertNotNull(classPath);
    }

    @Test
    public void testGetAllClassNamesMapInClassPath() {
        Map<String, Set<String>> allClassNamesMapInClassPath = ClassUtils.getClassPathToClassNamesMap();
        assertFalse(allClassNamesMapInClassPath.isEmpty());
    }

    @Test
    public void testGetAllClassNamesInClassPath() {
        Set<String> allClassNames = ClassUtils.getAllClassNamesInClassPaths();
        assertFalse(allClassNames.isEmpty());
    }

    @Test
    public void testGetCodeSourceLocation() throws IOException {
        URL codeSourceLocation = null;
        assertNull(codeSourceLocation);

        codeSourceLocation = ClassUtils.getCodeSourceLocation(getClass());
        assertNotNull(codeSourceLocation);

        codeSourceLocation = ClassUtils.getCodeSourceLocation(String.class);
        assertNotNull(codeSourceLocation);
    }
}
