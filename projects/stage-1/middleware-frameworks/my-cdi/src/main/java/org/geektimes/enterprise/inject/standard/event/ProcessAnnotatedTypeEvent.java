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
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator;

/**
 * {@link ProcessAnnotatedType} Event Object for every Java class, interface (excluding annotation type, a special kind
 * of interface type) or enum discovered as defined in Type discovery.
 *
 * @param <X> The class being annotated
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ProcessAnnotatedTypeEvent<X> implements ProcessAnnotatedType<X> {

    private final StandardBeanManager standardBeanManager;

    private AnnotatedType<X> annotatedType;

    public ProcessAnnotatedTypeEvent(AnnotatedType<X> annotatedType, StandardBeanManager standardBeanManager) {
        this.annotatedType = annotatedType;
        this.standardBeanManager = standardBeanManager;
    }

    @Override
    public AnnotatedType<X> getAnnotatedType() {
        return annotatedType;
    }

    @Override
    public void setAnnotatedType(AnnotatedType<X> type) {
        this.annotatedType = type;
    }

    @Override
    public AnnotatedTypeConfigurator configureAnnotatedType() {
        return null;
    }

    @Override
    public void veto() {
        standardBeanManager.removeType(getAnnotatedType());
    }

    @Override
    public String toString() {
        return "ProcessAnnotatedTypeEvent{" +
                " annotatedType=" + getAnnotatedType() +
                '}';
    }
}
