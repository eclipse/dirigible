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

import java.util.Map;

/**
 * Runs a script from the public registry, with a context of named objects bound for it.
 *
 * <p>
 * The platform features that hand control to a script a project declares - an asynchronous HTTP
 * callback, a websocket client or RabbitMQ handler, a workspace hook - depend on this interface
 * rather than on a script engine, so they start where none is deployed. The JavaScript engine
 * provides the implementation; without it, a consumer refuses a script with a
 * {@link ScriptEngineUnavailableException}.
 */
public interface ScriptExecutor {

    /**
     * Runs a script of a project.
     *
     * @param projectName the project the script belongs to
     * @param projectFilePath the path of the script inside the project
     * @param projectFilePathParam the rest of the request path after the script, or {@code null}
     * @param parameters the objects bound as the script's context, or {@code null}
     * @param debug whether the script waits for a debugger, where the engine supports one
     * @return what the script evaluates to
     */
    Object execute(String projectName, String projectFilePath, String projectFilePathParam, Map<Object, Object> parameters, boolean debug);

    /**
     * Runs a callback script given by its registry path.
     *
     * @param filePath the registry path of the script, its project first
     * @param parameters the objects bound as the script's context
     * @return what the script evaluates to
     */
    Object executeCallback(String filePath, Map<Object, Object> parameters);
}
