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

import org.springframework.context.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * {@link ApplicationListener} 示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ApplicationListener
 * @see EventListener
 * @since
 */
@EnableAsync
public class ApplicationListenerDemo implements ApplicationEventPublisherAware {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // 将引导类 ApplicationListenerDemo 作为 Configuration Class
        context.register(ApplicationListenerDemo.class);

        // 方法一：基于 Spring 接口：向 Spring 应用上下文注册事件
        // a 方法：基于 ConfigurableApplicationContext API 实现
        context.addApplicationListener(new ApplicationListener<ApplicationEvent>() {
            @Override
            public void onApplicationEvent(ApplicationEvent event) {
                println("ApplicationListener - 接收到 Spring 事件：" + event);
            }
        });

        // b 方法：基于 ApplicationListener 注册为 Spring Bean
        // 通过 Configuration Class 来注册
        context.register(MyApplicationListener.class);

        // 方法二：基于 Spring 注解：@org.springframework.context.event.EventListener

        // ApplicationEventMulticaster
        // 启动 Spring 应用上下文
        context.refresh(); // ContextRefreshedEvent
        // 启动 Spring 上下文
        context.start();  // ContextStartedEvent
        // 停止 Spring 上下文
        context.stop();  // ContextStoppedEvent
        // 关闭 Spring 应用上下文
        context.close(); // ContextClosedEvent
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new ApplicationEvent("Hello,World") {
        });

        // 发送 PayloadApplicationEvent
        applicationEventPublisher.publishEvent("Hello,World");
        applicationEventPublisher.publishEvent(new MyPayloadApplicationEvent(this, "Hello,World"));
    }

    static class MyPayloadApplicationEvent extends PayloadApplicationEvent<String> {

        /**
         * Create a new PayloadApplicationEvent.
         *
         * @param source  the object on which the event initially occurred (never {@code null})
         * @param payload the payload object (never {@code null})
         */
        public MyPayloadApplicationEvent(Object source, String payload) {
            super(source, payload);
        }
    }

    @EventListener
    public void onPayloadApplicationEvent(PayloadApplicationEvent<String> event) {
        println("onPayloadApplicationEvent - 接收到 Spring PayloadApplicationEvent：" + event);
    }

    static class MyApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            println("MyApplicationListener - 接收到 Spring 事件：" + event);
        }
    }

    @EventListener
    @Order(2)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        println("@EventListener(onApplicationEvent) - 接收到 Spring ContextRefreshedEvent");
    }

    @EventListener
    @Order(1)
    public void onApplicationEvent1(ContextRefreshedEvent event) {
        println("@EventListener(onApplicationEvent1) - 接收到 Spring ContextRefreshedEvent");
    }

    @EventListener
    @Async
    public void onApplicationEventAsync(ContextRefreshedEvent event) {
        println("@EventListener（异步） - 接收到 Spring ContextRefreshedEvent");
    }

    @EventListener
    public void onApplicationEvent(ContextStartedEvent event) {
        println("@EventListener - 接收到 Spring ContextStartedEvent");
    }

    @EventListener
    public void onApplicationEvent(ContextClosedEvent event) {
        println("@EventListener - 接收到 Spring ContextClosedEvent");
    }

    private static void println(Object printable) {
        System.out.printf("[线程：%s] : %s\n", Thread.currentThread().getName(), printable);
    }
}
