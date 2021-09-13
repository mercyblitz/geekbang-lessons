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
package org.geektimes.enterprise.inject.standard.annotation;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * {@link AnnotatedParameter} based on Java Reflection {@link Parameter}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveAnnotatedParameter<X> extends ReflectiveAnnotated<Parameter> implements AnnotatedParameter<X> {

    private final int index;

    private final AnnotatedCallable<X> declaringCallable;

    public ReflectiveAnnotatedParameter(Parameter parameter, int index, AnnotatedCallable<X> declaringCallable) {
        super(parameter);
        this.index = index;
        this.declaringCallable = declaringCallable;
    }

    @Override
    public int getPosition() {
        return index;
    }

    @Override
    public AnnotatedCallable<X> getDeclaringCallable() {
        return declaringCallable;
    }

    @Override
    public Type getBaseType() {
        return getAnnotatedElement().getParameterizedType();
    }
}
