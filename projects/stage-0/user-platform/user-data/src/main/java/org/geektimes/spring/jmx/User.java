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
package org.geektimes.spring.jmx;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * 用户模型类
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-17
 */
@ManagedResource(objectName = "org.geektimes:name=User")
public class User {

    private String name;

    private int age;

    @ManagedAttribute
    public String getName() {
        return name;
    }

    @ManagedOperation
    @ManagedOperationParameter(name = "name", description = "name")
    public void setName(String name) {
        this.name = name;
    }

    @ManagedAttribute
    public int getAge() {
        return age;
    }

    @ManagedOperation
    @ManagedOperationParameter(name = "age", description = "age")
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    @ManagedOperation
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
