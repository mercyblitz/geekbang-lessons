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

import org.geektimes.enterprise.inject.util.Injections;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.*;
import static org.geektimes.commons.collection.util.CollectionUtils.newLinkedHashSet;
import static org.geektimes.commons.reflect.util.FieldUtils.setFieldValue;
import static org.geektimes.commons.reflect.util.MemberUtils.isStatic;
import static org.geektimes.commons.reflect.util.MethodUtils.invokeMethod;
import static org.geektimes.enterprise.inject.util.Beans.getBeanTypes;

/**
 * {@link AnnotatedType} {@link InjectionTarget}
 *
 * @param <T> The class of the instance
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class AnnotatedTypeInjectionTarget<T> implements InjectionTarget<T> {

    private final AnnotatedType<T> annotatedType;

    private final Bean<T> bean;

    private final BeanManager beanManager;

    private Map<AnnotatedConstructor, List<ConstructorParameterInjectionPoint>> constructorParameterInjectionPointsMap;

    private Set<FieldInjectionPoint> fieldInjectionPoints;

    private Map<AnnotatedMethod, Set<MethodParameterInjectionPoint>> methodParameterInjectionPointsMap;

    public AnnotatedTypeInjectionTarget(AnnotatedType<T> annotatedType, Bean<T> bean, BeanManager beanManager) {
        this.annotatedType = annotatedType;
        this.bean = bean;
        this.beanManager = beanManager;
    }

    @Override
    public T produce(CreationalContext<T> ctx) {
        return null;
    }

    @Override
    public void dispose(T instance) {
        Class<?> beanClass = instance.getClass();
        Set<Type> beanTypes = getBeanTypes(beanClass);
    }

    @Override
    public void inject(T instance, CreationalContext<T> ctx) {
        // Injected Fields
        injectFields(instance, ctx);

        // Initializer Methods
        injectInitializerMethods(instance, ctx);
    }

    private void injectFields(T instance, CreationalContext<T> ctx) {
        getFieldInjectionPoints().forEach(fieldInjectionPoint -> {
            injectField(fieldInjectionPoint, instance, ctx);
        });
    }

    private void injectField(FieldInjectionPoint fieldInjectionPoint, T instance, CreationalContext<T> ctx) {
        Field field = fieldInjectionPoint.getMember();
        Object injectedObject = beanManager.getInjectableReference(fieldInjectionPoint, ctx);
        setFieldValue(instance, field, injectedObject);
    }

    private void injectInitializerMethods(T instance, CreationalContext<T> ctx) {
        getMethodParameterInjectionPointsMap().forEach((annotatedMethod, methodParameterInjectionPoints) -> {
            injectInitializerMethod(annotatedMethod, methodParameterInjectionPoints, instance, ctx);
        });
    }

    private void injectInitializerMethod(AnnotatedMethod annotatedMethod,
                                         Set<MethodParameterInjectionPoint> methodParameterInjectionPoints,
                                         T instance, CreationalContext<T> ctx) {
        Method method = annotatedMethod.getJavaMember();
        int size = methodParameterInjectionPoints.size();
        Object[] arguments = new Object[size];
        for (MethodParameterInjectionPoint injectionPoint : methodParameterInjectionPoints) {
            AnnotatedParameter parameter = injectionPoint.getAnnotated();
            arguments[parameter.getPosition()] = beanManager.getInjectableReference(injectionPoint, ctx);
        }
        invokeMethod(instance, method, arguments);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The container must ensure that:
     * Any @PostConstruct callback declared by a class X in the type hierarchy of the bean is called after
     * all initializer methods declared by X or by superclasses of X have been called,
     * after all injected fields declared by X or by superclasses of X have been initialized.
     *
     * @param instance
     */
    @Override
    public void postConstruct(T instance) {
        invokeCallback(instance, PostConstruct.class);
    }

    @Override
    public void preDestroy(T instance) {
        invokeCallback(instance, PreDestroy.class);
    }

    private void invokeCallback(T instance, Class<? extends Annotation> annotationType) {
        AnnotatedType annotatedType = getAnnotatedType();
        Set<AnnotatedMethod> annotatedMethods = annotatedType.getMethods();
        annotatedMethods.stream()
                .filter(annotatedMethod -> isCallback(annotatedMethod, annotationType))
                .map(AnnotatedMethod::getJavaMember)
                .forEach(method -> {
                    invokeMethod(instance, method);
                });
    }

    private boolean isCallback(AnnotatedMethod annotatedMethod, Class<? extends Annotation> annotationType) {
        Method method = annotatedMethod.getJavaMember();
        return !isStatic(method) && method.isAnnotationPresent(annotationType) && method.getParameterCount() < 1;
    }

    public AnnotatedType<T> getAnnotatedType() {
        return annotatedType;
    }

    public Map<AnnotatedConstructor, List<ConstructorParameterInjectionPoint>> getConstructorParameterInjectionPointsMap() {
        if (constructorParameterInjectionPointsMap == null) {
            constructorParameterInjectionPointsMap = Injections.getConstructorParameterInjectionPointsMap(getAnnotatedType(), bean);
        }
        return constructorParameterInjectionPointsMap;
    }

    public List<ConstructorParameterInjectionPoint> getConstructorParameterInjectionPoints() {
        Map<AnnotatedConstructor, List<ConstructorParameterInjectionPoint>> injectionPointsMap =
                getConstructorParameterInjectionPointsMap();
        if (injectionPointsMap.isEmpty()) {
            return emptyList();
        }
        List<ConstructorParameterInjectionPoint> injectionPoints = new LinkedList<>();
        injectionPointsMap.values().forEach(injectionPoints::addAll);
        return unmodifiableList(injectionPoints);
    }

    public Set<FieldInjectionPoint> getFieldInjectionPoints() {
        if (fieldInjectionPoints == null) {
            fieldInjectionPoints = Injections.getFieldInjectionPoints(getAnnotatedType(), bean);
        }
        return fieldInjectionPoints;
    }

    public Map<AnnotatedMethod, Set<MethodParameterInjectionPoint>> getMethodParameterInjectionPointsMap() {
        if (methodParameterInjectionPointsMap == null) {
            methodParameterInjectionPointsMap = Injections.getMethodParameterInjectionPoints(getAnnotatedType(), bean);
        }
        return methodParameterInjectionPointsMap;
    }

    public List<MethodParameterInjectionPoint> getMethodParameterInjectionPoints() {
        List<MethodParameterInjectionPoint> injectionPoints = new LinkedList<>();
        getMethodParameterInjectionPointsMap().values().forEach(injectionPoints::addAll);
        return unmodifiableList(injectionPoints);
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {

        List<ConstructorParameterInjectionPoint> constructorParameterInjectionPoints = getConstructorParameterInjectionPoints();

        Set<FieldInjectionPoint> fieldInjectionPoints = getFieldInjectionPoints();

        List<MethodParameterInjectionPoint> methodParameterInjectionPoints = getMethodParameterInjectionPoints();

        int size = constructorParameterInjectionPoints.size() + fieldInjectionPoints.size()
                + methodParameterInjectionPoints.size();

        Set<InjectionPoint> injectionPoints = newLinkedHashSet(size);
        // add the InjectionPoints from Constructors' parameters
        injectionPoints.addAll(constructorParameterInjectionPoints);
        // add the InjectionPoints from Fields
        injectionPoints.addAll(fieldInjectionPoints);
        // add the InjectionPoints from Methods' parameters
        injectionPoints.addAll(methodParameterInjectionPoints);

        return unmodifiableSet(injectionPoints);
    }
}
