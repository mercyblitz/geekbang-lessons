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
package org.geektimes.interceptor.cglib;


import net.sf.cglib.proxy.Enhancer;
import org.geektimes.interceptor.InterceptorEnhancer;

import javax.interceptor.Interceptor;

/**
 * {@link Interceptor @Interceptor} enhancer by CGLIB
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class CglibInterceptorEnhancer implements InterceptorEnhancer {

    @Override
    public <T> T enhance(T source, Class<? super T> type, Object... interceptors) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallback(new MethodInterceptorAdapter(source, interceptors));
        return (T) enhancer.create();
    }
}
