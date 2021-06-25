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
package org.geektimes.projects.user.rpc.dubbo;

import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.rpc.cluster.loadbalance.ConsistentHashLoadBalance;
import org.geektimes.projects.user.service.EchoService;

import static org.apache.dubbo.common.constants.CommonConstants.COMPOSITE_METADATA_STORAGE_TYPE;
import static org.apache.dubbo.common.constants.RegistryConstants.REGISTRY_TYPE_KEY;
import static org.apache.dubbo.common.constants.RegistryConstants.SERVICE_REGISTRY_TYPE;

/**
 * Dubbo Provider Bootstrap
 *
 * @since 2.7.5
 */
public class ZookeeperDubboServiceConsumerBootstrap {

    public static void main(String[] args) throws Exception {

        DubboBootstrap bootstrap = DubboBootstrap.getInstance()
                .application("zookeeper-dubbo-consumer", app -> app.metadata(COMPOSITE_METADATA_STORAGE_TYPE))
                .registry("zookeeper", builder -> builder.address("zookeeper://127.0.0.1:2181")
//                        .parameter(REGISTRY_TYPE_KEY, SERVICE_REGISTRY_TYPE)
                        .useAsConfigCenter(true)
                        .useAsMetadataCenter(true))
                .reference("echo", builder -> builder.interfaceClass(EchoService.class)
                        .loadbalance(ConsistentHashLoadBalance.NAME)
//                        .protocol("rest")
                        .services("zookeeper-dubbo-provider"))
                .start();

        EchoService echoService = bootstrap.getCache().get(EchoService.class);

        for (int i = 0; i < 100; i++) {
            Thread.sleep(200L);
            System.out.println(echoService.echo("Hello,World"));
        }

        bootstrap.stop();
    }
}
