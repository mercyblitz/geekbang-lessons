package com.salesmanager.shop.application;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import javax.jms.JMSException;
import javax.jms.MessageNotWriteableException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.sql.DataSource;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ShopApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ShopApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }

    @ConditionalOnBean(name = "secondaryDataSource")
    @Bean
    @Autowired
    public ApplicationRunner runner(@Qualifier("secondaryDataSource") DataSource dataSource) {
        return args -> {
            System.out.println("Get Connection : " + dataSource.getConnection());
        };
    }

    @Bean
    @Autowired
    public ApplicationRunner runner2(MessageProducer messageProducer) {
        return args -> {
            TextMessage textMessage = createTextMessage("Hello,World");
            messageProducer.send(textMessage);
        };
    }

    private TextMessage createTextMessage(String content) throws JMSException {
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText(content);
        return textMessage;
    }

}
