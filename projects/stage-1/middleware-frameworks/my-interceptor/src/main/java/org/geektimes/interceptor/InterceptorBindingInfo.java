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
import java.lang.reflect.Method;
import java.util.Map;

import static org.geektimes.commons.lang.util.AnnotationUtils.getAttributesMap;
import static org.geektimes.interceptor.util.InterceptorUtils.isInterceptorBinding;

/**
 * The Metadata Info Class for {@link InterceptorBinding}
 *
 * <pre> {@code
 * @Inherited
 * @InterceptorBinding
 * @Target({TYPE, METHOD})
 * @Retention(RUNTIME)
 * @Monitored
 * public @interface DataAccess {}
 * }
 * </pre>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class InterceptorBindingInfo {

    private final Class<? extends Annotation> declaredAnnotationType;

    /**
     * If <code>true</code>, the declared annotation does not annotate {@link InterceptorBinding}
     */
    private final boolean synthetic;

    private final Map<String, Object> attributes;

    public InterceptorBindingInfo(Annotation declaredAnnotation) {
        this.declaredAnnotationType = declaredAnnotation.annotationType();
        this.synthetic = !isInterceptorBinding(declaredAnnotationType);
        this.attributes = getAttributesMap(declaredAnnotation, this::isNonBindingAttribute);
    }

    private boolean isNonBindingAttribute(Method method) {
        return false;
    }


}
