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
package org.geektimes.cache.annotation;

import javax.cache.annotation.CacheMethodDetails;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static org.geektimes.cache.annotation.util.CacheAnnotationUtils.findCacheAnnotation;
import static org.geektimes.cache.annotation.util.CacheAnnotationUtils.findCacheName;

/**
 * {@link CacheMethodDetails} based on Java Reflection.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ReflectiveCacheMethodDetails<A extends Annotation> implements CacheMethodDetails<A> {

    private final Method method;

    private final Set<Annotation> annotations;

    private A cacheAnnotation;

    private String cacheName;

    public ReflectiveCacheMethodDetails(Method method) {
        requireNonNull(method, "The 'method' argument must not be null!");
        this.method = method;
        this.annotations = getAnnotations(method.getAnnotations());
    }

    protected String resolveCacheName() {
        return findCacheName(getCacheAnnotation(), getMethod());
    }

    protected A resolveCacheAnnotation() {
        return findCacheAnnotation(getMethod());
    }

    static Set<Annotation> getAnnotations(Annotation[] annotations) {
        if (annotations == null || annotations.length < 1) {
            return emptySet();
        }
        Set<Annotation> annotationsSet = new LinkedHashSet<>();
        for (Annotation annotation : annotations) {
            annotationsSet.add(annotation);
        }
        return unmodifiableSet(annotationsSet);
    }

    @Override
    public final Method getMethod() {
        return method;
    }

    @Override
    public final Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public final A getCacheAnnotation() {
        if (cacheAnnotation == null) {
            cacheAnnotation = resolveCacheAnnotation();
        }
        return cacheAnnotation;
    }

    @Override
    public final String getCacheName() {
        if (cacheName == null) {
            cacheName = resolveCacheName();
        }
        return cacheName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReflectiveCacheMethodDetails{");
        sb.append("method=").append(getMethod());
        sb.append(", annotations=").append(getAnnotations());
        sb.append(", cacheAnnotation=").append(getCacheAnnotation());
        sb.append(", cacheName='").append(getCacheName()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
