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

import org.geektimes.commons.util.BaseUtils;

import javax.enterprise.context.NormalScope;
import javax.inject.Qualifier;
import javax.inject.Scope;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import static org.geektimes.commons.util.AnnotationUtils.findAnnotation;
import static org.geektimes.commons.util.AnnotationUtils.isMetaAnnotation;

/**
 * The utilities class for {@link Scope}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Scopes {

    public static Class<? extends Annotation> getScopeType(AnnotatedElement annotatedElement) {
        Annotation scope = findAnnotation(annotatedElement, Scopes::isScope);
        final Class<? extends Annotation> scopeType;
        if (scope != null) {
            scopeType = scope.annotationType();
        } else {
            scopeType = NormalScope.class;
        }
        return scopeType;
    }

    public static boolean isScope(Annotation annotation) {
        return isScope(annotation.annotationType());
    }

    public static boolean isScope(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotation() && isMetaAnnotation(annotationType, Scope.class);
    }
}
