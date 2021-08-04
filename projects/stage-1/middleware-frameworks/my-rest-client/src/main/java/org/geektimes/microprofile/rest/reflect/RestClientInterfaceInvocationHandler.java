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
import org.geektimes.microprofile.rest.uri.UriBuilderAssembler;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

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

    private final Iterable<UriBuilderAssembler> uriBuilderAssemblers;

    public RestClientInterfaceInvocationHandler(Configuration configuration, Map<Method, RequestTemplate> requestTemplates) {
        this.client = ClientBuilder.newClient(configuration);
        this.requestTemplates = requestTemplates;
        this.uriBuilderAssemblers = initUriBuilderAssemblers();
    }

    private Iterable<UriBuilderAssembler> initUriBuilderAssemblers() {
        return ServiceLoader.load(UriBuilderAssembler.class);
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

        for (UriBuilderAssembler uriBuilderAssembler : uriBuilderAssemblers) {
            uriBuilderAssembler.assemble(uriBuilder, requestTemplate, args);
        }

        String httpMethod = requestTemplate.getMethod();

        String[] acceptedResponseTypes = requestTemplate.getProduces().toArray(new String[0]);

        Class<?> returnType = method.getReturnType();

        Entity<?> entity = buildRequestEntity(requestTemplate, method, args);

        String uri = uriBuilder.build().toString();

        Invocation invocation = client.target(uri)
                .request(acceptedResponseTypes)
                .build(httpMethod, entity);

        return invocation.invoke(returnType);
    }

    private Entity<?> buildRequestEntity(RequestTemplate requestTemplate, Method method, Object[] args) {
        Annotation[][] annotationsArray = method.getParameterAnnotations();
        int parameterCount = method.getParameterCount();
        int beanParamIndex = -1;
        for (int i = 0; i < parameterCount; i++) { // Iterate parameters
            Annotation[] annotations = annotationsArray[i];
            for (Annotation annotation : annotations) {
                if (BeanParam.class.equals(annotation.annotationType())) {
                    beanParamIndex = i;
                    break;
                }
            }
        }

        if (beanParamIndex > -1) {
            MediaType mediaType = resolveMediaType(requestTemplate);
            Object arg = args[beanParamIndex];
            return Entity.entity(arg, mediaType);
        }

        return null;
    }

    private MediaType resolveMediaType(RequestTemplate requestTemplate) {
        Set<String> consumes = requestTemplate.getConsumes();
        return consumes.isEmpty() ? MediaType.APPLICATION_JSON_TYPE :
                MediaType.valueOf(consumes.iterator().next());
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
