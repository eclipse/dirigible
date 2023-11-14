package org.eclipse.dirigible.components.listeners.service;

import static org.mockito.Mockito.doThrow;
import java.io.IOException;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectionArtifactsTest {

    @InjectMocks
    private ConnectionArtifacts artifacts;
    @Mock
    private Connection connection;

    @Mock
    private Session session;

    @Mock
    private MessageConsumer messageConsumer;

    @Test
    void testCloseAll() throws JMSException {
        artifacts.closeAll();

        verifyClosedArtifacts();
    }

    @Test
    void testCloseAllClosesAllResourceOnException() throws JMSException {
        doThrow(IOException.class).when(messageConsumer)
                                  .close();
        doThrow(IOException.class).when(session)
                                  .close();
        doThrow(IOException.class).when(connection)
                                  .close();

        artifacts.closeAll();

        verifyClosedArtifacts();
    }

    private void verifyClosedArtifacts() throws JMSException {
        InOrder inOrder = Mockito.inOrder(messageConsumer, session, connection);

        inOrder.verify(messageConsumer)
               .close();

        inOrder.verify(session)
               .close();

        inOrder.verify(connection)
               .close();
    }
}
