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
package org.geekbang.thinking.in.spring.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.annotation.PostConstruct;

/**
 * 注入 {@link ApplicationEventPublisher} 示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class InjectingApplicationEventPublisherDemo implements ApplicationEventPublisherAware,
        ApplicationContextAware, BeanPostProcessor {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        //# 3
        applicationEventPublisher.publishEvent(new MySpringEvent("The event from @Autowired ApplicationEventPublisher"));
        // #4
        applicationContext.publishEvent(new MySpringEvent("The event from @Autowired ApplicationContext"));
    }

    public static void main(String[] args) {

        // 创建注解驱动 Spring 应用上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // 注册 Configuration Class
        context.register(InjectingApplicationEventPublisherDemo.class);

        // 增加 Spring 事件监听器
        context.addApplicationListener(new MySpringEventListener());

        // 启动 Spring 应用上下文
        context.refresh();

        // 关闭 Spring 应用上下文
        context.close();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) { // #1
        applicationEventPublisher.publishEvent(new MySpringEvent("The event from ApplicationEventPublisherAware"));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException { // #2
        applicationEventPublisher.publishEvent(new MySpringEvent("The event from ApplicationContextAware"));
    }
}
