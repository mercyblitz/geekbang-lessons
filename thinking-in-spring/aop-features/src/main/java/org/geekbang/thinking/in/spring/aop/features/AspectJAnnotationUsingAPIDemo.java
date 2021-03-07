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

import org.aspectj.lang.annotation.Aspect;
import org.geekbang.thinking.in.spring.aop.features.aspect.AspectConfiguration;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class AspectJAnnotationUsingAPIDemo {

    public static void main(String[] args) {

        // 通过创建一个 HashMap 缓存，作为被代理对象
        Map<String, Object> cache = new HashMap<>();
        // 创建 Proxy 工厂(AspectJ)
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(cache);
        // 增加 Aspect 配置类
        proxyFactory.addAspect(AspectConfiguration.class);
        // 设置暴露代理对象到 AopContext
        proxyFactory.setExposeProxy(true);
        proxyFactory.addAdvice(new MethodBeforeAdvice() {
            @Override
            public void before(Method method, Object[] args, Object target) throws Throwable {
                if ("put".equals(method.getName()) && args.length == 2) {
                    Object proxy = AopContext.currentProxy();
                    System.out.printf("[MethodBeforeAdvice] 当前存放是 Key: %s , Value : %s ，" +
                            "代理对象：%s\n", args[0], args[1], proxy);
                }
            }
        });

        // 添加 AfterReturningAdvice
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
            public void afterReturning(Object returnValue, Method method, Object[] args, Object target)
                    throws Throwable {
                if ("put".equals(method.getName()) && args.length == 2) {
                    System.out.printf("[AfterReturningAdvice] 当前存放是 Key: %s , 新存放的 Value : %s , 之前关联的 Value : %s\n ",
                            args[0],    // key
                            args[1],    // new value
                            returnValue // old value
                    );
                }
            }
        });

        // 存储数据
        // cache.put("1", "A");
        // 通过代理对象存储数据
        Map<String, Object> proxy = proxyFactory.getProxy();
        proxy.put("1", "A");
        proxy.put("1", "B");
        System.out.println(cache.get("1"));

    }
}
