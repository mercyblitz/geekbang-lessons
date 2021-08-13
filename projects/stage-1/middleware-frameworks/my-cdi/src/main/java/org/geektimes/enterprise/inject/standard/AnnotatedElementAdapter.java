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
package org.geektimes.enterprise.inject.standard;

import javax.enterprise.inject.spi.Annotated;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;

/**
 * The adapter of {@link AnnotatedElement} based on {@link Annotated}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class AnnotatedElementAdapter implements AnnotatedElement {

    private final Annotated annotated;

    public AnnotatedElementAdapter(Annotated annotated) {
        this.annotated = annotated;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return annotated.getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return getDeclaredAnnotations();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        Set<Annotation> annotations = annotated.getAnnotations();
        return annotations.toArray(new Annotation[annotations.size()]);
    }
}
