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

import javax.enterprise.inject.spi.EventContext;
import javax.enterprise.inject.spi.EventMetadata;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Default {@link EventContext} implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DefaultEventContext<T> implements EventContext<T> {

    private final T event;

    private final EventMetadata eventMetadata;

    public DefaultEventContext(T event, EventMetadata eventMetadata) {
        this.event = event;
        this.eventMetadata = eventMetadata;
    }

    public DefaultEventContext(T event, Type type, Set<Annotation> qualifiers, InjectionPoint injectionPoint) {
        this(event, new DefaultEventMetadata(type, qualifiers, injectionPoint));
    }

    @Override
    public T getEvent() {
        return event;
    }

    @Override
    public EventMetadata getMetadata() {
        return eventMetadata;
    }
}
