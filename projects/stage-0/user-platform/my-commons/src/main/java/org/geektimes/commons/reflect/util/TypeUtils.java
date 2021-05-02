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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Type utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class TypeUtils {

    private TypeUtils() {
    }

    public static List<Class<?>> resolveTypeArguments(Class<?> targetClass) {
        List<Class<?>> typeArguments = emptyList();
        while (targetClass != null) {
            typeArguments = resolveTypeArgumentsFromInterfaces(targetClass);
            if (!typeArguments.isEmpty()) {
                break;
            }

            Type superType = targetClass.getGenericSuperclass();
            if (superType instanceof ParameterizedType) {
                typeArguments = resolveTypeArgumentsFromType(superType);
            }

            if (!typeArguments.isEmpty()) {
                break;
            }
            // recursively
            targetClass = targetClass.getSuperclass();
        }

        return typeArguments;
    }

    private static List<Class<?>> resolveTypeArgumentsFromInterfaces(Class<?> type) {
        List<Class<?>> typeArguments = emptyList();
        for (Type superInterface : type.getGenericInterfaces()) {
            typeArguments = resolveTypeArgumentsFromType(superInterface);
            if (typeArguments != null && !typeArguments.isEmpty()) {
                break;
            }
        }
        return typeArguments;
    }

    private static List<Class<?>> resolveTypeArgumentsFromType(Type type) {
        List<Class<?>> typeArguments = emptyList();
        if (type instanceof ParameterizedType) {
            typeArguments = new LinkedList<>();
            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getRawType() instanceof Class) {
                for (Type argument : pType.getActualTypeArguments()) {
                    Class<?> typeArgument = asClass(argument);
                    if (typeArgument != null) {
                        typeArguments.add(typeArgument);
                    }
                }
            }
        }
        return typeArguments;
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
}
