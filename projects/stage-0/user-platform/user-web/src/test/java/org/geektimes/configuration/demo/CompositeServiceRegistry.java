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
package org.geektimes.configuration.demo;

import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import java.util.List;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class CompositeServiceRegistry implements ServiceRegistry<Registration> {

    private final List<ServiceRegistry<Registration>> serviceRegistries;

    public CompositeServiceRegistry(List<ServiceRegistry<Registration>> serviceRegistries) {
        this.serviceRegistries = serviceRegistries;
    }

    @Override
    public void register(Registration registration) { // 等同于 ServiceInstance
        serviceRegistries.forEach(registry -> registry.register(registration));
        // 假设第一个元素是：EurekaServiceRegistry 依赖 EurekaRegistration
        // 第二个元素是：NacosServiceRegistry 依赖 NacosRegistration
        // 仅有一种情况满足两种注册中心实现：
        // XXXRegistration 继承了 EurekaRegistration，又继承 NacosRegistration
        // registration is EurekaRegistration & registration is NacosRegistration
        // 否则会抛出 ClassCastException
    }

    @Override
    public void deregister(Registration registration) {

    }

    @Override
    public void close() {

    }

    @Override
    public void setStatus(Registration registration, String status) {

    }

    @Override
    public Object getStatus(Registration registration) {
        return null;
    }
}
