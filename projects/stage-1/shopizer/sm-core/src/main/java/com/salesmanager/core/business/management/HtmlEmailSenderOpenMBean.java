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
package com.salesmanager.core.business.management;

import com.salesmanager.core.business.modules.email.EmailConfig;
import com.salesmanager.core.business.modules.email.HtmlEmailSender;

import javax.management.*;
import javax.management.openmbean.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * {@link HtmlEmailSender}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class HtmlEmailSenderOpenMBean implements DynamicMBean {

    private final HtmlEmailSender htmlEmailSender;

    public HtmlEmailSenderOpenMBean(HtmlEmailSender htmlEmailSender) {
        this.htmlEmailSender = htmlEmailSender;
    }

    private static final Map<Class, SimpleType> simpleTypesMapping = new LinkedHashMap<Class, SimpleType>() {

        {
            ClassLoader classLoader = this.getClass().getClassLoader();

            for (Field field : SimpleType.class.getFields()) {
                if (isSimpleTypeField(field)) {
                    try {
                        SimpleType simpleType = (SimpleType) field.get(null);
                        put(classLoader.loadClass(simpleType.getClassName()), simpleType);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }

            // Primitive types mapping
            put(void.class, SimpleType.VOID);
            put(boolean.class, SimpleType.BOOLEAN);
            put(char.class, SimpleType.CHARACTER);
            put(byte.class, SimpleType.BYTE);
            put(short.class, SimpleType.SHORT);
            put(int.class, SimpleType.INTEGER);
            put(long.class, SimpleType.LONG);
            put(float.class, SimpleType.FLOAT);
            put(double.class, SimpleType.DOUBLE);
        }

        private boolean isSimpleTypeField(Field field) {
            int modifiers = field.getModifiers();
            return Modifier.isFinal(modifiers)
                    && Modifier.isStatic(modifiers)
                    && SimpleType.class.equals(field.getType());
        }
    };

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        Object attributeValue = null;

        switch (attribute) {
            case "EmailConfig":
                EmailConfig emailConfig = htmlEmailSender.getEmailConfig();
                try {
                    CompositeDataSupport compositeDataSupport = new CompositeDataSupport(
                            (CompositeType) asOpenType(emailConfig.getClass()),
                            items(emailConfig));
                    attributeValue = compositeDataSupport;
                } catch (OpenDataException e) {
                    throw new MBeanException(e);
                }
                break;
        }

        return attributeValue;
    }

    private Map<String, ?> items(Object bean) throws ReflectionException {
        Map<String, Object> items = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                Method getterMethod = propertyDescriptor.getReadMethod();
                items.put(propertyDescriptor.getName(), getterMethod.invoke(bean));
            }
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
        return items;
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        String attributeName = attribute.getName();
        switch (attributeName) {
            case "EmailConfig":
                CompositeData compositeData = (CompositeData) attribute.getValue();
                try {
                    EmailConfig emailConfig = EmailConfig.from(compositeData);
                    htmlEmailSender.setEmailConfig(emailConfig);
                } catch (Exception e) {
                    throw new ReflectionException(e);
                }
                break;
        }
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        return null;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        try {
            return new MBeanInfo(getClass().getName(), "No Desc",
                    // MBeanAttributeInfo[] attributes,
                    of(mBeanAttributeInfo("EmailConfig", EmailConfig.class, true, true)),
                    // MBeanConstructorInfo[] constructors,
                    of(),
                    // MBeanOperationInfo[] operations,
                    of(),
                    // MBeanNotificationInfo[] notifications
                    of()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MBeanAttributeInfo mBeanAttributeInfo(String attributeName, Class<?> type,
                                                  boolean isReadable, boolean isWriteable) {
        return new OpenMBeanAttributeInfoSupport(attributeName, attributeName, asOpenType(type),
                isReadable, isWriteable, !(isReadable || isWriteable));
    }

    private OpenType<?> asOpenType(Class<?> type) {

        OpenType openType = null;

        if (isInvalidType(type)) {
            throw new IllegalArgumentException("Invalid type");
        } else if (isSimpleType(type) || isPrimitiveType(type)) {     // Simple type or Primitive type
            return simpleTypesMapping.get(type);
        } else if (isCollectionType(type)) { // Collection type

        } else if (isArrayType(type)) {      // Array type

        } else {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(type, Object.class);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                int propertySize = propertyDescriptors.length;
                String[] itemNames = new String[propertySize];
                OpenType[] itemTypes = new OpenType[propertySize];
                for (int i = 0; i < propertySize; i++) {
                    PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                    itemNames[i] = propertyDescriptor.getName();
                    Class<?> propertyType = propertyDescriptor.getPropertyType();
                    itemTypes[i] = asOpenType(propertyType);
                }

                openType = new CompositeType(type.getName(), type.getSimpleName(), itemNames, itemNames, itemTypes);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return openType;
    }

    private boolean isInvalidType(Class<?> type) {
        return type.isInterface()
                || type.isEnum()
                || type.isAnnotation();
    }

    private boolean isSimpleType(Class<?> type) {
        return simpleTypesMapping.containsKey(type);
    }

    private boolean isPrimitiveType(Class<?> type) {
        return type.isPrimitive();
    }

    private boolean isCollectionType(Class<?> type) {
        return Collection.class.isAssignableFrom(type)
                || Map.class.isAssignableFrom(type);
    }

    private boolean isArrayType(Class<?> type) {
        return type.isArray();
    }


    private MBeanOperationInfo getEmailConfigMBeanOperationInfo() throws OpenDataException {
        return new OpenMBeanOperationInfoSupport("getEmailConfig", "getEmailConfig", of(),
                emailConfigCompositeType(EmailConfig.class,
                        of("host", "port", "protocol", "username", "password", "smtpAuth", "starttls"),
                        of(SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING,
                                SimpleType.BOOLEAN, SimpleType.BOOLEAN)
                ), MBeanOperationInfo.ACTION);

    }

    private MBeanOperationInfo mBeanOperationInfo(String methodName, Class returnType, MBeanParameterInfo... parameters) {
        return new MBeanOperationInfo(methodName, methodName, parameters, returnType.getName(), MBeanOperationInfo.ACTION);
    }

    private MBeanParameterInfo emailConfigMBeanParameterInfo() throws OpenDataException {
        return new OpenMBeanParameterInfoSupport("emailConfig", "emailConfig",
                /**
                 private String host;
                 private String port;
                 private String protocol;
                 private String username;
                 private String password;
                 private boolean smtpAuth = false;
                 private boolean starttls = false;
                 */
                emailConfigCompositeType(EmailConfig.class,
                        of("host", "port", "protocol", "username", "password", "smtpAuth", "starttls"),
                        of(SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING,
                                SimpleType.BOOLEAN, SimpleType.BOOLEAN)
                ));
    }

    private CompositeType emailConfigCompositeType(Class type, String[] itemNames, OpenType... itemTypes) throws OpenDataException {
        return new CompositeType(type.getName(), type.getName(), itemNames, itemNames, itemTypes);
    }

    public static <T> T[] of(T... values) {
        return values;
    }
}
