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

import org.geektimes.commons.lang.util.ArrayUtils;
import org.geektimes.commons.util.BaseUtils;

import java.util.*;

import static java.util.Collections.*;

/**
 * Miscellaneous collection utility methods.
 * Mainly for internal use within the framework.
 *
 * @since 1.0.0
 */
public abstract class CollectionUtils extends BaseUtils {

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


    public static <T> Set<T> ofSet(Collection<T> values, T... others) {
        int size = size(values);

        if (size < 1) {
            return ofSet(others);
        }

        Set<T> elements = newLinkedHashSet(size + others.length);
        // add values
        elements.addAll(values);

        // add others
        for (T other : others) {
            elements.add(other);
        }
        return unmodifiableSet(elements);
    }

    /**
     * Convert to multiple values to be {@link LinkedHashSet}
     *
     * @param values values
     * @param <T>    the type of <code>values</code>
     * @return read-only {@link Set}
     */
    public static <T> Set<T> ofSet(T[] values) {
        int size = ArrayUtils.length(values);
        if (size < 1) {
            return emptySet();
        }

        Set<T> elements = newLinkedHashSet(size);
        for (int i = 0; i < size; i++) {
            elements.add(values[i]);
        }
        return unmodifiableSet(elements);
    }

    /**
     * Convert to multiple values to be {@link LinkedHashSet}
     *
     * @param one    one value
     * @param others others values
     * @param <T>    the type of <code>values</code>
     * @return read-only {@link Set}
     */
    public static <T> Set<T> ofSet(T one, T... others) {
        int size = others == null ? 0 : others.length;
        if (size < 1) {
            return singleton(one);
        }

        Set<T> elements = new LinkedHashSet<>(size + 1, Float.MIN_NORMAL);
        elements.add(one);
        for (int i = 0; i < size; i++) {
            elements.add(others[i]);
        }
        return unmodifiableSet(elements);
    }

    public static <T> Set<T> newLinkedHashSet() {
        return new LinkedHashSet<>();
    }

    public static <T> Set<T> newLinkedHashSet(int size) {
        return newLinkedHashSet(size, Float.MIN_NORMAL);
    }

    public static <T> Set<T> newLinkedHashSet(int size, float loadFactor) {
        return new LinkedHashSet<>(size, loadFactor);
    }

    public static <T> Set<T> newLinkedHashSet(Iterable<T> values) {
        Set<T> set = newLinkedHashSet();
        values.forEach(set::add);
        return set;
    }

    public static <T> List<T> newArrayList(int size) {
        return new ArrayList<>(size);
    }

    public static <T> List<T> newArrayList(Iterable<T> values) {
        List<T> list = new ArrayList<>();
        values.forEach(list::add);
        return list;
    }

    public static <T> List<T> newLinkedList(Iterable<T> values) {
        List<T> list = newLinkedList();
        values.forEach(list::add);
        return list;
    }

    public static <T> List<T> newLinkedList() {
        return new LinkedList<>();
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

    public static <T> boolean addIfAbsent(Collection<T> collection, T valueToAdd) {
        if (collection == null || valueToAdd == null) {
            return false;
        }
        boolean added = false;
        if (!collection.contains(valueToAdd)) {
            added = collection.add(valueToAdd);
        }
        return added;
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

    public static <T> T findDuplicatedElement(Collection<T> values) {
        Set<T> elements = new LinkedHashSet<>();
        T duplicatedElement = null;
        for (T value : values) {
            if (!elements.add(value)) {
                duplicatedElement = value;
                break;
            }
        }
        return duplicatedElement;
    }
}
