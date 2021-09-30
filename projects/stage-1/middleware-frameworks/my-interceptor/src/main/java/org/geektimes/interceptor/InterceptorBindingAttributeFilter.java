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

import org.geektimes.commons.function.Predicates;

import javax.interceptor.InterceptorBinding;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import static org.geektimes.commons.util.ServiceLoaders.loadAsArray;

/**
 * The attribute filter of {@lnk InterceptorBinding}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface InterceptorBindingAttributeFilter extends Predicate<Method> {

    default boolean test(Method attributeMethod) {
        return accept(attributeMethod);
    }

    /**
     * @param attributeMethod the attribute method declared in the
     *                        {@link InterceptorBinding interceptor binding}
     * @return <code>true</code> if attribute method is accepted, <code>false</code> otherwise
     */
    boolean accept(Method attributeMethod);

    static Predicate<Method>[] filters() {
        return loadAsArray(InterceptorBindingAttributeFilter.class);
    }

}
