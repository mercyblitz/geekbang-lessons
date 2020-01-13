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
package org.geekbang.thinking.in.spring.configuration.metadata;

import org.geekbang.thinking.in.spring.ioc.overview.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.util.Map;

/**
 * 基于 Java 注解 Spring IoC 容器元信息配置示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
// 将当前类作为 Configuration Class
@ImportResource("classpath:/META-INF/dependency-lookup-context.xml")
@Import(User.class)
@PropertySource("classpath:/META-INF/user-bean-definitions.properties") // Java 8+ @Repeatable 支持
@PropertySource("classpath:/META-INF/user-bean-definitions.properties")
// @PropertySources(@PropertySource(...))
public class AnnotatedSpringIoCContainerMetadataConfigurationDemo {

    /**
     * user.name 是 Java Properties 默认存在，当前用户：mercyblitz，而非配置文件中定义"小马哥"
     * @param id
     * @param name
     * @return
     */
    @Bean
    public User configuredUser(@Value("${user.id}") Long id, @Value("${user.name}") String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 注册当前类作为 Configuration Class
        context.register(AnnotatedSpringIoCContainerMetadataConfigurationDemo.class);
        // 启动 Spring 应用上下文
        context.refresh();
        // beanName 和 bean 映射
        Map<String, User> usersMap = context.getBeansOfType(User.class);
        for (Map.Entry<String, User> entry : usersMap.entrySet()) {
            System.out.printf("User Bean name : %s , content : %s \n", entry.getKey(), entry.getValue());
        }
        // 关闭 Spring 应用上下文
        context.close();
    }
}
