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
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
public class ServiceConsumer {

    @Autowired
    private EchoService echoService;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping("/call/echo/{message}")
//    @CircuitBreaker(name = "test",fallback)
    public String echo(@PathVariable String message) {

        circuitBreakerFactory.create("test")
                .run(() -> "Call : " + echoService.echo(message));

        return "Call : " + echoService.echo(message);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceConsumer.class)
                .run(args);
    }
}
