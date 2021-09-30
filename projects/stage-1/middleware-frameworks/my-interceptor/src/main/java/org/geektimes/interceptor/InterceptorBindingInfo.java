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
package org.geektimes.interceptor;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

import static org.geektimes.commons.lang.util.AnnotationUtils.getAttributesMap;
import static org.geektimes.interceptor.InterceptorBindingAttributeFilter.filters;
import static org.geektimes.interceptor.util.InterceptorUtils.isAnnotatedInterceptorBinding;

/**
 * The Metadata Info Class for {@link InterceptorBinding}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class InterceptorBindingInfo {

    private final Annotation declaredAnnotation;

    private final Class<? extends Annotation> declaredAnnotationType;

    /**
     * If <code>true</code>, the declared annotation does not annotate {@link InterceptorBinding}
     */
    private final boolean synthetic;

    private final Map<String, Object> attributes;

    public InterceptorBindingInfo(Annotation declaredAnnotation) {
        this.declaredAnnotation = declaredAnnotation;
        this.declaredAnnotationType = declaredAnnotation.annotationType();
        this.synthetic = !isAnnotatedInterceptorBinding(declaredAnnotationType);
        this.attributes = getAttributesMap(declaredAnnotation, filters());
    }

    public Class<? extends Annotation> getDeclaredAnnotationType() {
        return declaredAnnotationType;
    }

    public boolean isSynthetic() {
        return synthetic;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterceptorBindingInfo that = (InterceptorBindingInfo) o;
        return synthetic == that.synthetic
                && Objects.equals(declaredAnnotationType, that.declaredAnnotationType)
                && Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaredAnnotationType, synthetic, attributes);
    }

    public Annotation getDeclaredAnnotation() {
        return declaredAnnotation;
    }

    /**
     * New instance of {@link InterceptorBindingInfo}
     *
     * @param interceptorBinding the instance of {@linkplain InterceptorBinding interceptor binding}
     * @return non-null
     */
    public static InterceptorBindingInfo valueOf(Annotation interceptorBinding) {
        return new InterceptorBindingInfo(interceptorBinding);
    }

    public boolean equals(Annotation declaredAnnotation) {
        if (declaredAnnotation == null) {
            return false;
        }
        return this.equals(valueOf(declaredAnnotation));
    }
}
