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
package org.geektimes.enterprise.inject.standard;

import org.geektimes.commons.lang.util.AnnotationUtils;

import javax.decorator.Decorator;
import javax.enterprise.context.*;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Scope;
import javax.interceptor.Interceptor;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.geektimes.commons.function.Streams.map;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Bean
 * @since 1.0.0
 */
public interface AnnotatedBean {

    /**
     * Get all {@link Annotation annotations} of the bean
     *
     * @return non-null {@link Set}
     */
    Set<Annotation> getAnnotations();

    /**
     * Obtains the {@linkplain javax.inject.Qualifier qualifiers} of the bean.
     *
     * @return the {@linkplain javax.inject.Qualifier qualifiers}
     */
    Set<Annotation> getQualifiers();

    /**
     * Get the scope {@link Annotation}
     *
     * @return
     */
    Optional<Annotation> getScope();

    /**
     * Obtains the {@linkplain javax.enterprise.context scope} of the bean.
     *
     * @return the {@linkplain javax.enterprise.context scope} if found
     * @see {@link Bean#getScope()}
     */
    default Class<? extends Annotation> getScopeType() {
        return getScope().map(Annotation::annotationType).orElse(null);
    }

    /**
     * Obtains the {@linkplain javax.enterprise.inject.Stereotype stereotypes} of the bean.
     *
     * @return the set of {@linkplain javax.enterprise.inject.Stereotype stereotypes}
     */
    Set<Annotation> getStereotypes();

    /**
     * Obtains the types of {@linkplain javax.enterprise.inject.Stereotype stereotypes} of the bean.
     *
     * @return the set of {@linkplain javax.enterprise.inject.Stereotype stereotypes}
     * @see {@link Bean#getStereotypes()}
     */
    default Set<Class<? extends Annotation>> getStereotypeTypes() {
        return map(getStereotypes(), Annotation::annotationType);
    }

    default boolean isAnnotatedScope() {
        return isAnnotated(getScopeType(), Scope.class);
    }

    default boolean isAnnotatedNormalScope() {
        return isAnnotated(getScopeType(), NormalScope.class);
    }

    default boolean isAnnotatedApplicationScoped() {
        return isAnnotated(getScopeType(), ApplicationScoped.class);
    }

    default boolean isAnnotatedSessionScoped() {
        return isAnnotated(getScopeType(), SessionScoped.class);
    }

    default boolean isAnnotatedConversationScoped() {
        return isAnnotated(getScopeType(), ConversationScoped.class);
    }

    default boolean isAnnotatedRequestScoped() {
        return isAnnotated(getScopeType(), RequestScoped.class);
    }

    default boolean isAnnotatedDependent() {
        return isAnnotated(getScopeType(), Dependent.class);
    }

    default boolean isAnnotated(Class<? extends Annotation> annotationType,
                                Class<? extends Annotation> metaAnnotationType) {
        return Objects.equals(annotationType, metaAnnotationType) ||
                AnnotationUtils.existsAnnotated(annotationType, metaAnnotationType);
    }

    /**
     * Is the bean annotated {@link Interceptor} or not.
     *
     * @return <code>true</code> if annotated {@link Interceptor}, <code>false</code> otherwise
     */
    boolean isAnnotatedInterceptor();

    /**
     * Is the bean annotated {@link Decorator} or not.
     *
     * @return <code>true</code> if annotated {@link Decorator}, <code>false</code> otherwise
     */
    boolean isAnnotatedDecorator();

    /**
     * Bean defining annotations
     * <p>
     * A bean class may have a bean defining annotation, allowing it to be placed anywhere in an application,
     * as defined in Bean archives. A bean class with a bean defining annotation is said to be an implicit bean.
     * The set of bean defining annotations contains:
     * <ul>
     *     <li>{@link ApplicationScoped @ApplicationScoped}, {@link SessionScoped @SessionScoped},
     *         {@link ConversationScoped @ConversationScoped} and {@link RequestScoped @RequestScoped} annotations
     *     </li>
     *     <li>all other normal scope types</li>
     *     <li>{@link Interceptor @Interceptor} and {@link Decorator @Decorator} annotations</li>
     *     <li>all stereotype annotations (i.e. annotations annotated with {@link Stereotype @Stereotype})</li>
     *     <li>the {@link Dependent @Dependent} scope annotation</li>
     * </ul>
     *
     * @return
     */
    default boolean isDefiningAnnotation() {
        return isAnnotatedScope() ||
                isAnnotatedNormalScope() ||
                isAnnotatedInterceptor() ||
                isAnnotatedDecorator() ||
                isAnnotatedDependent();
    }

}
