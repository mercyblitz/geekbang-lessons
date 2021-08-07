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
package org.geektimes.configuration.microprofile.config.annotation;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.net.URL;

/**
 * The factory interface of {@link org.eclipse.microprofile.config.spi.ConfigSource}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface ConfigSourceFactory {

    /**
     * Create a new {@link org.eclipse.microprofile.config.spi.ConfigSource} instance
     *
     * @param name     {@link ConfigSource#getName()}
     * @param ordinal  {@link ConfigSource#getOrdinal()}
     * @param resource the {@link URL} for the content of {@link ConfigSource}
     * @param encoding the encoding of the content of resource
     * @return {@link org.eclipse.microprofile.config.spi.ConfigSource}
     */
    org.eclipse.microprofile.config.spi.ConfigSource createConfigSource(String name, int ordinal, URL resource, String encoding);
}
