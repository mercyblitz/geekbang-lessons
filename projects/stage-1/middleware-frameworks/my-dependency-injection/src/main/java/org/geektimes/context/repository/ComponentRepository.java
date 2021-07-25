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
package org.geektimes.context.repository;

import org.geektimes.context.core.Lifecycle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 组件仓库
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface ComponentRepository extends Lifecycle {

    /**
     * 通过名称查找组件对象
     *
     * @param name the name of component
     * @param <C>  the type of component
     * @return <code>null</code> if not found
     */
    <C> C getComponent(String name);

    /**
     * Register a component with name.
     *
     * @param name      the name of component
     * @param component the instance of component
     */
    void registerComponent(String name, Object component);

    /**
     * Get all names of components
     *
     * @return non-null
     */
    Set<String> getComponentNames();

    /**
     * Get all components
     *
     * @return non-null
     */
    default Map<String, Object> getComponents() {
        final Map<String, Object> componentsMap = new HashMap<>();
        getComponentNames().forEach(name -> componentsMap.put(name, getComponent(name)));
        return componentsMap;
    }
}
