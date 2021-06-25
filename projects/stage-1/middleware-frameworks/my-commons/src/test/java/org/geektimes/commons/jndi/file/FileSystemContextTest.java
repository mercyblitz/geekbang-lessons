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
package org.geektimes.commons.jndi.file;

import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.geektimes.commons.jndi.file.FileSystemInitialContextFactoryTest.INITIAL_CONTEXT_FACTORY_IMPL_CLASS_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * {@link FileSystemContext} test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class FileSystemContextTest {


    /***
     * 外部化配置 javax.naming.spi.InitialContextFactory 实现
     * - Applet 参数
     * - Java System Properties（通过 -D)
     * - 应用资源文件
     *
     * @throws Exception
     */
    @Test
    public void testInJavaSystemProperties() throws Exception {
        // 通过 Java System Properties 配置
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY_IMPL_CLASS_NAME);
        // 测试逻辑
        doTest();
        // 移除 Java System Properties 配置
        System.getProperties().remove(Context.INITIAL_CONTEXT_FACTORY);
    }

    @Test
    public void testInApplicationResourceFile() throws Exception {
        // 测试逻辑
        doTest();
    }

    private void doTest() throws NamingException {
        InitialContext context = new InitialContext();
        String name = "def";
        Object value = "2021";
        Context envContext = (Context) context.lookup("java:comp/env");
        envContext.bind(name, value);

        assertEquals(value, envContext.lookup(name));

        envContext.unbind(name);
        assertNull(envContext.lookup(name));
    }

}
