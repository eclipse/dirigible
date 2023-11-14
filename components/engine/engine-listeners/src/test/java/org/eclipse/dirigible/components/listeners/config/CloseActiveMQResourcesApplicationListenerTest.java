package org.eclipse.dirigible.components.listeners.config;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.eclipse.dirigible.components.listeners.service.BackgroundListenersManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

@ExtendWith(MockitoExtension.class)
class CloseActiveMQResourcesApplicationListenerTest {

    @InjectMocks
    private CloseActiveMQResourcesApplicationListener listener;

    @Mock
    private BrokerService broker;

    @Mock
    private Connection connection;

    @Mock
    private Session session;

    @Mock
    private BackgroundListenersManager listenersManager;

    @Mock
    private ContextClosedEvent closedEvent;

    @Mock
    private ContextStoppedEvent stoppedEvent;

    @Mock
    private ContextStartedEvent startedEvent;

    @Test
    void testOnContextClosedEvent() throws Exception {
        listener.onApplicationEvent(closedEvent);

        verifyClosedResources();
    }

    @Test
    void testOnContextStoppedEvent() throws Exception {
        listener.onApplicationEvent(stoppedEvent);

        verifyClosedResources();
    }

    private void verifyClosedResources() throws JMSException, Exception {
        InOrder inOrder = Mockito.inOrder(listenersManager, session, connection, broker);

        inOrder.verify(listenersManager)
               .stopListeners();

        inOrder.verify(session)
               .close();

        inOrder.verify(connection)
               .close();

        inOrder.verify(broker)
               .stop();
    }

    @Test
    void testOnNotApplicableEvent() {
        listener.onApplicationEvent(startedEvent);

        verifyNoInteractions(listenersManager, session, connection, broker);
    }

    @Test
    void testStopListenersDoesntTerminateTheClose() throws Exception {
        doThrow(RuntimeException.class).when(listenersManager)
                                       .stopListeners();

        listener.onApplicationEvent(closedEvent);

        verifyClosedResources();
    }

    @Test
    void testCloseSessionDoesntTerminateTheClose() throws Exception {
        doThrow(Exception.class).when(session)
                                .close();

        listener.onApplicationEvent(closedEvent);

        verifyClosedResources();
    }

    @Test
    void testCloseConnectionDoesntTerminateTheClose() throws Exception {
        doThrow(Exception.class).when(connection)
                                .close();

        listener.onApplicationEvent(closedEvent);

        verifyClosedResources();
    }

    @Test
    void testStopBrokerDoesntTerminateTheClose() throws Exception {
        doThrow(Exception.class).when(broker)
                                .stop();

        listener.onApplicationEvent(closedEvent);

        verifyClosedResources();
    }

}
