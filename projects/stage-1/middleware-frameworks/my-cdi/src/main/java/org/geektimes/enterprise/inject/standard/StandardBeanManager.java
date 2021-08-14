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

import org.geektimes.enterprise.inject.se.StandardContainer;
import org.geektimes.enterprise.inject.util.*;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static org.geektimes.enterprise.inject.util.Injections.validateForbiddenAnnotation;
import static org.geektimes.enterprise.inject.util.Parameters.isConstructorParameter;
import static org.geektimes.enterprise.inject.util.Parameters.isMethodParameter;

/**
 * Standard {@link BeanManager}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class StandardBeanManager implements BeanManager {

    public StandardBeanManager(StandardContainer standardContainer) {
        // TODO
    }

    @Override
    public Object getReference(Bean<?> bean, Type beanType, CreationalContext<?> ctx) {
        // TODO
        return null;
    }

    @Override
    public Object getInjectableReference(InjectionPoint ij, CreationalContext<?> ctx) {
        // TODO
        return null;
    }

    @Override
    public <T> CreationalContext<T> createCreationalContext(Contextual<T> contextual) {
        // TODO
        return null;
    }

    @Override
    public Set<Bean<?>> getBeans(Type beanType, Annotation... qualifiers) {
        // TODO
        return null;
    }

    @Override
    public Set<Bean<?>> getBeans(String name) {
        // TODO
        return null;
    }

    @Override
    public Bean<?> getPassivationCapableBean(String id) {
        // TODO
        return null;
    }

    @Override
    public <X> Bean<? extends X> resolve(Set<Bean<? extends X>> beans) {
        // TODO
        return null;
    }

    @Override
    public void validate(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        if (annotated instanceof AnnotatedField) { // InjectionPoint on Field
            validateFieldInjectionPoint(injectionPoint);
        } else if (annotated instanceof AnnotatedParameter) { // InjectionPoint on Parameter
            AnnotatedParameter annotatedParameter = (AnnotatedParameter) annotated;
            if (isConstructorParameter(annotatedParameter)) { // InjectionPoint on Constructors' Parameter
                validateConstructorParameterInjectionPoint(injectionPoint);
            } else if (isMethodParameter(annotatedParameter)) { // InjectionPoint on Methods' Parameter
                validateMethodParameterInjectionPoint(injectionPoint);
            }
        }
    }

    /**
     * @param injectionPoint {@link InjectionPoint}
     * @throws DefinitionException If an injected field is annotated @Produces, the container automatically detects
     *                             the problem and treats it as a definition error.
     */
    private void validateFieldInjectionPoint(InjectionPoint injectionPoint) throws DefinitionException {
        validateForbiddenAnnotation(injectionPoint, Produces.class);
    }

    /**
     * @param injectionPoint {@link InjectionPoint}
     * @throws DefinitionException If a bean constructor has a parameter annotated @Disposes, @Observes, or @ObservesAsync,
     *                             the container automatically detects the problem and treats it as a definition error.
     */
    private void validateConstructorParameterInjectionPoint(InjectionPoint injectionPoint) throws DefinitionException {
        validateForbiddenAnnotation(injectionPoint, Disposes.class);
        validateForbiddenAnnotation(injectionPoint, Observes.class);
        validateForbiddenAnnotation(injectionPoint, ObservesAsync.class);
    }

    /**
     * @param injectionPoint {@link InjectionPoint}
     * @throws DefinitionException If an initializer method is annotated @Produces, has a parameter annotated @Disposes,
     *                             has a parameter annotated @Observes, or has a parameter annotated @ObservesAsync,
     *                             the container automatically detects the problem and treats it as a definition error.
     */
    private void validateMethodParameterInjectionPoint(InjectionPoint injectionPoint) throws DefinitionException {
        validateForbiddenAnnotation((Method) injectionPoint.getMember(), Produces.class);
        validateForbiddenAnnotation(injectionPoint, Disposes.class);
        validateForbiddenAnnotation(injectionPoint, Observes.class);
        validateForbiddenAnnotation(injectionPoint, ObservesAsync.class);
    }

    @Override
    @Deprecated
    public void fireEvent(Object event, Annotation... qualifiers) {
        // TODO
    }

    @Override
    public <T> Set<ObserverMethod<? super T>> resolveObserverMethods(T event, Annotation... qualifiers) {
        // TODO
        return null;
    }

    @Override
    public List<Decorator<?>> resolveDecorators(Set<Type> types, Annotation... qualifiers) {
        // TODO
        return null;
    }

    @Override
    public List<Interceptor<?>> resolveInterceptors(InterceptionType type, Annotation... interceptorBindings) {
        // TODO
        return null;
    }

    @Override
    public boolean isScope(Class<? extends Annotation> annotationType) {
        return Scopes.isScope(annotationType);
    }

    @Override
    public boolean isNormalScope(Class<? extends Annotation> annotationType) {
        return Scopes.isNormalScope(annotationType);
    }

    @Override
    public boolean isPassivatingScope(Class<? extends Annotation> annotationType) {
        return Scopes.isPassivatingScope(annotationType);
    }

    @Override
    public boolean isQualifier(Class<? extends Annotation> annotationType) {
        return Qualifiers.isQualifier(annotationType);
    }

    @Override
    public boolean isInterceptorBinding(Class<? extends Annotation> annotationType) {
        return Interceptors.isInterceptorBinding(annotationType);
    }

    @Override
    public boolean isStereotype(Class<? extends Annotation> annotationType) {
        return Stereotypes.isStereotype(annotationType);
    }

    @Override
    public Set<Annotation> getInterceptorBindingDefinition(Class<? extends Annotation> bindingType) {
        // TODO
        return null;
    }

    @Override
    public Set<Annotation> getStereotypeDefinition(Class<? extends Annotation> stereotype) {
        // TODO
        return null;
    }

    @Override
    public boolean areQualifiersEquivalent(Annotation qualifier1, Annotation qualifier2) {
        return Annotations.equals(qualifier1, qualifier2);
    }

    @Override
    public boolean areInterceptorBindingsEquivalent(Annotation interceptorBinding1, Annotation interceptorBinding2) {
        return Annotations.equals(interceptorBinding1, interceptorBinding2);
    }

    @Override
    public int getQualifierHashCode(Annotation qualifier) {
        return Annotations.hashCode(qualifier);
    }

    @Override
    public int getInterceptorBindingHashCode(Annotation interceptorBinding) {
        return Annotations.hashCode(interceptorBinding);
    }

    @Override
    public Context getContext(Class<? extends Annotation> scopeType) {
        // TODO
        return null;
    }

    @Override
    public ELResolver getELResolver() {
        // TODO
        return null;
    }

    @Override
    public ExpressionFactory wrapExpressionFactory(ExpressionFactory expressionFactory) {
        // TODO
        return null;
    }

    @Override
    public <T> AnnotatedType<T> createAnnotatedType(Class<T> type) {
        return new ReflectiveAnnotatedType(type);
    }

    @Override
    public <T> InjectionTarget<T> createInjectionTarget(AnnotatedType<T> type) {
        // TODO
        return null;
    }

    @Override
    public <T> InjectionTargetFactory<T> getInjectionTargetFactory(AnnotatedType<T> annotatedType) {
        // TODO
        return null;
    }

    @Override
    public <X> ProducerFactory<X> getProducerFactory(AnnotatedField<? super X> field, Bean<X> declaringBean) {
        // TODO
        return null;
    }

    @Override
    public <X> ProducerFactory<X> getProducerFactory(AnnotatedMethod<? super X> method, Bean<X> declaringBean) {
        // TODO
        return null;
    }

    @Override
    public <T> BeanAttributes<T> createBeanAttributes(AnnotatedType<T> type) {
        // TODO
        return null;
    }

    @Override
    public BeanAttributes<?> createBeanAttributes(AnnotatedMember<?> type) {
        // TODO
        return null;
    }

    @Override
    public <T> Bean<T> createBean(BeanAttributes<T> attributes, Class<T> beanClass,
                                  InjectionTargetFactory<T> injectionTargetFactory) {
        // TODO
        return null;
    }

    @Override
    public <T, X> Bean<T> createBean(BeanAttributes<T> attributes, Class<X> beanClass, ProducerFactory<X> producerFactory) {
        // TODO
        return null;
    }

    @Override
    public InjectionPoint createInjectionPoint(AnnotatedField<?> field) {
        return new FieldInjectionPoint(field);
    }

    @Override
    public InjectionPoint createInjectionPoint(AnnotatedParameter<?> parameter) {
        if (isConstructorParameter(parameter)) {
            return new ConstructorParameterInjectionPoint(parameter);
        }
        return new MethodParameterInjectionPoint(parameter);
    }

    @Override
    public <T extends Extension> T getExtension(Class<T> extensionClass) {
        // TODO
        return null;
    }

    @Override
    public <T> InterceptionFactory<T> createInterceptionFactory(CreationalContext<T> ctx, Class<T> clazz) {
        // TODO
        return null;
    }

    @Override
    public Event<Object> getEvent() {
        // TODO
        return null;
    }

    @Override
    public Instance<Object> createInstance() {
        // TODO
        return null;
    }
}
