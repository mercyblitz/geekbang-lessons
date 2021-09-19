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
package com.salesmanager.shop.spring.cloud.client.discovery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.Comparator;

/**
 * Service Discovery Bootstrap
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@EnableAutoConfiguration
@EnableServiceDiscovery
public class ServiceDiscoveryBootstrap {

    @Autowired
    private DiscoveryClient discoveryClient; // CompositeDiscoveryClient

    @Autowired
    private Comparator<String> stringComparator;

    @Autowired
    private Comparator<Integer> integerComparator;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println(discoveryClient.getServices());
            for (String serviceId : discoveryClient.getServices()) {
                System.out.println(discoveryClient.getInstances(serviceId));
            }
        };
    }

    @Bean
    public Comparator<String> stringComparator() {
        return new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1, o2);
            }
        };
    }

    @Bean
    public Comparator<Integer> integerComparator() {
        return new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o1, o2);
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceDiscoveryBootstrap.class, args);
    }
}
