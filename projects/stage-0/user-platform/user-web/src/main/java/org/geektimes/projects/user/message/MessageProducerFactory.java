package org.geektimes.projects.user.message;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

import static java.lang.String.valueOf;

public class MessageProducerFactory implements ObjectFactory {

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?, ?> environment) throws Exception {

        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }

        Reference reference = (Reference) obj;

        String queueName = getAttribute(reference, "queueName");

        String connectionFactoryJndiName = getAttribute(reference, "connectionFactoryJndiName");

        // nameCtx 发现同级目录的 Context
        ConnectionFactory connectionFactory = (ConnectionFactory) nameCtx.lookup(connectionFactoryJndiName);

        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue(queueName);

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);

        return producer;
    }

    private String getAttribute(Reference reference, String attributeName) {
        RefAddr refAddr = reference.get(attributeName);
        return refAddr == null ? null : valueOf(refAddr.getContent());
    }
}
