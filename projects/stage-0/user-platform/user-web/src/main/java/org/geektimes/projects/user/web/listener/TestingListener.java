package org.geektimes.projects.user.web.listener;

import org.geektimes.context.ClassicComponentContext;
import org.geektimes.function.ThrowableAction;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.jms.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;

/**
 * 测试用途
 */
@Deprecated
public class TestingListener implements ServletContextListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ClassicComponentContext context = ClassicComponentContext.getInstance();
        DBConnectionManager dbConnectionManager = context.getComponent("bean/DBConnectionManager");
        dbConnectionManager.getConnection();
        testPropertyFromServletContext(sce.getServletContext());
        testPropertyFromJNDI(context);
        testUser(dbConnectionManager.getEntityManager());
        logger.info("所有的 JNDI 组件名称：[");
        context.getComponentNames().forEach(logger::info);
        logger.info("]");

        ConnectionFactory connectionFactory = context.getComponent("jms/activemq-factory");
        testJms(connectionFactory);
    }

    private void testPropertyFromServletContext(ServletContext servletContext) {
        String propertyName = "application.name";
        logger.info("ServletContext Property[" + propertyName + "] : "
                + servletContext.getInitParameter(propertyName));
    }

    private void testPropertyFromJNDI(ClassicComponentContext context) {
        String propertyName = "maxValue";
        logger.info("JNDI Property[" + propertyName + "] : "
                + context.lookupComponent(propertyName));
    }

    private void testUser(EntityManager entityManager) {
        User user = new User();
        user.setName("小马哥");
        user.setPassword("******");
        user.setEmail("mercyblitz@gmail.com");
        user.setPhoneNumber("abcdefg");
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        transaction.commit();
    }

    private void testJms(ConnectionFactory connectionFactory) {
        ThrowableAction.execute(() -> {
//            testMessageProducer(connectionFactory);
            testMessageConsumer(connectionFactory);
        });
    }

    private void testMessageProducer(ConnectionFactory connectionFactory) throws JMSException {
        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Create a messages
        String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
        TextMessage message = session.createTextMessage(text);

        // Tell the producer to send the message
        producer.send(message);
        System.out.printf("[Thread : %s] Sent message : %s\n", Thread.currentThread().getName(), message.getText());

        // Clean up
        session.close();
        connection.close();

    }

    private void testMessageConsumer(ConnectionFactory connectionFactory) throws JMSException {

        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageConsumer from the Session to the Topic or Queue
        MessageConsumer consumer = session.createConsumer(destination);

        consumer.setMessageListener(m -> {
            TextMessage tm = (TextMessage) m;
            try {
                System.out.printf("[Thread : %s] Received : %s\n", Thread.currentThread().getName(), tm.getText());
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });

        // Clean up
        // session.close();
        // connection.close();
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
