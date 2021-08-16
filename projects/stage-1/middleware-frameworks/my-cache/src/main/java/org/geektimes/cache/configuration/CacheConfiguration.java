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
package org.geektimes.cache.configuration;

import org.geektimes.commons.convert.multiple.MultiValueConverter;

import javax.cache.Cache;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import java.util.List;
import java.util.stream.Collectors;

import static org.geektimes.commons.convert.Converter.convertIfPossible;

/**
 * Java Caching Configuration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CompleteConfiguration
 * @since 1.0.0
 */
public interface CacheConfiguration extends CompleteConfiguration {

    /**
     * The property prefix for {@link Cache}
     */
    String CACHE_PROPERTY_PREFIX = "javax.cache.Cache.";

    /**
     * The property name for {@link Configuration#getKeyType()}
     */
    String CACHE_KEY_TYPE_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "key-type";

    /**
     * The property name for {@link Configuration#getValueType()}
     */
    String CACHE_VALUE_TYPE_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "value-type";

    /**
     * The property name for {@link Configuration#isStoreByValue()}
     */
    String STORE_BY_VALUE_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "store-by-value";

    /**
     * The property name for {@link CompleteConfiguration#isReadThrough()}
     */
    String READ_THROUGH_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "read-through";

    /**
     * The property name for {@link CompleteConfiguration#isWriteThrough()}
     */
    String WRITE_THROUGH_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "write-through";

    /**
     * The property name for {@link CompleteConfiguration#isStatisticsEnabled()}
     */
    String STATISTICS_ENABLED_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "statistics-enabled";

    /**
     * The property name for {@link CompleteConfiguration#isManagementEnabled()}
     */
    String MANAGEMENT_ENABLED_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "management-enabled";

    /**
     * The property name for {@link CompleteConfiguration#getCacheEntryListenerConfigurations()}
     */
    String ENTRY_LISTENER_CONFIGURATIONS_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "entry-listener-configurations";

    /**
     * The property name for {@link CompleteConfiguration#getCacheLoaderFactory()}
     */
    String CACHE_LOADER_FACTORY_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "loader.factory";

    /**
     * The property name for {@link CompleteConfiguration#getCacheWriterFactory()}
     */
    String CACHE_WRITER_FACTORY_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "writer.factory";

    /**
     * The property name for {@link CompleteConfiguration#getExpiryPolicyFactory()}
     */
    String EXPIRY_POLICY_FACTORY_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "expiry-policy.factory";

    /**
     * Get the string representing property value via the specified name
     *
     * @param propertyName the name of property
     * @return <code>null</code> if not found
     */
    String getProperty(String propertyName);

    /**
     * Get the string representing property value via the specified name
     *
     * @param propertyName the name of property
     * @param defaultValue the default value
     * @return <code>defaultValue</code> if not found
     */
    default String getProperty(String propertyName, String defaultValue) {
        String propertyValue = getProperty(propertyName);
        return propertyValue == null ? defaultValue : propertyValue;
    }

    /**
     * Get the specified-type property value via the specified name
     *
     * @param propertyName the name of property
     * @param propertyType the type of property value
     * @return <code>null</code> if not found
     */
    default <T> T getProperty(String propertyName, Class<T> propertyType) {
        String propertyValue = getProperty(propertyName);
        return convertIfPossible(propertyValue, propertyType);
    }

    /**
     * Get the specified-type property value via the specified name
     *
     * @param propertyName the name of property
     * @param propertyType the type of property value
     * @param defaultValue the default value
     * @return <code>null</code> if not found
     */
    default <T> T getProperty(String propertyName, Class<T> propertyType, T defaultValue) {
        T propertyValue = getProperty(propertyName, propertyType);
        return propertyValue == null ? defaultValue : propertyValue;
    }

    @Override
    default Class<?> getKeyType() {
        return getProperty(CACHE_KEY_TYPE_PROPERTY_NAME, Class.class, Object.class);
    }

    @Override
    default Class<?> getValueType() {
        return getProperty(CACHE_VALUE_TYPE_PROPERTY_NAME, Class.class, Object.class);
    }

    @Override
    default boolean isStoreByValue() {
        return getProperty(STORE_BY_VALUE_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default boolean isReadThrough() {
        return getProperty(READ_THROUGH_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default boolean isWriteThrough() {
        return getProperty(WRITE_THROUGH_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default boolean isStatisticsEnabled() {
        return getProperty(STATISTICS_ENABLED_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default boolean isManagementEnabled() {
        return getProperty(MANAGEMENT_ENABLED_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default Iterable<CacheEntryListenerConfiguration> getCacheEntryListenerConfigurations() {
        String propertyValue = getProperty(ENTRY_LISTENER_CONFIGURATIONS_PROPERTY_NAME);
        List<Class> configurationClasses = MultiValueConverter.convertIfPossible(propertyValue, List.class, Class.class);
        return (List<CacheEntryListenerConfiguration>) configurationClasses.stream()
                .map(this::unwrap)
                .collect(Collectors.toList());
    }

    @Override
    default Factory<CacheLoader> getCacheLoaderFactory() {
        Class<Factory<CacheLoader>> factoryClass = getProperty(CACHE_LOADER_FACTORY_PROPERTY_NAME, Class.class);
        return factoryClass == null ? null : unwrap(factoryClass);
    }

    @Override
    default Factory<CacheWriter> getCacheWriterFactory() {
        Class<Factory<CacheWriter>> factoryClass = getProperty(CACHE_WRITER_FACTORY_PROPERTY_NAME, Class.class);
        return factoryClass == null ? null : unwrap(factoryClass);
    }

    @Override
    default Factory<ExpiryPolicy> getExpiryPolicyFactory() {
        Class<Factory<ExpiryPolicy>> factoryClass = getProperty(EXPIRY_POLICY_FACTORY_PROPERTY_NAME, Class.class);
        return factoryClass == null ? null : unwrap(factoryClass);
    }

    default <T> T unwrap(java.lang.Class<T> clazz) {
        T value = null;
        try {
            value = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return value;
    }
}
