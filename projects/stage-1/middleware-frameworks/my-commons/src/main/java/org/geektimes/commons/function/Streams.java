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
package org.geektimes.commons.function;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static org.geektimes.commons.collection.util.CollectionUtils.*;
import static org.geektimes.commons.function.Predicates.and;
import static org.geektimes.commons.function.Predicates.or;

/**
 * The utilities class for {@link Stream}
 *
 * @since 1.0.0
 */
public interface Streams {

    static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    static <T, S extends Iterable<T>> Stream<T> filterStream(S values, Predicate<T> predicate) {
        return stream(values).filter(predicate);
    }

    static <E, L extends List<E>> List<E> filter(L values, Predicate<E> predicate) {
        final L result;
        if (predicate == null) {
            result = values;
        } else {
            result = (L) filterStream(values, predicate).collect(toList());
        }
        return unmodifiableList(result);
    }

    static <E, S extends Set<E>> Set<E> filter(S values, Predicate<E> predicate) {
        final S result;
        if (predicate == null) {
            result = values;
        } else {
            result = (S) filterStream(values, predicate).collect(Collectors.toSet());
        }
        return unmodifiableSet(result);
    }

    static <E, Q extends Queue<E>> Queue<E> filter(Q values, Predicate<E> predicate) {
        final Q result;
        if (predicate == null) {
            result = values;
        } else {
            result = (Q) filterStream(values, predicate).collect(LinkedList::new, List::add, List::addAll);
        }
        return unmodifiableQueue(result);
    }

    static <T, S extends Iterable<T>> S filter(S values, Predicate<T> predicate) {
        if (isSet(values)) {
            return (S) filter((Set) values, predicate);
        } else if (isList(values)) {
            return (S) filter((List) values, predicate);
        } else if (isQueue(values)) {
            return (S) filter((Queue) values, predicate);
        }
        String message = format("The 'values' type can't be supported!", values.getClass().getName());
        throw new UnsupportedOperationException(message);
    }

    static <T, S extends Iterable<T>> S filterAll(S values, Predicate<T>... predicates) {
        return filter(values, and(predicates));
    }

    static <T, S extends Iterable<T>> S filterAny(S values, Predicate<T>... predicates) {
        return filter(values, or(predicates));
    }

    static <T> T filterFirst(Iterable<T> values, Predicate<T>... predicates) {
        return stream(values)
                .filter(and(predicates))
                .findFirst()
                .orElse(null);
    }

    static <T, R> List<R> map(List<T> values, Function<T, R> mapper) {
        return stream(values)
                .map(mapper)
                .collect(Collectors.toList());
    }

    static <T, R> Set<R> map(Set<T> values, Function<T, R> mapper) {
        return stream(values)
                .map(mapper)
                .collect(Collectors.toSet());
    }
}


