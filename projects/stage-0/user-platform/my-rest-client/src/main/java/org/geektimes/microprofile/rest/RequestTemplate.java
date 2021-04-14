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

import org.geektimes.microprofile.rest.annotation.AnnotatedParamMetadata;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableSet;

/**
 * The template of HTTP request is resolved from the
 * RestClient {@link Method}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-14
 */
public class RequestTemplate {

    /**
     * @see PathParam
     * @see QueryParam
     * @see MatrixParam
     * @see FormParam
     * @see CookieParam
     * @see HeaderParam
     */
    public static Set<Class<? extends Annotation>> SUPPORTED_PARAM_ANNOTATION_TYPES =
            unmodifiableSet(new LinkedHashSet<>(asList(
                    PathParam.class,
                    QueryParam.class,
                    MatrixParam.class,
                    FormParam.class,
                    CookieParam.class,
                    HeaderParam.class
            )));

    /**
     * The value is resolved from {@link HttpMethod @HttpMethod}
     */
    private String method;

    /**
     * The value is resolved from {@link Path @Path}
     */
    private String uriTemplate;

    /**
     * The @*Param maps to {@link AnnotatedParamMetadata}
     *
     * @see PathParam
     * @see QueryParam
     * @see MatrixParam
     * @see FormParam
     * @see CookieParam
     * @see HeaderParam
     */
    private Map<Class<? extends Annotation>, List<AnnotatedParamMetadata>> annotatedParamMetadataMap = new HashMap<>();

    /**
     * The value is resolved from {@link Consumes @Consumes}
     */
    private Set<String> consumes = new LinkedHashSet<>();

    /**
     * The value is resolved from {@link Produces @Produces}
     */
    private Set<String> produces = new LinkedHashSet<>();

    public RequestTemplate method(String method) {
        this.method = method;
        return this;
    }

    public RequestTemplate urlTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
        return this;
    }

    public RequestTemplate annotatedParamMetadata(List<AnnotatedParamMetadata> annotatedParamMetadata) {
        annotatedParamMetadata.forEach(this::annotatedParamMetadata);
        return this;
    }

    public RequestTemplate annotatedParamMetadata(AnnotatedParamMetadata... annotatedParamMetadata) {
        Arrays.stream(annotatedParamMetadata).forEach(this::annotatedParamMetadata);
        return this;
    }

    public RequestTemplate annotatedParamMetadata(AnnotatedParamMetadata annotatedParamMetadata) {
        Class<? extends Annotation> annotationType = annotatedParamMetadata.getAnnotationType();
        List<AnnotatedParamMetadata> metadataList = annotatedParamMetadataMap.computeIfAbsent(annotationType, type -> new LinkedList<>());
        metadataList.add(annotatedParamMetadata);
        return this;
    }

    public RequestTemplate consumes(String... consumes) {
        this.consumes.addAll(asList(consumes));
        return this;
    }

    public RequestTemplate produces(String... produces) {
        this.produces.addAll(asList(produces));
        return this;
    }

    public List<AnnotatedParamMetadata> getAnnotatedParamMetadata(Class<? extends Annotation> annotationType) {
        return annotatedParamMetadataMap.getOrDefault(annotationType, emptyList());
    }


    public String getMethod() {
        return method;
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    public Set<String> getConsumes() {
        return unmodifiableSet(consumes);
    }

    public Set<String> getProduces() {
        return unmodifiableSet(produces);
    }
}
