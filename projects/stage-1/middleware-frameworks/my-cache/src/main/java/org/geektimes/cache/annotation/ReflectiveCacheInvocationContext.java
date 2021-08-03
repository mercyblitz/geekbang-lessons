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

import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheInvocationParameter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.geektimes.cache.annotation.util.CacheAnnotationUtils.findCacheAnnotation;
import static org.geektimes.cache.annotation.util.CacheAnnotationUtils.findCacheName;

/**
 * {@link CacheInvocationContext} based on reflection.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveCacheInvocationContext<A extends Annotation> extends ReflectiveCacheMethodDetails<A>
        implements CacheInvocationContext<A> {

    private final Object target;

    private final CacheInvocationParameter[] allParameters;

    public ReflectiveCacheInvocationContext(Object target, Method method, Object... parameterValues) {
        super(method);
        requireNonNull(target, "The 'target' argument must not be null!");
        requireNonNull(parameterValues, "The 'parameterValues' argument must not be null!");
        assertMethodParameterCounts(method, parameterValues);
        this.target = target;
        this.allParameters = resolveAllParameters(method, parameterValues);
    }

    protected String resolveCacheName() {
        return findCacheName(getCacheAnnotation(), getMethod(), getTarget());
    }

    protected A resolveCacheAnnotation() {
        return findCacheAnnotation(getMethod());
    }

    private void assertMethodParameterCounts(Method method, Object[] parameterValues) {
        if (method.getParameterCount() != parameterValues.length) {
            throw new IllegalArgumentException(format("The count[%d] of method parameters must equal the length[%d] of "
                    + "the parameter values", method.getParameterCount(), parameterValues.length));
        }
    }

    private CacheInvocationParameter[] resolveAllParameters(Method method, Object[] parameterValues) {
        int parameterCount = getMethod().getParameterCount();
        Parameter[] parameters = method.getParameters();
        CacheInvocationParameter[] allParameters = new CacheInvocationParameter[parameterCount];
        for (int index = 0; index < parameterCount; index++) {
            allParameters[index] = new ReflectiveCacheInvocationParameter(parameters[index], index, parameterValues[index]);
        }
        return allParameters;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public CacheInvocationParameter[] getAllParameters() {
        return allParameters;
    }

    @Override
    public Object unwrap(Class cls) {
        Object instance = null;
        try {
            instance = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(format("The class[%s] must contain a public non-argument constructor"
                    , cls.getName()));
        }
        return instance;
    }

    @Override
    public String toString() {
        return "ReflectiveCacheInvocationContext{" +
                "target=" + target +
                ", allParameters=" + Arrays.toString(allParameters) +
                "} " + super.toString();
    }
}
