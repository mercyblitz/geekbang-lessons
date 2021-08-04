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
package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Enumeration;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Enumerable {@link ConfigSource} extends {@link MapBasedConfigSource}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class EnumerableConfigSource extends MapBasedConfigSource {

    protected EnumerableConfigSource(String name, int ordinal) {
        super(name, ordinal);
    }

    @Override
    protected final void prepareConfigData(Map configData) throws Throwable {
        prepareConfigData(configData, namesSupplier(), valueResolver());
    }

    private void prepareConfigData(Map configData,
                                   Supplier<Enumeration<String>> namesSupplier,
                                   Function<String, String> valueResolver) {
        Enumeration<String> names = namesSupplier.get();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = valueResolver.apply(name);
            configData.put(name, value);
        }
    }

    protected abstract Supplier<Enumeration<String>> namesSupplier();

    protected abstract Function<String, String> valueResolver();

}
