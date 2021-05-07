package org.geektimes.cache.redis;

import org.geektimes.cache.AbstractCache;
import org.geektimes.cache.ExpirableEntry;
import org.geektimes.commons.io.Deserializer;
import org.geektimes.commons.io.Deserializers;
import org.geektimes.commons.io.Serializer;
import org.geektimes.commons.io.Serializers;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import javax.cache.CacheException;
import javax.cache.configuration.Configuration;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static redis.clients.jedis.params.SetParams.setParams;

public class JedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

    private final Jedis jedis;

    private final Serializers serializers;

    private final Deserializers deserializers;

    private final byte[] keyPrefixBytes;

    private final int keyPrefixBytesLength;

    public JedisCache(JedisCacheManager jedisCacheManager, String cacheName,
                      Configuration<K, V> configuration, Jedis jedis) {
        super(jedisCacheManager, cacheName, configuration);
        this.jedis = jedis;
        this.serializers = jedisCacheManager.getSerializers();
        this.deserializers = jedisCacheManager.getDeserializers();
        this.keyPrefixBytes = buildKeyPrefixBytes(cacheName);
        this.keyPrefixBytesLength = keyPrefixBytes.length;
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = getKeyBytes(key);
        return jedis.exists(keyBytes);
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = getKeyBytes(key);
        return getEntry(keyBytes);
    }

    protected ExpirableEntry<K, V> getEntry(byte[] keyBytes) throws CacheException, ClassCastException {
        byte[] valueBytes = jedis.get(keyBytes);
        return deserialize(valueBytes, ExpirableEntry.class);
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> entry) throws CacheException, ClassCastException {
        byte[] keyBytes = getKeyBytes(entry.getKey());
        byte[] valueBytes = serialize(entry);
        if (entry.isEternal()) {
            jedis.set(keyBytes, valueBytes);
        } else {
            jedis.set(keyBytes, valueBytes, setParams().px(entry.getExpiredTime()));
        }
    }

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = getKeyBytes(key);
        ExpirableEntry<K, V> oldEntry = getEntry(keyBytes);
        jedis.del(keyBytes);
        return oldEntry;
    }

    @Override
    protected void clearEntries() throws CacheException {
        Set<byte[]> keysBytes = jedis.keys(keyPrefixBytes);
        for (byte[] keyBytes : keysBytes) {
            jedis.del(keyBytes);
        }
    }

    @Override
    protected Set<K> keySet() {
        Set<byte[]> keysBytes = jedis.keys(keyPrefixBytes);
        Set<K> keys = new LinkedHashSet<>(keysBytes.size());
        for (byte[] keyBytes : keysBytes) {
            keys.add(deserialize(keyBytes, getConfiguration().getKeyType()));
        }
        return Collections.unmodifiableSet(keys);
    }

    @Override
    protected void doClose() {
        this.jedis.close();
    }


    private byte[] buildKeyPrefixBytes(String cacheName) {
        StringBuilder keyPrefixBuilder = new StringBuilder("JedisCache-")
                .append(cacheName).append(":");
        return keyPrefixBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getKeyBytes(Object key) {
        byte[] suffixBytes = serialize(key);
        int suffixBytesLength = suffixBytes.length;
        byte[] bytes = new byte[keyPrefixBytesLength + suffixBytesLength];
        System.arraycopy(keyPrefixBytes, 0, bytes, 0, keyPrefixBytesLength);
        System.arraycopy(suffixBytes, 0, bytes, keyPrefixBytesLength, suffixBytesLength);
        return bytes;
    }

    // 是否可以抽象出一套序列化和反序列化的 API
    private byte[] serialize(Object value) throws CacheException {
        Serializer serializer = serializers.getMostCompatible(value.getClass());
        try {
            return serializer.serialize(value);
        } catch (IOException e) {
            throw new CacheException(e);
        }
    }

    private <T> T deserialize(byte[] bytes, Class<T> deserializedType) throws CacheException {
        Deserializer deserializer = deserializers.getMostCompatible(deserializedType);
        try {
            return (T) deserializer.deserialize(bytes);
        } catch (IOException e) {
            throw new CacheException(e);
        }
    }

}
