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
package org.geekbang.thinking.in.spring.application.context.lifecycle;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.Lifecycle;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.GenericApplicationContext;

import java.io.IOException;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

/**
 * Spring Shutdown Hook 线程示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Lifecycle
 * @since
 */
public class SpringShutdownHookThreadDemo {

    public static void main(String[] args) throws IOException {
        GenericApplicationContext context = new GenericApplicationContext();

        context.addApplicationListener(new ApplicationListener<ContextClosedEvent>() {
            @Override
            public void onApplicationEvent(ContextClosedEvent event) {
                System.out.printf("[线程 %s] ContextClosedEvent 处理\n", Thread.currentThread().getName());
            }
        });

        // 刷新 Spring 应用上下文
        context.refresh();

        // 注册 Shutdown Hook
        context.registerShutdownHook();

        System.out.println("按任意键继续并且关闭 Spring 应用上下文");
        System.in.read();

        // 关闭 Spring 应用（同步）
        context.close();
    }
}
