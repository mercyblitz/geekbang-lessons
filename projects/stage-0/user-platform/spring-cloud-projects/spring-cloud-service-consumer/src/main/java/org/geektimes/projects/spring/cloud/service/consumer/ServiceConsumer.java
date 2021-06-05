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
package org.geektimes.projects.spring.cloud.service.consumer;

import org.geektimes.projects.spring.cloud.service.EchoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;

/**
 * 服务消费方应用引导类
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableFeignClients(basePackages = "org.geektimes.projects.spring.cloud.service")
//@EnableBinding(MySink.class)
public class ServiceConsumer {

    @Autowired
    private EchoService echoService;

//    @Autowired
//    private MySink mySink;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping("/call/echo/{message}")
//    @CircuitBreaker(name = "test",fallback)
    public String echo(@PathVariable String message) {

        circuitBreakerFactory.create("test")
                .run(() -> "Call : " + echoService.echo(message));

        return "Call : " + echoService.echo(message);
    }

//    @Bean
//    public ApplicationRunner runner() {
//        return args -> {
//            // 耦合 Spring Message API
//            SubscribableChannel subscribableChannel = mySink.input1();
//            subscribableChannel.subscribe(message -> {
//                System.out.println("SubscribableChannel 消息内容：" + message.getPayload());
//            });
//        };
//    }

//    @ServiceActivator(inputChannel = "input1") // 耦合 Spring Integration API
//    public void onMessage(Message<?> message) {
//        System.out.println("@ServiceActivator 消息内容：" + message.getPayload());
//    }

//    @StreamListener("input1")  // 耦合 Spring Cloud Stream API
//    public void onStreamMessage(Message<?> message) {
//        System.out.println("@StreamListener 消息内容：" + message.getPayload());
//    }

    // Spring Cloud Stream 3.0+ 函数接口方式
    @Bean
    public Consumer<String> message() {
        // 可能与 Spring Cloud Stream 老版本的 @EnableBinding(MySink.class) 冲突
        // 与 @StreamListener("input1")
        return message -> {
            System.out.println("Consumer 消息内容：" + message);
        };
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceConsumer.class)
                .run(args);
    }
}
