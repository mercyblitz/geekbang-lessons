package org.geekbang.thinking.in.spring.annotation.config;

import org.geekbang.thinking.in.spring.ioc.overview.domain.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaoheitalk
 * @type UserConfiguration
 * @date 2020/7/20 12:45
 */
@Configuration
public class UserConfiguration {
    @Bean
    public User initUser(){
        User user = new User();
        user.setId(2L);
        user.setName("小明");
        return user;
    }
}
