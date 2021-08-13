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

import org.geektimes.enterprise.inject.util.Beans;

import javax.decorator.Decorator;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.spi.*;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static org.geektimes.enterprise.inject.util.ProducerMethods.*;

/**
 * Producer {@link Method} {@link Bean} based on Java Reflection
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ProducerMethodBean<T> extends AbstractBean<Method, T> {

    public ProducerMethodBean(Method producerMethod) {
        super(producerMethod, producerMethod.getReturnType());
    }

    @Override
    protected void validateAnnotatedElement(Method producerMethod) {
        validateProducerMethodProduces(producerMethod);
        validateProducerMethodSpecializes(producerMethod);
        validateProducerMethodDeclaringClass(producerMethod);
        validateProducerMethodParameters(producerMethod);
    }


    @Override
    protected String getBeanName(Method producerMethod) {
        return Beans.getBeanName(producerMethod);
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        // TODO
        return null;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        // TODO
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        Method producerMethod = getAnnotatedElement();
        AnnotatedMethod annotatedMethod = new ReflectiveAnnotatedMethod(producerMethod);

        List<AnnotatedParameter> annotatedParameters = annotatedMethod.getParameters();
        Set<InjectionPoint> injectionPoints = new LinkedHashSet<>();

        for (AnnotatedParameter annotatedParameter : annotatedParameters) {
            InjectionPoint injectionPoint = new MethodParameterInjectionPoint(annotatedParameter, annotatedMethod, this);
            injectionPoints.add(injectionPoint);
        }

        return unmodifiableSet(injectionPoints);
    }
}
