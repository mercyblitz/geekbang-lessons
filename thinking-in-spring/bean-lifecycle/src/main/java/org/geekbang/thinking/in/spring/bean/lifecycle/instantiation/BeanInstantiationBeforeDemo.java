package org.geekbang.thinking.in.spring.bean.lifecycle.instantiation;

import org.geekbang.thinking.in.spring.bean.lifecycle.UserHolder;
import org.geekbang.thinking.in.spring.ioc.overview.domain.SuperUser;
import org.geekbang.thinking.in.spring.ioc.overview.domain.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.ObjectUtils;

import javax.xml.ws.Holder;
import java.util.Objects;

/**
 * @author xiaoheitalk
 * @type BeanInstantiationBeforeDemo
 * @date 2020/6/13 14:39
 */
public class BeanInstantiationBeforeDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new InstantiationAwareBeanPostProcessorDemo());

        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        String location = "META-INF/dependency-lookup-context.xml";
        ClassPathResource resource = new ClassPathResource(location);
        EncodedResource encodedResource = new EncodedResource(resource, "utf-8");

        int number = xmlBeanDefinitionReader.loadBeanDefinitions(encodedResource);
        System.out.println("number = " + number);
        int number2 = xmlBeanDefinitionReader.loadBeanDefinitions("META-INF/bean-constructor-dependency-injection.xml");
        System.out.println("number2 = " + number2);


        User user = beanFactory.getBean("user", User.class);
        System.out.println("user = " + user);

        SuperUser superUser = beanFactory.getBean("superUser", SuperUser.class);
        System.out.println("superUser = " + superUser);

        UserHolder userHolder = beanFactory.getBean("userHolder", UserHolder.class);
        System.out.println("userHolder = " + userHolder);
    }

    static class InstantiationAwareBeanPostProcessorDemo implements InstantiationAwareBeanPostProcessor {
        @Override
        public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals("supperUser", beanName) && SuperUser.class.equals(beanClass)) {
                SuperUser user = new SuperUser();
                user.setId(111L);
                // 将覆盖 superUser
                return user;
            }
            return null;
        }

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals("user", beanName) && User.class.equals(bean.getClass())) {
                User user = (User) bean;
                user.setId(222L);
                // user对象不允许属性赋值（填入）：配置元信息 --> 属性值
                return false;
            }
            return true;
        }

        @Override
        public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
            if (ObjectUtils.nullSafeEquals("userHolder", beanName) && UserHolder.class.equals(bean.getClass())) {
                System.out.println("pvs = " + pvs);
                MutablePropertyValues propertyValues = new MutablePropertyValues();

                propertyValues.addPropertyValue("number", 10);

                return propertyValues;
            }
            return null;
        }

    }
}
