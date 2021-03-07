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
package org.geekbang.thinking.in.spring.aop.features.event;

import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.EventPublicationInterceptor;

import java.lang.reflect.Method;

/**
 * {@link EventPublicationInterceptor} 示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
@Configuration // Configuration Class
@EnableAspectJAutoProxy
public class EventPublicationInterceptorDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(EventPublicationInterceptorDemo.class, Executor.class, StaticExecutor.class);
        // 启动 Spring 应用上下文
        context.refresh();

        // 5. 执行目标方法
        Executor executor = context.getBean(Executor.class);

        StaticExecutor staticExecutor = context.getBean(StaticExecutor.class);

        executor.execute();

        staticExecutor.execute();

        // 关闭 Spring 应用上下文
        context.close();
    }

    // 1. 将 EventPublicationInterceptor 声明为 Spring Bean
    @Bean
    public static EventPublicationInterceptor eventPublicationInterceptor() {
        EventPublicationInterceptor eventPublicationInterceptor = new EventPublicationInterceptor();
        // 关联目标（自定义）事件类型 - ExecutedEvent
        eventPublicationInterceptor.setApplicationEventClass(ExecutedEvent.class);
        return eventPublicationInterceptor;
    }

    // 2. 实现 Pointcut Bean
    @Bean
    public static Pointcut pointcut() {
        return new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return "execute".equals(method.getName()) && Executor.class.equals(targetClass);
            }
        };
    }

    // 3. 声明一个 Advisor Bean
    @Bean
    public static PointcutAdvisor pointcutAdvisor(Pointcut pointcut, EventPublicationInterceptor eventPublicationInterceptor) {
        // EventPublicationInterceptor is MethodInterceptor is Advice
        return new DefaultPointcutAdvisor(pointcut, eventPublicationInterceptor);
    }

    // 4. 处理事件 - ExecutedEvent
    @EventListener(ExecutedEvent.class)
    public void executed(ExecutedEvent event) {
        System.out.println("Executed : " + event);
    }
}
