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
package org.geektimes.configuration.demo.netflix.archaius;

import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-Memory {@link PolledConfigurationSource}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class InMemoryPolledConfigurationSource implements PolledConfigurationSource {

    private AtomicInteger versions = new AtomicInteger(1);

    @Override
    public PollResult poll(boolean initial, Object checkPoint) throws Exception {
        if (initial) {
            return initialLoad();
        } else {
            Map<String, Object> added = Collections.emptyMap();
            Map<String, Object> changed = ofMap("currentTime", System.currentTimeMillis() / 1000);
            Map<String, Object> deleted = Collections.emptyMap();
            return PollResult.createIncremental(added, changed, deleted, versions.getAndIncrement());
        }
    }

    private PollResult initialLoad() {
        return PollResult.createFull(initialData());
    }

    private Map<String, Object> initialData() {
        return ofMap("currentTime", System.currentTimeMillis() / 1000);
    }

    private Map<String, Object> ofMap(Object... keysAndValues) {
        Map<String, Object> data = new HashMap<>();
        int length = keysAndValues.length;
        for (int i = 0; i < length; ) {
            String key = String.valueOf(keysAndValues[i++]);
            Object value = keysAndValues[i++];
            data.put(key, value);
        }
        return data;
    }
}
