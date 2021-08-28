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
package org.geektimes.commons.event;

import org.junit.Test;

import java.util.Date;

/**
 * {@link EventDispatcher} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class EventDispatcherTest {

    @Test
    public void test() {
        EventDispatcher eventDispatcher = EventDispatcher.getDefaultExtension();
        // Add Listeners
        eventDispatcher.addEventListener(new MyEventListener());

        // Conditional Listener
        eventDispatcher.addEventListener(new ConditionalEventListener<MyEvent>() {

            @Override
            public boolean accept(MyEvent event) {
                return true;
            }

            @Override
            public void onEvent(MyEvent event) {
                System.out.printf("[Thread : %s] %s Handles %s[Source : %s] at %s\n",
                        Thread.currentThread().getName(),
                        getClass().getName(),
                        event.getClass().getSimpleName(),
                        event.getSource(),
                        new Date(event.getTimestamp()));
            }

        });

        // Generic multiple-type listener
        eventDispatcher.addEventListener(new MyGenericEventListener());

        // Dispatch Events
        eventDispatcher.dispatch(new MyEvent("Hello,World"));
        eventDispatcher.dispatch(new Event("Hello,World") {
        });

    }
}
