package org.geektimes.projects.user.web.listener;

import org.geektimes.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.management.Address;
import org.geektimes.projects.user.management.UserManager;
import org.geektimes.projects.user.management.UserManagerInterface;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.management.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

/**
 * 测试用途
 */
@Deprecated
public class TestingListener implements ServletContextListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ComponentContext context = ComponentContext.getInstance();
        DBConnectionManager dbConnectionManager = context.getComponent("bean/DBConnectionManager");
        dbConnectionManager.getConnection();

//        testUser(dbConnectionManager.getEntityManager());
        testPropertyFromServletContext(sce.getServletContext());
        testPropertyFromJNDI(context);
//        testUser(dbConnectionManager.getEntityManager());
        logger.info("所有的 JNDI 组件名称：[");
        context.getComponentNames().forEach(logger::info);
        logger.info("]");

        registerMBean();
    }

    private void registerMBean() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            User user = new User();
            user.setId(9527L);
            user.setName("suiyuanfeng");
            user.setEmail("1713716445@qq.com");
            user.setPhoneNumber("15705113753");
            user.setPassword("*******");
            Address address = new Address();
            address.setPro("上海市");
            address.setArea("宝山区");
            // 将静态的 MBean 接口转化成 DynamicMBean
            ObjectName objectName = new ObjectName("org.geektimes.projects.user.management:type=User");
            StandardMBean standardMBean = new StandardMBean(new UserManager(user, address), UserManagerInterface.class);

            mBeanServer.registerMBean(standardMBean, objectName);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private void testPropertyFromServletContext(ServletContext servletContext) {
        String propertyName = "application.name";
        logger.info("ServletContext Property[" + propertyName + "] : "
                + servletContext.getInitParameter(propertyName));
    }

    private void testPropertyFromJNDI(ComponentContext context) {
        String propertyName = "maxValue";
        logger.info("JNDI Property[" + propertyName + "] : "
                + context.lookupComponent(propertyName));
    }

    private void testUser(EntityManager entityManager) {
        User user = new User();
        user.setName("小马哥");
        user.setPassword("******");
        user.setEmail("mercyblitz@gmail.com");
        user.setPhoneNumber("15705113700");
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        transaction.commit();
        System.out.println(entityManager.find(User.class, user.getId()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
