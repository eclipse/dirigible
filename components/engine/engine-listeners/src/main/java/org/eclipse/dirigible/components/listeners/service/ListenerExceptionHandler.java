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

import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ListenerExceptionHandler.
 */
class ListenerExceptionHandler implements ExceptionListener {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerExceptionHandler.class);

    /** The handler path. */
    private final String handlerPath;

    /** The executor of the listener's script handler. */
    private final ListenerScriptExecutor scriptExecutor;

    /**
     * Instantiates a new listener exception handler.
     *
     * @param handlerPath the handler path
     * @param scriptExecutor the executor of the listener's script handler
     */
    ListenerExceptionHandler(String handlerPath, ListenerScriptExecutor scriptExecutor) {
        this.handlerPath = handlerPath;
        this.scriptExecutor = scriptExecutor;
    }

    /**
     * On exception.
     *
     * @param jmsException the jms exception
     */
    @Override
    public void onException(JMSException jmsException) {
        try {
            String errorMessage = jmsException.getMessage();
            scriptExecutor.onError(handlerPath, errorMessage);
        } catch (RuntimeException ex) {
            ex.addSuppressed(jmsException);
            LOGGER.error("Failed to handle exception properly", ex);
        }
    }

}
