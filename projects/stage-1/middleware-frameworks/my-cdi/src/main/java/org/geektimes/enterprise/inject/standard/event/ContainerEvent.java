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

import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import java.util.EventObject;

import static java.lang.String.format;
import static org.geektimes.enterprise.inject.standard.ReflectiveObserverMethod.getBeanInstance;

/**
 * Container Event
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ContainerEvent extends EventObject {

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ContainerEvent(Object source) {
        super(source);
    }

    /**
     * @return {@link Extension} instance
     * @throws IllegalStateException If any method is called outside of the observer method invocation
     */
    protected Extension getCallerExtension() throws IllegalStateException {
        Object beanInstance = getBeanInstance();
        Class<?> callerClass = beanInstance.getClass();
        if (callerClass == null || !Extension.class.isAssignableFrom(callerClass)) {
            String message = format("Any %s method must not called outside of the observer method invocation in the" +
                            " %s implementation!",
                    BeforeBeanDiscovery.class.getName(), Extension.class.getName());
            throw new IllegalStateException(message);
        }
        return (Extension) beanInstance;
    }
}
