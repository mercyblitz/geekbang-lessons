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
package org.geektimes.cache.annotation;

import javax.cache.annotation.CacheInvocationParameter;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Set;

/**
 * {@link CacheInvocationParameter} based on Reflection.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveCacheInvocationParameter implements CacheInvocationParameter, Serializable {

    private final Class<?> parameterType;

    private final Object parameterValue;

    private final Set<Annotation> parameterAnnotations;

    private final int parameterIndex;

    public ReflectiveCacheInvocationParameter(Parameter parameter, int parameterIndex, Object parameterValue) {
        this.parameterType = parameter.getType();
        this.parameterValue = parameterValue;
        this.parameterAnnotations = ReflectiveCacheMethodDetails.getAnnotations(parameter.getAnnotations());
        this.parameterIndex = parameterIndex;
    }

    @Override
    public Class<?> getRawType() {
        return parameterType;
    }

    @Override
    public Object getValue() {
        return parameterValue;
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return parameterAnnotations;
    }

    @Override
    public int getParameterPosition() {
        return parameterIndex;
    }

    @Override
    public String toString() {
        return "ReflectiveCacheInvocationParameter{" +
                "parameterType=" + parameterType +
                ", parameterValue=" + parameterValue +
                ", parameterAnnotations=" + parameterAnnotations +
                ", parameterIndex=" + parameterIndex +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReflectiveCacheInvocationParameter that = (ReflectiveCacheInvocationParameter) o;
        return parameterIndex == that.parameterIndex
                && Objects.equals(parameterType, that.parameterType)
                && Objects.equals(parameterValue, that.parameterValue)
                && Objects.equals(parameterAnnotations, that.parameterAnnotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameterType, parameterValue, parameterAnnotations, parameterIndex);
    }
}
