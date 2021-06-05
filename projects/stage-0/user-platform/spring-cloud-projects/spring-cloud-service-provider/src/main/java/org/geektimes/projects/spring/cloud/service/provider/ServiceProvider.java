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
package org.geektimes.projects.spring.cloud.service.provider;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * 服务提供方引导类
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@RestController
//@EnableCircuitBreaker
@EnableBinding({MySource.class})
@EnableScheduling
public class ServiceProvider {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProvider.class, args);
    }

    @Autowired
    private InetUtils inetUtils;

    private String localIp;

    private int port;

    @Autowired
    private MySource mySource;

    @PostConstruct
    public void init() {
        this.localIp = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
    }

    @EventListener(WebServerInitializedEvent.class)
    public void onWebServerInitialized(WebServerInitializedEvent event) {
        this.port = event.getWebServer().getPort();
    }


    @Bean
    public ApplicationRunner runner() {
        return args -> {
            sendMessage();
        };
    }

    @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        MessageChannel messageChannel = mySource.output1();
        GenericMessage<String> message = new GenericMessage("Hello,World");
        messageChannel.send(message);
    }

    @Scheduled(fixedRate = 3000)
    public void sendMessage2() {
        MessageChannel messageChannel = mySource.output2();
        GenericMessage<String> message = new GenericMessage("2021");
        messageChannel.send(message);
    }

    @CircuitBreaker(name = "echo", fallbackMethod = "fallback")
    @GetMapping("/echo/{message}")
    public String echo(@PathVariable String message) {
        return messageWithExtraInfo("[ECHO] : " + message);
    }

    public String fallback(String message, Throwable throwable) {
        return "FALLBACK";
    }

//    private String messageWithThreadInfo(String message) {
//        return String.format("线程:%s - %s", Thread.currentThread().getName(), message);
//    }

    // 辅助信息包括：
    // - IP
    // - Port
    // - Thread

    private String messageWithExtraInfo(String message) {
        return String.format("IP: %s , 端口: %d , 线程:%s - %s",
                localIp,
                port,
                Thread.currentThread().getName(),
                message);
    }
}
