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

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.reflect.Type;
import java.util.Set;

import static org.geektimes.commons.reflect.util.TypeUtils.asClass;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveAnnotatedType<X> extends AbstractReflectiveAnnotated<Class> implements AnnotatedType<X> {

    public ReflectiveAnnotatedType(Type type){
        this(asClass(type));
    }

    public ReflectiveAnnotatedType(Class annotatedElement) {
        super(annotatedElement);
    }

    @Override
    public Class<X> getJavaClass() {
        return getAnnotatedElement();
    }

    @Override
    public Set<AnnotatedConstructor<X>> getConstructors() {
        return null;
    }

    @Override
    public Set<AnnotatedMethod<? super X>> getMethods() {
        return null;
    }

    @Override
    public Set<AnnotatedField<? super X>> getFields() {
        return null;
    }

    @Override
    public Type getBaseType() {
        return getJavaClass();
    }
}
