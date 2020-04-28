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
package org.geekbang.thinking.in.spring.validation;

import org.geekbang.thinking.in.spring.ioc.overview.domain.User;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.stream.Stream;

/**
 * JavaBeans 示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class JavaBeansDemo {

    public static void main(String[] args) throws IntrospectionException {

        // stopClass 排除（截止）类
        BeanInfo beanInfo = Introspector.getBeanInfo(User.class, Object.class);
        // 属性描述符 PropertyDescriptor

        // 所有 Java 类均继承 java.lang.Object
        // class 属性来自于 Object#getClass() 方法
        Stream.of(beanInfo.getPropertyDescriptors())
                .forEach(propertyDescriptor -> {
//                    propertyDescriptor.getReadMethod(); // Getter 方法
//                    propertyDescriptor.getWriteMethod(); // Setter 方法
                    System.out.println(propertyDescriptor);
                });

        // 输出 User 定义的方法 MethodDescriptor
        Stream.of(beanInfo.getMethodDescriptors()).forEach(System.out::println);

    }
}
