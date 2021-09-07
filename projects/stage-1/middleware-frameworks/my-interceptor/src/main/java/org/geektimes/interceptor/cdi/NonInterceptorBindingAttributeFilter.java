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
package org.geektimes.interceptor.cdi;

import org.geektimes.interceptor.InterceptorBindingAttributeFilter;

import javax.enterprise.util.Nonbinding;
import java.lang.reflect.Method;

/**
 * {@link InterceptorBindingAttributeFilter} for {@link Nonbinding}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class NonInterceptorBindingAttributeFilter implements InterceptorBindingAttributeFilter {

    @Override
    public boolean accept(Method attributeMethod) {
        return !attributeMethod.isAnnotationPresent(Nonbinding.class);
    }
}
