/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.service;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.modules.Module;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class BackgroundMessageListenerTest.
 */
@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class MessageListenerTest {

    /** The Constant MESSAGE. */
    private static final String MESSAGE = "This is a test message";

    /** The Constant HANDLER. */
    private static final String HANDLER = "test-handler";

    /** The background message listener. */
    private AsynchronousMessageListener backgroundMessageListener;

    /** The js code runner. */
    @Mock
    private DirigibleJavascriptCodeRunner jsCodeRunner;

    /** The text message. */
    @Mock
    private TextMessage textMessage;

    /** The listener. */
    @Mock
    private Listener listener;

    /** The bytes message. */
    @Mock
    private BytesMessage bytesMessage;

    /** The module. */
    @Mock
    private Module module;

    /** The value. */
    @Mock
    private Value value;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        backgroundMessageListener = spy(new AsynchronousMessageListener(listener));
    }

    /**
     * Test on message.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void testOnMessage() throws JMSException {
        doReturn(jsCodeRunner).when(backgroundMessageListener)
                              .createJSCodeRunner();
        when(listener.getHandler()).thenReturn(HANDLER);
        when(textMessage.getText()).thenReturn(MESSAGE);
        when(jsCodeRunner.run(HANDLER)).thenReturn(module);

        backgroundMessageListener.onMessage(textMessage);

        verify(jsCodeRunner).runMethod(module, "onMessage", MESSAGE);

    }

    /**
     * Test on message failed to extract message.
     *
     * @throws JMSException the JMS exception
     */
    @Test
    void testOnMessageFailedToExtractMessage() throws JMSException {
        when(textMessage.getText()).thenThrow(JMSException.class);

        assertThrows(IllegalStateException.class, () -> backgroundMessageListener.onMessage(textMessage));
    }

    /**
     * Test on message with unsupported message.
     */
    @Test
    void testOnMessageWithUnsupportedMessage() {
        assertThrows(IllegalStateException.class, () -> backgroundMessageListener.onMessage(bytesMessage));
    }

}
