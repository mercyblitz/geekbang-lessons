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
package org.geektimes.http.server.jdk.servlet;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

/**
 * Simple Servlet {@link HttpHandler}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SimpleServletHttpHandler implements HttpHandler {

    private ServletContext servletContext;

    private URLPatternsMatcher urlPatternsMatcher = new SimpleURLPatternsMatcher();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Request URI Mapping -> Servlet
        URI requestURI = exchange.getRequestURI();

        // ServletContext contains all ServletRegistration
        Map<String, ? extends ServletRegistration> servletRegistrations = servletContext.getServletRegistrations();

        for (ServletRegistration servletRegistration : servletRegistrations.values()) {
            Collection<String> urlPatterns = servletRegistration.getMappings();
            if (urlPatternsMatcher.matches(urlPatterns, requestURI.toString())) {
                // Create HttpServletRequest and HttpServletResponse
                HttpServletRequest request = new HttpServletRequestAdapter(exchange, servletContext);
                HttpServletResponse response = new HttpServletResponseAdapter(exchange, servletContext);
                // invoke Servlet#service(HttpServletRequest,HttpServletResponse)
                ServletRegistrationWrapper wrapper = (ServletRegistrationWrapper) servletRegistration;
                Servlet servlet = wrapper.getServlet();
                try {
                    servlet.service(request, response);
                } catch (ServletException e) {
                    throw new IOException(e.getCause());
                }
            }
        }
        exchange.getResponseBody().flush();
        exchange.close();
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
