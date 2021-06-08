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
package org.geektimes.microprofile.rest;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.geektimes.microprofile.rest.reflect.RestClientInterfaceInvocationHandler;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Configuration;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.KeyStore;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * Default {@link RestClientBuilder} implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-14
 */
public class DefaultRestClientBuilder implements RestClientBuilder {

    private final ClassLoader classLoader;

    private final RequestTemplateResolver requestTemplateResolver;

    private URL baseUrl;

    private long connectTimeoutInMillis;

    private long readTimeoutInMillis;

    private ExecutorService executor;

    private SSLContext sslContext;

    private KeyStore trustStore;

    private KeyStore keyStore;

    private String keystorePassword;

    private HostnameVerifier hostnameVerifier;

    private String proxyHost;

    private int proxyPort;

    private QueryParamStyle queryParamStyle;

    public DefaultRestClientBuilder() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public DefaultRestClientBuilder(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.requestTemplateResolver = new ReflectiveRequestTemplateResolver();
    }

    @Override
    public RestClientBuilder baseUrl(URL url) {
        this.baseUrl = url;
        return this;
    }

    @Override
    public RestClientBuilder connectTimeout(long timeout, TimeUnit timeUnit) {
        this.connectTimeoutInMillis = timeUnit.toMillis(timeout);
        return this;
    }

    @Override
    public RestClientBuilder readTimeout(long timeout, TimeUnit timeUnit) {
        this.readTimeoutInMillis = timeUnit.toMillis(timeout);
        return this;
    }

    @Override
    public RestClientBuilder executorService(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public RestClientBuilder sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    @Override
    public RestClientBuilder trustStore(KeyStore trustStore) {
        this.trustStore = trustStore;
        return this;
    }

    @Override
    public RestClientBuilder keyStore(KeyStore keyStore, String keystorePassword) {
        this.keyStore = keyStore;
        this.keystorePassword = keystorePassword;
        return this;
    }

    @Override
    public RestClientBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public RestClientBuilder followRedirects(boolean follow) {
        throw new UnsupportedOperationException("To Support in the future.");
    }

    @Override
    public RestClientBuilder proxyAddress(String proxyHost, int proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        return this;
    }

    @Override
    public RestClientBuilder queryParamStyle(QueryParamStyle style) {
        this.queryParamStyle = style;
        return this;
    }

    @Override
    public <T> T build(Class<T> clazz) throws IllegalStateException, RestClientDefinitionException {
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("The 'clazz' argument must be a Java interface.");
        }

        Map<Method, RequestTemplate> requestTemplates = resolveRequestTemplates(clazz);

        return (T) newProxyInstance(classLoader, new Class[]{clazz},
                new RestClientInterfaceInvocationHandler(getConfiguration(), requestTemplates));
    }

    private <T> Map<Method, RequestTemplate> resolveRequestTemplates(Class<T> resourceClass) {
        Map<Method, RequestTemplate> requestTemplates = new LinkedHashMap<>();
        for (Method resourceMethod : resourceClass.getMethods()) {
            RequestTemplate requestTemplate = requestTemplateResolver.resolve(resourceClass, resourceMethod);
            if (requestTemplate != null) {
                requestTemplate.urlTemplate(baseUrl.toString() + requestTemplate.getUriTemplate());
                requestTemplates.put(resourceMethod, requestTemplate);
            }
        }
        return requestTemplates;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public RestClientBuilder property(String name, Object value) {
        return null;
    }

    @Override
    public RestClientBuilder register(Class<?> componentClass) {
        return null;
    }

    @Override
    public RestClientBuilder register(Class<?> componentClass, int priority) {
        return null;
    }

    @Override
    public RestClientBuilder register(Class<?> componentClass, Class<?>... contracts) {
        return null;
    }

    @Override
    public RestClientBuilder register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
        return null;
    }

    @Override
    public RestClientBuilder register(Object component) {
        return null;
    }

    @Override
    public RestClientBuilder register(Object component, int priority) {
        return null;
    }

    @Override
    public RestClientBuilder register(Object component, Class<?>... contracts) {
        return null;
    }

    @Override
    public RestClientBuilder register(Object component, Map<Class<?>, Integer> contracts) {
        return null;
    }
}
