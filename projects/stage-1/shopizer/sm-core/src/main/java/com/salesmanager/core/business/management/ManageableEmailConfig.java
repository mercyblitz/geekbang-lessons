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

import org.json.simple.JSONAware;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public interface ManageableEmailConfig extends JSONAware {

    boolean isSmtpAuth();

    void setSmtpAuth(boolean smtpAuth);

    boolean isStarttls();

    void setStarttls(boolean starttls);

    void setEmailTemplatesPath(String emailTemplatesPath);

    String getEmailTemplatesPath();

    String getHost();

    void setHost(String host);

    String getPort();

    void setPort(String port);

    String getProtocol();

    void setProtocol(String protocol);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);
}
