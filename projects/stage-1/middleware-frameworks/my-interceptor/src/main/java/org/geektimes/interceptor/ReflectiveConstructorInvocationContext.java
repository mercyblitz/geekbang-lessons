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
package org.geektimes.interceptor;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * {@link Constructor} {@link InvocationContext}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveConstructorInvocationContext implements InvocationContext {

    private final Constructor constructor;

    private Object[] parameters;

    private final Map<String, Object> contextData;

    public ReflectiveConstructorInvocationContext(Constructor constructor, Object... parameters) {
        requireNonNull(constructor, "The argument 'constructor' must not be null");
        requireNonNull(parameters, "The arguments 'parameters' must not be null");
        this.constructor = constructor;
        this.setParameters(parameters);
        this.contextData = new HashMap<>();
    }

    @Override
    public final Object getTarget() {
        return null;
    }

    @Override
    public final Object getTimer() {
        return null;
    }

    @Override
    public final Method getMethod() {
        return null;
    }

    @Override
    public final Constructor<?> getConstructor() {
        return constructor;
    }

    @Override
    public final Object[] getParameters() {
        return parameters;
    }

    @Override
    public final void setParameters(Object[] params) {
        this.parameters = params != null ? params : new Object[0];
    }

    @Override
    public final Map<String, Object> getContextData() {
        return contextData;
    }

    @Override
    public Object proceed() throws Exception {
        return constructor.newInstance(parameters);
    }
}
