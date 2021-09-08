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

/**
 * Component Enhancer
 * <p>
 * If a component class declares or inherits a class-level interceptor binding,
 * it must not be declared final, or have any non-static, non-private, final
 * methods. If a component has a class-level interceptor binding and is declared
 * final or has a non-static, non-private, final method, the container automatically
 * detects the problem and treats it as a definition error, and causes deployment to
 * fail.
 * <p>
 * If a non-static, non-private method of a component class declares a method-level
 * interceptor binding, neither the method nor the component class may be declared final.
 * If a non-static, non-private, final method of a component has a method-level interceptor
 * binding, the container automatically detects the problem and treats it as a definition
 * error, and causes deployment to fail.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface ComponentEnhancer {

    default <T> T enhance(T component) {
        return enhance(component, (Class<? super T>) component.getClass());
    }

    default <T> T enhance(T component, Class<? super T> componentClass) {
        return enhance(component, componentClass, new Object[0]);
    }

    default <T> T enhance(T component, Object... defaultInterceptors) {
        return enhance(component, (Class<? super T>) component.getClass(), defaultInterceptors);
    }

    <T> T enhance(T component, Class<? super T> componentClass, Object... defaultInterceptors);

}
