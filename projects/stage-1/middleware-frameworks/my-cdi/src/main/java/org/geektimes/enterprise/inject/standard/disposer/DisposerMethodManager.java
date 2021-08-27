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
package org.geektimes.enterprise.inject.standard.disposer;

import org.geektimes.enterprise.inject.standard.MethodParameterInjectionPoint;
import org.geektimes.enterprise.inject.standard.beans.StandardBeanManager;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.DefinitionException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.geektimes.commons.function.ThrowableAction.execute;
import static org.geektimes.enterprise.inject.util.Beans.getBeanTypes;

/**
 * Disposer {@link Method} Mananger
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DisposerMethodManager {

    private final Map<Type, AnnotatedMethod> storage;

    private final StandardBeanManager standardBeanManager;

    public DisposerMethodManager(StandardBeanManager standardBeanManager) {
        this.storage = new LinkedHashMap<>();
        this.standardBeanManager = standardBeanManager;
    }

    public void registerDisposerMethods(Map<Type, AnnotatedMethod> disposerMethods) throws DefinitionException {
        storage.putAll(disposerMethods);
    }

    public AnnotatedMethod findDisposerMethod(Type producerBeanType) {
        return storage.get(producerBeanType);
    }

    public void invokeDisposerMethod(Object instance) {
        Class<?> instanceType = instance.getClass();
        Set<Type> beanTypes = getBeanTypes(instanceType);
        AnnotatedMethod disposerMethod = null;
        for (Type beanType : beanTypes) {
            disposerMethod = findDisposerMethod(beanType);
            if (disposerMethod != null) {
                break;
            }
        }

        if (disposerMethod == null) {
            return;
        }

        List<AnnotatedParameter> parameters = disposerMethod.getParameters();
        Object invocable = disposerMethod.isStatic() ? null : instance;
        Object[] arguments = new Object[parameters.size()];
        for (AnnotatedParameter parameter : parameters) {
            MethodParameterInjectionPoint injectionPoint = new MethodParameterInjectionPoint(parameter);
            arguments[parameter.getPosition()] = standardBeanManager.getInjectableReference(injectionPoint, null);
        }

        Method method = disposerMethod.getJavaMember();
        execute(() -> method.invoke(invocable, arguments));
    }
}
