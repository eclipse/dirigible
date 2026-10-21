/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.websockets.service;

import java.util.Map;

/**
 * Dispatches the events of a {@code .websocket} artefact's session to its script handler's
 * {@code onOpen}, {@code onMessage}, {@code onError} and {@code onClose} exports.
 *
 * <p>
 * The JavaScript engine provides the implementation. Where none is deployed the websocket
 * synchronizer registers a script websocket {@code FAILED}, with the missing engine as the reason.
 * Client-Java {@code @Websocket} handlers do not come through here.
 */
public interface WebsocketScriptDispatcher {

    /**
     * Calls the handler's {@code onOpen} export.
     *
     * @param handler the registry path of the handler, relative to the public registry
     * @param context the objects bound as the script's context
     */
    void onOpen(String handler, Map<Object, Object> context);

    /**
     * Calls the handler's {@code onMessage} export with the context's {@code message} and {@code from}.
     *
     * @param handler the registry path of the handler, relative to the public registry
     * @param context the objects bound as the script's context
     * @return the reply the export returned, as text
     */
    String onMessage(String handler, Map<Object, Object> context);

    /**
     * Calls the handler's {@code onError} export with the context's {@code error}.
     *
     * @param handler the registry path of the handler, relative to the public registry
     * @param context the objects bound as the script's context
     */
    void onError(String handler, Map<Object, Object> context);

    /**
     * Calls the handler's {@code onClose} export.
     *
     * @param handler the registry path of the handler, relative to the public registry
     * @param context the objects bound as the script's context
     */
    void onClose(String handler, Map<Object, Object> context);
}
