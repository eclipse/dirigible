/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.workspace.deprecation;

import java.util.List;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;

/**
 * Tells the IDE which published projects use server-side JavaScript or TypeScript, so its shell can
 * show the deprecation banner while there are any.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_IDE + "javascript-deprecation")
@RolesAllowed({"ADMINISTRATOR", "DEVELOPER"})
class ServerSideScriptDeprecationEndpoint {

    private final ServerSideScriptDeprecation deprecation;

    ServerSideScriptDeprecationEndpoint(ServerSideScriptDeprecation deprecation) {
        this.deprecation = deprecation;
    }

    /**
     * The projects with a deprecation entry in the Problems view, and the migration guide.
     *
     * @return the notice, with no projects on an instance where none uses server-side scripts
     */
    @GetMapping
    ResponseEntity<Notice> getNotice() {
        return ResponseEntity.ok(new Notice(deprecation.flaggedProjects(), ServerSideScriptDeprecation.MIGRATION_GUIDE));
    }

    /**
     * The deprecation notice.
     *
     * @param projects the projects that use server-side JavaScript or TypeScript
     * @param guide the migration guide
     */
    record Notice(List<String> projects, String guide) {
    }
}
