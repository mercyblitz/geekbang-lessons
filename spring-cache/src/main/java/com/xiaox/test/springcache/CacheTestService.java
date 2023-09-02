package com.xiaox.test.springcache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @Classname CacheTestService
 * @Description TODO
 * @Date 2021/5/13 3:33 下午
 * @Author by xiaoxiong
 */
@Service
public class CacheTestService {

    @Cacheable(value = "com.xiaox.test.cache",key = "#a",unless = "#result == null")
    public String test(String a){
       System.out.println("进入方法执行！！！！传入参数："+a);
       return "Hello world";
    }

}
