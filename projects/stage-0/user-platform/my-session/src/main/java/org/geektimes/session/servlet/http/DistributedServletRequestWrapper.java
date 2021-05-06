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

import org.geektimes.session.SessionRepository;

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
public class DistributedServletRequestWrapper extends HttpServletRequestWrapper {

    private final HttpServletRequest request;

    private final SessionRepository sessionRepository;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request           {@link HttpServletRequest}
     * @param sessionRepository {@link SessionRepository}
     * @throws IllegalArgumentException if the request is null
     */
    public DistributedServletRequestWrapper(HttpServletRequest request, SessionRepository sessionRepository) {
        super(request);
        this.request = request;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public HttpSession getSession(boolean create) {

        HttpSession session = super.getSession(create);

        if (session != null) {
            return new DistributedHttpSession(request, session, sessionRepository);
        } else {
            // invalidate session
            return session;
        }
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
