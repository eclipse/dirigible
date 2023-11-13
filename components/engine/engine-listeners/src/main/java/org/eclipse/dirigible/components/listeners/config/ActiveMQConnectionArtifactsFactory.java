package org.eclipse.dirigible.components.listeners.config;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQConnectionArtifactsFactory {

    private static final String CONNECTOR_URL_ATTACH = "vm://localhost?create=false";

    public Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public Connection createConnection(ExceptionListener exceptionListener) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(CONNECTOR_URL_ATTACH);
        try {
            Connection connection = connectionFactory.createConnection();
            connection.setExceptionListener(exceptionListener);

            connection.start();

            return connection;
        } catch (JMSException ex) {
            throw new IllegalStateException("Failed to create connection to ActiveMQ", ex);
        }
    }

}
