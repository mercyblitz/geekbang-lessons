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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.geektimes.commons.lang.util.ClassLoaderUtils.getClassLoader;
import static org.geektimes.commons.reflect.util.ClassUtils.isPresent;
import static org.geektimes.commons.reflect.util.ClassUtils.resolveClass;

/**
 * {@link InterceptorBindingAttributeFilter} for {@link Nonbinding}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class NonInterceptorBindingAttributeFilter implements InterceptorBindingAttributeFilter {

    private static final String NON_BINDING_ANNOTATION_CLASS_NAME = "javax.enterprise.util.Nonbinding";

    private static final ClassLoader classLoader = getClassLoader(NonInterceptorBindingAttributeFilter.class);

    private static final boolean NON_BINDING_ANNOTATION_ABSENT = isPresent(NON_BINDING_ANNOTATION_CLASS_NAME, classLoader);

    @Override
    public boolean accept(Method attributeMethod) {
        if (NON_BINDING_ANNOTATION_ABSENT) {
            Class<? extends Annotation> nonbindingClass = (Class<? extends Annotation>)
                    resolveClass(NON_BINDING_ANNOTATION_CLASS_NAME, classLoader);
            return !attributeMethod.isAnnotationPresent(nonbindingClass);
        }
        return true;
    }
}
