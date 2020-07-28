package org.geekbang.thinking.in.spring.configuration.metadata.xml;

import org.geekbang.thinking.in.spring.configuration.metadata.model.Student;
import org.geekbang.thinking.in.spring.ioc.overview.domain.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author xiaoheitalk
 * @type XmlBeanDefinitionReaderDemo
 * @date 2020/6/19 12:44
 */
public class XmlBeanDefinitionReaderDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(XmlBeanDefinitionReaderDemo.class);
        context.refresh();

        System.out.println("context.getBeanDefinition(\"initUser\") = " + context.getBeanDefinition("initUser"));
        System.out.println("context.getBeanDefinition(\"student\") = " + context.getBeanDefinition("student"));

        User bean = context.getBean(User.class);
        System.out.println( bean);
    }

    @Bean
    public User initUser(){
        User user = new User();
        user.setId(2L);
        user.setName("晓敏");
        return user;
    }

    @Bean
    public Student student(){
        Student student = new Student();
        student.setId(1L);
        return student;
    }

    @Bean
    public Student student2(){
        Student student = new Student(2L, "xiaoming");
        return student;
    }

}
