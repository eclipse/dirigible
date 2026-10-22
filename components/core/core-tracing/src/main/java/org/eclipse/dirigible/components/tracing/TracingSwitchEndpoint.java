/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tracing;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;

/**
 * The switch of the Tracing perspective: whether task tracing is on, and turning it over - answered
 * without running a script.
 *
 * <p>
 * It is served at the script URL that used to answer it, so the perspective keeps calling what it
 * called. A literal mapping wins over the JavaScript endpoint's pattern, and the script itself is
 * gone. The answer is the plain text {@code true} or {@code false}, as the script printed it.
 */
@RestController
@RolesAllowed({"ADMINISTRATOR", "DEVELOPER", "OPERATOR"})
class TracingSwitchEndpoint {

    private static final String SWITCH = "/services/js/perspective-tracing/service/enable-tracing.js";

    private final TaskStateService taskStateService;

    /**
     * Instantiates a new tracing switch endpoint.
     *
     * @param taskStateService the task state service
     */
    TracingSwitchEndpoint(TaskStateService taskStateService) {
        this.taskStateService = taskStateService;
    }

    /**
     * Whether task tracing is on.
     *
     * @return {@code true} or {@code false}
     */
    @GetMapping(value = SWITCH, produces = MediaType.TEXT_PLAIN_VALUE)
    String isEnabled() {
        return String.valueOf(taskStateService.isTracingEnabled());
    }

    /**
     * Turns task tracing on when it is off, and off when it is on.
     *
     * @return whether task tracing is now on
     */
    @PostMapping(value = SWITCH, produces = MediaType.TEXT_PLAIN_VALUE)
    String toggle() {
        if (taskStateService.isTracingEnabled()) {
            taskStateService.disableTracing();
        } else {
            taskStateService.enableTracing();
        }
        return isEnabled();
    }
}
