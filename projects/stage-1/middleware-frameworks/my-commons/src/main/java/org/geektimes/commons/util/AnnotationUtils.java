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
package org.geektimes.commons.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.geektimes.commons.function.Streams.filterAll;
import static org.geektimes.commons.function.Streams.filterFirst;
import static org.geektimes.commons.reflect.util.ClassUtils.getAllInheritedTypes;
import static org.geektimes.commons.reflect.util.ClassUtils.isDerived;

/**
 * {@link Annotation} Utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class AnnotationUtils extends BaseUtils {

    /**
     * Is the specified type a generic {@link Class type}
     *
     * @param annotatedElement the annotated element
     * @return if <code>annotatedElement</code> is the {@link Class}, return <code>true</code>, or <code>false</code>
     * @see ElementType#TYPE
     */
    static boolean isType(AnnotatedElement annotatedElement) {
        return annotatedElement instanceof Class;
    }

    /**
     * Is the type of specified annotation same to the expected type?
     *
     * @param annotation     the specified {@link Annotation}
     * @param annotationType the expected annotation type
     * @return if same, return <code>true</code>, or <code>false</code>
     */
    static boolean isSameType(Annotation annotation, Class<? extends Annotation> annotationType) {
        if (annotation == null || annotationType == null) {
            return false;
        }
        return Objects.equals(annotation.annotationType(), annotationType);
    }

    /**
     * Find the annotation that is annotated on the specified element may be a meta-annotation
     *
     * @param annotatedElement the annotated element
     * @param annotationType   the type of annotation
     * @param <A>              the required type of annotation
     * @return If found, return first matched-type {@link Annotation annotation}, or <code>null</code>
     */
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return findAnnotation(annotatedElement, a -> isSameType(a, annotationType));
    }

    /**
     * Find the annotation that is annotated on the specified element may be a meta-annotation
     *
     * @param annotatedElement  the annotated element
     * @param annotationFilters the filters of annotations
     * @param <A>               the required type of annotation
     * @return If found, return first matched-type {@link Annotation annotation}, or <code>null</code>
     */
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement,
                                                          Predicate<Annotation>... annotationFilters) {
        return (A) filterFirst(getAllDeclaredAnnotations(annotatedElement), annotationFilters);
    }

    public static boolean isMetaAnnotation(Annotation annotation,
                                           Class<? extends Annotation>... metaAnnotationTypes) {
        return isMetaAnnotation(annotation.annotationType(), metaAnnotationTypes);
    }

    public static boolean isMetaAnnotation(Class<? extends Annotation> annotationType,
                                           Class<? extends Annotation>... metaAnnotationTypes) {
        boolean annotated = true;
        for (Class<? extends Annotation> metaAnnotationType : metaAnnotationTypes) {
            annotated &= isAnnotated(annotationType, metaAnnotationType);
        }
        return annotated;
    }

    /**
     * Get all directly declared annotations of the the annotated element, not including
     * meta annotations.
     *
     * @param annotatedElement    the annotated element
     * @param annotationsToFilter the annotations to filter
     * @return non-null read-only {@link List}
     */
    public static List<Annotation> getAllDeclaredAnnotations(AnnotatedElement annotatedElement,
                                                             Predicate<Annotation>... annotationsToFilter) {
        if (isType(annotatedElement)) {
            return getAllDeclaredAnnotations((Class) annotatedElement, annotationsToFilter);
        } else {
            return getDeclaredAnnotations(annotatedElement, annotationsToFilter);
        }
    }

    public static Set<Annotation> filterAnnotations(Collection<Annotation> annotations,
                                                    Predicate<Annotation>... annotationsToFilter) {
        return unmodifiableSet(filterAll(new LinkedHashSet<>(annotations), annotationsToFilter));
    }

    /**
     * Get all directly declared annotations of the specified type and its' all hierarchical types, not including
     * meta annotations.
     *
     * @param type                the specified type
     * @param annotationsToFilter the annotations to filter
     * @return non-null read-only {@link List}
     */
    public static List<Annotation> getAllDeclaredAnnotations(Class<?> type, Predicate<Annotation>... annotationsToFilter) {

        if (type == null) {
            return emptyList();
        }

        List<Annotation> allAnnotations = new LinkedList<>();

        // All types
        Set<Class<?>> allTypes = new LinkedHashSet<>();
        // Add current type
        allTypes.add(type);
        // Add all inherited types
        allTypes.addAll(getAllInheritedTypes(type, t -> !Object.class.equals(t)));

        for (Class<?> t : allTypes) {
            allAnnotations.addAll(getDeclaredAnnotations(t, annotationsToFilter));
        }

        return unmodifiableList(allAnnotations);
    }

    /**
     * Get annotations that are <em>directly present</em> on this element.
     * This method ignores inherited annotations.
     *
     * @param annotatedElement    the annotated element
     * @param annotationsToFilter the annotations to filter
     * @return non-null read-only {@link List}
     */
    public static List<Annotation> getDeclaredAnnotations(AnnotatedElement annotatedElement,
                                                          Predicate<Annotation>... annotationsToFilter) {
        if (annotatedElement == null) {
            return emptyList();
        }

        return unmodifiableList(filterAll(asList(annotatedElement.getDeclaredAnnotations()), annotationsToFilter));
    }

    public static <T> T getAttributeValue(Annotation[] annotations, String attributeName, Class<T> returnType) {
        T attributeValue = null;
        for (Annotation annotation : annotations) {
            if (annotation != null) {
                attributeValue = getAttributeValue(annotation, attributeName, returnType);
                if (attributeValue != null) {
                    break;
                }
            }
        }
        return attributeValue;
    }

    public static <T> T getAttributeValue(Annotation annotation, String attributeName, Class<T> returnType) {
        Class<?> annotationType = annotation.annotationType();
        T attributeValue = null;
        try {
            Method method = annotationType.getMethod(attributeName);
            Object value = method.invoke(annotation);
            attributeValue = returnType.cast(value);
        } catch (Exception ignored) {
            attributeValue = null;
        }
        return attributeValue;
    }

    public static boolean contains(Collection<Annotation> annotations, Class<? extends Annotation> annotationType) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        boolean contained = false;
        for (Annotation annotation : annotations) {
            if (Objects.equals(annotationType, annotation.annotationType())) {
                contained = true;
                break;
            }
        }
        return contained;
    }

    public static boolean isAnnotated(AnnotatedElement annotatedElement, Class<? extends Annotation> annotationType) {
        return annotatedElement != null && annotatedElement.isAnnotationPresent(annotationType);
    }

}