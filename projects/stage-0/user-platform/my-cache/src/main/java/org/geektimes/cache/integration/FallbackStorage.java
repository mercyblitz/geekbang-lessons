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
package org.geektimes.cache.integration;

import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import java.util.Comparator;

/**
 * Fallback Storage that only extends {@link CacheLoader} and {@link CacheWriter}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
public interface FallbackStorage<K, V> extends CacheLoader<K, V>, CacheWriter<K, V> {

    Comparator<FallbackStorage> PRIORITY_COMPARATOR = new PriorityComparator();

    /**
     * Get the priority of current {@link FallbackStorage}.
     *
     * @return the less value , the more priority.
     */
    int getPriority();

    /**
     * Destroy
     */
    void destroy();

    class PriorityComparator implements Comparator<FallbackStorage> {

        @Override
        public int compare(FallbackStorage o1, FallbackStorage o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
        }
    }
}
