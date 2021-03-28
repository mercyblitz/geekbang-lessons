package org.geektimes.projects.user.message;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

public class MessageProducerFactory implements ObjectFactory {

    private String queueName;

    private String connectionFactoryJndiName;


    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?, ?> environment) throws Exception {

        // nameCtx 发现同级目录的 Context
        ConnectionFactory connectionFactory = (ConnectionFactory) nameCtx.lookup("activemq-factory");

        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);

        return producer;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public void setConnectionFactoryJndiName(String connectionFactoryJndiName) {
        this.connectionFactoryJndiName = connectionFactoryJndiName;
    }
}
