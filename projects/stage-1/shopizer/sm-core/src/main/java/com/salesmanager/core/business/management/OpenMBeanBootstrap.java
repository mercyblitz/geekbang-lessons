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

import com.salesmanager.core.business.modules.email.Email;
import com.salesmanager.core.business.modules.email.EmailConfig;
import com.salesmanager.core.business.modules.email.HtmlEmailSender;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * Open MBean
 * <p>
 * -Dcom.sun.management.jmxremote.port=12345
 * -Dcom.sun.management.jmxremote.ssl=false
 * -Dcom.sun.management.jmxremote.authenticate=false
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class OpenMBeanBootstrap {

    public static void main(String[] args) throws MalformedObjectNameException,
            NotCompliantMBeanException,
            InstanceAlreadyExistsException,
            MBeanRegistrationException, IOException {
        // Create a OpenMBean instance
        HtmlEmailSender htmlEmailSender = new HtmlEmailSender() {

            private EmailConfig emailConfig = new EmailConfig();

            {
                emailConfig.setHost("127.0.0.1");
                emailConfig.setPort("25");
                emailConfig.setProtocol("https");
                emailConfig.setEmailTemplatesPath("/email");
                emailConfig.setSmtpAuth(true);
                emailConfig.setStarttls(true);
                emailConfig.setUsername("anonymous");
                emailConfig.setPassword("password");
            }

            @Override
            public void send(Email email) throws Exception {

            }

            @Override
            public void setEmailConfig(EmailConfig emailConfig) {
                System.out.println(emailConfig.toJSONString());
                this.emailConfig = emailConfig;
            }

            @Override
            public EmailConfig getEmailConfig() {
                return emailConfig;
            }

        };
        HtmlEmailSenderOpenMBean htmlEmailSenderOpenMBean = new HtmlEmailSenderOpenMBean(htmlEmailSender);

        // Create an ObjectName for MBean
        String packageName = HtmlEmailSenderOpenMBean.class.getPackage().getName();
        String simpleClassName = HtmlEmailSenderOpenMBean.class.getSimpleName();
        ObjectName objectName = new ObjectName(packageName + ":type=" + simpleClassName);
        // Get the MBeanServer
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        // Register MBean
        mBeanServer.registerMBean(htmlEmailSenderOpenMBean, objectName);

        System.out.println("Press any key to exit");
        System.in.read();
    }

}
