/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalium.execution.platform;

import org.eclipse.dirigible.commons.config.Configuration;
import org.graalvm.polyglot.Engine;

public class GraalJSEngineCreator {
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN = "DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN";

    private static Engine engine = null;
    private static Engine debuggableEngine = null;

    public static Engine getOrCreateEngine() {
        if (engine == null) {
            engine = getDefaultEngineBuilder().build();
        }

        return engine;
    }

    public static Engine getOrCreateDebuggableEngine() {
        if (debuggableEngine == null) {
            debuggableEngine = getDefaultEngineBuilder()
                    .option("inspect", "9229")
                    .option("inspect.Secure", "false")
                    .option("inspect.Path", "test")
                    .build();
        }

        return debuggableEngine;
    }

    private static Engine.Builder getDefaultEngineBuilder() {
        Engine.Builder engineBuilder = Engine.newBuilder()
                .allowExperimentalOptions(true)
                .option("js.ecmascript-version", "2022")
                .option("engine.WarnInterpreterOnly", "false")
                .option("js.esm-eval-returns-exports", "true");

        if (Boolean.parseBoolean(Configuration.get(DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN, "true"))) {
            engineBuilder.option("js.nashorn-compat", "true");
        }

        return engineBuilder;
    }
}
