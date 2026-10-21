/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.javascript.bridges.websockets;

import java.util.Map;

import org.eclipse.dirigible.components.websockets.service.WebsocketScriptDispatcher;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.modules.Module;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Component;

/**
 * Calls a websocket's JavaScript handler module with the event's context bound, one code runner per
 * event.
 */
@Component
class JavascriptWebsocketScriptDispatcher implements WebsocketScriptDispatcher {

    @Override
    public void onOpen(String handler, Map<Object, Object> context) {
        call(handler, context, "onOpen");
    }

    @Override
    public String onMessage(String handler, Map<Object, Object> context) {
        return call(handler, context, "onMessage", context.get("message"), context.get("from"));
    }

    @Override
    public void onError(String handler, Map<Object, Object> context) {
        call(handler, context, "onError", context.get("error"));
    }

    @Override
    public void onClose(String handler, Map<Object, Object> context) {
        call(handler, context, "onClose");
    }

    private static String call(String handler, Map<Object, Object> context, String export, Object... arguments) {
        try (DirigibleJavascriptCodeRunner runner = new DirigibleJavascriptCodeRunner(context, false)) {
            Module module = runner.run(handler);
            Value result = runner.runMethod(module, export, arguments);
            return result != null ? result.toString() : "";
        }
    }
}
