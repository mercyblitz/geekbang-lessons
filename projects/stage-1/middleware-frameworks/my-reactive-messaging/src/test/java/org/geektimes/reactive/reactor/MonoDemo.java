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
package org.geektimes.reactive.reactor;

import org.geektimes.reactive.streams.BusinessSubscriber;
import org.geektimes.reactive.streams.SimplePublisher;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class MonoDemo {

    public static void main(String[] args) throws InterruptedException {
//        Mono.just("Hello, Mono")
////                .subscribeOn(Schedulers.single())
//                .subscribeOn(Schedulers.elastic())
//                .subscribe(data -> {
//                    System.out.printf("[Thread : %s] %s\n", Thread.currentThread().getName(), data);
//                });
//
//        Stream.of("Hello, Stream","Hello, Stream2")
//                .parallel()
//                .forEach(data -> {
//                    System.out.printf("[Thread : %s] %s\n", Thread.currentThread().getName(), data);
//                });

        demoMonoPublisher();

    }

    private static void demoMonoPublisher() {
        SimplePublisher publisher = new SimplePublisher();
        Mono.from(publisher)
                .subscribe(new BusinessSubscriber(5));

        for (int i = 0; i < 5; i++) {
            publisher.publish(i);
        }

        Supplier<String> supplier = () -> "Hello,World";

        Mono.fromSupplier(supplier)
                .subscribe(data -> {
                    System.out.printf("[Thread : %s] %s\n", Thread.currentThread().getName(), data);
                });
    }
}
