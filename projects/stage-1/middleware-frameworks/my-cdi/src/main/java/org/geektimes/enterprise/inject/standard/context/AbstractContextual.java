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
package org.geektimes.enterprise.inject.standard.context;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.CreationException;

/**
 * Abstract implementation of {@link Contextual}
 *
 * @param <T> type of the instance
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class AbstractContextual<T> implements Contextual<T> {

    /**
     * {@inheritDoc}
     *
     * @param creationalContext {@link CreationalContext}
     * @return The instance
     * @throws CreationException If an exception occurs while creating an instance, the exception is rethrown by the create() method. If the exception is a checked exception,
     *                           it must be wrapped and rethrown as an (unchecked) CreationException.
     */
    @Override
    public final T create(CreationalContext<T> creationalContext) throws CreationException {
        T instance = null;
        try {
            instance = doCreate(creationalContext);
            if (creationalContext != null) {
                creationalContext.push(instance);
            }
        } catch (Throwable t) {
            throw new CreationException(t);
        }
        return instance;
    }

    /**
     * The subclass will override this method to create the instance
     *
     * @param creationalContext {@link CreationalContext}
     * @return non-null
     * @throws Throwable any exception occurs while creating an instance
     */
    protected abstract T doCreate(CreationalContext<T> creationalContext) throws Throwable;

    /**
     * {@inheritDoc}
     * <p>
     * If an exception occurs while destroying an instance, the exception must be caught by the destroy() method.
     *
     * @param instance
     * @param creationalContext
     */
    @Override
    public final void destroy(T instance, CreationalContext<T> creationalContext) {
        try {
            doDestroy(instance, creationalContext);
        } catch (Throwable t) {

        }
    }

    /**
     * The subclass will override this method to destroy the instance with {@link CreationalContext}
     *
     * @param instance          the instance to be destroyed
     * @param creationalContext {@link CreationalContext}
     * @throws Throwable any exception occurs while destroying an instance
     */
    protected abstract void doDestroy(T instance, CreationalContext<T> creationalContext) throws Throwable;
}
