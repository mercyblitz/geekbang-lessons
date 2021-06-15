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

import java.lang.reflect.Constructor;
import java.util.Arrays;

import static org.geektimes.commons.function.ThrowableSupplier.execute;

/**
 * {@link Exception} Utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface ExceptionUtils {

    static <T extends Throwable> T wrapThrowable(Throwable source, Class<T> exceptionType) {
        String message = source.getMessage();
        Throwable cause = source.getCause();

        Constructor[] constructors = exceptionType.getConstructors();

        if (constructors.length == 0) {
            throw new IllegalArgumentException("The exceptionType must have one public constructor.");
        }

        Arrays.sort(constructors, (o1, o2) -> Integer.compare(o2.getParameterCount(), o1.getParameterCount()));

        // find the longest arguments constructor
        Constructor constructor = constructors[0];
        Class[] parameterTypes = constructor.getParameterTypes();
        int parameterTypesLength = parameterTypes.length;
        Object[] parameters = new Object[parameterTypesLength];
        for (int i = 0; i < parameterTypesLength; i++) {
            Class parameterType = parameterTypes[i];
            if (String.class.isAssignableFrom(parameterType)) {
                parameters[i] = message;
            }
            if (Throwable.class.isAssignableFrom(parameterType)) {
                parameters[i] = cause;
            }
        }
        return execute(() -> (T) constructor.newInstance(parameters));
    }
}
