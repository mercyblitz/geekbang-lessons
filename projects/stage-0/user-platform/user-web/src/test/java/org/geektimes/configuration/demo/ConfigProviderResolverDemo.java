package org.geektimes.configuration.demo;


import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.configuration.microprofile.config.DefaultConfigProviderResolver;
import org.geektimes.configuration.microprofile.config.source.LocalConfigSource;
import org.geektimes.configuration.microprofile.config.source.SystemEnvironmentConfigSource;
import org.junit.Test;


public class ConfigProviderResolverDemo {

    /**
     * 测试获取所有的属性值，包括系统属性、系统环境变量、本地文件配置
     *
     * {@link SystemEnvironmentConfigSource}
     * {@link LocalConfigSource}
     */
    @Test
    public void test() {
        ConfigProviderResolver providerResolver = ConfigProviderResolver.instance();
        Config config = providerResolver.getBuilder().addDefaultSources().addDiscoveredSources().addDiscoveredConverters().build();
        providerResolver.registerConfig(config, this.getClass().getClassLoader());
        for (String propertyName : config.getPropertyNames()) {
            System.out.println(propertyName + "=" + config.getValue(propertyName, String.class));
        }
    }

    /**
     * 测试获取application.name属性
     */
    @Test
    public void test2() {
        String propertyName = "application.name";

        ConfigProviderResolver providerResolver = ConfigProviderResolver.instance();
        Config config = providerResolver.getBuilder().addDefaultSources().addDiscoveredSources().addDiscoveredConverters().build();
        providerResolver.registerConfig(config, this.getClass().getClassLoader());
        System.out.println(propertyName + "=" + config.getValue(propertyName, String.class));
    }

    /**
     * 测试Convert扩展
     *
     * 改变convert.value对应的属性值和type类型
     */
    @Test
    public void test3() {
        String propertyName = "convert.value";
        Class type = Long.class;

        ConfigProviderResolver providerResolver = ConfigProviderResolver.instance();
        Config config = providerResolver.getBuilder().addDefaultSources().addDiscoveredSources().addDiscoveredConverters().build();
        providerResolver.registerConfig(config, this.getClass().getClassLoader());
        System.out.println(propertyName + "=" + config.getValue(propertyName, type));
    }

}
