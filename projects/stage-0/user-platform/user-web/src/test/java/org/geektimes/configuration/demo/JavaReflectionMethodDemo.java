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
package org.geektimes.configuration.demo;

import java.lang.reflect.Method;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class JavaReflectionMethodDemo {

    public static void main(String[] args) throws Exception {
        String className = "org.geektimes.configuration.demo.JavaReflectionMethodDemo";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> klass = classLoader.loadClass(className);
        Method method = klass.getMethod("echo");
        // 关闭成员检查
        method.setAccessible(true);
        for (int i = 0; i < 10000; i++) {
            method.invoke(null);
        }

        System.out.println(method);

        for (int i = 0; i < 10000; i++) {
            echo(); // 生成字节码 -> JIT C2 编译器将该方法编程 Native Code
        }
    }

    public static void echo() {
        System.out.println("echo");
    }
}
