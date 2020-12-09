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
package org.geekbang.thinking.in.spring.aop.features;

import org.geekbang.thinking.in.spring.aop.features.aspect.AspectConfiguration;
import org.geekbang.thinking.in.spring.aop.features.aspect.AspectConfiguration2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Pointcut 示例
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
@Configuration // Configuration class
@EnableAspectJAutoProxy // 激活 Aspect 注解自动代理
public class AspectJAnnotatedPointcutDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AspectJAnnotatedPointcutDemo.class,
                AspectConfiguration.class,
                AspectConfiguration2.class);
        context.refresh();

        AspectJAnnotatedPointcutDemo aspectJAnnotationDemo = context.getBean(AspectJAnnotatedPointcutDemo.class);

        aspectJAnnotationDemo.execute();

        context.close();
    }

    public void execute() {
        System.out.println("execute()...");
    }
}
