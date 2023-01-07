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
package org.eclipse.dirigible.graalium.core.graal;

import java.io.PrintStream;

import org.eclipse.dirigible.graalium.core.graal.configuration.Configuration;
import org.graalvm.polyglot.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GraalJSEngineCreator.
 */
public class GraalJSEngineCreator {
	
	private static Logger sysOutLogger = LoggerFactory.getLogger("app.out");
	private static Logger sysErrLogger = LoggerFactory.getLogger("app.err");
	
    /** The engine. */
    private static Engine engine = null;
    
    /** The debuggable engine. */
    private static Engine debuggableEngine = null;

    /**
     * Gets the or create engine.
     *
     * @return the or create engine
     */
    public static Engine getOrCreateEngine() {
        if (engine == null) {
            engine = getDefaultEngineBuilder().build();
        }

        return engine;
    }

    /**
     * Gets the or create debuggable engine.
     *
     * @return the or create debuggable engine
     */
    public static Engine getOrCreateDebuggableEngine() {
        if (debuggableEngine == null) {
            debuggableEngine = getDefaultEngineBuilder()
                    .option("inspect", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_PORT", "8081"))
                    .option("inspect.Secure", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_SECURE", "false"))
                    .option("inspect.Suspend", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_SUSPEND", "true"))
                    .option("inspect.Path", Configuration.get("DIRIGIBLE_GRAALIUM_DEBUG_PATH", "debug"))
                    .build();
        }

        return debuggableEngine;
    }

    /**
     * Gets the default engine builder.
     *
     * @return the default engine builder
     */
    private static Engine.Builder getDefaultEngineBuilder() {
        return Engine.newBuilder()
                .allowExperimentalOptions(true)
                .option("js.ecmascript-version", "staging")
                .option("engine.WarnInterpreterOnly", "false")
                .option("js.esm-eval-returns-exports", "true")
                .out(new PrintStream(new GraalJSLogging(sysOutLogger, false), true))
                .err(new PrintStream(new GraalJSLogging(sysErrLogger, true), true));
    }
}
