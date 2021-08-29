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

import static org.geektimes.commons.reflect.util.MemberUtils.isPublic;

/**
 * The utilities class of {@link Constructor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class ConstructorUtils {

    /**
     * Is a public no-arg constructor or not ?
     *
     * @param constructor {@link Constructor}
     * @return <code>true</code> if the given {@link Constructor} is a public no-arg one,
     * otherwise <code>false</code>
     */
    public static boolean isPublicNoArgConstructor(Constructor<?> constructor) {
        return isPublic(constructor) && constructor.getParameterCount() == 0;
    }

    public static boolean hasPublicNoArgConstructor(Class<?> type) {
        boolean has = false;
        for (Constructor<?> constructor : type.getConstructors()) {
            if (isPublicNoArgConstructor(constructor)) {
                has = true;
                break;
            }
        }
        return has;
    }
}
