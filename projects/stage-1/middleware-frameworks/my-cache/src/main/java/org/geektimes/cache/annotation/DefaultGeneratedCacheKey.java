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
import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.GeneratedCacheKey;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Arrays.deepEquals;
import static java.util.Arrays.deepHashCode;

/**
 * Default {@link GeneratedCacheKey}
 * <p>
 * Defaults to a key generator that uses {@link Arrays#deepHashCode(Object[])}
 * and {@link Arrays#deepEquals(Object[], Object[])} with the array
 * returned by {@link CacheKeyInvocationContext#getKeyParameters()}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CacheKeyInvocationContext
 * @since 1.0.0
 */
class DefaultGeneratedCacheKey implements GeneratedCacheKey {

    private final Object[] parameters;

    DefaultGeneratedCacheKey(CacheKeyInvocationContext context) {
        this.parameters = getParameters(context.getKeyParameters());
    }

    private Object[] getParameters(CacheInvocationParameter[] keyParameters) {
        int size = keyParameters.length;
        Object[] parameters = new Object[keyParameters.length];
        for (int i = 0; i < size; i++) {
            CacheInvocationParameter keyParameter = keyParameters[i];
            parameters[i] = keyParameter.getValue();
        }
        return parameters;
    }

    @Override
    public int hashCode() {
        return deepHashCode(parameters);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof DefaultGeneratedCacheKey) {
            return deepEquals(this.parameters, ((DefaultGeneratedCacheKey) other).parameters);
        } else {
            return Objects.deepEquals(this.parameters, other);
        }
    }

    @Override
    public String toString() {
        return "DefaultGeneratedCacheKey - " + Arrays.toString(parameters);
    }
}
