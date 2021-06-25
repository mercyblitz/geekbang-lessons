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
package org.geektimes.rpc.service.registry;

import org.apache.commons.io.FileUtils;
import org.geektimes.rpc.serializer.Serializer;
import org.geektimes.rpc.service.ServiceInstance;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认实现
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class FileSystemServiceRegistry implements ServiceRegistry {

    private final Serializer serializer = Serializer.DEFAULT;

    private File rootDirectory;

    @Override
    public void initialize(Map<String, Object> config) {
        rootDirectory = new File(System.getProperty("java.io.tmpdir"));
    }

    @Override
    public void register(ServiceInstance serviceInstance) {
        String serviceName = serviceInstance.getServiceName();
        File serviceDirectory = new File(rootDirectory, serviceName);
        File serviceInstanceFile = new File(serviceDirectory, serviceInstance.getId());
        try {
            byte[] bytes = serializer.serialize(serviceInstance);
            FileUtils.writeByteArrayToFile(serviceInstanceFile, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deregister(ServiceInstance serviceInstance) {
        String serviceName = serviceInstance.getServiceName();
        File serviceDirectory = new File(rootDirectory, serviceName);
        File serviceInstanceFile = new File(serviceDirectory, serviceInstance.getId());
        FileUtils.deleteQuietly(serviceInstanceFile);
    }

    @Override
    public List<ServiceInstance> getServiceInstances(String serviceName) {
        File serviceDirectory = new File(rootDirectory, serviceName);
        Collection<File> files = FileUtils.listFiles(serviceDirectory, null, false);
        return (List) files.stream().map(file -> {
            try {
                byte[] bytes = FileUtils.readFileToByteArray(file);
                return serializer.deserialize(bytes, ServiceInstance.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public void close() {
        FileUtils.deleteQuietly(rootDirectory);
    }
}
