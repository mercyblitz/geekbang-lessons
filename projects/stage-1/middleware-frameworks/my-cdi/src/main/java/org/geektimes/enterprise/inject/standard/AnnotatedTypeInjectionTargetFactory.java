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

import javax.enterprise.inject.spi.*;

/**
 * {@link InjectionTargetFactory} based on {@link AnnotatedType}
 *
 * @param <T> type on which this InjectionTarget operates
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class AnnotatedTypeInjectionTargetFactory<T> implements InjectionTargetFactory<T> {

    private final AnnotatedType<T> annotatedType;

    private final BeanManager beanManager;

    public AnnotatedTypeInjectionTargetFactory(AnnotatedType<T> annotatedType, BeanManager beanManager) {
        this.annotatedType = annotatedType;
        this.beanManager = beanManager;
    }

    @Override
    public InjectionTarget<T> createInjectionTarget(Bean<T> bean) {
        return new AnnotatedTypeInjectionTarget(annotatedType, bean, beanManager);
    }
}
