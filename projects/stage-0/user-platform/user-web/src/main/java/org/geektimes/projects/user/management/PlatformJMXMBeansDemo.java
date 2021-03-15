package org.geektimes.projects.user.management;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

public class PlatformJMXMBeansDemo {

    public static void main(String[] args) {
        // 客户端去获取 ClassLoadingMXBean 对象（代理对象）
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();

        classLoadingMXBean.getLoadedClassCount();
    }
}
