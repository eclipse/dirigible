package org.eclipse.dirigible.components.listeners.service;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ActiveMQConnectionArtifacts {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMQConnectionArtifacts.class);

    private final Connection connection;
    private final Session session;
    private final MessageConsumer consumer;

    ActiveMQConnectionArtifacts(Connection connection, Session session, MessageConsumer consumer) {
        this.connection = connection;
        this.session = session;
        this.consumer = consumer;
    }

    public void close() {
        closeResource(consumer);
        closeResource(session);
        closeResource(connection);
    }

    private void closeResource(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (Exception ex) {
            LOGGER.warn("Failed to close " + closeable, ex);
        }
    }

}
