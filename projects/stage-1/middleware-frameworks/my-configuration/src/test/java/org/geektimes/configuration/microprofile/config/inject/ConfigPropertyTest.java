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
package org.geektimes.configuration.microprofile.config.inject;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

import static org.eclipse.microprofile.config.inject.ConfigProperty.UNCONFIGURED_VALUE;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class ConfigPropertyTest {

    @Inject
    @ConfigProperty(name = "myprj.some.url", defaultValue = "/some/url")
    private String someUrl;

    @Inject
    @ConfigProperty(name = "myprj.another.url")
    private ConfigValue anotherUrl;

    /**
     * ParameterizedType :  java.util.Optional<Integer>
     * <p>
     * RawType : java.util.Optional
     * <p>
     * TypeArguments :
     * [0] : java.lang.Integer
     */
    @Inject
    @ConfigProperty(name = "myprj.some.port",defaultValue = "8080")
    private Optional<Integer> somePort;

    private Config config;

    @Before
    public void mockConfigPropertyInject() throws Throwable {
        initConfig();
        mockConfigPropertyInjectForSomePort();
    }

    private void initConfig() {
        initConfigSources();
        this.config = ConfigProvider.getConfig();
    }

    private void initConfigSources() {
        System.setProperty("myprj.some.port", "9090");
    }

    private void mockConfigPropertyInjectForSomePort() throws Throwable {
        Class<?> beanClass = getClass();
        Field[] configPropertyFields = findConfigPropertyFields(beanClass);
        Field field = Stream.of(configPropertyFields)
                .filter(f -> "somePort".equals(f.getName()))
                .findFirst()
                .orElse(null);
        Type fieldType = field.getGenericType();
        Object fieldValue = null;
        if (fieldType instanceof ParameterizedType) {
            ParameterizedType fieldParamType = (ParameterizedType) fieldType;
            Class containerType = (Class) fieldParamType.getRawType();
            // convertedType == java.lang.Integer.class
            Class convertedType = (Class) fieldParamType.getActualTypeArguments()[0];
            ConfigProperty configProperty = field.getAnnotation(ConfigProperty.class);
            String propertyName = configProperty.name();
            String defaultValue = configProperty.defaultValue();
            Integer somePort = config.getValue(propertyName, Integer.class);
            if (Optional.class.equals(containerType)) {

                if (somePort == null && !UNCONFIGURED_VALUE.equals(defaultValue)) {
                    somePort = Integer.decode(defaultValue);
                }

                fieldValue = Optional.ofNullable(somePort);
            }

            if (fieldValue != null) {
                // inject
                field.setAccessible(true);
                field.set(this, fieldValue);
            }
        }
    }

    private Field[] findConfigPropertyFields(Class<?> beanClass) {
        Field[] allFields = beanClass.getDeclaredFields();
        // filter condition:
        // 1. non-static
        // 2. non-final
        // 3. annotated @javax.inject.Inject
        // 4. annotated @org.eclipse.microprofile.config.inject.ConfigProperty
        return Stream.of(allFields)
                .filter(field -> {
                    int mods = field.getModifiers();
                    return !Modifier.isStatic(mods)
                            && !Modifier.isFinal(mods);
                })
                .filter(field -> field.isAnnotationPresent(Inject.class)
                        && field.isAnnotationPresent(ConfigProperty.class)
                ).toArray(Field[]::new);
    }

    @Test
    public void testSomePort() {
        System.out.println(somePort);
    }

    // InjectionPoint

//    @Test
//    public void testReflectionMetaInfo() throws Throwable {
//        Field field = getClass().getDeclaredField("somePort");
//        Type fieldType = field.getGenericType();
//        if (fieldType instanceof ParameterizedType) {
//            ParameterizedType fieldParamType = (ParameterizedType) fieldType;
//            System.out.println("ParameterizedType : " + fieldParamType);
//            System.out.println("RawType : " + fieldParamType.getRawType());
//            System.out.println("ActualTypeArguments : " + Arrays.asList(fieldParamType.getActualTypeArguments()));
//        }
//    }

}
