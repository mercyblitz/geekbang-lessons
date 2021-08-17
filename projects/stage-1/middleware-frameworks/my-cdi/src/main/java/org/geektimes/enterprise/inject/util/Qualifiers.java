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

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.geektimes.commons.collection.util.CollectionUtils.ofSet;
import static org.geektimes.commons.lang.util.AnnotationUtils.*;

/**
 * The utilities class for {@link Qualifier}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Qualifiers {

    static final Map<AnnotatedElement, Set<Annotation>> qualifiersCache = new ConcurrentHashMap<>();

    public static boolean isQualifier(Annotation annotation) {
        return isQualifier(annotation.annotationType());
    }

    public static boolean isQualifier(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotation() && isAnnotationPresent(annotationType, Qualifier.class);
    }

    public static Set<Annotation> getQualifiers(AnnotatedElement annotatedElement) {
        return qualifiersCache.computeIfAbsent(annotatedElement, e -> {
            List<Annotation> annotations = getAllDeclaredAnnotations(e);
            return getQualifiers(annotations);
        });
    }

    public static Set<Annotation> getQualifiers(Collection<Annotation> annotations) {
        Set<Annotation> qualifiers = filterAnnotations(new LinkedHashSet<>(annotations), Qualifiers::isQualifier);
        return ofSet(qualifiers, Any.Literal.INSTANCE, Default.Literal.INSTANCE);
    }

    public static <A extends Annotation> A findQualifier(AnnotatedElement annotatedElement, Class<A> qualifierType) {
        Set<Annotation> qualifiers = getQualifiers(annotatedElement);
        Annotation foundQualifier = null;
        for (Annotation qualifier : qualifiers) {
            if (qualifierType.equals(qualifier.annotationType())) {
                foundQualifier = qualifier;
                break;
            }
        }
        return (A) foundQualifier;
    }
}
