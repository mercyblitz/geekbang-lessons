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
package org.geektimes.commons.util;

import org.apache.commons.lang.StringUtils;

import java.util.*;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

/**
 * Miscellaneous collection utility methods.
 * Mainly for internal use within the framework.
 *
 * @since 1.0.0
 */
public class CollectionUtils {

    /**
     * Return {@code true} if the supplied Collection is {@code null} or empty.
     * Otherwise, return {@code false}.
     *
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Return {@code true} if the supplied Collection is {@code not null} or not empty.
     * Otherwise, return {@code false}.
     *
     * @param collection the Collection to check
     * @return whether the given Collection is not empty
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Convert to multiple values to be {@link LinkedHashSet}
     *
     * @param values one or more values
     * @param <T>    the type of <code>values</code>
     * @return read-only {@link Set}
     */
    public static <T> Set<T> ofSet(T... values) {
        int size = values == null ? 0 : values.length;
        if (size < 1) {
            return emptySet();
        }

        float loadFactor = 1f / ((size + 1) * 1.0f);

        if (loadFactor > 0.75f) {
            loadFactor = 0.75f;
        }

        Set<T> elements = new LinkedHashSet<>(size, loadFactor);
        for (int i = 0; i < size; i++) {
            elements.add(values[i]);
        }
        return unmodifiableSet(elements);
    }

    /**
     * Get the size of the specified {@link Collection}
     *
     * @param collection the specified {@link Collection}
     * @return must be positive number
     * @since 1.0.0
     */
    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * Compares the specified collection with another, the main implementation references
     * {@link AbstractSet}
     *
     * @param one     {@link Collection}
     * @param another {@link Collection}
     * @return if equals, return <code>true</code>, or <code>false</code>
     * @since 1.0.0
     */
    public static boolean equals(Collection<?> one, Collection<?> another) {

        if (one == another) {
            return true;
        }

        if (isEmpty(one) && isEmpty(another)) {
            return true;
        }

        if (size(one) != size(another)) {
            return false;
        }

        try {
            return one.containsAll(another);
        } catch (ClassCastException | NullPointerException unused) {
            return false;
        }
    }

    /**
     * Add the multiple values into {@link Collection the specified collection}
     *
     * @param collection {@link Collection the specified collection}
     * @param values     the multiple values
     * @param <T>        the type of values
     * @return the effected count after added
     * @since 1.0.0
     */
    public static <T> int addAll(Collection<T> collection, T... values) {

        int size = values == null ? 0 : values.length;

        if (collection == null || size < 1) {
            return 0;
        }

        int effectedCount = 0;
        for (int i = 0; i < size; i++) {
            if (collection.add(values[i])) {
                effectedCount++;
            }
        }

        return effectedCount;
    }

    /**
     * Take the first element from the specified collection
     *
     * @param values the collection object
     * @param <T>    the type of element of collection
     * @return if found, return the first one, or <code>null</code>
     * @since 1.0.0
     */
    public static <T> T first(Collection<T> values) {
        if (isEmpty(values)) {
            return null;
        }
        if (values instanceof List) {
            List<T> list = (List<T>) values;
            return list.get(0);
        } else {
            return values.iterator().next();
        }
    }

}
