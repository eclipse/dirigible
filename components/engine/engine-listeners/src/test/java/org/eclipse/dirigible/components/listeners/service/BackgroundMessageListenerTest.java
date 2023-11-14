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
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.modules.Module;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("resource")
@ExtendWith(MockitoExtension.class)
class BackgroundMessageListenerTest {

    private static final String MESSAGE = "This is a test message";
    private static final String HANDLER = "test-handler";

    private BackgroundMessageListener backgroundMessageListener;

    @Mock
    private DirigibleJavascriptCodeRunner jsCodeRunner;

    @Mock
    private TextMessage textMessage;

    @Mock
    private Listener listener;

    @Mock
    private BytesMessage bytesMessage;

    @Mock
    private Module module;

    @Mock
    private Value value;

    @BeforeEach
    void setUp() {
        backgroundMessageListener = spy(new BackgroundMessageListener(listener));
    }

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

    @Test
    void testOnMessageFailedToExtractMessage() throws JMSException {
        when(textMessage.getText()).thenThrow(JMSException.class);

        assertThrows(IllegalStateException.class, () -> backgroundMessageListener.onMessage(textMessage));
    }

    @Test
    void testOnMessageWithUnsupportedMessage() {
        assertThrows(IllegalStateException.class, () -> backgroundMessageListener.onMessage(bytesMessage));
    }

}
