package org.eclipse.dirigible.components.listeners.service;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import javax.jms.JMSException;
import org.eclipse.dirigible.components.listeners.util.LogsAsserter;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.modules.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ch.qos.logback.classic.Level;

@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class ListenerExceptionHandlerTest {

    private static final String ERROR_MESSAGE = "Opsss";
    private static final String HANDLER_PATH = "handler/path.js";

    private ListenerExceptionHandler handler;

    private JMSException jmsException;

    private LogsAsserter logsAsserter;

    @Mock
    private DirigibleJavascriptCodeRunner jsCodeRunner;

    @Mock
    private Module module;

    @BeforeEach
    void setUp() {
        jmsException = new JMSException(ERROR_MESSAGE);
        handler = Mockito.spy(new ListenerExceptionHandler(HANDLER_PATH));
        doReturn(jsCodeRunner).when(handler)
                              .createJSCodeRunner();
        logsAsserter = new LogsAsserter(ListenerExceptionHandler.class, Level.ERROR);
    }

    @Test
    void testOnException() {
        when(jsCodeRunner.run(HANDLER_PATH)).thenReturn(module);


        handler.onException(jmsException);

        verify(jsCodeRunner).runMethod(module, "onError", ERROR_MESSAGE);
    }

    @Test
    void testOnExceptionOnFailToRunMethod() {
        when(jsCodeRunner.run(HANDLER_PATH)).thenThrow(IllegalStateException.class);

        handler.onException(jmsException);

        logsAsserter.assertLoggedMessage("Failed to handle exception properly", Level.ERROR);
    }

}
