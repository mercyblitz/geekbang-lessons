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
package org.geektimes.http.server.jdk;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 * Access Log
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class AccessLogFilter extends Filter {

    private final Logger logger = Logger.getLogger(getClass().getName());


    /* CLF log format */
    private static final String cLFFormat = "dd/MMM/yyyy:HH:mm:ss Z";

    // 127.0.0.1 - - [20/Mar/2021:21:09:02 +0800] "GET /manager/images/tomcat.svg HTTP/1.1" 200 68761

    /**
     * {0} : Request IP
     * {1} : Date
     * {2} : Request Method
     * {3} : Request URI
     * {4} : Protocol
     * {5} : Status Code
     * {6} : Content-Length
     */
    private static final String LOG_MESSAGE_FORMAT_PATTERN = "{0} - - [{1}] \"{2} {3} {4}\" {5} {6}";

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        chain.doFilter(exchange);
        // Handler
        SimpleDateFormat dateFormat = new SimpleDateFormat(cLFFormat);
        MessageFormat messageFormat = new MessageFormat(LOG_MESSAGE_FORMAT_PATTERN);
        String requestIP = exchange.getRemoteAddress().toString();
        String date = dateFormat.format(System.currentTimeMillis());
        String requestMethod = exchange.getRequestMethod();
        URI requestURI = exchange.getRequestURI();
        String protocol = exchange.getProtocol();
        int statusCode = exchange.getResponseCode();
        String contentLength = exchange.getResponseHeaders().getFirst("Content-length");
        String message = messageFormat.format(new Object[]{requestIP, date, requestMethod, requestURI, protocol, statusCode, contentLength});
        logger.info(message);
    }

    @Override
    public String description() {
        return "AccessLog Filter";
    }
}
