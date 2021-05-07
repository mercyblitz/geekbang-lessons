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
package org.geektimes.session.cache;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.configuration.microprofile.config.util.DelegatingPropertiesAdapter;
import org.geektimes.session.SessionInfo;
import org.geektimes.session.SessionRepository;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.eclipse.microprofile.config.spi.ConfigProviderResolver.instance;

/**
 * {@link SessionRepository} based on Java Cache.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-05-06
 */
public class ConfigurableCachingSessionRepository implements SessionRepository {

    public static final String CACHE_URI_PROPERTY_NAME = "javax.cache.CacheManager.uri";

    public static final String SESSION_MAX_INACTIVE_INTERVAL_PROPERTY_NAME = "session.max.inactive.internal.seconds";

    private final ClassLoader classLoader;

    private Config config;

    private CacheManager cacheManager;

    private Cache<String, SessionInfo> sessionInfoCache;

    public ConfigurableCachingSessionRepository() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public ConfigurableCachingSessionRepository(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void initialize() {
        this.config = getConfig();
        this.cacheManager = buildCacheManager(config, classLoader);
        this.sessionInfoCache = resolveSessionInfoCache();
    }

    private Config getConfig() {
        ConfigProviderResolver configProviderResolver = instance();
        return configProviderResolver.getConfig(classLoader);
    }

    private CacheManager buildCacheManager(Config config, ClassLoader classLoader) {
        URI uri = config.getValue(CACHE_URI_PROPERTY_NAME, URI.class);
        CachingProvider cachingProvider = Caching.getCachingProvider(classLoader);
        return cachingProvider.getCacheManager(uri, classLoader, new DelegatingPropertiesAdapter(config));
    }

    private Cache<String, SessionInfo> resolveSessionInfoCache() {
        String cacheName = "sessionInfoCache";
        Cache<String, SessionInfo> cache = cacheManager.getCache(cacheName, String.class, SessionInfo.class);
        if (cache == null) {
            MutableConfiguration<String, SessionInfo> configuration =
                    new MutableConfiguration<String, SessionInfo>()
                            .setTypes(String.class, SessionInfo.class)
                            .setExpiryPolicyFactory(this::createExpiryPolicy);
            cache = cacheManager.createCache(cacheName, configuration);
        }
        return cache;
    }

    private Cache<String, Object> resolveAttributesCache(String sessionId) {
        String cacheName = "sessionAttributesCache-" + sessionId;
        Class keyType = String.class;
        Class valueType = Object.class;
        Cache<String, Object> attributeCache = cacheManager.getCache(cacheName, keyType, valueType);
        if (attributeCache == null) {
            MutableConfiguration<String, Object> configuration = new MutableConfiguration<String, Object>()
                    .setTypes(keyType, valueType)
                    .setExpiryPolicyFactory(this::createExpiryPolicy)
                    .setStoreByValue(true);
            attributeCache = cacheManager.createCache(cacheName, configuration);
        }
        return attributeCache;
    }

    private ExpiryPolicy createExpiryPolicy() {
        return new ExpiryPolicy() {
            @Override
            public Duration getExpiryForCreation() {
                return newDuration();
            }

            @Override
            public Duration getExpiryForAccess() {
                return newDuration();
            }

            @Override
            public Duration getExpiryForUpdate() {
                return newDuration();
            }

            private Duration newDuration() {
                Long maxInactiveInterval = config.getValue(SESSION_MAX_INACTIVE_INTERVAL_PROPERTY_NAME, Long.class);
                if (maxInactiveInterval == null) {
                    maxInactiveInterval = 30L; // 30L as the default
                }
                return new Duration(TimeUnit.SECONDS, maxInactiveInterval);
            }
        };
    }

    @Override
    public SessionRepository saveSessionInfo(SessionInfo sessionInfo) {
        sessionInfo.setLastAccessedTime(System.currentTimeMillis());
        sessionInfoCache.put(sessionInfo.getId(), sessionInfo);
        return this;
    }

    @Override
    public SessionInfo getSessionInfo(String sessionId) {
        return sessionInfoCache.get(sessionId);
    }

    @Override
    public SessionRepository removeSessionInfo(String sessionId) {
        sessionInfoCache.remove(sessionId);
        return this;
    }

    @Override
    public SessionRepository setAttribute(String sessionId, String name, Object value) {
        Cache<String, Object> attributesCache = resolveAttributesCache(sessionId);
        attributesCache.put(name, value);
        return this;
    }

    @Override
    public SessionRepository removeAttribute(String sessionId, String name) {
        Cache<String, Object> attributesCache = resolveAttributesCache(sessionId);
        attributesCache.remove(name);
        return this;
    }

    @Override
    public Object getAttribute(String sessionId, String name) {
        Cache<String, Object> attributesCache = resolveAttributesCache(sessionId);
        return attributesCache.get(name);
    }

    @Override
    public Set<String> getAttributeNames(String sessionId) {
        Cache<String, Object> attributesCache = resolveAttributesCache(sessionId);
        Set<String> attributeNames = new LinkedHashSet<>();
        for (Cache.Entry<String, Object> entry : attributesCache) {
            attributeNames.add(entry.getKey());
        }
        return attributeNames;
    }

    @Override
    public void destroy() {
        destroyCacheManager();
    }

    private void destroyCacheManager() {
        cacheManager.getCacheNames().forEach(cacheManager::destroyCache);
    }
}
