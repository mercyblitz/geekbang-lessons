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
package org.geektimes.session.config;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Default Session {@link ConfigSource}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-05-06
 */
public class DefaultSessionConfigSource extends MapBasedConfigSource {

    public static final String RESOURCE_NAME = "META-INF/session-defaults.properties";

    private final ClassLoader classLoader;

    public DefaultSessionConfigSource(ClassLoader classLoader) {
        super("Session Defaults", Integer.MIN_VALUE);
        this.classLoader = classLoader;
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        URL resource = classLoader.getResource(RESOURCE_NAME);
        try (InputStream inputStream = resource.openStream();
             Reader reader = new InputStreamReader(inputStream, "UTF-8")) {
            Properties properties = new Properties();
            properties.load(reader);
            configData.putAll(properties);
            properties.clear();
        }
    }
}
