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

import java.util.function.Consumer;
import java.util.function.Function;

import static org.geektimes.commons.util.ExceptionUtils.wrapThrowable;

/**
 * A function interface for {@link Consumer} with {@link Throwable}
 *
 * @param <T> the type to be consumed
 * @see Consumer
 * @see Throwable
 */
@FunctionalInterface
public interface ThrowableConsumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     * @throws Throwable if met with error
     */
    void accept(T t) throws Throwable;

    /**
     * Executes {@link ThrowableConsumer}
     *
     * @param t        the input argument
     * @param consumer {@link ThrowableConsumer}
     * @throws RuntimeException wrap {@link Exception} to {@link RuntimeException}
     */
    static <T> void execute(T t, ThrowableConsumer consumer) throws RuntimeException {
        execute(t, consumer, RuntimeException.class);
    }

    /**
     * Executes {@link ThrowableConsumer}
     *
     * @param t        the input argument
     * @param consumer {@link ThrowableConsumer}
     * @throws T wrap {@link Throwable} to the specified {@link Throwable} type
     */
    static <E, T extends Throwable> void execute(E t, ThrowableConsumer consumer, Class<T> throwableType) throws T {
        try {
            consumer.accept(t);
        } catch (Throwable e) {
            throw wrapThrowable(e, throwableType);
        }
    }
}
