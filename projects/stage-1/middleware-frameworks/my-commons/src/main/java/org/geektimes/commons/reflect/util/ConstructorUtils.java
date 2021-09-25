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

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.geektimes.commons.function.Streams.filter;
import static org.geektimes.commons.reflect.util.MemberUtils.isPrivate;

/**
 * The utilities class of {@link Constructor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class ConstructorUtils {

    /**
     * Is a non-private constructor without parameters
     *
     * @param constructor {@link Constructor}
     * @return <code>true</code> if the given {@link Constructor} is a public no-arg one,
     * otherwise <code>false</code>
     */
    public static boolean isNonPrivateConstructorWithoutParameters(Constructor<?> constructor) {
        return !isPrivate(constructor) && constructor.getParameterCount() < 1;
    }

    public static boolean hasNonPrivateConstructorWithoutParameters(Class<?> type) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        boolean has = false;
        for (Constructor<?> constructor : constructors) {
            if (isNonPrivateConstructorWithoutParameters(constructor)) {
                has = true;
                break;
            }
        }
        return has;
    }

    public static List<Constructor<?>> getConstructors(Class<?> type,
                                                       Predicate<? super Constructor<?>>... constructorFilters) {
        List<Constructor<?>> constructors = asList(type.getConstructors());
        return filter(constructors, constructorFilters);
    }
}
