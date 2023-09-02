package com.xiaox.test.springcache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Classname CacheTestServiceTest
 * @Description TODO
 * @Date 2021/5/13 3:37 下午
 * @Author by xiaoxiong
 */
@SpringBootTest
class CacheTestServiceTest {

    @Autowired
    private CacheTestService cacheTestService;

    @Test
    void test1() {
        System.out.println("输出："+cacheTestService.test("222"));
    }
}