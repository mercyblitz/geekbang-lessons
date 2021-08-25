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

import java.lang.reflect.*;

/**
 * The utilities class for Java Reflection {@link Member}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class MemberUtils {

    /**
     * check the specified {@link Member member} is static or not ?
     *
     * @param member {@link Member} instance, e.g, {@link Constructor}, {@link Method} or {@link Field}
     * @return Iff <code>member</code> is static one, return <code>true</code>, or <code>false</code>
     */
    public static boolean isStatic(Member member) {
        return member != null && Modifier.isStatic(member.getModifiers());
    }


    /**
     * check the specified {@link Member member} is abstract or not ?
     *
     * @param member {@link Member} instance, e.g, {@link Constructor}, {@link Method} or {@link Field}
     * @return Iff <code>member</code> is static one, return <code>true</code>, or <code>false</code>
     */
    public static boolean isAbstract(Member member) {
        return member != null && Modifier.isAbstract(member.getModifiers());
    }

    public static boolean isNonStatic(Member member) {
        return member != null && !Modifier.isStatic(member.getModifiers());
    }

    /**
     * check the specified {@link Member member} is private or not ?
     *
     * @param member {@link Member} instance, e.g, {@link Constructor}, {@link Method} or {@link Field}
     * @return Iff <code>member</code> is private one, return <code>true</code>, or <code>false</code>
     */
    public static boolean isPrivate(Member member) {
        return member != null && Modifier.isPrivate(member.getModifiers());
    }

    /**
     * check the specified {@link Member member} is public or not ?
     *
     * @param member {@link Member} instance, e.g, {@link Constructor}, {@link Method} or {@link Field}
     * @return Iff <code>member</code> is public one, return <code>true</code>, or <code>false</code>
     */
    public static boolean isPublic(Member member) {
        return member != null && Modifier.isPublic(member.getModifiers());
    }
}
