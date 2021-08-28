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
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.EventObject;

/**
 * {@link EventListener} {@link Subscriber} Adapter
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ListenerSubscriberAdapter implements Subscriber<EventObject> {

    private final EventListener listener;

    public ListenerSubscriberAdapter(EventListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(EventObject eventObject) {
        listener.onEvent(eventObject);
    }

    @Override
    public void onError(Throwable t) {
        // TODO
    }

    @Override
    public void onComplete() {
        // TODO
    }
}
