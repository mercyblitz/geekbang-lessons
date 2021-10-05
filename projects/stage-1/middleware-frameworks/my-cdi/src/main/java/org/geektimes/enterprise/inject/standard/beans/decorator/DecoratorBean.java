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
package org.geektimes.enterprise.inject.standard.beans.decorator;

import org.geektimes.enterprise.inject.standard.beans.ManagedBean;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Decorator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import static org.geektimes.enterprise.inject.util.Exceptions.newDefinitionException;

/**
 * {@link Decorator} {@link Bean}
 * <p>
 * A decorator is a managed bean. The set of decorated types of a decorator includes
 * all bean types of the managed bean which are Java interfaces, except for java.io.Serializable.
 * The decorator bean class and its superclasses are not decorated types of the decorator.
 * The decorator class may be abstract.
 * <p>
 * A decorator may be an abstract Java class, and is not required to implement every method of
 * every decorated type. Whenever the decorator does not implement a method of the decorated type,
 * the container will provide an implicit implementation that calls the method on the delegate.
 * <p>
 * The decorator intercepts every method which is declared by a decorated type of the decorator
 * and is implemented by the bean class of the decorator.
 * <p>
 * Decorators are called after interceptors. Decorators enabled using @Priority are called
 * before decorators enabled using beans.xml.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DecoratorBean<T> extends ManagedBean<T> implements Decorator<T> {

    public DecoratorBean(AnnotatedType<T> decoratorType, BeanManager beanManager) {
        super(decoratorType, beanManager);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        Class<? extends Annotation> scope = super.getScope();
        if (scope == null) {
            scope = Dependent.class;
        } else if (scope != null && !Dependent.class.equals(scope)) {
            throw newDefinitionException("The scope of decorator must be declared as @%s!", Dependent.class.getName());
        }
        return scope;
    }

    @Override
    public Type getDelegateType() {
        return null;
    }

    @Override
    public Set<Annotation> getDelegateQualifiers() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public Set<Type> getDecoratedTypes() {
        return null;
    }

    @Override
    protected void validate(Class<T> annotatedElement) {
        super.validate(annotatedElement);

        // If the set of decorated types of a decorator is empty,
        // the container automatically detects the problem and treats it as a definition error.

        // If a decorator declares any scope other than @Dependent,
        // the container automatically detects the problem and treats it as a definition error.

        // If the delegate type does not implement or extend a decorated type of the decorator
        // (or specifies different type parameters),
        // the container automatically detects the problem and treats it as a definition error.


        // validate the injection points

        // If a decorator has more than one delegate injection point, or does not have a delegate injection point,
        // the container automatically detects the problem and treats it as a definition error.

        // The delegate injection point must be an injected field, initializer method parameter or
        // bean constructor method parameter. If an injection point that is not an injected field,
        // initializer method parameter or bean constructor method parameter is annotated @Delegate,
        // the container automatically detects the problem and treats it as a definition error.

        // If a bean class that is not a decorator has an injection point annotated @Delegate,
        // the container automatically detects the problem and treats it as a definition error.


        // validate methods

        // If a decorator has abstract methods that are not declared by a decorated type,
        // the container automatically detects the problem and treats it as a definition error.
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        // TODO
        return super.create(creationalContext);
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        // TODO
        super.destroy(instance, creationalContext);
    }
}
