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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static java.lang.String.format;

/**
 * Simple {@link HttpHandler}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SimpleHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                doGet(httpExchange);
                break;
            case "POST":
                doPost(httpExchange);
                break;
            case "HEAD":
                doHead(httpExchange);
                break;
            case "PUT":
                doPut(httpExchange);
                break;
            case "DELETE":
                doDelete(httpExchange);
                break;
            case "TRACE":
                doTrace(httpExchange);
                break;
            case "OPTIONS":
                doOptions(httpExchange);
                break;

        }
    }

    protected void doGet(HttpExchange httpExchange) throws IOException {
        String content = format("[%s] Hello,World!", Thread.currentThread().getName());
        httpExchange.sendResponseHeaders(200, content.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        httpExchange.close();
    }

    protected void doPost(HttpExchange httpExchange) throws IOException {

    }

    protected void doHead(HttpExchange httpExchange) throws IOException {

    }

    protected void doPut(HttpExchange httpExchange) throws IOException {

    }

    protected void doDelete(HttpExchange httpExchange) throws IOException {

    }

    protected void doTrace(HttpExchange httpExchange) throws IOException {

    }

    protected void doOptions(HttpExchange httpExchange) throws IOException {

    }

}
