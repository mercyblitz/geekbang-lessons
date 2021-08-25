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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;
import java.lang.reflect.Field;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.geektimes.enterprise.inject.util.Producers.validateProducerField;

/**
 * Producer {@link Field} {@link Bean} based on Java Reflection
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ProducerFieldBean<T> extends AbstractBean<Field, T> implements Producer<T> {

    private final AnnotatedField producerField;

    public ProducerFieldBean(AnnotatedField producerField) {
        super(producerField.getJavaMember(), producerField.getJavaMember().getType());
        this.producerField = producerField;
    }

    @Override
    protected void validateAnnotatedElement(Field producerField) {
        validateProducerField(producerField);
    }


    @Override
    protected String getBeanName(Field producerField) {
        return Beans.getBeanName(producerField);
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
    public T produce(CreationalContext<T> ctx) {
        return create(ctx);
    }

    @Override
    public void dispose(T instance) {
        destroy(instance, null);
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return emptySet();
    }

    @Override
    public AnnotatedField getAnnotated() {
        return producerField;
    }

    public AnnotatedField getProducerField() {
        return producerField;
    }
}
