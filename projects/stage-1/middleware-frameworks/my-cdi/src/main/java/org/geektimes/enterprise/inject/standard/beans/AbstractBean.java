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
package org.geektimes.enterprise.inject.standard.beans;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * The Standard abstract implementation  {@link Bean} based on Java Reflection.
 *
 * @param <A> The sub-type of {@link AnnotatedElement} as annotated object that may be :
 *            <ul>
 *            <li>{@link Class Bean Class}</li>
 *            <li>{@link Method Producer Method}</li>
 *            <li>{@link Field Producer Field}</li>
 *            </ul>
 * @param <T> the type of {@link Bean}
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class AbstractBean<A extends AnnotatedElement, T> extends AbstractBeanAttributes<A, T> implements Bean<T> {

    public AbstractBean(A annotatedElement, AnnotatedType<T> beanType) {
        super(annotatedElement, beanType);
    }

    /**
     * @return As of CDI 1.1 this method is deprecated and can safely always return false.
     */
    @Override
    @Deprecated
    public final boolean isNullable() {
        return false;
    }

}
