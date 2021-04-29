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
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 * Date : 2021-04-29
 */
public class DistributedHttpSessionAttributeListener implements
        HttpSessionAttributeListener {

    /**
     * 当前 Key 需要排除天际到 Cache
     */
    public static final String ATTRIBUTES_CACHE_ATTRIBUTE_NAME = "_attributesCache";


    public Cache<String, Object> getAttributesCache(HttpSessionBindingEvent event) {
        HttpSession session = event.getSession();
        return (Cache<String, Object>) session.getAttribute(ATTRIBUTES_CACHE_ATTRIBUTE_NAME);
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        String name = event.getName();
        if (ATTRIBUTES_CACHE_ATTRIBUTE_NAME.equals(name)) {
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
        // 先删除缓存
        Cache<String, Object> attributesCache = getAttributesCache(event);
        //
    }
}
