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

import java.lang.annotation.*;
import java.net.URL;

import static org.eclipse.microprofile.config.spi.ConfigSource.DEFAULT_ORDINAL;

/**
 * Annotation providing a convenient and declarative mechanism for adding a
 * {@link org.eclipse.microprofile.config.spi.ConfigSource}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see org.eclipse.microprofile.config.spi.ConfigSource
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigSource {

    /**
     * The name of {@link org.eclipse.microprofile.config.spi.ConfigSource}
     *
     * @see {@link org.eclipse.microprofile.config.spi.ConfigSource#getName()}
     */
    String name() default "";

    /**
     * The ordinal of {@link org.eclipse.microprofile.config.spi.ConfigSource}
     *
     * @see {@link org.eclipse.microprofile.config.spi.ConfigSource#getOrdinal()}
     */
    int ordinal() default DEFAULT_ORDINAL;

    /**
     * The source resource of configuration
     *
     * @return the string representing of {@link URL}
     */
    String resource();

    /**
     * The encoding of resource content
     *
     * @return default "UTF-8"
     */
    String encoding() default "UTF-8";

    /**
     * The factory to create {@link org.eclipse.microprofile.config.spi.ConfigSource}
     *
     * @return the factory class
     * @see DefaultConfigSourceFactory
     */
    Class<? extends ConfigSourceFactory> factory() default ConfigSourceFactory.class;
}
