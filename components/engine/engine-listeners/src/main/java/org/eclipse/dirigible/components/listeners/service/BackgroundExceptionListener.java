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

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.modules.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BackgroundExceptionListener implements ExceptionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundExceptionListener.class);
    private final String handlerPath;

    BackgroundExceptionListener(String handlerPath) {
        this.handlerPath = handlerPath;
    }

    @Override
    public void onException(JMSException jmsException) {
        try {
            String errorMessage = escapeCodeString(jmsException.getMessage());
            executeOnErrorHandler(errorMessage);
        } catch (RuntimeException ex) {
            ex.addSuppressed(jmsException);
            LOGGER.error("Failed to handle exception properly", ex);
        }
    }

    private void executeOnErrorHandler(String errorMessage) {
        try (DirigibleJavascriptCodeRunner runner = new DirigibleJavascriptCodeRunner()) {
            Module module = runner.run(handlerPath);
            runner.runMethod(module, "onError", errorMessage);
        }
    }

    private String escapeCodeString(String raw) {
        return raw.replace("'", "&amp;");
    }

}
