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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.geektimes.commons.collection.util.CollectionUtils.asSet;

/**
 * The composite {@link InterceptorBindingInfo} instance
 * <p>
 * An interceptor class may declare multiple interceptor bindings.
 * <p>
 * Multiple interceptors may declare the same interceptor bindings.
 * <p>
 * The method or constructor has all the interceptor bindings of the interceptor.
 * A method or constructor has an interceptor binding of an interceptor
 * if it has an interceptor binding with (a) the same type and (b) the same
 * annotation member value for each member.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class InterceptorBindings implements Iterable<InterceptorBindingInfo> {

    private final Set<Annotation> declaredAnnotations;

    private final Set<InterceptorBindingInfo> bindingInfoSet;

    private final Set<Class<? extends Annotation>> interceptorBindingTypes;

    public InterceptorBindings(Collection<Annotation> interceptorBindings) {
        this(asSet(interceptorBindings));
    }

    public InterceptorBindings(Set<Annotation> interceptorBindings) {
        this.declaredAnnotations = interceptorBindings;
        this.bindingInfoSet = asInterceptorBindings(interceptorBindings);
        this.interceptorBindingTypes = asInterceptorBindingTypes(interceptorBindings);
    }

    public Set<Annotation> getDeclaredAnnotations() {
        return declaredAnnotations;
    }

    public Set<Class<? extends Annotation>> getInterceptorBindingTypes() {
        return interceptorBindingTypes;
    }

    @Override
    public Iterator<InterceptorBindingInfo> iterator() {
        return bindingInfoSet.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterceptorBindings that = (InterceptorBindings) o;
        return Objects.equals(bindingInfoSet, that.bindingInfoSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bindingInfoSet);
    }

    private Set<InterceptorBindingInfo> asInterceptorBindings(Collection<Annotation> interceptorBindings) {
        return asSet(sortedStream(interceptorBindings)
                .map(InterceptorBindingInfo::new)
                .collect(Collectors.toList()));
    }

    private Set<Class<? extends Annotation>> asInterceptorBindingTypes(Collection<Annotation> interceptorBindings) {
        return asSet(sortedStream(interceptorBindings)
                .map(Annotation::annotationType)
                .collect(Collectors.toList()));
    }

    private Stream<Annotation> sortedStream(Collection<Annotation> interceptorBindings) {
        return interceptorBindings.stream().sorted(InterceptorBindingComparator.INSTANCE);
    }
}
