/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.components.base.script.ScriptExecutor;
import org.eclipse.dirigible.repository.api.RepositoryPath;

/**
 * Hands the messages of a scripting consumer to its handler script, through the messaging wrapper
 * scripts that load the handler and call its {@code onMessage} / {@code onError}.
 */
class ScriptMessageListener implements RabbitMQMessageListener {

    /** The wrapper calling the handler's {@code onMessage}. */
    private static final String DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_MESSAGE = "messaging/wrappers/onMessage.js";

    /** The wrapper calling the handler's {@code onError}. */
    private static final String DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_ERROR = "messaging/wrappers/onError.js";

    private final String handler;

    private final ScriptExecutor scriptExecutor;

    /**
     * Instantiates the listener of a scripting consumer.
     *
     * @param handler the registry path of the handler script
     * @param scriptExecutor the executor of the wrapper scripts
     */
    ScriptMessageListener(String handler, ScriptExecutor scriptExecutor) {
        this.handler = handler;
        this.scriptExecutor = scriptExecutor;
    }

    @Override
    public void onMessage(String message) {
        Map<Object, Object> context = context();
        context.put("message", message);
        execute(DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_MESSAGE, context);
    }

    /**
     * Hands the failure to the handler's {@code onError}. A failure of the error wrapper itself is
     * rethrown, wrapped.
     */
    @Override
    public void onError(String error) {
        try {
            Map<Object, Object> context = context();
            context.put("error", escapeCodeString(error));
            execute(DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_ERROR, context);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Map<Object, Object> context() {
        Map<Object, Object> context = new HashMap<>();
        context.put("handler", handler);
        return context;
    }

    private void execute(String wrapper, Map<Object, Object> context) {
        RepositoryPath path = new RepositoryPath(wrapper);
        scriptExecutor.execute(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
    }

    /**
     * Escape code string.
     *
     * @param raw the raw
     * @return the string
     */
    private static String escapeCodeString(String raw) {
        return raw.replace("'", "&amp;");
    }
}
