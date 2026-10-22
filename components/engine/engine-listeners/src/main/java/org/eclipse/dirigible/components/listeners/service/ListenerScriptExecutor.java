/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.service;

/**
 * Runs the script handler of a {@code .listener} artefact: its {@code onMessage} export for every
 * message received, its {@code onError} export for a failure of the connection.
 *
 * <p>
 * The JavaScript engine provides the implementation. Where none is deployed a listener is not
 * subscribed: the listener synchronizer registers it {@code FAILED}, with the missing engine as the
 * reason. Client-Java listeners do not come through here.
 */
public interface ListenerScriptExecutor {

    /**
     * Calls the handler's {@code onMessage} export.
     *
     * @param handlerPath the registry path of the handler, relative to the public registry
     * @param message the text of the message
     */
    void onMessage(String handlerPath, String message);

    /**
     * Calls the handler's {@code onError} export.
     *
     * @param handlerPath the registry path of the handler, relative to the public registry
     * @param errorMessage the message of the connection's failure
     */
    void onError(String handlerPath, String errorMessage);
}
