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
package org.geektimes.interceptor.util;

import org.geektimes.commons.lang.util.AnnotationUtils;

import javax.interceptor.Interceptor;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.geektimes.commons.reflect.util.ConstructorUtils.hasPublicNoArgConstructor;

/**
 * The utilities class for {@link Interceptor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Interceptors {

    public static boolean isInterceptorClass(Class<?> interceptorClass) {
        // TODO
        return false;
    }

    /**
     * An interceptor class must not be abstract and must have a public no-arg constructor.
     *
     * @param interceptorClass the class of interceptor
     * @throws NullPointerException  If <code>interceptorClass</code> is <code>null</code>
     * @throws IllegalStateException If an interceptor class is abstract or have not a public no-arg constructor
     */
    public static void validatorInterceptorClass(Class<?> interceptorClass) throws NullPointerException, IllegalStateException {
        requireNonNull(interceptorClass, "The argument 'interceptorClass' must not be null!");
        int modifies = interceptorClass.getModifiers();
        if (Modifier.isAbstract(modifies)) {
            throw new IllegalStateException(format("The Interceptor class[%s] must not be abstract!",
                    interceptorClass.getName()));
        }
        if (!hasPublicNoArgConstructor(interceptorClass)) {
            throw new IllegalStateException(format("The Interceptor class[%s] must have a public no-arg constructor!",
                    interceptorClass.getName()));
        }
    }

    public static boolean isInterceptorBinding(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotationPresent(InterceptorBinding.class);
    }

    public static boolean isInterceptor(AnnotatedElement annotatedElement) {
        return AnnotationUtils.isAnnotationPresent(annotatedElement, Interceptor.class);
    }
}
