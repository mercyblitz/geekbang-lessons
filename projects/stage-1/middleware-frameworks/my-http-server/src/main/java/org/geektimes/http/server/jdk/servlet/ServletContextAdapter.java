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

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import static org.geektimes.commons.function.ThrowableSupplier.execute;
import static org.geektimes.http.server.jdk.servlet.ListenerHandler.create;

/**
 * {@link ServletContext} Adapter based on {@link HttpContext}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ServletContextAdapter implements ServletContext {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final HttpContext httpContext;

    private final File rootDirectory;

    private final Path rootDirectoryPath;

    private final ClassLoader classLoader;

    private final Map<String, String> initParameters = new HashMap<>();

    private final List<ServletContextListener> servletContextListeners = new LinkedList<>();

    private final List<ServletContextAttributeListener> servletContextAttributeListeners = new LinkedList<>();

    private final List<ListenerHandler> listenerHandlers = new LinkedList<>();

    private final Map<String, ServletRegistrationWrapper> servletRegistrationsMap = new HashMap<>();


    public ServletContextAdapter(HttpContext httpContext, File rootDirectory, ClassLoader classLoader) {
        this.httpContext = httpContext;
        this.rootDirectory = rootDirectory;
        this.rootDirectoryPath = rootDirectory.toPath();
        this.classLoader = classLoader;
        initListenerHandlers();
    }

    private void initListenerHandlers() {
        listenerHandlers.add(create(ServletContextListener.class, servletContextListeners::add));
        listenerHandlers.add(create(ServletContextAttributeListener.class, servletContextAttributeListeners::add));
    }

    @Override
    public String getContextPath() {
        return httpContext.getPath();
    }

    @Override
    public ServletContext getContext(String uripath) {
        // TODO
        throw unsupported();
    }

    @Override
    public int getMajorVersion() {
        return 3;
    }

    @Override
    public int getMinorVersion() {
        return 1;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 3;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 1;
    }

    @Override
    public String getMimeType(String file) {
        // TODO
        throw unsupported();
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        // TODO
        throw unsupported();
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        // TODO
        throw unsupported();
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        try {
            return getResource(path).openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        // TODO
        throw unsupported();
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        // TODO
        throw unsupported();
    }

    @Deprecated
    @Override
    public Servlet getServlet(String name) throws ServletException {
        // TODO
        throw unsupported();
    }

    @Deprecated
    @Override
    public Enumeration<Servlet> getServlets() {
        // TODO
        throw unsupported();
    }

    @Deprecated
    @Override
    public Enumeration<String> getServletNames() {
        // TODO
        throw unsupported();
    }

    @Override
    public void log(String msg) {
        logger.info(msg);
    }

    @Override
    public void log(Exception exception, String msg) {
        log(msg, exception);
    }

    @Override
    public void log(String message, Throwable throwable) {
        logger.info(throwable.getMessage());
    }

    @Override
    public String getRealPath(String path) {
        Path newPath = rootDirectoryPath.resolve(path);
        return newPath.toString();
    }

    @Override
    public String getServerInfo() {
        return "Servlet Engine based on JDK HttpServer";
    }

    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return initParameters.put(name, value) == null;
    }

    @Override
    public Object getAttribute(String name) {
        Map<String, Object> attributes = httpContext.getAttributes();
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Map<String, Object> attributes = httpContext.getAttributes();
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(String name, Object object) {
        Map<String, Object> attributes = httpContext.getAttributes();
        // TODO Event Dispatching - ServletContextAttributeListener
        Object oldValue = attributes.put(name, object);
        if (oldValue == null) {
            //  TODO : Refactor
            ServletContextAttributeEvent event = new ServletContextAttributeEvent(this, name, object);
            for (ServletContextAttributeListener listener : servletContextAttributeListeners) {
                listener.attributeAdded(event);
            }
        } else {
            //  TODO : Refactor
            ServletContextAttributeEvent event = new ServletContextAttributeEvent(this, name, oldValue);
            for (ServletContextAttributeListener listener : servletContextAttributeListeners) {
                listener.attributeReplaced(event);
            }
        }

    }

    @Override
    public void removeAttribute(String name) {
        Map<String, Object> attributes = httpContext.getAttributes();
        Object oldValue = attributes.remove(name);
        //  TODO : Refactor
        if (oldValue == null) {
            return;
        }
        ServletContextAttributeEvent event = new ServletContextAttributeEvent(this, name, oldValue);
        for (ServletContextAttributeListener listener : servletContextAttributeListeners) {
            listener.attributeRemoved(event);
        }
    }

    @Override
    public String getServletContextName() {
        return "ServletContext(JDK HttpServer)";
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        Class<?> servletClass = loadClass(className);
        if (!Servlet.class.isAssignableFrom(servletClass)) { // 当前加载的 Class 是 Servlet 实现
            throw new ClassCastException("The target class does not implement javax.servlet.Servlet!");
        }
        return addServlet(servletName, (Class<? extends Servlet>) servletClass);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        ServletRegistrationWrapper wrapper = new ServletRegistrationWrapper(servletName, servlet);
        servletRegistrationsMap.put(servletName, wrapper);
        return wrapper;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return addServlet(servletName, newInstance(servletClass));
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return newInstance(clazz);
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return servletRegistrationsMap.get(servletName);
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return Collections.unmodifiableMap(servletRegistrationsMap);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return addFilter(filterName, loadClass(className));
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        // TODO
        throw unsupported();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return addFilter(filterName, newInstance(filterClass));
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) {
        return newInstance(clazz);
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        // TODO
        throw unsupported();
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        // TODO
        throw unsupported();
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        // TODO
        throw unsupported();
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        // TODO
        throw unsupported();
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        // TODO
        throw unsupported();
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        // TODO
        throw unsupported();
    }

    @Override
    public void addListener(String className) {
        Class<EventListener> listenerClass = loadClass(className);
        addListener(createListener(listenerClass));
    }

    private <T> Class<T> loadClass(String className) {
        return (Class<T>) execute(() -> classLoader.loadClass(className));
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        for (ListenerHandler listenerHandler : listenerHandlers) {
            listenerHandler.handle(t);
        }
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        EventListener eventListener = execute(() -> createListener(listenerClass));
        addListener(eventListener);
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) {
        return newInstance(clazz);
    }

    protected <T> T newInstance(Class<T> clazz) {
        return execute(() -> clazz.newInstance());
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        // TODO
        throw unsupported();
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public void declareRoles(String... roleNames) {
        // TODO
        throw unsupported();
    }

    @Override
    public String getVirtualServerName() {
        return null;
    }

    public static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("This method is not supported.");
    }
}
