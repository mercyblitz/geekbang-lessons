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
package org.geektimes.projects.user.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Redis {@link CacheManager} 实现
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-29
 */
public class RedisCacheManager extends AbstractCacheManager {

    private final JedisPool jedisPool;

    public RedisCacheManager(String uri) {
        this.jedisPool = new JedisPool(uri);
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        // 确保接口不返回 null
        List<? extends Cache> caches = new LinkedList<>();
        prepareCaches(caches);
        return caches;
    }

    protected Cache getMissingCache(String name) {
        Jedis jedis = jedisPool.getResource();
        return new RedisCache(name, jedis);
    }

    private void prepareCaches(List<? extends Cache> caches) {
    }
}
