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

package org.geektimes.web.mvc.dbProxy;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lw1243925457
 */
public class JdkRepositoryProxy {

    private static Map<String, Object> repositoryMap = new ConcurrentHashMap<>();

    public static <T> T create(Class clazz) {
        // 查询是否之前生成过，存储的直接返回
        if (!repositoryMap.containsKey(clazz.getName())) {
            repositoryMap.put(clazz.getName(), newProxy(clazz));
        }
        return (T) repositoryMap.get(clazz.getName());
    }

    private static <T> T newProxy(Class clazz) {
        ClassLoader loader = JdkRepositoryProxy.class.getClassLoader();
        Class[] classes = new Class[]{clazz};
        return (T) Proxy.newProxyInstance(loader, classes, new RepositoryInvocationHandler());
    }
}
