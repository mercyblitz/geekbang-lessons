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
package org.geektimes.session;

import java.util.Set;

/**
 * Session Repository
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface SessionRepository {

    // SessionInfo manipulation methods

    SessionRepository saveSessionInfo(SessionInfo sessionInfo);

    SessionInfo getSessionInfo(String sessionId);

    SessionRepository removeSessionInfo(String sessionId);

    // Attribute manipulation methods

    SessionRepository setAttribute(String sessionId, String name, Object value);

    SessionRepository removeAttribute(String sessionId, String name);

    Object getAttribute(String sessionId, String name);

    Set<String> getAttributeNames(String sessionId);

    // Lifecycle methods

    /**
     * Initialize
     */
    void initialize();

    /**
     * Destroy
     */
    void destroy();
}
