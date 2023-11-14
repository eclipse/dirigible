package org.eclipse.dirigible.components.listeners.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class MessageConsumerTest {

    private static final String QUEUE = "test-queue";
    private static final long TIMEOUT = 100L;
    private static final String MESSAGE = "This is a test message";
    private static final String TOPIC = "test-topic";

    @InjectMocks
    private MessageConsumer consumer;

    @Mock
    private Session session;

    @Mock
    private javax.jms.MessageConsumer jsmConsumer;

    @Mock
    private Queue queue;

    @Mock
    private Topic topic;

    @Mock
    private TextMessage txtMessage;

    @Mock
    private BytesMessage byteMessage;

    @Test
    void testReceiveMessageFromQueue() throws TimeoutException, JMSException {
        when(session.createQueue(QUEUE)).thenReturn(queue);
        when(session.createConsumer(queue)).thenReturn(jsmConsumer);
        when(jsmConsumer.receive(TIMEOUT)).thenReturn(txtMessage);
        when(txtMessage.getText()).thenReturn(MESSAGE);

        String actualMessage = consumer.receiveMessageFromQueue(QUEUE, TIMEOUT);

        assertThat(actualMessage).isEqualTo(MESSAGE);
    }

    @Test
    void testReceiveMessageFromQueueOnTimeout() throws TimeoutException, JMSException {
        when(session.createQueue(QUEUE)).thenReturn(queue);
        when(session.createConsumer(queue)).thenReturn(jsmConsumer);
        when(jsmConsumer.receive(TIMEOUT)).thenReturn(null);

        assertThrows(TimeoutException.class, ()->  consumer.receiveMessageFromQueue(QUEUE, TIMEOUT));
    }

    @Test
    void testReceiveMessageFromQueueOnUnsupportedMessageIsReceived() throws TimeoutException, JMSException {
        when(session.createQueue(QUEUE)).thenReturn(queue);
        when(session.createConsumer(queue)).thenReturn(jsmConsumer);
        when(jsmConsumer.receive(TIMEOUT)).thenReturn(byteMessage);

        assertThrows(IllegalStateException.class, ()->  consumer.receiveMessageFromQueue(QUEUE, TIMEOUT));
    }

    @Test
    void testReceiveMessageFromTopic() throws JMSException {
        when(session.createTopic(TOPIC)).thenReturn(topic);
        when(session.createConsumer(topic)).thenReturn(jsmConsumer);
        when(jsmConsumer.receive(TIMEOUT)).thenReturn(txtMessage);
        when(txtMessage.getText()).thenReturn(MESSAGE);

        String actualMessage = consumer.receiveMessageFromTopic(TOPIC, TIMEOUT);

        assertThat(actualMessage).isEqualTo(MESSAGE);
    }

    @Test
    void testReceiveMessageFromTopicOnTimeout() throws JMSException {
        when(session.createTopic(TOPIC)).thenReturn(topic);
        when(session.createConsumer(topic)).thenReturn(jsmConsumer);
        when(jsmConsumer.receive(TIMEOUT)).thenReturn(null);

        assertThrows(TimeoutException.class, ()->  consumer.receiveMessageFromTopic(TOPIC, TIMEOUT));
    }

}
