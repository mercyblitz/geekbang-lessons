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
package org.geektimes.session.config.converter;

import org.eclipse.microprofile.config.spi.Converter;
import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;
import org.geektimes.configuration.microprofile.config.converter.ClassConverter;
import org.geektimes.session.SessionRepository;

import java.lang.reflect.Constructor;

/**
 * {@link SessionRepository} {@link Converter}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 * Date : 2021-05-06
 */
public class SessionRepositoryConverter extends AbstractConverter<SessionRepository> {

    private final ClassLoader classLoader;

    private final ClassConverter classConverter;

    public SessionRepositoryConverter(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.classConverter = new ClassConverter(classLoader);
    }

    @Override
    protected SessionRepository doConvert(String value) throws Throwable {
        Class repositoryClass = classConverter.convert(value);
        Constructor constructor = null;
        Object[] arguments;
        try {
            // try to Constructor with ClassLoader typed argument
            constructor = repositoryClass.getConstructor(ClassLoader.class);
            arguments = new Object[]{classLoader};
        } catch (Throwable ignored) {
            constructor = repositoryClass.getConstructor();
            arguments = new Object[0];
        }

        return (SessionRepository) constructor.newInstance(arguments);
    }
}
