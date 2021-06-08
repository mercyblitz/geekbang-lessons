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
package org.geektimes.microprofile.rest;

import java.lang.reflect.Method;

/**
 * {@link RequestTemplate} resolver
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-14
 */
public interface RequestTemplateResolver {

    /**
     * Resolve an instance of {@link RequestTemplate} by the specified
     * resource class and method.
     *
     * @param resourceClass  the resource class
     * @param resourceMethod the resource method
     * @return {@link RequestTemplate} if can be resolved
     */
    RequestTemplate resolve(Class<?> resourceClass, Method resourceMethod);
}
