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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;
import java.util.Set;

/**
 * {@link Producer} represents a {@link ManagedBean}
 *
 * @param <T> The class of object produced by the producer
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ManagedBeanProducer<T> implements Producer<T> {

    private final ManagedBean<T> managedBean;

    private CreationalContext<T> ctx;

    public ManagedBeanProducer(ManagedBean<T> managedBean) {
        this.managedBean = managedBean;
    }

    @Override
    public T produce(CreationalContext<T> ctx) {
        this.ctx = ctx;
        return managedBean.create(ctx);
    }

    @Override
    public void dispose(T instance) {
        managedBean.destroy(instance, ctx);
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return managedBean.getInjectionPoints();
    }

    public ManagedBean<T> getManagedBean() {
        return managedBean;
    }
}
