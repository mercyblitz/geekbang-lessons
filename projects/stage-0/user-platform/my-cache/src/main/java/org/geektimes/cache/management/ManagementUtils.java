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

import javax.cache.Cache;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.management.CacheMXBean;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Cache JMX Utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-13
 */
public abstract class ManagementUtils {

    public static CacheMXBean createCacheMXBean(CompleteConfiguration<?, ?> configuration) {
        return new CacheMXBeanAdapter(configuration);
    }

    private static ObjectName createObjectName(Cache<?, ?> cache,
                                               String type) {
        Hashtable<String, String> props = new Hashtable<>();
        props.put("type", type);
        props.put("name", cache.getName());
        props.put("uri", getUri(cache));
//        props.putAll(getProperties(cache));
        ObjectName objectName = null;
        try {
            objectName = new ObjectName("javax.cache", props);
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(e);
        }
        return objectName;
    }

//    private static Map<String, String> getProperties(Cache<?, ?> cache) {
//        Properties properties = cache.getCacheManager().getProperties();
//        Map<String, String> map = new LinkedHashMap<>();
//        for (String propertyName : properties.stringPropertyNames()) {
//            map.put(propertyName, properties.getProperty(propertyName));
//        }
//        return map;
//    }

    private static String getUri(Cache<?, ?> cache) {
        URI uri = cache.getCacheManager().getURI();
        try {
            return URLEncoder.encode(uri.toASCIIString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void registerCacheMXBeanIfRequired(Cache<?, ?> cache) {
        CompleteConfiguration configuration = cache.getConfiguration(CompleteConfiguration.class);
        if (configuration.isManagementEnabled()) {
            ObjectName objectName = createObjectName(cache, "CacheConfiguration");
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            try {
                if (!mBeanServer.isRegistered(objectName)) {
                    mBeanServer.registerMBean(createCacheMXBean(configuration), objectName);
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
