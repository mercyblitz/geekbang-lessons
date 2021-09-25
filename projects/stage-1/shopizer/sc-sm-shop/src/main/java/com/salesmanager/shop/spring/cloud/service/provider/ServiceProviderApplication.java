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
package com.salesmanager.shop.spring.cloud.service.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
@EnableAutoConfiguration
@RestController
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceProviderApplication {

    private static final Logger log = LoggerFactory.getLogger(ServiceProviderApplication.class);

    @Autowired
    private Tracer tracer;

    @Autowired
    private EchoService echoService;


    @RequestMapping("/")
    @NewSpan
    String home() {
        log.info("Hello,World");
        return echoService.echo("Hello world!");
    }

    @RequestMapping("/hello/world")
    String helloWorld() {
        log.info("Hello world 2021!");
        return echoService.echo("Hello world 2021!");
    }

//    @RequestMapping("/trace/id")
//    String traceId() {
//        log.info(tracer.toString());
//        return tracer.toString();
//    }
//
//    @RequestMapping("/span/id")
//    String spanId() {
//        Span span = tracer.currentSpan();
//        log.info(span.toString());
//        return span.toString();
//    }

    @Bean
    public EchoService echoService() {
        return new EchoService();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceProviderApplication.class)
                .run("--spring.config.additional-location=classpath:/META-INF/service-provider.yaml");
    }


}
