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

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.geektimes.http.server.WebServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import static java.lang.Integer.getInteger;
import static java.lang.String.format;
import static java.lang.System.getProperty;

/**
 * Simple Http Server
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SimpleWebServer implements WebServer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final int port;

    private final String path;

    private HttpServer httpServer;

    private ExecutorService executorService;

    private Future future;

    public SimpleWebServer() {
        this(getInteger("simple.http.server.port", 8080));
    }

    public SimpleWebServer(int port) {
        this(getProperty("simple.http.server.path", "/"), port);
    }

    public SimpleWebServer(String path, int port) {
        this.path = path;
        this.port = port;
    }

    public SimpleWebServer start(boolean sync) throws IOException {

        httpServer = HttpServer.create(new InetSocketAddress(port), 0);

        HttpContext context = httpServer.createContext(path, new SimpleHttpHandler());

        initFilters(context);

        executorService = ForkJoinPool.commonPool();

        httpServer.setExecutor(executorService);

        if (sync) {
            startHttpServer();
        } else {
            future = executorService.submit(this::startHttpServer);
        }

        return this;
    }

    private void initFilters(HttpContext context) {
        context.getFilters().add(new AccessLogFilter());
    }

    private void startHttpServer() {
        logger.info(format("Simple HTTP Server(port : %d, path : %s) is starting...%n", port, path));
        httpServer.start();
    }

    public SimpleWebServer stop() {
        logger.info(format("Simple HTTP Server(port : %d, path : %s) is stopping...%n", port, path));
        if (future != null && !future.isDone()) {
            future.cancel(true);
            executorService.shutdown();
        }

        httpServer.stop(0);
        logger.info(format("Simple HTTP Server(port : %d, path : %s) is stopped...%n", port, path));
        return this;
    }

}
