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


import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static org.geektimes.commons.function.ThrowableSupplier.execute;
import static org.geektimes.commons.util.ArrayUtils.of;
import static org.geektimes.commons.util.CollectionUtils.newFixedSet;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveAnnotatedType<X> extends ReflectiveAnnotated<Class> implements AnnotatedType<X> {

    public ReflectiveAnnotatedType(Class type) {
        super(type);
    }

    @Override
    public Class<X> getJavaClass() {
        return getAnnotatedElement();
    }

    @Override
    public Set<AnnotatedConstructor<X>> getConstructors() {
        Class<?> javaClass = getJavaClass();
        Constructor[] constructors = javaClass.getDeclaredConstructors();
        int size = constructors.length;
        if (size < 1) {
            constructors = of(execute(() -> javaClass.getDeclaredConstructor()));
        }

        Set<AnnotatedConstructor<X>> annotatedConstructors = newFixedSet(constructors.length);

        for (Constructor constructor : constructors) {
            AnnotatedConstructor annotatedConstructor = new ReflectiveAnnotatedConstructor(constructor, this);
            annotatedConstructors.add(annotatedConstructor);
        }

        return unmodifiableSet(annotatedConstructors);
    }

    @Override
    public Set<AnnotatedMethod<? super X>> getMethods() {
        Class<?> javaClass = getJavaClass();
        Method[] methods = javaClass.getDeclaredMethods();
        int size = methods.length;
        if (size < 1) {
            return emptySet();
        }

        Set<AnnotatedMethod<? super X>> annotatedMethods = newFixedSet(size);

        for (Method method : methods) {
            AnnotatedMethod annotatedMethod = new ReflectiveAnnotatedMethod(method, this);
            annotatedMethods.add(annotatedMethod);
        }

        return unmodifiableSet(annotatedMethods);
    }

    @Override
    public Set<AnnotatedField<? super X>> getFields() {
        Class<?> javaClass = getJavaClass();
        Field[] fields = javaClass.getDeclaredFields();
        int size = fields.length;
        if (size < 1) {
            return emptySet();
        }

        Set<AnnotatedField<? super X>> annotatedFields = newFixedSet(size);

        for (Field field : fields) {
            AnnotatedField annotatedField = new ReflectiveAnnotatedField(field, this);
            annotatedFields.add(annotatedField);
        }

        return unmodifiableSet(annotatedFields);
    }

    @Override
    public Type getBaseType() {
        return getJavaClass();
    }
}
