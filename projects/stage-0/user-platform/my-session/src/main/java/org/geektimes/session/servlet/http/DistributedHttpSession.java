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

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

import static org.geektimes.session.servlet.http.DistributedHttpSessionAttributeListener.ATTRIBUTES_CACHE_ATTRIBUTE_NAME;

/**
 * The Distributed {@link HttpSession}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-28
 */
public class DistributedHttpSession implements HttpSession {

    private final CacheManager cacheManager;

    private final HttpSession source;

    private final SessionInfo sessionInfo;

    private final Cache<String, Object> attributesCache;

    public DistributedHttpSession(CacheManager cacheManager, HttpSession source, SessionInfo sessionInfo) {
        this.cacheManager = cacheManager;
        this.source = source;
        this.sessionInfo = resolveSessionInfo(source, sessionInfo);
        this.attributesCache = getCache();
    }

    private Cache<String, Object> getCache() {
        Cache<String, Object> cache = buildCache();
        source.setAttribute(ATTRIBUTES_CACHE_ATTRIBUTE_NAME, cache);
        return cache;
    }

    private Cache<String, Object> buildCache() {
        return null;
    }

    private SessionInfo resolveSessionInfo(HttpSession source, SessionInfo sessionInfo) {
        if (sessionInfo != null) {
            return sessionInfo;
        }
        SessionInfo newSessionInfo = new SessionInfo();
        newSessionInfo.setId(source.getId());
        newSessionInfo.setCreationTime(source.getCreationTime());
        newSessionInfo.setLastAccessedTime(source.getLastAccessedTime());
        newSessionInfo.setMaxInactiveInterval(source.getMaxInactiveInterval());
        return newSessionInfo;
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
        return source.getAttributeNames();
    }

    @Override
    @Deprecated
    public String[] getValueNames() {
        return source.getValueNames();
    }

    @Override
    public void setAttribute(String name, Object value) {
        source.setAttribute(name, value);
    }

    @Override
    @Deprecated
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        source.removeAttribute(name);
    }

    @Override
    @Deprecated
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        source.invalidate();
    }

    @Override
    public boolean isNew() {
        return source.isNew();
    }

}
