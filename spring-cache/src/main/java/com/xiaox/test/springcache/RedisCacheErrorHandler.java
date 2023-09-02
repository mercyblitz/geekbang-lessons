package com.xiaox.test.springcache;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * @Classname RedisCacheErrorHandler
 * @Description TODO
 * @Date 2021/5/13 4:20 下午
 * @Author by xiaoxiong
 */
public class RedisCacheErrorHandler implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
        System.out.println(e.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
        System.out.println(e.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
        System.out.println(e.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        System.out.println(e.getMessage());
    }
}
