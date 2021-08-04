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

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.geektimes.http.server.WebServer;
import org.geektimes.http.server.jdk.servlet.demo.HelloWorldServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.io.File;
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
 * Simple {@link Servlet} Http Server
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SimpleServletWebServer implements WebServer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final int port;

    private final String contextPath;

    private final File rootDirectory;

    private HttpServer httpServer;

    private ExecutorService executorService;

    private Future future;

    public SimpleServletWebServer() {
        this(getInteger("simple.http.server.port", 8080));
    }

    public SimpleServletWebServer(int port) {
        this(getProperty("simple.http.server.path", "/"), port);
    }

    public SimpleServletWebServer(String path, int port) {
        this.contextPath = path;
        this.port = port;
        this.rootDirectory = initRootDirectory();
    }

    private File initRootDirectory() {
        File rootDirectory = new File(System.getProperty("user.dir"), ".servlet-web-server");
        rootDirectory.mkdirs();
        return rootDirectory;
    }

    public SimpleServletWebServer start(boolean sync) throws IOException {

        httpServer = HttpServer.create(new InetSocketAddress(port), 0);

        SimpleServletHttpHandler httpHandler = new SimpleServletHttpHandler();

        HttpContext httpContext = httpServer.createContext(contextPath, httpHandler);

        ServletContext servletContext = adaptServletContext(httpContext);

        httpHandler.setServletContext(servletContext);

        initFilters(servletContext);

        initServlets(servletContext);

        executorService = ForkJoinPool.commonPool();

        httpServer.setExecutor(executorService);

        if (sync) {
            startHttpServer();
        } else {
            future = executorService.submit(this::startHttpServer);
        }

        return this;
    }

    private void initServlets(ServletContext servletContext) {
        // TODO : Refactor - replace hard-code
        initHelloWorldServlet(servletContext);
    }

    private void initHelloWorldServlet(ServletContext servletContext) {
        ServletRegistration.Dynamic dynamic = servletContext.addServlet("helloWorld", HelloWorldServlet.class);
        dynamic.addMapping("/hello-world");
        dynamic.addMapping("/helloworld");
        dynamic.addMapping("/hello/world");
        dynamic.setLoadOnStartup(1);
    }

    private void initFilters(ServletContext servletContext) {
        // TODO
    }

    private ServletContext adaptServletContext(HttpContext httpContext) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return new ServletContextAdapter(httpContext, rootDirectory, classLoader);
    }

    private void startHttpServer() {
        logger.info(format("Simple HTTP Server(port : %d, path : %s) is starting...%n", port, contextPath));
        httpServer.start();
    }

    public SimpleServletWebServer stop() {
        logger.info(format("Simple HTTP Server(port : %d, path : %s) is stopping...%n", port, contextPath));
        if (future != null && !future.isDone()) {
            future.cancel(true);
            executorService.shutdown();
        }

        httpServer.stop(0);
        logger.info(format("Simple HTTP Server(port : %d, path : %s) is stopped...%n", port, contextPath));
        return this;
    }

}
