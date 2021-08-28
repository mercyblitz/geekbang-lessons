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

import org.geektimes.enterprise.inject.util.Qualifiers;

import javax.decorator.Delegate;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

/**
 * Abstract {@link InjectionPoint} based on Java Reflection
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class AbstractInjectionPoint<A extends Annotated, AM extends AnnotatedMember, M extends Member>
        implements InjectionPoint {

    private final A annotated;

    private final M member;

    private final Bean<?> bean;

    public AbstractInjectionPoint(A annotated, AM annotatedMember, Bean<?> bean) {
        requireNonNull(annotated, "The 'annotated' argument must not be null!");
        requireNonNull(annotatedMember, "The 'member' argument must not be null!");
        this.annotated = annotated;
        this.member = (M) annotatedMember.getJavaMember();
        this.bean = bean;
    }

    @Override
    public final Type getType() {
        return getAnnotated().getBaseType();
    }

    @Override
    public final Set<Annotation> getQualifiers() {
        return Qualifiers.getQualifiers(getAnnotated().getAnnotations());
    }

    @Override
    public final Bean<?> getBean() {
        return bean;
    }

    @Override
    public final M getMember() {
        return member;
    }

    @Override
    public final A getAnnotated() {
        return annotated;
    }

    @Override
    public final boolean isDelegate() {
        return getAnnotated().isAnnotationPresent(Delegate.class);
    }

    @Override
    public final boolean isTransient() {
        return Modifier.isTransient(getMember().getModifiers());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("type=" + getType())
                .add("bean=" + getBean())
                .add("member=" + getMember())
                .add("qualifiers=" + getQualifiers())
                .add("annotated=" + getAnnotated())
                .add("isDelegate=" + isDelegate())
                .add("isTransient=" + isTransient())
                .toString();
    }
}