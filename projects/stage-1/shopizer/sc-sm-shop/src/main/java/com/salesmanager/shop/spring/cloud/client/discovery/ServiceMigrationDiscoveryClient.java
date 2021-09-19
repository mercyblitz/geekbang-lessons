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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Migration {@link DiscoveryClient}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ServiceMigrationDiscoveryClient implements DiscoveryClient {

    private final ObjectProvider<DiscoveryClient> discoveryClients;

    public ServiceMigrationDiscoveryClient(ObjectProvider<DiscoveryClient> discoveryClients) {
        this.discoveryClients = discoveryClients;
    }

    @Override
    public String description() {
        return "Service Migration - DiscoveryClient";
    }

    private List<DiscoveryClient> getDiscoveryClients() {
        List<DiscoveryClient> discoveryClients = new LinkedList<>();
        for (DiscoveryClient discoveryClient : this.discoveryClients) {
            if (discoveryClient != this &&  // Exclude self
                    !CompositeDiscoveryClient.class.equals(discoveryClient.getClass())) {  // Exclude CompositeDiscoveryClient
                discoveryClients.add(discoveryClient);
            }
        }
        return discoveryClients;
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        List<ServiceInstance> allServiceInstances = new LinkedList<>();
        for (DiscoveryClient discoveryClient : getDiscoveryClients()) {
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
            if (serviceInstances != null && !serviceInstances.isEmpty()) {
                allServiceInstances.addAll(serviceInstances);
            }
        }
        return allServiceInstances;
    }

    @Override
    public List<String> getServices() {
        List<String> allServices = new LinkedList<>();
        for (DiscoveryClient discoveryClient : getDiscoveryClients()) {
            List<String> services = discoveryClient.getServices();
            if (services != null && !services.isEmpty()) {
                allServices.addAll(services);
            }
        }
        return allServices;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 5;
    }
}
