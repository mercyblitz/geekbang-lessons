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
package org.geektimes.cache.management;

import javax.cache.configuration.CompleteConfiguration;
import javax.cache.management.CacheMXBean;

import static java.util.Objects.requireNonNull;

/**
 * {@link CacheMXBean} Adapter based on
 * {@link CompleteConfiguration}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CacheMXBean
 * @see CompleteConfiguration
 * @since 1.0.0
 * Date : 2021-04-13
 */
class CacheMXBeanAdapter implements CacheMXBean {

    private final CompleteConfiguration<?, ?> configuration;

    public CacheMXBeanAdapter(CompleteConfiguration<?, ?> configuration) throws NullPointerException {
        requireNonNull(configuration, "The argument 'configuration' must not be null!");
        this.configuration = configuration;
    }

    @Override
    public String getKeyType() {
        return configuration.getKeyType().getName();
    }

    @Override
    public String getValueType() {
        return configuration.getValueType().getName();
    }

    @Override
    public boolean isReadThrough() {
        return configuration.isReadThrough();
    }

    @Override
    public boolean isWriteThrough() {
        return configuration.isWriteThrough();
    }

    @Override
    public boolean isStoreByValue() {
        return configuration.isStoreByValue();
    }

    @Override
    public boolean isStatisticsEnabled() {
        return configuration.isStatisticsEnabled();
    }

    @Override
    public boolean isManagementEnabled() {
        return configuration.isManagementEnabled();
    }
}
