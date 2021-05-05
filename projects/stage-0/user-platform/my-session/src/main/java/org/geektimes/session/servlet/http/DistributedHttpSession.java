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
package org.geektimes.session.servlet.http;

import org.geektimes.session.SessionInfo;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.enumeration;

/**
 * The Distributed {@link HttpSession}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-28
 */
public class DistributedHttpSession implements HttpSession {

    /**
     * The attribute name of {@link DistributedHttpSession} instance
     */
    public static final String ATTRIBUTE_NAME = "_distributedHttpSession";

    private final HttpServletRequest request;

    private final CacheManager cacheManager;

    private final HttpSession source;

    private final Cache<String, SessionInfo> sessionInfoCache;

    private final Cache<String, Object> attributesCache;

    private final SessionInfo sessionInfo;


    public DistributedHttpSession(HttpServletRequest request, HttpSession source, CacheManager cacheManager) {
        this.request = request;
        this.source = source;
        this.cacheManager = cacheManager;
        this.sessionInfoCache = resolveSessionInfoCache();
        this.attributesCache = resolveAttributesCache();
        this.sessionInfo = resolveSessionInfo();
        // set self into Request Context
        request.setAttribute(ATTRIBUTE_NAME, this);

    }

    public static DistributedHttpSession get(HttpServletRequest request) {
        return (DistributedHttpSession) request.getAttribute(ATTRIBUTE_NAME);
    }

//    public HttpServletRequest getRequest() {
//        return request;
//    }
//
//    public CacheManager getCacheManager() {
//        return cacheManager;
//    }
//
//    public HttpSession getSource() {
//        return source;
//    }
//
//    public SessionInfo getSessionInfo() {
//        return sessionInfo;
//    }

    private SessionInfo resolveSessionInfo() {
        String requestSessionId = request.getRequestedSessionId();
        SessionInfo sessionInfo = null;
        if (requestSessionId != null) { // the "requestSessionId" that the server generated was stored by HTTP client
            // Try to get the SessionInfo from cache,
            // If the "sessionInfo" is present , it indicates that the session was created by another server
            // in the distributed cluster, or the session had been expired in the server
            sessionInfo = getSessionInfo(requestSessionId);
        }
        if (sessionInfo == null) { // Maybe the first time access to the server when the "requestSessionId" is absent
            sessionInfo = new SessionInfo(source);
        }
        return sessionInfo;
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

    private Cache<String, Object> resolveAttributesCache() {
        String sessionId = getId();
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

//    /**
//     * @return The requestedSessionId matches current session id and session is exited.
//     */
//    private boolean isReentrantSession() {
//        return request.isRequestedSessionIdValid();
//    }
//
//    /**
//     * Is first time access
//     *
//     * @return
//     */
//    private boolean isNewSession() {
//        return source.isNew();
//    }

    /**
     * Commit the {@link SessionInfo} into cache
     */
    public void commitSessionInfo() {
        saveSessionInfo(sessionInfo);
    }

    public void saveSessionInfo(SessionInfo sessionInfo) {
        Cache<String, SessionInfo> sessionInfoCache = getSessionInfoCache();
        sessionInfo.setLastAccessedTime(System.currentTimeMillis());
        sessionInfoCache.put(sessionInfo.getId(), sessionInfo);
    }

    public Cache<String, Object> getAttributesCache() {
        return attributesCache;
    }

    public Cache<String, SessionInfo> getSessionInfoCache() {
        return sessionInfoCache;
    }

    /**
     * Get the {@link SessionInfo} from cache.
     *
     * @param sessionId session id
     * @return if not null, it indicates that current requested associating distributed session is present
     * in the cache, or current new session is a new one absolutely
     */
    public SessionInfo getSessionInfo(String sessionId) {
        Cache<String, SessionInfo> sessionInfoCache = getSessionInfoCache();
        return sessionInfoCache.get(sessionId);
    }

    private ExpiryPolicy createExpiryPolicy() {
        return new ExpiryPolicy() {

            @Override
            public Duration getExpiryForCreation() {
                return getDuration();
            }

            @Override
            public Duration getExpiryForAccess() {
                return getDuration();
            }

            @Override
            public Duration getExpiryForUpdate() {
                return getDuration();
            }

            private Duration getDuration() {
                return new Duration(TimeUnit.SECONDS, getMaxInactiveInterval());
            }
        };
    }

    @Override
    public long getCreationTime() {
        return sessionInfo.getCreationTime();
    }

    @Override
    public String getId() {
        return sessionInfo.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return sessionInfo.getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return source.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        sessionInfo.setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return sessionInfo.getMaxInactiveInterval();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return source.getSessionContext();
    }

    @Override
    public Object getAttribute(String name) {
        // try to find the value in local session
        Object value = source.getAttribute(name);
        if (value == null) { // If not found, try to find it in the cache
            value = attributesCache.get(name);
            // restore the cached value into local session if found
            if (value != null) {
                source.setAttribute(name, value);
            }
        }
        return value;
    }

    @Override
    @Deprecated
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        List<String> attributeNames = new LinkedList<>();
        for (Cache.Entry<String, Object> entry : attributesCache) {
            attributeNames.add(entry.getKey());
        }
        return enumeration(attributeNames);
    }

    @Override
    @Deprecated
    public String[] getValueNames() {
        return source.getValueNames();
    }

    @Override
    public void setAttribute(String name, Object value) {
        source.setAttribute(name, value);
        attributesCache.put(name, value);
    }

    @Override
    @Deprecated
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        source.removeAttribute(name);
        attributesCache.remove(name);
    }

    @Override
    @Deprecated
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        source.invalidate();
        invalidateSessionInfoCache();
        invalidateAttributesCache();
    }

    private void invalidateSessionInfoCache() {
        getSessionInfoCache().remove(getId());
    }

    private void invalidateAttributesCache() {

    }

    @Override
    public boolean isNew() {
        return source.isNew();
    }

}
