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

/**
 * Dummy {@link CacheStatistics}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-13
 */
public class DummyCacheStatistics implements CacheStatistics {

    /**
     * Singleton instance
     */
    public static final CacheStatistics INSTANCE = new DummyCacheStatistics();

    private DummyCacheStatistics() {
    }

    @Override
    public CacheStatistics reset() {
        return this;
    }

    @Override
    public CacheStatistics cacheHits() {
        return this;
    }

    @Override
    public CacheStatistics cacheGets() {
        return this;
    }

    @Override
    public CacheStatistics cachePuts() {
        return this;
    }

    @Override
    public CacheStatistics cacheRemovals() {
        return this;
    }

    @Override
    public CacheStatistics cacheEvictions() {
        return this;
    }

    @Override
    public CacheStatistics cacheGetsTime(long costTime) {
        return this;
    }

    @Override
    public CacheStatistics cachePutsTime(long costTime) {
        return this;
    }

    @Override
    public CacheStatistics cacheRemovesTime(long costTime) {
        return this;
    }

    @Override
    public void clear() {

    }

    @Override
    public long getCacheHits() {
        return 0;
    }

    @Override
    public float getCacheHitPercentage() {
        return 0;
    }

    @Override
    public long getCacheMisses() {
        return 0;
    }

    @Override
    public float getCacheMissPercentage() {
        return 0;
    }

    @Override
    public long getCacheGets() {
        return 0;
    }

    @Override
    public long getCachePuts() {
        return 0;
    }

    @Override
    public long getCacheRemovals() {
        return 0;
    }

    @Override
    public long getCacheEvictions() {
        return 0;
    }

    @Override
    public float getAverageGetTime() {
        return 0;
    }

    @Override
    public float getAveragePutTime() {
        return 0;
    }

    @Override
    public float getAverageRemoveTime() {
        return 0;
    }
}
