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
package com.salesmanager.shop.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJndi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

import javax.jms.MessageProducer;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * JNDI Configuration Class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Configuration
public class JndiConfig {

    @ConditionalOnJndi("java:comp/env/jdbc/ShopizerDataSource")
    @Bean(name = "secondaryDataSource")
    public JndiObjectFactoryBean jndiObjectFactoryBean() {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("jdbc/ShopizerDataSource");
        bean.setResourceRef(true);
        bean.setExpectedType(DataSource.class);
        return bean;
    }

    @Bean
    public JndiTemplate jndiTemplate() {
        return new JndiTemplate();
    }

    @Bean
    public MessageProducer messageProducer(@Autowired JndiTemplate jndiTemplate) throws NamingException {
        return jndiTemplate.lookup("jms/message-producer", MessageProducer.class);
    }

}
