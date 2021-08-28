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
package org.geektimes.event.reactive.stream;

import org.geektimes.event.EventListener;
import org.geektimes.reactive.streams.SimplePublisher;

import java.util.EventObject;

/**
 * {@link EventObject} Publisher
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class EventPublisher {

    private final SimplePublisher<EventObject> simplePublisher;

    public EventPublisher() {
        simplePublisher = new SimplePublisher();
    }

    public void publish(Object event) {
        simplePublisher.publish(new EventObject(event));
    }

    public void addEventListener(EventListener eventListener) {
        simplePublisher.subscribe(new ListenerSubscriberAdapter(eventListener));
    }

    public static void main(String[] args) {
        EventPublisher eventPublisher = new EventPublisher();
        // Add Listener
        eventPublisher.addEventListener(event -> {
            System.out.printf("[Thread : %s] Handles %s[Source : %s]\n",
                    Thread.currentThread().getName(),
                    event.getClass().getSimpleName(),
                    event.getSource());
        });

        // Publish Event
        eventPublisher.publish("Hello,World");
    }
}
