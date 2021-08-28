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

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.ProcessBean;
import java.util.StringJoiner;

/**
 * The {@link ProcessBean} Event
 *
 * @param <X> The class of the bean
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ProcessBeanEvent<X> implements ProcessBean<X> {

    private final Annotated annotated;

    private final Bean<X> bean;

    private final StandardBeanManager standardBeanManager;

    public ProcessBeanEvent(Annotated annotated, Bean<X> bean, StandardBeanManager standardBeanManager) {
        this.annotated = annotated;
        this.bean = bean;
        this.standardBeanManager = standardBeanManager;
    }


    @Override
    public Annotated getAnnotated() {
        return annotated;
    }

    @Override
    public Bean<X> getBean() {
        return bean;
    }

    @Override
    public void addDefinitionError(Throwable t) {
        standardBeanManager.addBeanDiscoveryDefinitionError(t);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("annotated=" + annotated)
                .add("bean=" + bean)
                .toString();
    }
}
