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
package org.geektimes.http.server.jdk.servlet;

import java.util.EventListener;
import java.util.function.Consumer;

/**
 * Listener Handler
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ListenerHandler<T extends EventListener> {

    private final Class<T> listenerClass;

    private final Consumer<T> consumer;

    ListenerHandler(Class<T> listenerClass, Consumer<T> consumer) {
        this.listenerClass = listenerClass;
        this.consumer = consumer;
    }

    public void handle(T listener) {
        if (listenerClass.isInstance(listener)) {
            consumer.accept(listener);
        }
    }

    public static <T extends EventListener> ListenerHandler create(Class<T> listenerClass, Consumer<T> consumer) {
        return new ListenerHandler(listenerClass, consumer);
    }
}
