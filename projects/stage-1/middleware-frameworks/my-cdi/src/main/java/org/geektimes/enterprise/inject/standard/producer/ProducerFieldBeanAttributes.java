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
package org.geektimes.enterprise.inject.standard.producer;

import org.geektimes.enterprise.inject.standard.AbstractBeanAttributes;
import org.geektimes.enterprise.inject.util.Beans;
import org.geektimes.enterprise.inject.util.Producers;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.BeanAttributes;
import java.lang.reflect.Field;

/**
 * {@link Produces Producer} {@link Field} {@link BeanAttributes} implementation
 *
 * @param <T> the class of the bean instance
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ProducerFieldBeanAttributes<T> extends AbstractBeanAttributes<Field, T> {

    private final AnnotatedField annotatedField;

    public ProducerFieldBeanAttributes(AnnotatedField annotatedField) {
        super(annotatedField.getJavaMember(), annotatedField.getJavaMember().getType());
        this.annotatedField = annotatedField;
    }

    @Override
    protected String getBeanName(Field producerField) {
        return Beans.getBeanName(producerField);
    }

    @Override
    protected void validateAnnotatedElement(Field producerField) {
        Producers.validateProducerField(producerField);
    }

    @Override
    public AnnotatedField getAnnotated() {
        return annotatedField;
    }
}
