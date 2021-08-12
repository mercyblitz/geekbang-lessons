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

import org.geektimes.commons.util.AnnotationUtils;
import org.geektimes.commons.util.BaseUtils;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import static org.geektimes.commons.util.AnnotationUtils.getAllDeclaredAnnotations;
import static org.geektimes.commons.util.AnnotationUtils.isAnnotated;
import static org.geektimes.commons.util.CollectionUtils.ofSet;

/**
 * The utilities class for {@link Qualifier}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Qualifiers {

    public static boolean isQualifier(Annotation annotation) {
        return isQualifier(annotation.annotationType());
    }

    public static boolean isQualifier(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotation() && isAnnotated(annotationType, Qualifier.class);
    }

    public static Set<Annotation> getAllQualifiers(Class<?> beanClass) {
        List<Annotation> qualifiers = getAllDeclaredAnnotations(beanClass, Qualifiers::isQualifier);
        if (qualifiers.isEmpty()) {
            return ofSet(Any.Literal.INSTANCE, Default.Literal.INSTANCE);
        } else {
            return ofSet(qualifiers);
        }
    }
}
