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
package org.geektimes.commons.lang.util;

import org.geektimes.commons.util.BaseUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Consumer;

import static java.lang.reflect.Array.newInstance;
import static java.util.Collections.list;

/**
 * The utilities class for {@link Array}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class ArrayUtils extends BaseUtils {

    public static <T> T[] of(T... values) {
        return values;
    }

    public static <T> int length(T... values) {
        return values == null ? 0 : values.length;
    }

    public static <T> boolean isEmpty(T... values) {
        return length(values) == 0;
    }

    public static <T> boolean isNotEmpty(T... values) {
        return !isEmpty(values);
    }

    public static <E> E[] asArray(Enumeration<E> enumeration, Class<?> componentType) {
        return asArray(list(enumeration), componentType);
    }

    public static <E> E[] asArray(Collection<E> collection, Class<?> componentType) {
        return collection.toArray((E[]) newInstance(componentType, 0));
    }

    public static <T> void iterate(T[] values, Consumer<T> consumer) {
        Objects.requireNonNull(values, "The argument must not be null!");
        for (T value : values) {
            consumer.accept(value);
        }
    }

}
