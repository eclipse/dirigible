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
package org.eclipse.dirigible.components.listeners.config;

import java.util.HashMap;
import java.util.Map;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MessageConsumerExceptionListener implements ExceptionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerExceptionListener.class);

    private static final String DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_ERROR = "messaging/wrappers/onError.js";

    private final JavascriptService javascriptService;

    MessageConsumerExceptionListener(JavascriptService javascriptService) {
        this.javascriptService = javascriptService;
    }

    @Override
    public synchronized void onException(JMSException jmsException) {
        try {
            Map<Object, Object> context = createMessagingContext();
            context.put("error", escapeCodeString(jmsException.getMessage()));
            RepositoryPath path = new RepositoryPath(DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_ERROR);
            javascriptService.handleRequest(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
        } catch (RuntimeException ex) {
            ex.addSuppressed(jmsException);
            LOGGER.error("Failed to handle exception properly", ex);
        }
    }

    private Map<Object, Object> createMessagingContext() {
        Map<Object, Object> context = new HashMap<>();
        String handler = null; // TODO get handler if needed?
        context.put("handler", handler);
        return context;
    }

    private String escapeCodeString(String raw) {
        return raw.replace("'", "&amp;");
    }

}
