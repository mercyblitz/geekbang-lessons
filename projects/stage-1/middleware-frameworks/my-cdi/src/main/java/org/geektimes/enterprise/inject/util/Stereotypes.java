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

import javax.enterprise.inject.Stereotype;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableSet;
import static org.geektimes.commons.collection.util.CollectionUtils.ofSet;
import static org.geektimes.commons.lang.util.AnnotationUtils.getAllDeclaredAnnotations;
import static org.geektimes.commons.lang.util.AnnotationUtils.isMetaAnnotation;

/**
 * The utilities class for {@link Stereotype}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Stereotypes {

    public static boolean isStereotype(Annotation annotation) {
        return isStereotype(annotation.annotationType());
    }

    public static boolean isStereotype(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotation() && isMetaAnnotation(annotationType, Stereotype.class);
    }

    public static Set<Annotation> getAllStereotypes(AnnotatedElement annotatedElement) {
        return ofSet(getAllDeclaredAnnotations(annotatedElement, Stereotypes::isStereotype));
    }

    public static Set<Class<? extends Annotation>> getStereotypeTypes(AnnotatedElement annotatedElement) {
        Set<Annotation> stereotypes = getAllStereotypes(annotatedElement);
        return unmodifiableSet(stereotypes.stream()
                .map(Annotation::annotationType)
                .collect(Collectors.toSet()));
    }
}
