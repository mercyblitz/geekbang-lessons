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

import org.junit.Before;
import org.junit.Test;

import java.net.URL;

/**
 * 1.0.0
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@ConfigSource(ordinal = 200, resource = "classpath:/META-INF/default.properties")
public class ConfigSourceTest {

    @Before
    public void initConfigSourceFactory() throws Throwable {
        ConfigSource configSource = getClass().getAnnotation(ConfigSource.class);
        String name = configSource.name();
        int ordinal = configSource.ordinal();
        String encoding = configSource.encoding();
        String resource = configSource.resource();
        URL resourceURL = new URL(resource);
        Class<? extends ConfigSourceFactory> configSourceFactoryClass = configSource.factory();
        if (ConfigSourceFactory.class.equals(configSourceFactoryClass)) {
            configSourceFactoryClass = DefaultConfigSourceFactory.class;
        }

        ConfigSourceFactory configSourceFactory = configSourceFactoryClass.newInstance();
        org.eclipse.microprofile.config.spi.ConfigSource source =
                configSourceFactory.createConfigSource(name, ordinal, resourceURL, encoding);
        System.out.println(source.getProperties());
    }

    @Test
    public void test() {

    }
}
