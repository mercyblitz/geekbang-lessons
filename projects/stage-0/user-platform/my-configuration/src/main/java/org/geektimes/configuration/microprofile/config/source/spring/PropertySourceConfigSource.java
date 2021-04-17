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
package org.geektimes.configuration.microprofile.config.source.spring;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.springframework.core.Ordered;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@link ConfigSource} Adapter implementation based on Spring's {@link EnumerablePropertySource}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-17
 */
public class PropertySourceConfigSource implements ConfigSource {

    private final EnumerablePropertySource propertySource;

    private int ordinal;

    public PropertySourceConfigSource(EnumerablePropertySource propertySource) {
        this.propertySource = propertySource;
        if (propertySource instanceof Ordered) {
            this.setOrdinal(((Ordered) propertySource).getOrder());
        } else {
            this.setOrdinal(ConfigSource.super.getOrdinal());
        }
    }

    @Override
    public Map<String, String> getProperties() {
        return ConfigSource.super.getProperties();
    }

    @Override
    public Set<String> getPropertyNames() {
        String[] propertyNames = propertySource.getPropertyNames();
        Set<String> propertyNamesSet = new LinkedHashSet<>();
        for (String propertyName : propertyNames) {
            propertyNamesSet.add(propertyName);
        }
        return Collections.unmodifiableSet(propertyNamesSet);
    }

    @Override
    public int getOrdinal() {
        return this.ordinal;
    }

    @Override
    public String getValue(String propertyName) {
        Object propertyValue = propertySource.getProperty(propertyName);
        return propertyValue instanceof String ? String.valueOf(propertyValue) : null;
    }

    @Override
    public String getName() {
        return propertySource.getName();
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }
}
