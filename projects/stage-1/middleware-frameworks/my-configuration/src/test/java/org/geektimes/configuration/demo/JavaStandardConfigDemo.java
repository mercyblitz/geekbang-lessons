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

import java.util.prefs.Preferences;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class JavaStandardConfigDemo {

    /**
     * Java System Properties 必须定义：
     * <p>
     * java.version
     * java.vendor
     * java.vendor.url
     * java.home
     * java.class.version
     * java.class.path
     * os.name
     * os.arch
     * os.version
     * file.separator
     * path.separator
     * line.separator
     * user.name
     * user.home
     * user.dir
     *
     * @param args
     */
    public static void main(String[] args) {
        demoJavaSystemProperties();
        demoOSEnvironmentVariables();
        demoPreferences();
    }

    private static void demoPreferences() {
        Preferences preferences = Preferences.userRoot();
        preferences.put("user.name", "mercyblitz");
        System.out.println(preferences.get("user.name", null));
    }

    private static void demoOSEnvironmentVariables() {
        System.out.println(System.getenv("JAVA_HOME"));
    }

    private static void demoJavaSystemProperties() {
        String userDir = System.getProperty("user.dir");
        // 如果非 String 类型
        System.out.println(Boolean.getBoolean("user.age"));
        System.out.println(userDir);

        // 原始 java.util.concurrent.ForkJoinPool.makeCommonPool() 实现
        String pp = System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
        // 从 Java System Properties 获取
        // Integer.parseInt 方法将 int -> Integer
        // Integer.parseInt 方法可能会抛出 NumberFormatException
        Integer parallelism = pp == null ? null : Integer.parseInt(pp);
        // 优化方案
        // 从 Java System Properties 属性直接变为 Integer 对象
        // 避免 NumberFormatException
        // 支持 10进制以外进制
        // 利用Integer.getInteger(String,Integer) 提供默认值
        // 同理 Long
        parallelism = Integer.getInteger("java.util.concurrent.ForkJoinPool.common.parallelism");
    }
}
