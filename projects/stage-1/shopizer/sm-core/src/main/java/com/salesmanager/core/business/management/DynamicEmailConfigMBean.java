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

import javax.management.*;

/**
 * {@link DynamicMBean} based on {@link EmailConfig}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DynamicEmailConfigMBean extends EmailConfig implements DynamicMBean {

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        String attributeValue = null;
        switch (attribute) {
            case "username":
                attributeValue = getUsername();
                break;
            default:
                throw new AttributeNotFoundException(attribute);
        }

        return attributeValue;
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        switch (attribute.getName()) {
            case "username":
                setUsername((String) attribute.getValue());
                break;
            default:
                throw new AttributeNotFoundException(attribute.getName());
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
        Object returnValue = null;
        switch (actionName) {
            case "toJSONString":
                returnValue = toJSONString();
                break;
            default:
                throw new RuntimeOperationsException(new IllegalArgumentException(), actionName);
        }
        return returnValue;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return new MBeanInfo(getClass().getName(), "No Desc",
                // MBeanAttributeInfo[] attributes,
                of(mBeanAttributeInfo("username", String.class, true, true)),
                // MBeanConstructorInfo[] constructors,
                of(),
                // MBeanOperationInfo[] operations,
                of(mBeanOperationInfo("toJSONString", String.class)),
                // MBeanNotificationInfo[] notifications
                of()
        );
    }

    private MBeanAttributeInfo mBeanAttributeInfo(String name,
                                                  Class type,
                                                  boolean isReadable,
                                                  boolean isWritable) {
        return new MBeanAttributeInfo(name, type.getName(), name, isReadable, isWritable, !(isReadable || isWritable));
    }

    private MBeanOperationInfo mBeanOperationInfo(String methodName, Class returnType) {
        return new MBeanOperationInfo(methodName, methodName, of(), returnType.getName(), MBeanOperationInfo.ACTION);
    }

    public static <T> T[] of(T... values) {
        return values;
    }
}
