/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.debug.java.endpoint;

import jakarta.annotation.security.RolesAllowed;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.ide.debug.java.JavaDebugManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Exposes debugger configuration values (currently the default JDWP port) to the browser IDE, or
 * answers 503 while the debugger is unavailable.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_IDE + "java-debug")
@RolesAllowed({"ADMINISTRATOR", "DEVELOPER"})
public class JavaDebugConfigEndpoint {

    private final JavaDebugManager manager;

    public JavaDebugConfigEndpoint(JavaDebugManager manager) {
        this.manager = manager;
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        if (!manager.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, JavaDebugManager.UNAVAILABLE_REASON);
        }
        int jdwpPort = DirigibleConfig.JAVA_DEBUG_JDWP_PORT.getIntValue();
        return ResponseEntity.ok(Map.of("jdwpPort", jdwpPort));
    }
}
