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

import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.enterprise.inject.spi.Producer;
import javax.enterprise.inject.spi.configurator.ProducerConfigurator;
import java.util.StringJoiner;

/**
 * {@link ProcessProducer} Event for Producer method or field is fired by container.
 *
 * @param <T> The bean class of the bean that declares the producer method or field
 * @param <X> The return type of the producer method or the type of the producer field
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ProcessProducerEvent<T, X> implements ProcessProducer<T, X> {

    private final AnnotatedMember annotatedMember;

    private final StandardBeanManager standardBeanManager;

    private Producer<X> producer;

    public ProcessProducerEvent(AnnotatedMember annotatedMember, Producer<X> producer,
                                StandardBeanManager standardBeanManager) {
        this.annotatedMember = annotatedMember;
        this.standardBeanManager = standardBeanManager;
        this.producer = producer;
    }

    @Override
    public AnnotatedMember<T> getAnnotatedMember() {
        return annotatedMember;
    }

    @Override
    public Producer<X> getProducer() {
        return producer;
    }

    @Override
    public void setProducer(Producer<X> producer) {
        this.producer = producer;
    }

    @Override
    public ProducerConfigurator<X> configureProducer() {
        // TODO
        return null;
    }

    @Override
    public void addDefinitionError(Throwable t) {
        standardBeanManager.addBeanDiscoveryDefinitionError(t);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("annotatedMember=" + getAnnotatedMember())
                .add("producer=" + getProducer())
                .toString();
    }
}
