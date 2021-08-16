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

import org.geektimes.commons.function.Predicates;
import org.geektimes.enterprise.inject.util.Qualifiers;
import org.geektimes.enterprise.inject.util.Scopes;
import org.geektimes.enterprise.inject.util.Stereotypes;

import javax.decorator.Decorator;
import javax.interceptor.Interceptor;
import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

import static org.geektimes.commons.collection.util.CollectionUtils.ofSet;
import static org.geektimes.commons.function.Streams.map;
import static org.geektimes.commons.lang.util.AnnotationUtils.*;

/**
 * {@link AnnotatedBean} based on Java reflection
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveAnnotatedBean implements AnnotatedBean {

    private final Set<Annotation> annotations;

    private final Set<Annotation> qualifiers;

    private final Optional<Annotation> scope;

    private final Set<Annotation> stereotypes;

    private final Class<? extends Annotation> scopeType;

    private final Set<Class<? extends Annotation>> stereotypeTypes;

    private final boolean annotatedInterceptor;

    private final boolean annotatedDecorator;

    public ReflectiveAnnotatedBean(Class<?> beanClass) {
        this.annotations = ofSet(beanClass.getAnnotations());
        this.qualifiers = Qualifiers.getQualifiers(annotations);
        this.scope = filterAnnotation(annotations, Predicates.or(Scopes::isScope, Scopes::isNormalScope));
        this.scopeType = scope.map(Annotation::annotationType).orElse(null);
        this.stereotypes = filterAnnotations(annotations, Stereotypes::isStereotype);
        this.stereotypeTypes = map(stereotypes, Annotation::annotationType);
        this.annotatedInterceptor = exists(annotations, Interceptor.class);
        this.annotatedDecorator = exists(annotations, Decorator.class);
    }

    public static AnnotatedBean of(Class<?> beanClass) {
        return new ReflectiveAnnotatedBean(beanClass);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public Optional<Annotation> getScope() {
        return scope;
    }

    @Override
    public Class<? extends Annotation> getScopeType() {
        return scopeType;
    }

    @Override
    public Set<Annotation> getStereotypes() {
        return stereotypes;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypeTypes() {
        return stereotypeTypes;
    }

    @Override
    public boolean isAnnotatedInterceptor() {
        return annotatedInterceptor;
    }

    @Override
    public boolean isAnnotatedDecorator() {
        return annotatedDecorator;
    }
}
