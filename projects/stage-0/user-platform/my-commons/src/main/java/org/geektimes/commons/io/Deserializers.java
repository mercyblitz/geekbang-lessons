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
 * {@link Deserializer} Utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class Deserializers {

    private final Map<Class<?>, List<Deserializer>> typedDeserializers = new HashMap<>();

    private final ClassLoader classLoader;

    public Deserializers(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Deserializers() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public void loadSPI() {
        for (Deserializer deserializer : load(Deserializer.class)) {
            List<Class<?>> typeArguments = resolveTypeArguments(deserializer.getClass());
            Class<?> targetClass = typeArguments.isEmpty() ? Object.class : typeArguments.get(0);
            List<Deserializer> deserializers = typedDeserializers.computeIfAbsent(targetClass, k -> new LinkedList());
            deserializers.add(deserializer);
            deserializers.sort(PriorityComparator.INSTANCE);
        }
    }

    /**
     * Get the most compatible instance of {@link Deserializer} by the specified deserialized type
     *
     * @param deserializedType the type to be deserialized
     * @return <code>null</code> if not found
     */
    public Deserializer<?> getMostCompatible(Class<?> deserializedType) {
        Deserializer<?> deserializer = getHighestPriority(deserializedType);
        if (deserializer == null) {
            deserializer = getLowestPriority(Object.class);
        }
        return deserializer;
    }

    /**
     * Get the highest priority instance of {@link Deserializer} by the specified deserialized type
     *
     * @param deserializedType the type to be deserialized
     * @param <T>              the type to be serialized
     * @return <code>null</code> if not found
     */
    public <T> Deserializer<T> getHighestPriority(Class<?> deserializedType) {
        List<Deserializer<T>> serializers = get(deserializedType);
        return serializers.isEmpty() ? null : serializers.get(0);
    }

    /**
     * Get the lowest priority instance of {@link Deserializer} by the specified deserialized type
     *
     * @param deserializedType the type to be deserialized
     * @param <T>              the type to be serialized
     * @return <code>null</code> if not found
     */
    public <T> Deserializer<T> getLowestPriority(Class<?> deserializedType) {
        List<Deserializer<T>> serializers = get(deserializedType);
        return serializers.isEmpty() ? null : serializers.get(0);
    }

    /**
     * Get all instances of {@link Deserializer} by the specified deserialized type
     *
     * @param deserializedType the type to be deserialized
     * @param <T>              the type to be serialized
     * @return non-null {@link List}
     */
    public <T> List<Deserializer<T>> get(Class<?> deserializedType) {
        return (List) typedDeserializers.getOrDefault(deserializedType, emptyList());
    }
}
