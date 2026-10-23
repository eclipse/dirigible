/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.script;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Whether the JavaScript engine is deployed on this instance.
 *
 * <p>
 * The engine is the only provider of a {@link ScriptExecutor}, so it is deployed exactly when that
 * bean exists. The IDE offers what only runs there - the editor's script typings, the JavaScript
 * debugger, the TypeScript and ESM file templates - only where this answers {@code true}: a
 * contribution (a view, a template) declaring {@value #REQUIRES_SCRIPT_ENGINE} is left out
 * elsewhere, and the pages read the same answer from the branding script. Where such a template has
 * a JSON or Java alternative, the alternative declares {@value #WITHOUT_SCRIPT_ENGINE} and is
 * offered in its place.
 */
@Component
public class ScriptEngineAvailability {

    /** The property with which a contribution declares that it is offered only with the engine. */
    public static final String REQUIRES_SCRIPT_ENGINE = "requiresScriptEngine";

    /**
     * The property with which a contribution declares that it is offered only without the engine: the
     * alternative of one that requires it.
     */
    public static final String WITHOUT_SCRIPT_ENGINE = "withoutScriptEngine";

    private final ObjectProvider<ScriptExecutor> scriptExecutor;

    /**
     * Instantiates a new script engine availability.
     *
     * @param scriptExecutor the script executor, absent where no engine is deployed
     */
    ScriptEngineAvailability(ObjectProvider<ScriptExecutor> scriptExecutor) {
        this.scriptExecutor = scriptExecutor;
    }

    /**
     * Whether the JavaScript engine is deployed.
     *
     * @return {@code true} when it is
     */
    public boolean isAvailable() {
        return scriptExecutor.getIfAvailable() != null;
    }
}
