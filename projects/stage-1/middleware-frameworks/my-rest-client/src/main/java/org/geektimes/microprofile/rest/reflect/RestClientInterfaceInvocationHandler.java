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
package org.geektimes.microprofile.rest.reflect;

import org.geektimes.microprofile.rest.RequestTemplate;
import org.geektimes.microprofile.rest.annotation.AnnotatedParamMetadata;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

/**
 * RestClient Interface Proxy {@link InvocationHandler}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-14
 */
public class RestClientInterfaceInvocationHandler implements InvocationHandler {

    private final Client client;

    private final Map<Method, RequestTemplate> requestTemplates;

    public RestClientInterfaceInvocationHandler(Configuration configuration, Map<Method, RequestTemplate> requestTemplates) {
        this.client = ClientBuilder.newClient(configuration);
        this.requestTemplates = requestTemplates;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // HTTP request Around -> 显示的 Invoke -> Invocation.invoke
        // Timeout Around ->
        // Priority 优先级

        RequestTemplate requestTemplate = requestTemplates.get(method);

        if (requestTemplate == null) {
            throw new NullPointerException();
        }

        String uriTemplate = requestTemplate.getUriTemplate();

        UriBuilder uriBuilder = UriBuilder.fromUri(uriTemplate);

        // Handle @PathParam @DefaultValue
        for (AnnotatedParamMetadata metadata : requestTemplate.getAnnotatedParamMetadata(PathParam.class)) {
            String paramName = metadata.getParamName();
            int paramIndex = metadata.getParameterIndex();
            Object paramValue = args[paramIndex];
            if (paramValue == null) { // Handle @DefaultValue
                paramValue = metadata.getDefaultValue();
            }
            uriBuilder.resolveTemplate(paramName, paramValue);
        }

        // Handle @QueryParam
        for (AnnotatedParamMetadata metadata : requestTemplate.getAnnotatedParamMetadata(QueryParam.class)) {
            String paramName = metadata.getParamName();
            int paramIndex = metadata.getParameterIndex();
            Object paramValue = args[paramIndex];
            uriBuilder.queryParam(paramName, paramValue);
        }

        // Handle @QueryParam
        for (AnnotatedParamMetadata metadata : requestTemplate.getAnnotatedParamMetadata(MatrixParam.class)) {
            String paramName = metadata.getParamName();
            int paramIndex = metadata.getParameterIndex();
            Object paramValue = args[paramIndex];
            uriBuilder.matrixParam(paramName, paramValue);
        }

        String httpMethod = requestTemplate.getMethod();

        String[] acceptedResponseTypes = requestTemplate.getProduces().toArray(new String[0]);

        Class<?> returnType = method.getReturnType();

        Entity<?> entity = buildEntity(method, args);

        String uri = uriBuilder.build().toString();

        Invocation invocation = client.target(uri)
                .request(acceptedResponseTypes)
                .build(httpMethod, entity);

        return invocation.invoke(returnType);
    }

    private Entity<?> buildEntity(Method method, Object[] args) {
        return null;
    }

    private String[] getAcceptedResponseTypes(Method method) {
        return new String[0];
    }

    private String getHttpMethod(Method method) {
        return null;
    }

    private URI buildURI(Method method) {
        return null;
    }
}
