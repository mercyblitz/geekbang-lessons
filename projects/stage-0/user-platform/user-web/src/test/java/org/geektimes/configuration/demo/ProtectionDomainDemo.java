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

import java.lang.reflect.Field;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Vector;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 * Date : 2021-04-24
 */
public class ProtectionDomainDemo {

    public static void main(String[] args) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Field field = ClassLoader.class.getDeclaredField("classes");
        field.setAccessible(true);
        // Bootstrap ClassLoader ( rt.jar in JDK ) 取不到
        Vector<Class> classes = (Vector<Class>) field.get(classLoader);
        for (Class klass : classes) {
            ProtectionDomain protectionDomain = klass.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            System.out.printf("Class[%s] in %s\n", klass.getName(), codeSource.getLocation().getPath());
        }
    }
}
