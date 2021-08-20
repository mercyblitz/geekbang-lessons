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
package org.geektimes.enterprise.inject.util;

import org.geektimes.enterprise.inject.BookShop;
import org.geektimes.enterprise.inject.Business;
import org.junit.Test;

import javax.decorator.Decorator;
import javax.enterprise.inject.spi.DefinitionException;
import javax.interceptor.Interceptor;
import java.lang.reflect.Type;
import java.util.Set;

import static org.geektimes.enterprise.inject.util.Beans.getBeanTypes;
import static org.geektimes.enterprise.inject.util.Beans.validateManagedBeanType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * {@link Beans} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class BeansTest {

    @Test
    public void testGetBeanTypes() {
        Set<Type> beanTypes = getBeanTypes(BookShop.class);
        assertEquals(4, beanTypes.size());
        assertTrue(beanTypes.contains(BookShop.class));
        assertTrue(beanTypes.contains(Business.class));
        assertTrue(beanTypes.contains(Object.class));
    }

    @Test
    public void testValidateManagedBean() {
        validateManagedBeanType(BookShop.class);
    }

    @Test(expected = DefinitionException.class)
    public void testValidateManagedBeanA() {
        validateManagedBeanType(A.class);
    }

    @Test(expected = DefinitionException.class)
    public void testValidateManagedBeanB() {
        validateManagedBeanType(B.class);
    }

    @Test(expected = DefinitionException.class)
    public void testValidateManagedBeanC() {
        validateManagedBeanType(C.class);
    }

    @Decorator
    @Interceptor
    static class A {
    }

    static class B {
        public String name;
    }

    static class C<T> {

    }

}
