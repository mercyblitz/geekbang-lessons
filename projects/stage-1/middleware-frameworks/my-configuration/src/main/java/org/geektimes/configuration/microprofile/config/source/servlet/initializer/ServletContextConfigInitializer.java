package org.geektimes.configuration.microprofile.config.source.servlet.initializer;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.configuration.microprofile.config.source.servlet.ServletContextConfigSource;
import org.geektimes.configuration.microprofile.config.source.servlet.ServletRequestHeaderConfigSource;
import org.geektimes.configuration.microprofile.config.source.servlet.ServletRequestParameterConfigSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 如何注册当前 ServletContextListener 实现
 *
 * @see ServletConfigInitializer
 */
public class ServletContextConfigInitializer implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        // 获取当前 ClassLoader
        ClassLoader classLoader = servletContext.getClassLoader();
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        ConfigBuilder configBuilder = configProviderResolver.getBuilder();
        // 配置 ClassLoader
        configBuilder.forClassLoader(classLoader);
        // 默认配置源（内建的，静态的）
        configBuilder.addDefaultSources();
        // 通过发现配置源（动态的）
        configBuilder.addDiscoveredConverters();
        // 增加扩展配置源（基于 Servlet 引擎）
        addServletContextConfigSource(servletContext, configBuilder);
        addServletRequestParameterConfigSource(configBuilder);
        addServletRequestHeaderConfigSource(configBuilder);
        // TODO
        // 增加动态配置源
        // DynamicServletConfigSource

        if (isServlet3orAbove(servletContext)) {

        } else { // Below Servlet 3
            addDynamicServletConfigSource(configBuilder);
        }

        // 获取 Config
        Config config = configBuilder.build();
        // 注册 Config 关联到当前 ClassLoader
        configProviderResolver.registerConfig(config, classLoader);
    }

    private boolean isServlet3orAbove(ServletContext servletContext) {
        return servletContext.getMajorVersion() >= 3;
    }

    private void addServletContextConfigSource(ServletContext servletContext, ConfigBuilder configBuilder) {
        ServletContextConfigSource configSource = new ServletContextConfigSource(servletContext);
        configBuilder.withSources(configSource);
    }

    private void addServletRequestParameterConfigSource(ConfigBuilder configBuilder) {
        ServletRequestParameterConfigSource configSource = new ServletRequestParameterConfigSource();
        configBuilder.withSources(configSource);
    }

    private void addServletRequestHeaderConfigSource(ConfigBuilder configBuilder) {
        ServletRequestHeaderConfigSource configSource = new ServletRequestHeaderConfigSource();
        configBuilder.withSources(configSource);
    }

    private void addDynamicServletConfigSource(ConfigBuilder configBuilder) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
//        ServletContext servletContext = servletContextEvent.getServletContext();
//        ClassLoader classLoader = servletContext.getClassLoader();
//        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
//        Config config = configProviderResolver.getConfig(classLoader);
    }
}
