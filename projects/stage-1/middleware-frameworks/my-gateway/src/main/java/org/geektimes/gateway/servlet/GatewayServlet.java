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
package org.geektimes.gateway.servlet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * The Gateway based on {@link Servlet}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class GatewayServlet extends HttpServlet {

    private ServletContext servletContext;

    private ClassLoader classLoader;

    private Config config;

    private Client client;

    /**
     * Initialization
     *
     * @param servletConfig {@link ServletConfig}
     */
    public void init(ServletConfig servletConfig) {
        // case 1 : Load on startup
        // case 2 : Load on access
        // DynamicServletConfigSource.withConfigSources
        this.servletContext = servletConfig.getServletContext();
        this.classLoader = servletContext.getClassLoader();
        this.config = ConfigProvider.getConfig(classLoader);
        this.client = ClientBuilder.newClient();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Backend URL: http://${shop.site.host}:${shop.site.port}/services/public/DEFAULT/products/group/FEATURED_ITEM
        // Gateway URL: http://${gateway.host}:${gateway.port}/shop/services/public/DEFAULT/products/group/FEATURED_ITEM
        String requestURI = request.getRequestURI();
        String contextPath = servletContext.getContextPath();
        // contextPath = /contextPath
        // requestURI = /contextPath/shop/...
        String startPath = contextPath != null ? contextPath : "/";
        String appPath = StringUtils.substringBetween(requestURI, startPath, "/");
        String targetServerAddress = config.getValue(appPath, String.class);
        String forwardPath = StringUtils.substringAfter(requestURI, appPath + "/");
        String protocol = request.getProtocol();
        String targetURLPattern = "%s://%s/%s";
        String targetURL = String.format(targetURLPattern, protocol, targetServerAddress, forwardPath);

        // "Accept" Header:
        // case : REST API (application/json | application/xml | text/xml)
        // case : Static Resources handler
        // case : HTML Pages render (text/html)

        // Request URL
        WebTarget webTarget = client.target(targetURL);
        // Request Parameters
        webTarget = initParams(request, webTarget);

        Response clientResponse = webTarget.request()
                // Request Headers
                .headers(headers(request))
                // Request Method
                .build(request.getMethod())
                .invoke();

        setupResponse(clientResponse, response);

    }

    private void setupResponse(Response clientResponse, HttpServletResponse response) throws IOException {
        // status code
        response.setStatus(clientResponse.getStatus());

        // response headers
        MultivaluedMap<String, String> headers = clientResponse.getStringHeaders();
        for (Map.Entry<String, List<String>> headerEntry : headers.entrySet()) {
            for (String headerValue : headerEntry.getValue()) {
                response.addHeader(headerEntry.getKey(), headerValue);
            }
        }

        // response body
        InputStream inputStream = clientResponse.readEntity(InputStream.class);
        // Gateway Response
        OutputStream outputStream = response.getOutputStream();
        IOUtils.copy(inputStream, outputStream);
    }


    private WebTarget initParams(HttpServletRequest request, WebTarget webTarget) {
        WebTarget result = webTarget;
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            result = webTarget.queryParam(paramName, paramValues);
        }
        return result;
    }

    private MultivaluedMap<String, Object> headers(HttpServletRequest request) {
        MultivaluedMap<String, Object> headersMap = new MultivaluedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                headersMap.add(headerName, headerValue);
            }
        }
        return headersMap;
    }
}
