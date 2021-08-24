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
package org.geektimes.enterprise.inject.standard.beans;

import java.util.Objects;

import static java.lang.String.format;
import static org.geektimes.commons.lang.util.StringUtils.isBlank;

/**
 * The Enumeration of "bean-discovery-mode" from "META-INF/beans.xml"
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public enum BeanDiscoveryMode {

    /**
     * All types in this archive will be considered.
     * A bean archive which contains a beans.xml file with no version has a default bean discovery mode of all.
     */
    ALL("all"),

    /**
     * Only those types with bean defining annotations will be considered.
     * A bean archive which contains a beans.xml file with version 1.1 (or later) must specify the bean-discovery-mode
     * attribute. The default value for the attribute is annotated.
     */
    ANNOTATED("annotated"),

    /**
     * This archive will be ignored.
     */
    NONE("none");

    private final String value;

    BeanDiscoveryMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Resolve the {@link BeanDiscoveryMode} from the specified value
     *
     * @param value the {@link #value} of {@link BeanDiscoveryMode}
     * @return One element of {@link BeanDiscoveryMode} if found
     * @throw IllegalArgumentException If value can't be resolved to an element of {@link BeanDiscoveryMode}
     */
    public static BeanDiscoveryMode of(String value) throws IllegalArgumentException {
        if (isBlank(value)) {
            return ANNOTATED;
        }
        BeanDiscoveryMode result = null;
        for (BeanDiscoveryMode element : values()) {
            if (Objects.equals(value, element.getValue())) {
                result = element;
                break;
            }
        }
        if (result == null) {
            String message = format("The 'value' argument[%s] can't resolved to an element of %s!",
                    value, BeanDiscoveryMode.class.getName());
            throw new IllegalArgumentException(message);
        }
        return result;
    }
}
