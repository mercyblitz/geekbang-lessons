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

import org.junit.Test;

import javax.decorator.Decorator;
import javax.enterprise.context.*;
import javax.enterprise.inject.Default;
import javax.interceptor.Interceptor;

import static org.geektimes.enterprise.inject.standard.ReflectiveAnnotatedBean.of;
import static org.junit.Assert.*;

/**
 * {@link ReflectiveAnnotatedBean} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveAnnotatedBeanTest {

    @Test
    public void testWithoutAnnotation() {
        AnnotatedBean instance = of(this.getClass());
        assertTrue(instance.getAnnotations().isEmpty());
        assertEquals(2, instance.getQualifiers().size());
        assertFalse(instance.getScopeAnnotation().isPresent());
        assertNull(instance.getScope());
        assertTrue(instance.getStereotypeAnnotations().isEmpty());
        assertTrue(instance.getStereotypes().isEmpty());
        assertFalse(instance.isAnnotatedScope());
        assertFalse(instance.isAnnotatedNormalScope());
        assertFalse(instance.isAnnotatedApplicationScoped());
        assertFalse(instance.isAnnotatedSessionScoped());
        assertFalse(instance.isAnnotatedConversationScoped());
        assertFalse(instance.isAnnotatedRequestScoped());
        assertFalse(instance.isAnnotatedDependent());
        assertFalse(instance.isAnnotatedInterceptor());
        assertFalse(instance.isAnnotatedDecorator());
        assertFalse(instance.hasDefiningAnnotation());
    }

    @ApplicationScoped
    @Interceptor
    @Default
    class ApplicationScopedType {
    }

    @Test
    public void testApplicationScopedType() {
        AnnotatedBean instance = of(ApplicationScopedType.class);
        assertEquals(3, instance.getAnnotations().size());
        assertEquals(2, instance.getQualifiers().size());
        assertTrue(instance.getScopeAnnotation().isPresent());
        assertEquals(ApplicationScoped.class, instance.getScope());
        assertTrue(instance.getStereotypeAnnotations().isEmpty());
        assertTrue(instance.getStereotypes().isEmpty());
        assertFalse(instance.isAnnotatedScope());
        assertTrue(instance.isAnnotatedNormalScope());
        assertTrue(instance.isAnnotatedApplicationScoped());
        assertFalse(instance.isAnnotatedSessionScoped());
        assertFalse(instance.isAnnotatedConversationScoped());
        assertFalse(instance.isAnnotatedRequestScoped());
        assertFalse(instance.isAnnotatedDependent());
        assertTrue(instance.isAnnotatedInterceptor());
        assertFalse(instance.isAnnotatedDecorator());
        assertTrue(instance.hasDefiningAnnotation());
    }

    @SessionScoped
    @Decorator
    class SessionScopedType {
    }

    @Test
    public void testSessionScoped() {
        AnnotatedBean instance = of(SessionScopedType.class);
        assertEquals(2, instance.getAnnotations().size());
        assertTrue(instance.getScopeAnnotation().isPresent());
        assertEquals(SessionScoped.class, instance.getScope());
        assertFalse(instance.getStereotypeAnnotations().isEmpty());
        assertFalse(instance.getStereotypes().isEmpty());
        assertTrue(instance.getStereotypes().contains(Decorator.class));
        assertFalse(instance.isAnnotatedScope());
        assertTrue(instance.isAnnotatedNormalScope());
        assertFalse(instance.isAnnotatedApplicationScoped());
        assertTrue(instance.isAnnotatedSessionScoped());
        assertFalse(instance.isAnnotatedConversationScoped());
        assertFalse(instance.isAnnotatedRequestScoped());
        assertFalse(instance.isAnnotatedDependent());
        assertFalse(instance.isAnnotatedInterceptor());
        assertTrue(instance.isAnnotatedDecorator());
        assertTrue(instance.hasDefiningAnnotation());
    }

    @ConversationScoped
    class ConversationScopedType {
    }

    @Test
    public void testConversationScoped() {
        AnnotatedBean instance = of(ConversationScopedType.class);
        assertEquals(1, instance.getAnnotations().size());
        assertTrue(instance.getScopeAnnotation().isPresent());
        assertEquals(ConversationScoped.class, instance.getScope());
        assertTrue(instance.getStereotypeAnnotations().isEmpty());
        assertTrue(instance.getStereotypes().isEmpty());
        assertFalse(instance.isAnnotatedScope());
        assertTrue(instance.isAnnotatedNormalScope());
        assertFalse(instance.isAnnotatedApplicationScoped());
        assertFalse(instance.isAnnotatedSessionScoped());
        assertTrue(instance.isAnnotatedConversationScoped());
        assertFalse(instance.isAnnotatedRequestScoped());
        assertFalse(instance.isAnnotatedDependent());
        assertFalse(instance.isAnnotatedInterceptor());
        assertFalse(instance.isAnnotatedDecorator());
        assertTrue(instance.hasDefiningAnnotation());
    }

    @RequestScoped
    class RequestScopedType {
    }

    @Test
    public void testRequestScoped() {
        AnnotatedBean instance = of(RequestScopedType.class);
        assertEquals(1, instance.getAnnotations().size());
        assertTrue(instance.getScopeAnnotation().isPresent());
        assertEquals(RequestScoped.class, instance.getScope());
        assertTrue(instance.getStereotypeAnnotations().isEmpty());
        assertTrue(instance.getStereotypes().isEmpty());
        assertFalse(instance.isAnnotatedScope());
        assertTrue(instance.isAnnotatedNormalScope());
        assertFalse(instance.isAnnotatedApplicationScoped());
        assertFalse(instance.isAnnotatedSessionScoped());
        assertFalse(instance.isAnnotatedConversationScoped());
        assertTrue(instance.isAnnotatedRequestScoped());
        assertFalse(instance.isAnnotatedDependent());
        assertFalse(instance.isAnnotatedInterceptor());
        assertFalse(instance.isAnnotatedDecorator());
        assertTrue(instance.hasDefiningAnnotation());
    }

    @Dependent
    class DependentType {
    }

    @Test
    public void testDependent() {
        AnnotatedBean instance = of(DependentType.class);
        assertEquals(1, instance.getAnnotations().size());
        assertTrue(instance.getScopeAnnotation().isPresent());
        assertEquals(Dependent.class, instance.getScope());
        assertTrue(instance.getStereotypeAnnotations().isEmpty());
        assertTrue(instance.getStereotypes().isEmpty());
        assertTrue(instance.isAnnotatedScope());
        assertFalse(instance.isAnnotatedNormalScope());
        assertFalse(instance.isAnnotatedApplicationScoped());
        assertFalse(instance.isAnnotatedSessionScoped());
        assertFalse(instance.isAnnotatedConversationScoped());
        assertFalse(instance.isAnnotatedRequestScoped());
        assertTrue(instance.isAnnotatedDependent());
        assertFalse(instance.isAnnotatedInterceptor());
        assertFalse(instance.isAnnotatedDecorator());
        assertTrue(instance.hasDefiningAnnotation());
    }


}
