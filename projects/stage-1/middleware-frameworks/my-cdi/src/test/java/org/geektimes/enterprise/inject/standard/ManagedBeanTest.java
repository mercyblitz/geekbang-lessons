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
package org.geektimes.enterprise.inject.standard;

import org.geektimes.enterprise.inject.BookShop;
import org.geektimes.enterprise.inject.Business;
import org.geektimes.enterprise.inject.standard.beans.StandardBeanManager;
import org.junit.Test;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import static java.util.Collections.emptySet;
import static org.geektimes.commons.collection.util.CollectionUtils.ofSet;
import static org.junit.Assert.*;

/**
 * {@link ManagedBean} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ManagedBeanTest {

    @Test
    public void test() {
        ManagedBean bean = new ManagedBean(BookShop.class, new StandardBeanManager());
        assertEquals(BookShop.class, bean.getBeanClass());
        assertFalse(bean.isNullable());
        assertEquals(4, bean.getTypes().size());
        assertTrue(bean.getTypes().contains(BookShop.class));
        assertTrue(bean.getTypes().contains(Business.class));
        assertTrue(bean.getTypes().contains(Object.class));
        assertEquals(ofSet(Any.Literal.INSTANCE, Default.Literal.INSTANCE), bean.getQualifiers());
        assertEquals(Dependent.class, bean.getScope());
        assertEquals("bookShop", bean.getName());
        assertEquals(emptySet(), bean.getStereotypes());
        assertFalse(bean.isAlternative());
    }

    @Test
    public void testA() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources("javax/enterprise/context");
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            System.out.println(resource);
        }
    }
}
