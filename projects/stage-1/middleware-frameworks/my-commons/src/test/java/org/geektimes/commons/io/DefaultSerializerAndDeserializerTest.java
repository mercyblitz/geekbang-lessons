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

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * {@link DefaultSerializer} and {@link DefaultDeserializer} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DefaultSerializerAndDeserializerTest {

    private DefaultSerializer serializer = new DefaultSerializer();

    private DefaultDeserializer deserializer = new DefaultDeserializer();

    @Test
    public void test() throws IOException {
        Object value = "Test";
        byte[] bytes = serializer.serialize(value);
        assertEquals(value, deserializer.deserialize(bytes));

        value = 1;
        bytes = serializer.serialize(value);
        assertEquals(value, deserializer.deserialize(bytes));
    }
}
