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

import org.geektimes.interceptor.cglib.CglibComponentEnhancer;
import org.geektimes.interceptor.jdk.DynamicProxyComponentEnhancer;

import static org.geektimes.commons.lang.util.ClassLoaderUtils.getClassLoader;

/**
 * Default {@link ComponentEnhancer}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DefaultComponentEnhancer implements ComponentEnhancer {

    private final ComponentEnhancer jdkProxyInterceptorEnhancer;

    private final ComponentEnhancer cglibInterceptorEnhancer;

    private final InterceptorManager interceptorManager;

    public DefaultComponentEnhancer() {
        this.jdkProxyInterceptorEnhancer = new DynamicProxyComponentEnhancer();
        this.cglibInterceptorEnhancer = new CglibComponentEnhancer();
        this.interceptorManager = InterceptorManager.getInstance(getClassLoader(this.getClass()));
        this.interceptorManager.registerDiscoveredInterceptors();
    }

    @Override
    public <T> T enhance(T source, Class<? super T> componentClass, Object... defaultInterceptors) {
        assertType(componentClass);
        if (componentClass.isInterface()) {
            return jdkProxyInterceptorEnhancer.enhance(source, componentClass, defaultInterceptors);
        } else {
            return cglibInterceptorEnhancer.enhance(source, componentClass, defaultInterceptors);
        }
    }

    private <T> void assertType(Class<? super T> type) {
        if (type.isAnnotation() || type.isEnum() || type.isPrimitive() || type.isArray()) {
            throw new IllegalArgumentException("The type must be an interface or a class!");
        }
    }
}
