/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.service;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import jakarta.jms.JMSException;
import org.eclipse.dirigible.components.listeners.util.LogsAsserter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ch.qos.logback.classic.Level;

/**
 * The Class ListenerExceptionHandlerTest.
 */
@ExtendWith(MockitoExtension.class)
class ListenerExceptionHandlerTest {

    /** The Constant ERROR_MESSAGE. */
    private static final String ERROR_MESSAGE = "Opsss";

    /** The Constant HANDLER_PATH. */
    private static final String HANDLER_PATH = "handler/path.js";

    /** The handler. */
    private ListenerExceptionHandler handler;

    /** The jms exception. */
    private JMSException jmsException;

    /** The logs asserter. */
    private LogsAsserter logsAsserter;

    /** The executor of the listener's script handler. */
    @Mock
    private ListenerScriptExecutor scriptExecutor;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        jmsException = new JMSException(ERROR_MESSAGE);
        handler = new ListenerExceptionHandler(HANDLER_PATH, scriptExecutor);
        logsAsserter = new LogsAsserter(ListenerExceptionHandler.class, Level.ERROR);
    }

    /**
     * Test on exception.
     */
    @Test
    void testOnException() {
        handler.onException(jmsException);

        verify(scriptExecutor).onError(HANDLER_PATH, ERROR_MESSAGE);
    }

    /**
     * Test on exception on fail to run method.
     */
    @Test
    void testOnExceptionOnFailToRunMethod() {
        doThrow(IllegalStateException.class).when(scriptExecutor)
                                            .onError(HANDLER_PATH, ERROR_MESSAGE);

        handler.onException(jmsException);

        logsAsserter.assertLoggedMessage("Failed to handle exception properly", Level.ERROR);
    }

}
