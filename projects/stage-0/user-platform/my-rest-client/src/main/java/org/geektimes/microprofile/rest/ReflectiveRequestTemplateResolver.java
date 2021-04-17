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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.geektimes.microprofile.rest.annotation.AnnotatedParamMetadata;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Produces;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;

import static org.geektimes.microprofile.rest.RequestTemplate.SUPPORTED_PARAM_ANNOTATION_TYPES;
import static org.geektimes.rest.util.PathUtils.resolvePath;

/**
 * The reflective {@link RequestTemplateResolver} implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-14
 */
public class ReflectiveRequestTemplateResolver implements RequestTemplateResolver {

    @Override
    public RequestTemplate resolve(Class<?> resourceClass, Method resourceMethod) {
        String method = resolveHttpMethod(resourceMethod);
        if (method == null) {
            return null;
        }

        String uriTemplate = resolvePath(resourceClass, resourceMethod);

        String[] consumes = resolveConsumes(resourceClass, resourceMethod);

        String[] produces = resolveProduces(resourceClass, resourceMethod);

        List<AnnotatedParamMetadata> metadataList = resolveAnnotatedParamMetadata(resourceMethod);

        RequestTemplate requestTemplate = new RequestTemplate();

        requestTemplate.method(method)
                .urlTemplate(uriTemplate)
                .annotatedParamMetadata(metadataList)
                .consumes(consumes)
                .produces(produces);

        return requestTemplate;
    }

    private List<AnnotatedParamMetadata> resolveAnnotatedParamMetadata(Method resourceMethod) {
        List<AnnotatedParamMetadata> metadataList = new LinkedList<>();

        Parameter[] parameters = resourceMethod.getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            Annotation paramAnnotation = null;
            for (Class<? extends Annotation> annotationType : SUPPORTED_PARAM_ANNOTATION_TYPES) {
                paramAnnotation = parameter.getAnnotation(annotationType);
                if (paramAnnotation != null) {
                    break;
                }
            }
            if (paramAnnotation != null) {
                AnnotatedParamMetadata metadata = new AnnotatedParamMetadata();
                Class<? extends Annotation> annotationType = paramAnnotation.annotationType();
                String paramName = resolveParamName(paramAnnotation);
                String defaultValue = resolveDefaultValue(parameter);
                metadata.setAnnotationType(annotationType);
                metadata.setParamName(paramName);
                metadata.setDefaultValue(defaultValue);
                metadata.setParameterIndex(index);
                metadataList.add(metadata);
            }
        }
        return metadataList;
    }

    private String resolveParamName(Annotation paramAnnotation) {
        Class<? extends Annotation> annotationType = paramAnnotation.annotationType();
        String paramName = null;
        try {
            paramName = (String) MethodUtils.invokeMethod(paramAnnotation, "value", ArrayUtils.EMPTY_OBJECT_ARRAY);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return paramName;
    }

    private String resolveDefaultValue(Parameter parameter) {
        DefaultValue defaultValueAnnotation = parameter.getAnnotation(DefaultValue.class);
        return defaultValueAnnotation == null ? null : defaultValueAnnotation.value();
    }

    private String[] resolveProduces(Class<?> resourceClass, Method resourceMethod) {
        Produces produces = getProduces(resourceClass);
        if (produces == null) {
            produces = getProduces(resourceMethod);
        }
        return getNullSafeStringArray(produces == null ? null : produces.value());
    }

    private String[] resolveConsumes(Class<?> resourceClass, Method resourceMethod) {
        Consumes consumes = getConsumes(resourceClass);
        if (consumes == null) {
            consumes = getConsumes(resourceMethod);
        }
        return getNullSafeStringArray(consumes == null ? null : consumes.value());
    }

    private Consumes getConsumes(AnnotatedElement annotatedElement) {
        return annotatedElement.getAnnotation(Consumes.class);
    }

    private Produces getProduces(AnnotatedElement annotatedElement) {
        return annotatedElement.getAnnotation(Produces.class);
    }

    private String[] getNullSafeStringArray(String[] values) {
        return values == null ? ArrayUtils.EMPTY_STRING_ARRAY : values;
    }

    /**
     * Resolve the HTTP method from the given resource {@link Method}.
     *
     * @param resourceMethod the given resource {@link Method}
     * @return if not null, it indicates the "resourceMethod" is annotated by
     * some annotation annotates @HttpMethod, e.g. @GET
     */
    private String resolveHttpMethod(Method resourceMethod) {
        String httpMethod = null;
        for (Annotation annotation : resourceMethod.getAnnotations()) {
            HttpMethod httpMethodMetaAnnotation = annotation.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethodMetaAnnotation != null) {
                httpMethod = httpMethodMetaAnnotation.value();
            }
        }
        return httpMethod;
    }
}
