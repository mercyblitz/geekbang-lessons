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
import static org.geektimes.commons.function.Predicates.*;

/**
 * The utilities class for {@link Stream}
 *
 * @since 1.0.0
 */
public interface Streams {

    static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    static <T, I extends Iterable<T>> Stream<T> filterStream(I values, Predicate<? super T> predicate) {
        return filterStream(values, predicate, EMPTY_ARRAY);
    }

    static <T, I extends Iterable<T>> Stream<T> filterStream(I values, Predicate<? super T>... predicates) {
        return filterStream(values, alwaysTrue(), predicates);
    }

    static <T, I extends Iterable<T>> Stream<T> filterStream(I values, Predicate<? super T> predicate,
                                                             Predicate<? super T>... otherPredicates) {
        return stream(values).filter(and(predicate, otherPredicates));
    }

    static <E, L extends List<E>> List<E> filter(L values, Predicate<? super E> predicate) {
        return filter(values, predicate, EMPTY_ARRAY);
    }

    static <E, L extends List<E>> List<E> filter(L values, Predicate<? super E>... predicates) {
        return filter(values, alwaysTrue(), predicates);
    }

    static <E, L extends List<E>> List<E> filter(L values, Predicate<? super E> predicate, Predicate<? super E>... otherPredicates) {
        final L result;
        if (predicate == null) {
            result = values;
        } else {
            result = (L) filterStream(values, predicate, otherPredicates).collect(toList());
        }
        return unmodifiableList(result);
    }

    static <E, S extends Set<E>> Set<E> filter(S values, Predicate<? super E> predicate) {
        return filter(values, predicate, EMPTY_ARRAY);
    }

    static <E, S extends Set<E>> Set<E> filter(S values, Predicate<? super E>... predicates) {
        return filter(values, alwaysTrue(), predicates);
    }

    static <E, S extends Set<E>> Set<E> filter(S values, Predicate<? super E> predicate,
                                               Predicate<? super E>... otherPredicates) {
        final S result;
        if (predicate == null) {
            result = values;
        } else {
            result = (S) filterStream(values, predicate, otherPredicates).collect(Collectors.toSet());
        }
        return unmodifiableSet(result);
    }

    static <E, Q extends Queue<E>> Queue<E> filter(Q values, Predicate<? super E> predicate) {
        return filter(values, predicate, EMPTY_ARRAY);
    }

    static <E, Q extends Queue<E>> Queue<E> filter(Q values, Predicate<? super E>... predicates) {
        return filter(values, alwaysTrue(), predicates);
    }

    static <E, Q extends Queue<E>> Queue<E> filter(Q values, Predicate<? super E> predicate,
                                                   Predicate<? super E>... otherPredicates) {
        final Q result;
        if (predicate == null) {
            result = values;
        } else {
            result = (Q) filterStream(values, predicate, otherPredicates)
                    .collect(LinkedList::new, List::add, List::addAll);
        }
        return unmodifiableQueue(result);
    }

    static <T, S extends Iterable<T>> S filter(S values, Predicate<? super T> predicate) {
        return (S) filter(values, predicate, EMPTY_ARRAY);
    }

    static <T, S extends Iterable<T>> S filter(S values, Predicate<? super T>... predicates) {
        return filter(values, alwaysTrue(), predicates);
    }

    static <T, S extends Iterable<T>> S filter(S values, Predicate<? super T> predicate,
                                               Predicate<? super T>... otherPredicates) {
        if (isSet(values)) {
            return (S) filter((Set) values, predicate, otherPredicates);
        } else if (isList(values)) {
            return (S) filter((List) values, predicate, otherPredicates);
        } else if (isQueue(values)) {
            return (S) filter((Queue) values, predicate, otherPredicates);
        }
        String message = format("The 'values' type can't be supported!", values.getClass().getName());
        throw new UnsupportedOperationException(message);
    }

    static <T, S extends Iterable<T>> S filterAny(S values, Predicate<? super T>... predicates) {
        return filterAny(values, alwaysTrue(), predicates);
    }

    static <T, S extends Iterable<T>> S filterAny(S values, Predicate<? super T> predicate,
                                                  Predicate<? super T>... otherPredicates) {
        return filter(values, or(predicate, otherPredicates));
    }

    static <T> T filterFirst(Iterable<T> values, Predicate<? super T> predicate) {
        return (T) filterFirst(values, predicate, EMPTY_ARRAY);
    }

    static <T> T filterFirst(Iterable<T> values, Predicate<? super T>... predicates) {
        return filterFirst(values, alwaysTrue(), predicates);
    }

    static <T> T filterFirst(Iterable<T> values, Predicate<? super T> predicate,
                             Predicate<? super T>... otherPredicates) {
        return stream(values)
                .filter(and(predicate, otherPredicates))
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


