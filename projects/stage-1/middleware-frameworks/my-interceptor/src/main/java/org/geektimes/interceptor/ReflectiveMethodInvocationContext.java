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
 * {@link Method} {@link InvocationContext}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveMethodInvocationContext implements InvocationContext {

    private final Object target;

    private final Method method;

    private Object[] parameters;

    private final Map<String, Object> contextData;

    public ReflectiveMethodInvocationContext(Object target, Method method, Object... parameters) {
        requireNonNull(target, "The target instance must not be null");
        requireNonNull(method, "The method must not be null");
        this.target = target;
        this.method = method;
        this.setParameters(parameters);
        this.contextData = new HashMap<>();
    }

    @Override
    public final Object getTarget() {
        return target;
    }

    @Override
    public final Object getTimer() {
        return null;
    }

    @Override
    public final Method getMethod() {
        return method;
    }

    @Override
    public final Constructor<?> getConstructor() {
        return null;
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
        return method.invoke(getTarget(), getParameters());
    }
}
