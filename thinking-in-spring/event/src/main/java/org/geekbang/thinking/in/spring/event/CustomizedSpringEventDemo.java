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

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.GenericApplicationContext;

/**
 * 自定义 Spring 事件示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class CustomizedSpringEventDemo {

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();

        // 1.添加自定义 Spring 事件监听器
        // ListenerRetriever -> 0 .. N 个 ApplicationListener<MySpringEvent> 实例
        // MySpringEvent 以及它子孙类
        context.addApplicationListener(new MySpringEventListener());

        context.addApplicationListener(new ApplicationListener<ApplicationEvent>() {

            @Override
            public void onApplicationEvent(ApplicationEvent event) {
                System.out.println("Event : " + event);
            }
        });

        // 2.启动 Spring 应用上下文
        context.refresh();

        // 3. 发布自定义 Spring 事件
        // ListenerCacheKey -> MySpringEvent
        context.publishEvent(new MySpringEvent("Hello,World"));
        context.publishEvent(new MySpringEvent2("2020"));

        // 4. 关闭 Spring 应用上下文
        context.close();
    }
}
