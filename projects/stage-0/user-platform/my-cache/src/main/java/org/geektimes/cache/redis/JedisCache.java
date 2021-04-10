package org.geektimes.cache.redis;

import org.geektimes.cache.AbstractCache;
import redis.clients.jedis.Jedis;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.*;
import java.util.Iterator;

public class JedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

    private final Jedis jedis;

    public JedisCache(CacheManager cacheManager, String cacheName,
                      Configuration<K, V> configuration, Jedis jedis) {
        super(cacheManager, cacheName, configuration);
        this.jedis = jedis;
    }

    @Override
    protected V doGet(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(key);
        return doGet(keyBytes);
    }

    protected V doGet(byte[] keyBytes) {
        byte[] valueBytes = jedis.get(keyBytes);
        V value = deserialize(valueBytes);
        return value;
    }

    @Override
    protected V doPut(K key, V value) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(key);
        byte[] valueBytes = serialize(value);
        V oldValue = doGet(keyBytes);
        jedis.set(keyBytes, valueBytes);
        return oldValue;
    }

    @Override
    protected V doRemove(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(key);
        V oldValue = doGet(keyBytes);
        jedis.del(keyBytes);
        return oldValue;
    }

    @Override
    protected void doClear() throws CacheException {

    }

    @Override
    protected Iterator<Entry<K, V>> newIterator() {
        return null;
    }

    @Override
    protected void doClose() {
        this.jedis.close();
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

    private V deserialize(byte[] bytes) throws CacheException {
        if (bytes == null) {
            return null;
        }
        V value = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            // byte[] -> Value
            value = (V) objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException(e);
        }
        return value;
    }

}
