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

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.event.CacheEntryListener;
import javax.cache.spi.CachingProvider;
import java.util.function.Supplier;

/**
 * Java Caching Configuration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface CachingConfiguration {

    /**
     * The property prefix for {@link CachingProvider}
     */
    String CACHING_PROVIDER_PROPERTY_PREFIX = "javax.cache.spi.CachingProvider.";

    /**
     * The property prefix for {@link CacheManager}
     */
    String CACHE_MANAGER_PROPERTY_PREFIX = "javax.cache.CacheManager.";

    /**
     * The property prefix for {@link Cache}
     */
    String CACHE_PROPERTY_PREFIX = "javax.cache.Cache.";

    /**
     * The property prefix for {@link CacheEntryListener}
     */
    String CACHE_ENTRY_LISTENER_PROPERTY_PREFIX = "javax.cache.event.CacheEntryListener.";

    /**
     * The property name of {@link CachingProvider#getDefaultURI()}
     */
    String CACHING_PROVIDER_DEFAULT_URI_PROPERTY_NAME = CACHING_PROVIDER_PROPERTY_PREFIX + "default-uri";

    /**
     * The prefix of property name for the mappings of {@link CacheManager}, e.g:
     * <p>
     * javax.cache.CacheManager.mappings.${uri.scheme}=com.acme.SomeSchemeCacheManager
     */
    String CACHE_MANAGER_MAPPINGS_PROPERTY_PREFIX = "javax.cache.CacheManager.mappings.";

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
     * The property name for {@link CacheEntryListenerConfiguration#getCacheEntryListenerFactory()}
     */
    String CACHE_ENTITY_LISTENER_PROPERTY_NAME = CACHE_ENTRY_LISTENER_PROPERTY_PREFIX + ".factory";

    /**
     * The property name for {@link CacheEntryListenerConfiguration#isOldValueRequired()}
     */
    String CACHE_ENTITY_LISTENER_OLD_VALUE_REQUIRED_PROPERTY_NAME = CACHE_ENTRY_LISTENER_PROPERTY_PREFIX + ".old-value";

    /**
     * The property name for {@link CacheEntryListenerConfiguration#isSynchronous()}
     */
    String CACHE_ENTITY_LISTENER_SYNCHRONOUS_PROPERTY_NAME = CACHE_ENTRY_LISTENER_PROPERTY_PREFIX + ".synchronous";

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
    default String getProperty(String propertyName, Supplier<String> defaultValue) {
        String propertyValue = getProperty(propertyName);
        return propertyValue == null ? defaultValue.get() : propertyValue;
    }

    /**
     * Get the specified-type property value via the specified name
     *
     * @param propertyName the name of property
     * @param propertyType the type of property value
     * @return <code>null</code> if not found
     */
    <T> T getProperty(String propertyName, Class<T> propertyType);

    /**
     * Get the specified-type property value via the specified name
     *
     * @param propertyName the name of property
     * @param propertyType the type of property value
     * @param defaultValue the default value as the supplier
     * @return <code>null</code> if not found
     */
    default <T> T getProperty(String propertyName, Class<T> propertyType, Supplier<T> defaultValue) {
        T propertyValue = getProperty(propertyName, propertyType);
        return propertyValue == null ? defaultValue.get() : propertyValue;
    }
}
