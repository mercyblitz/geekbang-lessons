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

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.geektimes.commons.collection.util.CollectionUtils.ofSet;
import static org.geektimes.commons.function.Predicates.and;
import static org.geektimes.commons.function.Streams.filter;
import static org.geektimes.commons.function.ThrowableSupplier.execute;
import static org.geektimes.commons.reflect.util.ClassUtils.getAllInheritedTypes;

/**
 * The utilities class for Java Reflection {@link Field}
 *
 * @since 1.0.0
 */
public abstract class FieldUtils {

    public static Set<Field> getAllFields(Class<?> declaredClass, Predicate<Field>... fieldFilters) {
        Set<Field> allFields = new LinkedHashSet<>(asList(declaredClass.getFields()));
        for (Class superType : getAllInheritedTypes(declaredClass)) {
            allFields.addAll(asList(superType.getFields()));
        }
        return filter(allFields, and(fieldFilters));
    }

    public static Set<Field> getAllDeclaredFields(Class<?> declaredClass, Predicate<Field>... fieldFilters) {
        Set<Field> allDeclaredFields = new LinkedHashSet<>(asList(declaredClass.getDeclaredFields()));
        for (Class superType : getAllInheritedTypes(declaredClass)) {
            allDeclaredFields.addAll(asList(superType.getDeclaredFields()));
        }
        return filter(allDeclaredFields, and(fieldFilters));
    }

    /**
     * Like the {@link Class#getDeclaredField(String)} method without throwing any {@link Exception}
     *
     * @param declaredClass the declared class
     * @param fieldName     the name of {@link Field}
     * @return if can't be found, return <code>null</code>
     */
    public static Field getDeclaredField(Class<?> declaredClass, String fieldName) {
        Field targetField = null;
        Field[] fields = declaredClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (Objects.equals(fieldName, field.getName())) {
                targetField = field;
            }
        }
        return targetField;
    }

    /**
     * Find the {@link Field} by the name in the specified class and its inherited types
     *
     * @param declaredClass the declared class
     * @param fieldName     the name of {@link Field}
     * @return if can't be found, return <code>null</code>
     */
    public static Field findField(Class<?> declaredClass, String fieldName) {
        Field field = getDeclaredField(declaredClass, fieldName);
        if (field != null) {
            return field;
        }
        for (Class superType : getAllInheritedTypes(declaredClass)) {
            field = getDeclaredField(superType, fieldName);
            if (field != null) {
                break;
            }
        }

        if (field == null) {
            throw new IllegalStateException(String.format("cannot find field %s,field is null", fieldName));
        }

        return field;
    }

    /**
     * Find the {@link Field} by the name in the specified class and its inherited types
     *
     * @param object    the object whose field should be modified
     * @param fieldName the name of {@link Field}
     * @return if can't be found, return <code>null</code>
     */
    public static Field findField(Object object, String fieldName) {
        return findField(object.getClass(), fieldName);
    }

    /**
     * Get the value of the specified {@link Field}
     *
     * @param object    the object whose field should be modified
     * @param fieldName the name of {@link Field}
     * @return the value of  the specified {@link Field}
     */
    public static <T> T getFieldValue(Object object, String fieldName) {
        return getFieldValue(object, findField(object, fieldName));
    }

    /**
     * Get the value of the specified {@link Field}
     *
     * @param object the object whose field should be modified
     * @param field  {@link Field}
     * @return the value of  the specified {@link Field}
     */
    public static <T> T getFieldValue(Object object, Field field) {
        return (T) execute(() -> {
            enableAccessible(field);
            return field.get(object);
        });
    }

    /**
     * Set the value for the specified {@link Field}
     *
     * @param object    the object whose field should be modified
     * @param fieldName the name of {@link Field}
     * @param value     the value of field to be set
     * @return the previous value of the specified {@link Field}
     */
    public static <T> T setFieldValue(Object object, String fieldName, T value) {
        return setFieldValue(object, findField(object, fieldName), value);
    }

    /**
     * Set the value for the specified {@link Field}
     *
     * @param object the object whose field should be modified
     * @param field  {@link Field}
     * @param value  the value of field to be set
     * @return the previous value of the specified {@link Field}
     */
    public static <T> T setFieldValue(Object object, Field field, T value) {
        Object previousValue = null;
        try {
            enableAccessible(field);
            previousValue = field.get(object);
            if (!Objects.equals(previousValue, value)) {
                field.set(object, value);
            }
        } catch (IllegalAccessException ignored) {
        }
        return (T) previousValue;
    }

    /**
     * Assert Field type match
     *
     * @param object       Object
     * @param fieldName    field name
     * @param expectedType expected type
     * @throws IllegalArgumentException if type is not matched
     */
    public static void assertFieldMatchType(Object object, String fieldName, Class<?> expectedType) throws IllegalArgumentException {
        Class<?> type = object.getClass();
        Field field = findField(type, fieldName);
        Class<?> fieldType = field.getType();
        if (!expectedType.isAssignableFrom(fieldType)) {
            String message = String.format("The type[%s] of field[%s] in Class[%s] can't match expected type[%s]", fieldType.getName(), fieldName, type.getName(), expectedType.getName());
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Enable field to be accessible
     *
     * @param field {@link Field}
     */
    public static void enableAccessible(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}
