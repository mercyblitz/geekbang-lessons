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
package com.salesmanager.core.business.management;

import com.salesmanager.core.business.modules.email.EmailConfig;
import com.salesmanager.core.business.modules.email.EmailConfigMBean;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * Standard MBean
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class StandardMBeanBootstrap {

    public static void main(String[] args) throws MalformedObjectNameException,
            NotCompliantMBeanException,
            InstanceAlreadyExistsException,
            MBeanRegistrationException, IOException {
        // Create a MBean instance
        EmailConfig emailConfig = new EmailConfig();

        // MBean interface
        registerMBean(EmailConfigMBean.class, emailConfig);
        // MXBean interface
        registerMBean(EmailConfigMXBean.class, emailConfig);
        // @MXBean annotated interface
        registerMBean(MXBeanAnnotatedEmailConfig.class, emailConfig);

        System.out.println("Press any key to exit");
        System.in.read();
    }

    private static void registerMBean(Class mBeanInterface, Object mBeanInstance) throws
            MalformedObjectNameException,
            NotCompliantMBeanException,
            InstanceAlreadyExistsException,
            MBeanRegistrationException, IOException {
        // Create an ObjectName for MBean
        String packageName = mBeanInterface.getPackage().getName();
        String simpleClassName = mBeanInterface.getSimpleName();
        ObjectName objectName = new ObjectName(packageName + ":type=" + simpleClassName);
        // Get the MBeanServer
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        // Register MBean
        mBeanServer.registerMBean(mBeanInstance, objectName);
    }
}
