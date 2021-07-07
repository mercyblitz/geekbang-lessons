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
package org.geektimes.commons.io;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * {@link Serializers} and {@link Deserializers} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SerializersAndDeserializersTest {

    private Serializers serializers;

    private Deserializers deserializers;

    @Before
    public void init() {
        serializers = new Serializers();
        deserializers = new Deserializers();
        serializers.loadSPI();
        deserializers.loadSPI();
    }

    @Test
    public void testGetHighestPriority() throws IOException {
        Serializer<String> serializer = serializers.getHighestPriority(String.class);
        String value = "Test";
        byte[] bytes = serializer.serialize(value);
        assertArrayEquals(value.getBytes(StandardCharsets.UTF_8), bytes);
        Deserializer<String> deserializer = deserializers.getHighestPriority(String.class);
        assertEquals(value, deserializer.deserialize(bytes));
    }

    @Test
    public void testGetMostCompatible() throws IOException {
        Serializer serializer = serializers.getMostCompatible(Integer.class);
        Integer value = 1;
        byte[] bytes = serializer.serialize(value);
        Deserializer deserializer = deserializers.getMostCompatible(Integer.class);
        assertEquals(value, deserializer.deserialize(bytes));
    }
}
