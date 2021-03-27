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
package org.geektimes.rest.util;

import javax.ws.rs.Path;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Path Utilities Class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public interface PathUtils {

    String SLASH = "/";

    char SLASH_CHAR = SLASH.charAt(0);

    String ENCODED_SLASH = URLUtils.encode(SLASH);

    static String resolvePath(Class<?> resourceClass, Method handleMethod) {
        String pathFromResourceClass = resolvePath(resourceClass);
        String pathFromHandleMethod = resolvePath(handleMethod);
        return pathFromResourceClass != null ? pathFromResourceClass + pathFromHandleMethod : resolvePath(handleMethod);
    }

    static String resolvePath(AnnotatedElement annotatedElement) {
        Path path = annotatedElement.getAnnotation(Path.class);
        if (path == null) {
            return null;
        }

        String value = path.value();
        if (!value.startsWith(SLASH)) {
            value = SLASH + value;
        }
        return value;
    }

    static String resolvePath(Class resource, String methodName) {
        return Stream.of(resource.getMethods())
                .filter(method -> Objects.equals(methodName, method.getName()))
                .map(PathUtils::resolvePath)
                .filter(Objects::nonNull)
                .findFirst()
                .get();
    }

    static String buildPath(String path, String... segments) {
        StringBuilder pathBuilder = new StringBuilder();

        if (path != null) {
            pathBuilder.append(path);
        }

        for (String segment : segments) {
            if (!segment.startsWith(SLASH)) {
                pathBuilder.append(SLASH);
            }
            pathBuilder.append(segment);
        }

        return pathBuilder.toString();
    }
}
