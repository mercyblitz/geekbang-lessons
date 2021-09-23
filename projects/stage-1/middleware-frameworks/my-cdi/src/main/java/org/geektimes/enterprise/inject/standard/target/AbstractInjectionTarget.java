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
package org.geektimes.enterprise.inject.standard.target;

import org.geektimes.enterprise.inject.standard.beans.manager.StandardBeanManager;
import org.geektimes.enterprise.inject.standard.producer.AbstractProducer;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import java.util.Set;

/**
 * Abstract {@link InjectionTarget}
 *
 * @param <T> The class of object produced by the producer
 * @param <X> THe class of {@link Bean}
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0.
 */
public class AbstractInjectionTarget<T, X> extends AbstractProducer<T, X> implements InjectionTarget<T> {

    public AbstractInjectionTarget(Bean<X> declaringBean, StandardBeanManager standardBeanManager) {
        super(declaringBean, standardBeanManager);
    }

    @Override
    public void inject(T instance, CreationalContext<T> ctx) {

    }

    @Override
    public void postConstruct(T instance) {

    }

    @Override
    public void preDestroy(T instance) {

    }

    @Override
    protected Set<InjectionPoint> resolveInjectionPoints() {
        return null;
    }

    @Override
    public T produce(CreationalContext<T> ctx) {
        return null;
    }
}
