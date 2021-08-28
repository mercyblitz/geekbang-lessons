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
package org.geektimes.event.distributed;

import org.geektimes.event.EventListener;
import org.geektimes.event.reactive.stream.ListenerSubscriberAdapter;
import org.geektimes.reactive.streams.SimplePublisher;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Date;
import java.util.EventObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Distributed {@link EventObject} Publisher
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DistributedEventPublisher {

    private final SimplePublisher<EventObject> simplePublisher;

    private final JedisPool jedisPool;

    private final ExecutorService executorService;

    public DistributedEventPublisher(String uri) {
        simplePublisher = new SimplePublisher();
        this.jedisPool = new JedisPool(uri);
        // Build-in listener
        addEventListener(event -> {
            if (event instanceof DistributedEventObject) {
                // Event -> Pub/Sub
                Jedis jedis = jedisPool.getResource();
                jedis.publish("test", (String) event.getSource());
                jedis.close();
            }
        });

        this.executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            Jedis jedis = jedisPool.getResource();
            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    if ("test".equals(channel)) {
                        publish(new EventObject(message));
                    }
                }
            }, "test");
            jedis.close();
        });
    }

    public void publish(Object event) {
//        simplePublisher.publish(new EventObject(event));
        simplePublisher.publish(new DistributedEventObject(event));
    }

    private void publish(EventObject event) {
//        simplePublisher.publish(new EventObject(event));
        simplePublisher.publish(event);
    }

    public void addEventListener(EventListener eventListener) {
        simplePublisher.subscribe(new ListenerSubscriberAdapter(eventListener));
    }

    public void close() {
        jedisPool.close();
        executorService.shutdown();
    }

    public static void main(String[] args) {
        DistributedEventPublisher eventPublisher = new DistributedEventPublisher("redis://127.0.0.1:6379");

        // Publish Event
        eventPublisher.publish(String.valueOf(new Date()));

        eventPublisher.close();
    }
}
