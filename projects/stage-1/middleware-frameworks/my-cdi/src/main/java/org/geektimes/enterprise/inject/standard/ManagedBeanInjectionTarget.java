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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.geektimes.commons.reflect.util.FieldUtils.setFieldValue;
import static org.geektimes.commons.reflect.util.MemberUtils.isStatic;
import static org.geektimes.commons.reflect.util.MethodUtils.invokeMethod;

/**
 * {@link InjectionTarget} presents {@link ManagedBean}
 *
 * @param <T> The class of the instance
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ManagedBeanInjectionTarget<T> extends ManagedBeanProducer<T> implements InjectionTarget<T> {

    public ManagedBeanInjectionTarget(ManagedBean<T> managedBean) {
        super(managedBean);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The container must ensure that:
     * Initializer methods declared by a class X in the type hierarchy of the bean are called after all injected
     * fields declared by X or by superclasses of X have been initialized.
     *
     * @param instance
     * @param ctx
     */
    @Override
    public void inject(T instance, CreationalContext<T> ctx) {
        ManagedBean managedBean = getManagedBean();
        BeanManager beanManager = managedBean.getBeanManager();
        // Injected Fields
        Set<FieldInjectionPoint> fieldInjectionPoints = managedBean.getFieldInjectionPoints();
        injectFields(fieldInjectionPoints, instance, ctx, beanManager);

        // Initializer Methods
        Map<AnnotatedMethod, List<MethodParameterInjectionPoint>> methodParameterInjectionPointsMap = managedBean.getMethodParameterInjectionPointsMap();
        injectInitializerMethods(methodParameterInjectionPointsMap, instance, ctx, beanManager);
    }

    private void injectFields(Set<FieldInjectionPoint> fieldInjectionPoints, T instance, CreationalContext<T> ctx,
                              BeanManager beanManager) {
        for (FieldInjectionPoint fieldInjectionPoint : fieldInjectionPoints) {
            injectField(fieldInjectionPoint, instance, ctx, beanManager);
        }
    }

    private void injectField(FieldInjectionPoint fieldInjectionPoint, T instance, CreationalContext<T> ctx,
                             BeanManager beanManager) {
        Field field = fieldInjectionPoint.getMember();
        Object injectedObject = beanManager.getInjectableReference(fieldInjectionPoint, ctx);
        setFieldValue(injectedObject, field, injectedObject);
    }

    private void injectInitializerMethods
            (Map<AnnotatedMethod, List<MethodParameterInjectionPoint>> methodParameterInjectionPointsMap,
             T instance, CreationalContext<T> ctx, BeanManager beanManager) {
        methodParameterInjectionPointsMap.forEach((annotatedMethod, methodParameterInjectionPoints) -> {
            injectInitializerMethod(annotatedMethod, methodParameterInjectionPoints, instance, ctx, beanManager);
        });
    }

    private void injectInitializerMethod(AnnotatedMethod annotatedMethod,
                                         List<MethodParameterInjectionPoint> methodParameterInjectionPoints,
                                         T instance, CreationalContext<T> ctx, BeanManager beanManager) {
        Method method = annotatedMethod.getJavaMember();
        int size = methodParameterInjectionPoints.size();
        Object[] arguments = new Object[size];
        for (int i = 0; i < size; i++) {
            MethodParameterInjectionPoint injectionPoint = methodParameterInjectionPoints.get(i);
            arguments[i] = beanManager.getInjectableReference(injectionPoint, ctx);
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
        ManagedBean managedBean = getManagedBean();
        AnnotatedType annotatedType = managedBean.getAnnotatedType();
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
}
