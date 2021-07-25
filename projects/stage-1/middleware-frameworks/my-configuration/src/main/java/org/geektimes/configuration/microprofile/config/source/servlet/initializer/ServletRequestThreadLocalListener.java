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
package org.geektimes.configuration.microprofile.config.source.servlet.initializer;


import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * The {@link ServletRequestListener} implementation for the {@link ThreadLocal} of {@link ServletRequest}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ServletRequestThreadLocalListener implements ServletRequestListener {

    private static ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        ServletRequest request = servletRequestEvent.getServletRequest();
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        requestThreadLocal.set(httpServletRequest);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        requestThreadLocal.remove();
    }

    public static HttpServletRequest getRequest() {
        return requestThreadLocal.get();
    }

}
