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
package org.geektimes.commons.io;

import org.geektimes.commons.util.PriorityComparator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.ServiceLoader.load;
import static org.geektimes.commons.reflect.util.TypeUtils.resolveTypeArguments;

/**
 * {@link Serializer} Utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class Serializers {

    private final Map<Class<?>, List<Serializer>> typedSerializers = new HashMap<>();

    private final ClassLoader classLoader;

    public Serializers(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Serializers() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public void loadSPI() {
        for (Serializer serializer : load(Serializer.class)) {
            List<Class<?>> typeArguments = resolveTypeArguments(serializer.getClass());
            Class<?> targetClass = typeArguments.isEmpty() ? Object.class : typeArguments.get(0);
            List<Serializer> serializers = typedSerializers.computeIfAbsent(targetClass, k -> new LinkedList());
            serializers.add(serializer);
            serializers.sort(PriorityComparator.INSTANCE);
        }
    }

    /**
     * Get the most compatible instance of {@link Serializer} by the specified deserialized type
     *
     * @param serializedType the type to be serialized
     * @return <code>null</code> if not found
     */
    public Serializer<?> getMostCompatible(Class<?> serializedType) {
        Serializer<?> serializer = getHighestPriority(serializedType);
        if (serializer == null) {
            serializer = getLowestPriority(Object.class);
        }
        return serializer;
    }

    /**
     * Get the highest priority instance of {@link Serializer} by the specified serialized type
     *
     * @param serializedType the type to be serialized
     * @param <S>            the type to be serialized
     * @return <code>null</code> if not found
     */
    public <S> Serializer<S> getHighestPriority(Class<S> serializedType) {
        List<Serializer<S>> serializers = get(serializedType);
        return serializers.isEmpty() ? null : serializers.get(0);
    }


    /**
     * Get the lowest priority instance of {@link Serializer} by the specified serialized type
     *
     * @param serializedType the type to be serialized
     * @param <S>            the type to be serialized
     * @return <code>null</code> if not found
     */
    public <S> Serializer<S> getLowestPriority(Class<S> serializedType) {
        List<Serializer<S>> serializers = get(serializedType);
        return serializers.isEmpty() ? null : serializers.get(serializers.size() - 1);
    }

    /**
     * Get all instances of {@link Serializer} by the specified serialized type
     *
     * @param serializedType the type to be serialized
     * @param <S>            the type to be serialized
     * @return non-null {@link List}
     */
    public <S> List<Serializer<S>> get(Class<S> serializedType) {
        return (List) typedSerializers.getOrDefault(serializedType, emptyList());
    }
}
