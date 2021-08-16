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
package org.geektimes.enterprise.inject.util;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.geektimes.commons.lang.util.AnnotationUtils.getAttributeValues;

/**
 * The utilities class for {@link Annotation}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class Annotations {

    public static int hashCode(Annotation annotation) {
        Object[] values = getAttributeValues(annotation, Annotations::isNonbindingAttribute);
        return Arrays.hashCode(values);
    }

    public static boolean equals(Annotation one, Annotation another) {
        Object[] oneValues = getAttributeValues(one, Annotations::isNonbindingAttribute);
        Object[] anotherValues = getAttributeValues(another, Annotations::isNonbindingAttribute);
        return Arrays.deepEquals(oneValues, anotherValues);
    }

    public static boolean isNonbindingAttribute(Method method) {
        return method.isAnnotationPresent(Nonbinding.class);
    }
}
