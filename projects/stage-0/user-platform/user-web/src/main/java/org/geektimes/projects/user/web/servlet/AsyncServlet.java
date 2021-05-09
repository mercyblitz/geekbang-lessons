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
package org.geektimes.projects.user.web.servlet;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 * Date : 2021-04-22
 */
@WebServlet(asyncSupported = true, urlPatterns = "/async.servlet")
public class AsyncServlet extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        PrintWriter writer = response.getWriter();
        // 开启异步上下文
        AsyncContext asyncContext = request.startAsync();

        writer.printf("[线程 : %s] AsyncContext 开始<p>", Thread.currentThread().getName());

        // HTTP 接受 Servlet 线程，将任务提交到异步线程
        // 当 Web Client 请求到当前 Servlet 时，开始通过当前 Servlet 执行线程操作，
        // 当 AsyncContext#start(Runnable) 方法执行后，将任务提交到运行 Runnable
        // 线程上，Servlet 执行线程快速释放。
        // Servlet 执行线程类似于 Netty 中的 Boss 线程，Runnable 执行线程相当于
        // Netty 中的 Worker 线程。
        asyncContext.start(() -> {
            writer.printf("[线程 : %s] AsyncContext Runnable 执行...", Thread.currentThread().getName());
        });

        // 主动调用 complete 方法，指示异步上下文执行结束
        asyncContext.complete();
    }
}
