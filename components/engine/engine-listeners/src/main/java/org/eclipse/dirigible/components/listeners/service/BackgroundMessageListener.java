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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.modules.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BackgroundMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundMessageListener.class);

    private final Listener listener;

    BackgroundMessageListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onMessage(Message message) {
        LOGGER.trace("Start processing a received message in [{}] by [{}] ...", listener.getName(), listener.getHandler());
        if (!(message instanceof TextMessage textMsg)) {
            String msg = String.format("Invalid message [%s] has been received in destination [%s]", message, listener.getName());
            throw new IllegalStateException(msg);
        }
        executeOnMessageHandler(textMsg);
        LOGGER.trace("Done processing the received message in [{}] by [{}]", listener.getName(), listener.getHandler());
    }

    private void executeOnMessageHandler(TextMessage textMsg) {
        String extractedMsg = extractMessage(textMsg);
        try (DirigibleJavascriptCodeRunner runner = createJSCodeRunner()) {
            String handlerPath = listener.getHandler();
            Module module = runner.run(handlerPath);
            runner.runMethod(module, "onMessage", extractedMsg);
        }
    }

    private String extractMessage(TextMessage textMsg) {
        try {
            return textMsg.getText();
        } catch (JMSException ex) {
            throw new IllegalStateException("Failed to extract test message from " + textMsg, ex);
        }
    }

    DirigibleJavascriptCodeRunner createJSCodeRunner() {
        return new DirigibleJavascriptCodeRunner();
    }

}
