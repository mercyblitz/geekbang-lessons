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

import org.geektimes.cache.ExpirableEntry;
import org.junit.After;
import org.junit.Test;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * {@link FileFallbackStorage} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
public class FileFallbackStorageTest {

    private FileFallbackStorage instance = new FileFallbackStorage();

    @Test
    public void writeAllAndLoadAll() {

        assertNull(instance.load("a"));
        assertNull(instance.load("b"));
        assertNull(instance.load("c"));

        instance.writeAll(asList(ExpirableEntry.of("a", 1), ExpirableEntry.of("b", 2), ExpirableEntry.of("c", 3)));

        Map map = instance.loadAll(asList("a", "b", "c"));
        assertEquals(1, map.get("a"));
        assertEquals(2, map.get("b"));
        assertEquals(3, map.get("c"));

        instance.write(ExpirableEntry.of("a", new Object()));
    }

    @After
    public void deleteAll() {
        instance.deleteAll(asList("a", "b", "c"));
    }

}
