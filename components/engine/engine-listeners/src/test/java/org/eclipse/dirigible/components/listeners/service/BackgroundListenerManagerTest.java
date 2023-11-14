package org.eclipse.dirigible.components.listeners.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import org.eclipse.dirigible.components.listeners.config.ActiveMQConnectionArtifactsFactory;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.components.listeners.domain.ListenerKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class BackgroundListenerManagerTest {

    private static final String TOPIC = "test-topic";
    private static final String QUEUE = "test-queue";

    @InjectMocks
    private BackgroundListenerManager manager;

    @Mock
    private Listener listener;

    @Mock
    private ActiveMQConnectionArtifactsFactory connectionArtifactsFactory;

    @Mock
    private Connection connection;

    @Mock
    private Session session;

    @Mock
    private Topic topic;

    @Mock
    private Queue queue;

    @Mock
    private MessageConsumer consumer;

    @Captor
    private ArgumentCaptor<ExceptionListener> exceptionListenerCaptor;

    @Captor
    private ArgumentCaptor<BackgroundMessageListener> messageListenerCaptor;

    @Test
    void testStartListenerOnStartError() throws JMSException {
        when(connectionArtifactsFactory.createConnection(any(ExceptionListener.class))).thenReturn(connection);
        when(connectionArtifactsFactory.createSession(connection)).thenThrow(JMSException.class);

        assertThrows(IllegalStateException.class, ()-> manager.startListener());
    }

    @Test
    void testStartListenerForUnsupportedListenerKind() {
        when(listener.getKind()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, ()-> manager.startListener());
    }

    @Test
    void testStartListenerForQueue() throws JMSException {
        mockConnectionAndSession();
        when(listener.getKind()).thenReturn(ListenerKind.QUEUE);
        when(listener.getName()).thenReturn(QUEUE);

        when(session.createQueue(QUEUE)).thenReturn(queue);
        when(session.createConsumer(queue)).thenReturn(consumer);

        manager.startListener();

        manager.startListener();

        // verify second call to start doesn't create new artifacts
        verifyConfiguredExceptionListener();
        verifyConfiguredMessageListener();
        verify(session).createQueue(QUEUE);
        verify(session).createConsumer(queue);
        verify(connectionArtifactsFactory).createConnection(any(ExceptionListener.class));
        verify(connectionArtifactsFactory).createSession(connection);
    }

    @Test
    void testStartListenerForTopic() throws JMSException {
        mockConnectionAndSession();
        when(listener.getKind()).thenReturn(ListenerKind.TOPIC);
        when(listener.getName()).thenReturn(TOPIC);

        when(session.createTopic(TOPIC)).thenReturn(topic);
        when(session.createConsumer(topic)).thenReturn(consumer);

        manager.startListener();

        verifyConfiguredExceptionListener();
        verifyConfiguredMessageListener();
    }

    private void mockConnectionAndSession() throws JMSException {
        when(connectionArtifactsFactory.createConnection(any(ExceptionListener.class))).thenReturn(connection);
        when(connectionArtifactsFactory.createSession(connection)).thenReturn(session);
    }

    private void verifyConfiguredExceptionListener() {
        verify(connectionArtifactsFactory).createConnection(exceptionListenerCaptor.capture());
        ExceptionListener actualExceptionListener = exceptionListenerCaptor.getValue();
        assertThat(actualExceptionListener).isInstanceOf(ListenerExceptionHandler.class);
    }

    private void verifyConfiguredMessageListener() throws JMSException {
        verify(consumer).setMessageListener(messageListenerCaptor.capture());
        assertThat(messageListenerCaptor.getValue()).isInstanceOf(BackgroundMessageListener.class);
    }

    @Test
    void testStop() throws JMSException {
        testStartListenerForTopic();

        manager.stopListener();

        manager.stopListener();

        InOrder inOrder = Mockito.inOrder(consumer, session, connection);

        // verify second call to stop doesn't close the artifacts again
        inOrder.verify(consumer)
               .close();

        inOrder.verify(session)
               .close();

        inOrder.verify(connection)
               .close();
    }

}
