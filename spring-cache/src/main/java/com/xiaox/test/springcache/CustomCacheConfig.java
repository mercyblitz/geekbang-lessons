package com.xiaox.test.springcache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname CustomCacheConfig
 * @Description TODO
 * @Date 2021/5/13 3:21 下午
 * @Author by xiaoxiong
 */
@Configuration
@EnableCaching
public class CustomCacheConfig extends CachingConfigurerSupport {

    @Value("${cache.custom.redis.uri}")
    private String uri;

    /*@Bean("CustomRedisCacheManager")
    @Override
    public CacheManager cacheManager() {
        return new RedisCacheManager(uri);
    }*/

    @Override
    @Bean
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }
}
