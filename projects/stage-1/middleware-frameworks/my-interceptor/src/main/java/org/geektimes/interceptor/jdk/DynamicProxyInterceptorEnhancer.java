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
package org.geektimes.interceptor.jdk;

import org.geektimes.interceptor.InterceptorEnhancer;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.geektimes.commons.lang.util.ClassLoaderUtils.getClassLoader;

/**
 * {@link InterceptorEnhancer} based on JDK Dynamic Proxy
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DynamicProxyInterceptorEnhancer implements InterceptorEnhancer {

    @Override
    public <T> T enhance(T source, Class<? super T> type, Object... interceptors) {
        ClassLoader classLoader = getClassLoader(type);
        return (T) newProxyInstance(classLoader, new Class[]{type}, new InvocationHandlerAdapter(source, interceptors));
    }
}
