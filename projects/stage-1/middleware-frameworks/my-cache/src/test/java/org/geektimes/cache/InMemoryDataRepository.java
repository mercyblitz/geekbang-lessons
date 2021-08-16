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
package org.geektimes.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * In-Memory {@link DataRepository} Implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class InMemoryDataRepository implements DataRepository {

    private final Map<String, Object> storage = new HashMap<>();

    @Override
    public boolean create(String name, Object value) {
        return save(name, value);
    }

    @Override
    public boolean save(String name, String alias, Object value) {
        return save(name + alias, value);
    }

    @Override
    public boolean save(String name, Object value) {
        return storage.put(name, value) == null;
    }

    @Override
    public boolean remove(String name) {
        return storage.remove(name) != null;
    }

    @Override
    public Object get(String name) {
        return getWithoutCache(name);
    }

    @Override
    public Object getWithoutCache(String name) {
        return storage.get(name);
    }

    @Override
    public void removeAll() {
        storage.clear();
    }
}
