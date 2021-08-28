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
package org.geektimes.enterprise.inject.standard.event;

import org.geektimes.enterprise.inject.standard.beans.StandardBeanManager;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import java.util.StringJoiner;

/**
 * {@link ProcessInjectionTarget} Event is fired by container for every bean, interceptor or decorator
 * in Bean discovery.
 *
 * @param <X> The class being annotated
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ProcessInjectionTargetEvent<X> implements ProcessInjectionTarget<X> {

    private AnnotatedType<X> annotatedType;

    private InjectionTarget<X> injectionTarget;

    private final StandardBeanManager standardBeanManager;

    public ProcessInjectionTargetEvent(AnnotatedType<X> annotatedType, InjectionTarget<X> injectionTarget, StandardBeanManager standardBeanManager) {
        this.annotatedType = annotatedType;
        this.injectionTarget = injectionTarget;
        this.standardBeanManager = standardBeanManager;
    }

    @Override
    public AnnotatedType<X> getAnnotatedType() {
        return annotatedType;
    }

    @Override
    public InjectionTarget<X> getInjectionTarget() {
        return injectionTarget;
    }

    @Override
    public void setInjectionTarget(InjectionTarget<X> injectionTarget) {
        this.injectionTarget = injectionTarget;
    }

    @Override
    public void addDefinitionError(Throwable t) {
        standardBeanManager.addBeanDiscoveryDefinitionError(t);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("annotatedType=" + getAnnotatedType())
                .add("injectionTarget=" + getInjectionTarget())
                .toString();
    }
}
