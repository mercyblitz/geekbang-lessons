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
package org.geektimes.commons.collection.util;

import org.geektimes.commons.util.BaseUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public abstract class MapUtils extends BaseUtils {

    public static Map of(Object... values) {
        int length = values.length;
        Map map = new LinkedHashMap(length / 2);
        for (int i = 0; i < length; ) {
            map.put(values[i++], values[i++]);
        }
        return map;
    }

    public static <K, V> Map<K, V> newLinkedHashMap() {
        return new LinkedHashMap<>();
    }

    public static <K, V> Map<K, V> newLinkedHashMap(int initialCapacity) {
        return new LinkedHashMap<>(initialCapacity, 0.75f);
    }

    public static <K, V> Map<K, V> newLinkedHashMap(int initialCapacity,
                                                    float loadFactor) {
        return newLinkedHashMap(initialCapacity, loadFactor, false);
    }

    public static <K, V> Map<K, V> newLinkedHashMap(int initialCapacity,
                                                    float loadFactor,
                                                    boolean accessOrder) {
        return new LinkedHashMap<>(initialCapacity, loadFactor, accessOrder);
    }

    public static <K, V> Map<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<>();
    }

    public static <K, V> Map<K, V> newConcurrentHashMap(int initialCapacity) {
        return newConcurrentHashMap(initialCapacity, 0.75f);
    }

    public static <K, V> Map<K, V> newConcurrentHashMap(int initialCapacity,
                                                        float loadFactor) {
        return new ConcurrentHashMap<>(initialCapacity, loadFactor);
    }

}
