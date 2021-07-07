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

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;
import java.util.*;

import static org.geektimes.http.server.jdk.servlet.ServletContextAdapter.unsupported;

/**
 * {@link ServletRegistration} Implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ServletRegistrationWrapper implements ServletRegistration.Dynamic {

    private final String servletName;

    private final Servlet servlet;

    // 唯一并且有序
    private final Set<String> urlPatterns = new LinkedHashSet<>();

    private final Map<String, String> initParameters = new HashMap<>();

    private int loadOnStartup = -1; // default value is "-1"

    private boolean asyncSupported = false; // default value is "false"

    public ServletRegistrationWrapper(String servletName, Servlet servlet) {
        this.servletName = servletName;
        this.servlet = servlet;
    }

    @Override
    public Set<String> addMapping(String... urlPatterns) {
        this.urlPatterns.addAll(Arrays.asList(urlPatterns));
        return this.urlPatterns;
    }

    @Override
    public Collection<String> getMappings() {
        return Collections.unmodifiableCollection(urlPatterns);
    }

    @Override
    public String getRunAsRole() {
        // TODO
        throw unsupported();
    }

    @Override
    public String getName() {
        return servletName;
    }

    @Override
    public String getClassName() {
        return servlet.getClass().getName();
    }

    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return initParameters.put(name, value) == null;
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        Set<String> duplicatedParamNames = new LinkedHashSet<>();
        for (Map.Entry<String, String> entry : initParameters.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (this.initParameters.containsKey(name)) {
                duplicatedParamNames.add(name);
            }
            this.initParameters.put(name, value);
        }
        return duplicatedParamNames;
    }

    @Override
    public Map<String, String> getInitParameters() {
        return Collections.unmodifiableMap(initParameters);
    }

    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        // TODO
        throw unsupported();
    }

    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        // TODO
        throw unsupported();
    }

    @Override
    public void setRunAsRole(String roleName) {
        // TODO
        throw unsupported();
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    public int getLoadOnStartup() {
        return loadOnStartup;
    }

    public String getServletName() {
        return servletName;
    }

    public Servlet getServlet() {
        return servlet;
    }
}
