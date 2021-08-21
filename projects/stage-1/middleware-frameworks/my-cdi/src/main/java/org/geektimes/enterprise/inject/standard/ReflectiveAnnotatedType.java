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


import org.geektimes.commons.reflect.util.FieldUtils;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static org.geektimes.commons.collection.util.CollectionUtils.newLinkedHashSet;
import static org.geektimes.commons.function.ThrowableSupplier.execute;
import static org.geektimes.commons.lang.util.ArrayUtils.of;
import static org.geektimes.commons.reflect.util.ClassUtils.isGeneralClass;
import static org.geektimes.commons.reflect.util.MethodUtils.getAllDeclaredMethods;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveAnnotatedType<X> extends ReflectiveAnnotated<Class> implements AnnotatedType<X> {

    private Set<AnnotatedConstructor<X>> annotatedConstructors;

    private Set<AnnotatedMethod<? super X>> annotatedMethods;

    private Set<AnnotatedField<? super X>> annotatedFields;

    public ReflectiveAnnotatedType(Class type) {
        super(type);
    }

    @Override
    public Class<X> getJavaClass() {
        return getAnnotatedElement();
    }

    @Override
    public Set<AnnotatedConstructor<X>> getConstructors() {

        if (annotatedConstructors != null) {
            return annotatedConstructors;
        }

        Class<?> javaClass = getJavaClass();
        Constructor[] constructors = javaClass.getDeclaredConstructors();
        int size = constructors.length;
        if (size < 1) {
            constructors = of(execute(() -> javaClass.getDeclaredConstructor()));
        }

        Set<AnnotatedConstructor<X>> annotatedConstructors = newLinkedHashSet(constructors.length);

        for (Constructor constructor : constructors) {
            AnnotatedConstructor annotatedConstructor = new ReflectiveAnnotatedConstructor(constructor, this);
            annotatedConstructors.add(annotatedConstructor);
        }

        annotatedConstructors = unmodifiableSet(annotatedConstructors);

        this.annotatedConstructors = annotatedConstructors;

        return annotatedConstructors;
    }

    @Override
    public Set<AnnotatedMethod<? super X>> getMethods() {

        if (annotatedMethods != null) {
            return annotatedMethods;
        }

        Class<?> javaClass = getJavaClass();
        Set<Method> methods = getAllDeclaredMethods(javaClass,
                method -> !Objects.equals(Object.class, method.getDeclaringClass()),
                method -> isGeneralClass(method.getDeclaringClass())
        );
        int size = methods.size();
        if (size < 1) {
            return emptySet();
        }

        Set<AnnotatedMethod<? super X>> annotatedMethods = newLinkedHashSet(size);

        for (Method method : methods) {
            AnnotatedMethod annotatedMethod = new ReflectiveAnnotatedMethod(method, this);
            annotatedMethods.add(annotatedMethod);
        }

        annotatedMethods = unmodifiableSet(annotatedMethods);

        this.annotatedMethods = annotatedMethods;

        return annotatedMethods;
    }

    @Override
    public Set<AnnotatedField<? super X>> getFields() {

        if (annotatedFields != null) {
            return annotatedFields;
        }

        Class<?> javaClass = getJavaClass();
        Set<Field> fields = FieldUtils.getAllDeclaredFields(javaClass,
                field -> isGeneralClass(field.getDeclaringClass())
        );
        int size = fields.size();
        if (size < 1) {
            return emptySet();
        }

        Set<AnnotatedField<? super X>> annotatedFields = newLinkedHashSet(size);

        for (Field field : fields) {
            AnnotatedField annotatedField = new ReflectiveAnnotatedField(field, this);
            annotatedFields.add(annotatedField);
        }

        annotatedFields = unmodifiableSet(annotatedFields);

        this.annotatedFields = annotatedFields;

        return annotatedFields;
    }

    @Override
    public Type getBaseType() {
        return getJavaClass();
    }

}
