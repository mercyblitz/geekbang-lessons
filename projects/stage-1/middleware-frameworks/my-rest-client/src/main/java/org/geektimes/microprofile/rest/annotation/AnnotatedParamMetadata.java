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
package org.geektimes.microprofile.rest.annotation;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;

/**
 * The metadata of Annotated @*Param
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see PathParam
 * @see QueryParam
 * @see MatrixParam
 * @see FormParam
 * @see CookieParam
 * @see HeaderParam
 * @see DefaultValue
 * @since 1.0.0
 * Date : 2021-04-14
 */
public class AnnotatedParamMetadata {

    /**
     * The type of annotation.
     */
    private Class<? extends Annotation> annotationType;

    /**
     * The value of value() attribute method, e.g {@link PathParam#value()}.
     */
    private String paramName;

    /**
     * The value of {@link DefaultValue}, may be <code>null</code>.
     */
    private String defaultValue;

    /**
     * The index of the method parameter.
     */
    private int parameterIndex;

    public Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getParameterIndex() {
        return parameterIndex;
    }

    public void setParameterIndex(int parameterIndex) {
        this.parameterIndex = parameterIndex;
    }
}
