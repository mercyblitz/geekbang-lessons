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
package org.geektimes.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

import static java.util.Objects.requireNonNull;

/**
 * The Delegating {@link Converter} implementation based on
 * {@link org.geektimes.commons.convert.Converter}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see org.geektimes.commons.convert.Converter
 * @since 1.0.0
 */
public class DelegatingConverter extends AbstractConverter {

    private final Class<?> convertedType;

    public DelegatingConverter(Class<?> convertedType) {
        requireNonNull(convertedType, "The 'convertedType' must not be null!");
        this.convertedType = convertedType;
    }

    @Override
    protected Object doConvert(String value) throws Throwable {
        return org.geektimes.commons.convert.Converter.convertIfPossible(value, convertedType);
    }
}
