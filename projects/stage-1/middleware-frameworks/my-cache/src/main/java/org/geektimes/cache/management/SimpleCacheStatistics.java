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

import javax.cache.management.CacheStatisticsMXBean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Cache Statistics
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-04-13
 */
public class SimpleCacheStatistics implements CacheStatisticsMXBean, CacheStatistics {

    private final AtomicLong cacheHits = new AtomicLong();

    private final AtomicLong cacheGets = new AtomicLong();

    private final AtomicLong cachePuts = new AtomicLong();

    private final AtomicLong cacheRemovals = new AtomicLong();

    private final AtomicLong cacheEvictions = new AtomicLong();

    private final LongAdder cacheGetTime = new LongAdder();

    private final LongAdder cachePutTime = new LongAdder();

    private final LongAdder cacheRemoveTime = new LongAdder();

    @Override
    public void clear() {
        reset();
    }

    @Override
    public SimpleCacheStatistics reset() {
        cacheHits.set(0);
        cacheGets.set(0);
        cachePuts.set(0);
        cacheRemovals.set(0);
        cacheEvictions.set(0);
        cacheGetTime.reset();
        cachePutTime.reset();
        cacheRemoveTime.reset();
        return this;
    }

    @Override
    public SimpleCacheStatistics cacheHits() {
        cacheHits.incrementAndGet();
        return this;
    }

    @Override
    public SimpleCacheStatistics cacheGets() {
        cacheGets.incrementAndGet();
        return this;
    }

    @Override
    public SimpleCacheStatistics cachePuts() {
        cachePuts.incrementAndGet();
        return this;
    }

    @Override
    public SimpleCacheStatistics cacheRemovals() {
        cacheRemovals.incrementAndGet();
        return this;
    }

    @Override
    public SimpleCacheStatistics cacheEvictions() {
        cacheEvictions.incrementAndGet();
        return this;
    }

    @Override
    public SimpleCacheStatistics cacheGetsTime(long costTime) {
        cacheGetTime.add(costTime);
        return this;
    }

    @Override
    public SimpleCacheStatistics cachePutsTime(long costTime) {
        cachePutTime.add(costTime);
        return this;
    }

    @Override
    public SimpleCacheStatistics cacheRemovesTime(long costTime) {
        cacheRemoveTime.add(costTime);
        return this;
    }

    @Override
    public long getCacheHits() {
        return cacheHits.get();
    }

    @Override
    public float getCacheHitPercentage() {
        if (getCacheGets() < 1) {
            return 0.0f;
        }
        return (getCacheHits() / getCacheGets()) / 100.0f;
    }

    @Override
    public long getCacheMisses() {
        return getCacheGets() - getCacheHits();
    }

    @Override
    public float getCacheMissPercentage() {
        if (getCacheGets() < 1) {
            return 0.0f;
        }
        return (getCacheMisses() / getCacheGets()) / 100.0f;
    }

    @Override
    public long getCacheGets() {
        return cacheGets.get();
    }

    @Override
    public long getCachePuts() {
        return cachePuts.get();
    }

    @Override
    public long getCacheRemovals() {
        return cacheRemovals.get();
    }

    @Override
    public long getCacheEvictions() {
        return cacheEvictions.get();
    }

    @Override
    public float getAverageGetTime() {
        return cacheGetTime.floatValue() / getCacheGets();
    }

    @Override
    public float getAveragePutTime() {
        return cachePutTime.floatValue() / getCachePuts();
    }

    @Override
    public float getAverageRemoveTime() {
        return cacheRemoveTime.floatValue() / getCacheRemovals();
    }
}
