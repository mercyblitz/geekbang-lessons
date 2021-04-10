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
package org.geektimes.cache;

import javax.cache.Cache;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Objects;

/**
 * The type pair of key and value.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
class KeyValueTypePair {

    private final Class<?> keyType;

    private final Class<?> valueType;

    KeyValueTypePair(Class<?> keyType, Class<?> valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyValueTypePair that = (KeyValueTypePair) o;
        return Objects.equals(keyType, that.keyType) && Objects.equals(valueType, that.valueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyType, valueType);
    }

    public Class<?> getKeyType() {
        return keyType;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public static KeyValueTypePair resolve(Class<?> targetClass) {
        assertCache(targetClass);

        KeyValueTypePair pair = null;
        while (targetClass != null) {
            pair = resolveFromInterfaces(targetClass);
            if (pair != null) {
                break;
            }

            Type superType = targetClass.getGenericSuperclass();
            if (superType instanceof ParameterizedType) {
                pair = resolveFromType(superType);
            }

            if (pair != null) {
                break;
            }
            // recursively
            targetClass = targetClass.getSuperclass();

        }

        return pair;
    }

    private static KeyValueTypePair resolveFromInterfaces(Class<?> type) {
        KeyValueTypePair pair = null;
        for (Type superInterface : type.getGenericInterfaces()) {
            pair = resolveFromType(superInterface);
            if (pair != null) {
                break;
            }
        }
        return pair;
    }

    private static KeyValueTypePair resolveFromType(Type type) {
        KeyValueTypePair pair = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getRawType() instanceof Class) {
                Class<?> rawType = (Class) pType.getRawType();
                Type[] arguments = pType.getActualTypeArguments();
                if (arguments.length == 2) {
                    Type keyTypeArg = arguments[0];
                    Type valueTypeArg = arguments[1];
                    Class<?> keyType = asClass(keyTypeArg);
                    Class<?> valueType = asClass(valueTypeArg);
                    if (keyType != null && valueType != null) {
                        pair = new KeyValueTypePair(keyType, valueType);
                    }
                }
            }
        }
        return pair;
    }

    private static Class<?> asClass(Type typeArgument) {
        if (typeArgument instanceof Class) {
            return (Class<?>) typeArgument;
        } else if (typeArgument instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) typeArgument;
            return asClass(typeVariable.getBounds()[0]);
        }
        return null;
    }


    private static void assertCache(Class<?> cacheClass) {
        if (cacheClass.isInterface()) {
            throw new IllegalArgumentException("The implementation class of Cache must not be an interface!");
        }
        if (Modifier.isAbstract(cacheClass.getModifiers())) {
            throw new IllegalArgumentException("The implementation class of Cache must not be abstract!");
        }
    }
}
