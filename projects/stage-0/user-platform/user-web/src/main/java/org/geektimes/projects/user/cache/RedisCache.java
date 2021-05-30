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
import redis.clients.jedis.Jedis;

import javax.cache.CacheException;
import java.io.*;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Redis Cache 实现
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-29
 */
public class RedisCache implements Cache {

    private final String name;

    private final Jedis jedis;

    public RedisCache(String name, Jedis jedis) {
        Objects.requireNonNull(name, "The 'name' argument must not be null.");
        Objects.requireNonNull(jedis, "The 'jedis' argument must not be null.");
        this.name = name;
        this.jedis = jedis;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return jedis;
    }

    @Override
    public ValueWrapper get(Object key) {
        byte[] keyBytes = serialize(key);
        byte[] valueBytes = jedis.get(keyBytes);
        return () -> deserialize(valueBytes);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        byte[] keyBytes = serialize(key);
        byte[] valueBytes = serialize(value);
        jedis.set(keyBytes, valueBytes);
    }

    @Override
    public void evict(Object key) {
        byte[] keyBytes = serialize(key);
        jedis.del(keyBytes);
    }

    @Override
    public void clear() {
        // Redis 是否支持 namespace
        // name:key
        // String 类型的 key :
    }

    // 是否可以抽象出一套序列化和反序列化的 API
    private byte[] serialize(Object value) throws CacheException {
        byte[] bytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            // Key -> byte[]
            objectOutputStream.writeObject(value);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CacheException(e);
        }
        return bytes;
    }

    private <T> T deserialize(byte[] bytes) throws CacheException {
        if (bytes == null) {
            return null;
        }
        T value = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            // byte[] -> Value
            value = (T) objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException(e);
        }
        return value;
    }
}
