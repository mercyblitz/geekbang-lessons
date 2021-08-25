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
package org.geektimes.enterprise.inject.util;

import javax.enterprise.inject.spi.DefinitionException;

import static java.lang.String.format;

/**
 * The utilities class for {@link Exception}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Exceptions {

    public static DefinitionException newDefinitionException(String errorMessage) {
        return new DefinitionException(errorMessage);
    }

    public static DefinitionException newDefinitionException(String errorMessage, Throwable cause) {
        if (cause == null) {
            return newDefinitionException(errorMessage);
        }
        return new DefinitionException(errorMessage, cause);
    }

    public static DefinitionException newDefinitionException(String messagePattern, Throwable cause, Object... args) {
        String errorMessage = format(messagePattern, args);
        return newDefinitionException(errorMessage, cause);
    }

    public static DefinitionException newDefinitionException(String messagePattern, Object... args) {
        String errorMessage = format(messagePattern, args);
        return newDefinitionException(errorMessage);
    }
}
