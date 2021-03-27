package org.geektimes.projects.user.web.listener;

import org.geektimes.context.ClassicComponentContext;
import org.geektimes.context.ComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * {@link ClassicComponentContext} 初始化器
 * ContextLoaderListener
 */
public class ComponentContextInitializerListener implements ServletContextListener {

    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        ClassicComponentContext context = new ClassicComponentContext();
        context.init(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ComponentContext context = ClassicComponentContext.getInstance();
        context.destroy();
    }

}
