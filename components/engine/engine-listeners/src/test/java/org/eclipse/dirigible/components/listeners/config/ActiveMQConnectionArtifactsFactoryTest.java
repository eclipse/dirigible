package org.eclipse.dirigible.components.listeners.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class ActiveMQConnectionArtifactsFactoryTest {

    @InjectMocks
    private ActiveMQConnectionArtifactsFactory connectionArtifactsFactory;

    @Mock
    private ActiveMQConnectionFactory connectionFactory;

    @Mock
    private ExceptionListener exceptionListener;

    @Mock
    private Connection connection;

    @Mock
    private Session session;

    @Test
    void testCreateConnection() throws JMSException {
        when(connectionFactory.createConnection()).thenReturn(connection);

      Connection actualConnection = connectionArtifactsFactory.createConnection(exceptionListener);

      assertThat(actualConnection).isEqualTo(connection);
      verify(connection).setExceptionListener(exceptionListener);
      verify(connection).start();
    }

    @Test
    void testCreateConnectionOnJMSException() throws JMSException {
        when(connectionFactory.createConnection()).thenThrow(JMSException.class);

        assertThrows(IllegalStateException.class, ()->connectionArtifactsFactory.createConnection(exceptionListener));
    }

    @Test
    void testCreateSession() throws JMSException {
        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);

      Session actualSession = connectionArtifactsFactory.createSession(connection);

      assertThat(actualSession).isEqualTo(session);
    }

}
