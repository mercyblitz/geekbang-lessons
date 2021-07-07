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

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import static org.geektimes.commons.function.ThrowableAction.execute;
import static org.geektimes.http.server.jdk.servlet.ServletContextAdapter.unsupported;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class HttpServletResponseAdapter implements HttpServletResponse {

    private final HttpExchange httpExchange;

    private final ServletContext servletContext;

    private boolean usedStream = false;

    private boolean usedWriter = false;

    private int statusCode = 200;

    private String statusMessage;

    private long contentLength;

    public HttpServletResponseAdapter(HttpExchange httpExchange,
                                      ServletContext servletContext) {
        this.httpExchange = httpExchange;
        this.servletContext = servletContext;
    }

    @Override
    public void addCookie(Cookie cookie) {
        // TODO
        throw unsupported();
    }

    @Override
    public boolean containsHeader(String name) {
        // TODO
        throw unsupported();
    }

    @Override
    public String encodeURL(String url) {
        // TODO
        throw unsupported();
    }

    @Override
    public String encodeRedirectURL(String url) {
        // TODO
        throw unsupported();
    }

    @Override
    public String encodeUrl(String url) {
        // TODO
        throw unsupported();
    }

    @Override
    public String encodeRedirectUrl(String url) {
        // TODO
        throw unsupported();
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        // TODO
        throw unsupported();
    }

    @Override
    public void sendError(int sc) throws IOException {
        // TODO
        throw unsupported();
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        // TODO
        throw unsupported();
    }

    @Override
    public void setDateHeader(String name, long date) {
        // TODO
        throw unsupported();
    }

    @Override
    public void addDateHeader(String name, long date) {
        // TODO
        throw unsupported();
    }

    @Override
    public void setHeader(String name, String value) {
        // TODO
        throw unsupported();
    }

    @Override
    public void addHeader(String name, String value) {
        // TODO
        throw unsupported();
    }

    @Override
    public void setIntHeader(String name, int value) {
        // TODO
        throw unsupported();
    }

    @Override
    public void addIntHeader(String name, int value) {
        // TODO
        throw unsupported();
    }

    @Override
    public void setStatus(int sc) {
        this.statusCode = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.setStatus(sc);
        this.statusMessage = sm;
    }

    @Override
    public int getStatus() {
        return statusCode;
    }

    @Override
    public String getHeader(String name) {
        // TODO
        throw unsupported();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        // TODO
        throw unsupported();
    }

    @Override
    public Collection<String> getHeaderNames() {
        // TODO
        throw unsupported();
    }

    @Override
    public String getCharacterEncoding() {
        // TODO : refactor
        return "UTF-8";
    }

    @Override
    public String getContentType() {
        // TODO
        throw unsupported();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (usedWriter) {
            throw new IOException("getWriter() was invoked!");
        }
        usedStream = true;
        return new ServletOutputStreamWrapper(httpExchange);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (usedStream) {
            throw new IOException("getOutputStream() was invoked!");
        }
        usedWriter = true;
        return new PrintWriter(new OutputStreamWriter(httpExchange.getResponseBody(), getCharacterEncoding()));
    }

    @Override
    public void setCharacterEncoding(String charset) {
        // TODO
        throw unsupported();
    }

    @Override
    public void setContentLength(int len) {
        setContentLengthLong(len);
    }

    @Override
    public void setContentLengthLong(long len) {
        execute(() -> this.httpExchange.sendResponseHeaders(getStatus(), len));
    }

    @Override
    public void setContentType(String type) {
        // TODO
        throw unsupported();
    }

    @Override
    public void setBufferSize(int size) {
        // TODO
        throw unsupported();
    }

    @Override
    public int getBufferSize() {
        // TODO
        throw unsupported();
    }

    @Override
    public void flushBuffer() throws IOException {
        // TODO
        throw unsupported();
    }

    @Override
    public void resetBuffer() {
        // TODO
        throw unsupported();
    }

    @Override
    public boolean isCommitted() {
        // TODO
        throw unsupported();
    }

    @Override
    public void reset() {
        // TODO
        throw unsupported();
    }

    @Override
    public void setLocale(Locale loc) {
        // TODO
        throw unsupported();
    }

    @Override
    public Locale getLocale() {
        // TODO
        throw unsupported();
    }
}
