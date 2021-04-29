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
import javax.cache.configuration.MutableConfiguration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * The distributed {@link HttpServletRequest} implementation based on {@link HttpServletRequestWrapper}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-28
 */
public class DistributedServletRequest extends HttpServletRequestWrapper {

    private final HttpServletRequest request;

    private final CacheManager cacheManager;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request HttpServletRequest
     * @throws IllegalArgumentException if the request is null
     */
    public DistributedServletRequest(HttpServletRequest request, CacheManager cacheManager) {
        super(request);
        this.request = request;
        this.cacheManager = cacheManager;
    }

    @Override
    public HttpSession getSession(boolean create) {
        // Get Session ID from request
        String requestedSessionId = request.getRequestedSessionId();

        HttpSession session = super.getSession(create);

        if (session != null) {
            SessionInfo sessionInfo = getSessionInfoFromCache(requestedSessionId);
            return new DistributedHttpSession(cacheManager, session, sessionInfo);
        } else {
            // invalidate session
            return null;
        }
    }

    /**
     * Get the {@link SessionInfo} from cache.
     *
     * @param sessionId session id
     * @return if not null, it indicates that current requested associating distributed session is present
     * in the cache, or current new session is a new one absolutely
     */
    private SessionInfo getSessionInfoFromCache(String sessionId) {
        Cache<String, SessionInfo> sessionInfoCache = getSessionInfoCache();
        return sessionInfoCache.get(sessionId);
    }

    private Cache<String, SessionInfo> getSessionInfoCache() {
        String cacheName = "SessionInfoCache";
        Cache<String, SessionInfo> cache = cacheManager.getCache(cacheName, String.class, SessionInfo.class);
        if (cache == null) {
            MutableConfiguration<String, SessionInfo> configuration = new MutableConfiguration<>();
            configuration.setTypes(String.class, SessionInfo.class);
            // TODO ExpiryPolicy
            // configuration.setExpiryPolicyFactory();
            cache = cacheManager.createCache(cacheName, configuration);
        }
        return cache;
    }

    private Cache<String, Object> buildCache(HttpServletRequest request, HttpSession session) {
        return null;
    }

    /**
     * The default behavior of this method is to return getSession()
     * on the wrapped request object.
     */
    @Override
    public HttpSession getSession() {
        HttpSession session = getSession(false);
        if (session == null) {
            session = getSession(true);
        }
        return session;
    }

}
