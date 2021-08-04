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
package org.geektimes.rpc.serializer;

import org.geektimes.commons.io.Deserializer;
import org.geektimes.commons.io.Deserializers;
import org.geektimes.commons.io.Serializers;

import java.io.IOException;

/**
 * 默认 {@link Serializer}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DefaultSerializer implements Serializer {

    private final Serializers serializers = new Serializers();

    private final Deserializers deserializers = new Deserializers();

    public DefaultSerializer() {
        serializers.loadSPI();
        deserializers.loadSPI();
    }

    @Override
    public byte[] serialize(Object source) throws IOException {
        Class<?> targetClass = source.getClass();
        org.geektimes.commons.io.Serializer serializer =
                serializers.getMostCompatible(targetClass);
        return serializer.serialize(source);
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> targetClass) throws IOException {
        Deserializer deserializer = deserializers.getMostCompatible(targetClass);
        return deserializer.deserialize(bytes);
    }
}
