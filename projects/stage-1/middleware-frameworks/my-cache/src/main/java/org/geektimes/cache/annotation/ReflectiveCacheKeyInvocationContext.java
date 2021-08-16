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
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.CacheValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.geektimes.commons.lang.util.AnnotationUtils.contains;

/**
 * {@link CacheKeyInvocationContext} based on reflection
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveCacheKeyInvocationContext<A extends Annotation> extends ReflectiveCacheInvocationContext<A>
        implements CacheKeyInvocationContext<A> {

    private final CacheInvocationParameter valueParameter;

    private final CacheInvocationParameter[] keyParameters;

    public ReflectiveCacheKeyInvocationContext(Object target, Method method, Object... parameterValues) {
        super(target, method, parameterValues);
        CacheInvocationParameter[] allParameters = getAllParameters();
        this.valueParameter = resolveValueParameter(allParameters);
        this.keyParameters = resolveKeyParameters(allParameters);
    }

    private CacheInvocationParameter[] resolveKeyParameters(CacheInvocationParameter[] parameters) {
        List<CacheInvocationParameter> keyParameters = new LinkedList<>(Arrays.asList(parameters));
        if (valueParameter != null) {
            keyParameters.remove(valueParameter);
        }

        List<CacheInvocationParameter> includedKeyParameters = new LinkedList<>();
        Iterator<CacheInvocationParameter> iterator = keyParameters.iterator();
        while (iterator.hasNext()) {
            CacheInvocationParameter parameter = iterator.next();
            if (containsCacheKey(parameter)) {
                includedKeyParameters.add(parameter);
            }
        }

        return includedKeyParameters.isEmpty() ?
                keyParameters.toArray(new CacheInvocationParameter[0]) :
                includedKeyParameters.toArray(new CacheInvocationParameter[0]);
    }

    private CacheInvocationParameter resolveValueParameter(CacheInvocationParameter[] parameters) {
        CacheInvocationParameter valueParameter = null;
        for (CacheInvocationParameter parameter : parameters) {
            if (containsCacheValue(parameter)) {
                valueParameter = parameter;
                break;
            }
        }
        return valueParameter;
    }

    private boolean containsCacheKey(CacheInvocationParameter parameter) {
        return contains(parameter.getAnnotations(), CacheKey.class);
    }

    private boolean containsCacheValue(CacheInvocationParameter parameter) {
        return contains(parameter.getAnnotations(), CacheValue.class);
    }

    @Override
    public CacheInvocationParameter[] getKeyParameters() {
        return keyParameters;
    }

    @Override
    public CacheInvocationParameter getValueParameter() {
        return valueParameter;
    }

    @Override
    public String toString() {
        return "ReflectiveCacheKeyInvocationContext{" +
                "valueParameter=" + valueParameter +
                ", keyParameters=" + Arrays.toString(keyParameters) +
                "} " + super.toString();
    }
}
