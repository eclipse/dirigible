/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.javascript.bridges.jobs;

import java.nio.file.Path;

import org.eclipse.dirigible.components.jobs.handler.JobScriptExecutor;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.springframework.stereotype.Component;

/**
 * Runs a script job's handler as a JavaScript module, one code runner per run.
 */
@Component
class JavascriptJobScriptExecutor implements JobScriptExecutor {

    @Override
    public void execute(String handler) {
        try (DirigibleJavascriptCodeRunner runner = new DirigibleJavascriptCodeRunner()) {
            runner.run(Path.of(handler));
        }
    }
}
