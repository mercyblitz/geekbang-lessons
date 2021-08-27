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

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * The implementation of {@link AnnotatedCallable} based on Java reflection {@link Executable}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveAnnotatedCallable<E extends Executable, X> extends
        ReflectiveAnnotatedMember<E, E, X> implements AnnotatedCallable<X> {

    private List<AnnotatedParameter<X>> annotatedParameters;

    public ReflectiveAnnotatedCallable(E executable) {
        super(executable, executable);
    }

    public ReflectiveAnnotatedCallable(E executable, AnnotatedType<X> declaringType) {
        super(executable, executable, declaringType);
    }

    @Override
    public final List<AnnotatedParameter<X>> getParameters() {

        if (annotatedParameters != null) {
            return annotatedParameters;
        }

        Executable executable = getAnnotatedElement();
        int size = executable.getParameterCount();

        if (size < 1) {
            return emptyList();
        }

        List<AnnotatedParameter<X>> annotatedParameters = new ArrayList<>(size);

        Parameter[] parameters = executable.getParameters();
        for (int i = 0; i < size; i++) {
            Parameter parameter = parameters[i];
            annotatedParameters.add(new ReflectiveAnnotatedParameter<>(parameter, i, this));
        }

        annotatedParameters = unmodifiableList(annotatedParameters);

        this.annotatedParameters = annotatedParameters;

        return annotatedParameters;
    }

    @Override
    public Type getBaseType() {
        E executable = getAnnotatedElement();
        if (executable instanceof Constructor) {
            return executable.getDeclaringClass();
        } else if (executable instanceof Method) {
            return ((Method) executable).getReturnType();
        }
        throw new UnsupportedOperationException();
    }
}
