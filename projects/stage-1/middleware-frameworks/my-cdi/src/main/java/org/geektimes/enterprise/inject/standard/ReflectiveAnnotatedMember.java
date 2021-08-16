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
package org.geektimes.enterprise.inject.standard;

import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * The abstract implementation of {@link AnnotatedMember} based on Java reflection {@link Member}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class ReflectiveAnnotatedMember<A extends AnnotatedElement, M extends Member, X>
        extends ReflectiveAnnotated<A> implements AnnotatedMember<X> {

    private final M member;

    private final boolean staticMember;

    private final AnnotatedType<X> declaringType;

    public ReflectiveAnnotatedMember(A annotatedElement, M member) {
        this(annotatedElement, member, new ReflectiveAnnotatedType<>(member.getDeclaringClass()));
    }

    public ReflectiveAnnotatedMember(A annotatedElement, M member, AnnotatedType<X> declaringType) {
        super(annotatedElement);
        this.member = member;
        this.staticMember = Modifier.isStatic(member.getModifiers());
        this.declaringType = declaringType;
    }

    @Override
    public final M getJavaMember() {
        return member;
    }

    @Override
    public final boolean isStatic() {
        return staticMember;
    }

    @Override
    public final AnnotatedType<X> getDeclaringType() {
        return declaringType;
    }

}
