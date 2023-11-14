package org.eclipse.dirigible.components.listeners.config;

import javax.jms.JMSException;
import org.eclipse.dirigible.components.listeners.util.LogsAsserter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ch.qos.logback.classic.Level;

@ExtendWith(MockitoExtension.class)
class LoggingExceptionListenerTest {

    @InjectMocks
    private LoggingExceptionListener listener;

    private LogsAsserter logsAsserter;

    @BeforeEach
    void setUp() {
        this.logsAsserter = new LogsAsserter(LoggingExceptionListener.class, Level.ERROR);
    }

    @Test
    void testOnException() {
        JMSException jmsException = new JMSException("Opsss");

        listener.onException(jmsException);

        logsAsserter.assertLoggedMessage("Exception occur", Level.ERROR);
    }

}
