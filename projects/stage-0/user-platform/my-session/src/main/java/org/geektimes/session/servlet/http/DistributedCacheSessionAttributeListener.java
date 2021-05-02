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
package org.geektimes.session.servlet.http;

import javax.cache.Cache;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import static org.geektimes.session.servlet.http.DistributedHttpSession.ATTRIBUTE_NAME;

/**
 * The {@link HttpSessionAttributeListener} of {@link DistributedHttpSession}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-29
 */
public class DistributedCacheSessionAttributeListener implements
        HttpSessionAttributeListener {

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        String name = event.getName();
        if (ATTRIBUTE_NAME.equals(name)) {
            return;
        }
        Object value = event.getValue();
        Cache<String, Object> attributesCache = getAttributesCache(event);
        attributesCache.put(name, value);
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        String name = event.getName();
        Cache<String, Object> attributesCache = getAttributesCache(event);
        attributesCache.remove(name);
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        String name = event.getName();
        Cache<String, Object> attributesCache = getAttributesCache(event);
        // 先删除缓存
        attributesCache.remove(name);
    }

    private DistributedHttpSession getDistributedHttpSession(HttpSessionBindingEvent event) {
        return DistributedHttpSession.get(event.getSession());
    }

    private Cache<String, Object> getAttributesCache(HttpSessionBindingEvent event) {
        DistributedHttpSession session = getDistributedHttpSession(event);
        return session.getAttributesCache();
    }

}
