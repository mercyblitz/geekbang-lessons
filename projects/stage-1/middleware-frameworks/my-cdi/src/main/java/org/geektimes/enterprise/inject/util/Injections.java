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
package org.geektimes.enterprise.inject.util;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.DefinitionException;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static java.lang.String.format;

/**
 * The utilities class for injection.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Injections {

    public static void validateForbiddenAnnotation(Method initializerMethod,
                                                   Class<? extends Annotation> annotationType) {
        if (initializerMethod.isAnnotationPresent(annotationType)) {
            String errorMessage = format("The Initializer Method[%s] must not annotate @%s!",
                    initializerMethod,
                    annotationType.getName()
            );
            throw new DefinitionException(errorMessage);
        }
    }

    public static void validateForbiddenAnnotation(InjectionPoint injectionPoint,
                                                   Class<? extends Annotation> annotationType) {
        if (injectionPoint.getAnnotated().isAnnotationPresent(annotationType)) {
            Member member = injectionPoint.getMember();
            String errorMessage = format("The @Inject %s[%s] must not annotate @%s!",
                    member.getClass().getSimpleName(),
                    member,
                    annotationType.getName()
            );
            throw new DefinitionException(errorMessage);
        }
    }
}
