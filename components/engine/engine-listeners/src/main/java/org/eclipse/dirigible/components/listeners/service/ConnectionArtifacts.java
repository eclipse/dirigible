package org.eclipse.dirigible.components.listeners.service;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConnectionArtifacts {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionArtifacts.class);

    private final Connection connection;
    private final Session session;
    private final MessageConsumer messageConsumer;

    ConnectionArtifacts(Connection connection, Session session, MessageConsumer messageConsumer) {
        this.connection = connection;
        this.session = session;
        this.messageConsumer = messageConsumer;
    }

    void closeAll() {
        close(messageConsumer);
        close(session);
        close(connection);
    }

    private void close(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception ex) {
            LOGGER.warn("Failed to close {}", closeable, ex);
        }

    }

}
