/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.javascript.bridges.listeners;

import org.eclipse.dirigible.components.listeners.service.ListenerScriptExecutor;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.modules.Module;
import org.springframework.stereotype.Component;

/**
 * Calls a listener's JavaScript handler module, one code runner per call.
 */
@Component
class JavascriptListenerScriptExecutor implements ListenerScriptExecutor {

    private static final String ON_MESSAGE = "onMessage";

    private static final String ON_ERROR = "onError";

    @Override
    public void onMessage(String handlerPath, String message) {
        call(handlerPath, ON_MESSAGE, message);
    }

    @Override
    public void onError(String handlerPath, String errorMessage) {
        call(handlerPath, ON_ERROR, errorMessage);
    }

    private static void call(String handlerPath, String export, String argument) {
        try (DirigibleJavascriptCodeRunner runner = new DirigibleJavascriptCodeRunner()) {
            Module module = runner.run(handlerPath);
            runner.runMethod(module, export, argument);
        }
    }
}
