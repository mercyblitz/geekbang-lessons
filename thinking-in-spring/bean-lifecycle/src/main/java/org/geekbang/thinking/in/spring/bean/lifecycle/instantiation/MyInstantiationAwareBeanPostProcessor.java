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
package org.geekbang.thinking.in.spring.bean.lifecycle.instantiation;

import org.geekbang.thinking.in.spring.bean.lifecycle.UserHolder;
import org.geekbang.thinking.in.spring.ioc.overview.domain.SuperUser;
import org.geekbang.thinking.in.spring.ioc.overview.domain.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.util.ObjectUtils;

/**
 * TODO
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        System.out.println("----------- postProcessBeforeInstantiation " + beanName);
        if (ObjectUtils.nullSafeEquals("superUser", beanName) && SuperUser.class.equals(beanClass)) {
            // 把配置完成 superUser Bean 覆盖
            System.out.println("----------- postProcessBeforeInstantiation 替换了 " + beanName);
            return new SuperUser();
        }
        // 保持 Spring IoC 容器的实例化操作
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        System.out.println("----------- postProcessAfterInstantiation " + beanName);
        if (ObjectUtils.nullSafeEquals("user", beanName) && User.class.equals(bean.getClass())) {
            User user = (User) bean;
            user.setId(2L);
            user.setName("mercyblitz");
            // "user" 对象不允许属性赋值（填入）（配置元信息 -> 属性值）
            System.out.println("----------- postProcessAfterInstantiation 修改了" + beanName + ", 跳过后续的属性赋值");
            return false;
        }
        return true;
    }

    // user 是跳过 Bean 属性赋值（填入）
    // superUser 也是完全跳过 Bean 实例化（Bean 属性赋值（填入））
    // userHolder
    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
            throws BeansException {
        System.out.println("----------- postProcessProperties " + beanName);
        // 对 "userHolder" Bean 进行拦截
        if (ObjectUtils.nullSafeEquals("userHolder", beanName) && UserHolder.class.equals(bean.getClass())) {
            // 假设 <property name="number" value="1" /> 配置的话，那么在 PropertyValues 就包含一个 PropertyValue(number=1)

            final MutablePropertyValues propertyValues;

            if (pvs instanceof MutablePropertyValues) {
                propertyValues = (MutablePropertyValues) pvs;
            } else {
                propertyValues = new MutablePropertyValues();
            }

            // 等价于 <property name="number" value="1" />
            // 原始配置 <property name="description" value="The user holder" />
            propertyValues.addPropertyValue("number", "2");

            // 如果存在 "description" 属性配置的话
            if (propertyValues.contains("description")) {
                // PropertyValue value 是不可变的
                PropertyValue oldPropertyValue = propertyValues.getPropertyValue("description");
                propertyValues.removePropertyValue("description");

                String newPropertyValue = "The user holder V2";
                propertyValues.addPropertyValue("description", newPropertyValue);

                System.out.println("----------- postProcessProperties " + beanName + ": oldPropertyValue : "
                        + ((TypedStringValue) oldPropertyValue.getValue()).getValue()
                        + " --> newPropertyValue : " + newPropertyValue);
            }
            System.out.println("----------- postProcessProperties " + beanName + ": 修改了属性");
            return propertyValues;
        }
        return null;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("----------- postProcessBeforeInitialization " + beanName);
        if (ObjectUtils.nullSafeEquals("userHolder", beanName) && UserHolder.class.equals(bean.getClass())) {
            UserHolder userHolder = (UserHolder) bean;
            // UserHolder description = "The user holder V2"
            String the_user_holder_v3 = "The user holder V3";
            userHolder.setDescription(the_user_holder_v3);
            System.out.println("----------- postProcessBeforeInitialization " + beanName + "，修改了 description = " + the_user_holder_v3);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("----------- postProcessAfterInitialization " + beanName);
        if (ObjectUtils.nullSafeEquals("userHolder", beanName) && UserHolder.class.equals(bean.getClass())) {
            UserHolder userHolder = (UserHolder) bean;
            // init() = The user holder V6
            // UserHolder description = "The user holder V6"
            String the_user_holder_v7 = "The user holder V7";
            userHolder.setDescription(the_user_holder_v7);
            System.out.println("----------- postProcessAfterInitialization " + beanName + "，修改了 description = " + the_user_holder_v7);
        }
        return bean;
    }
}
