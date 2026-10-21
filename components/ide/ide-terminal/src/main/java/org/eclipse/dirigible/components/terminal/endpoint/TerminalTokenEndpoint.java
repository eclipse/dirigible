/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.terminal.endpoint;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The token the terminal page hands the terminal's websocket - always empty, since the websocket is
 * authenticated by the platform session - answered without running a script.
 *
 * <p>
 * It is served at the script URL that used to answer it, the one the bundled terminal client
 * requests. A literal mapping wins over the JavaScript endpoint's pattern, and the script itself is
 * gone. It answers whether or not the terminal itself is enabled, as the script did.
 */
@RestController
class TerminalTokenEndpoint {

    private static final Map<String, String> EMPTY_TOKEN = Map.of("token", "");

    /**
     * The terminal token.
     *
     * @return an empty token
     */
    @GetMapping(value = "/services/js/view-terminal/js/token.js", produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> token() {
        return EMPTY_TOKEN;
    }
}
