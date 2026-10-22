/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.modules;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.databind.json.JsonMapper;

/**
 * Serves a JSON extension module as the script that defines the global its own page reads.
 *
 * <p>
 * A view, editor or perspective page reads its descriptor - the object its extension module
 * contributes - from a global ({@code viewData}, {@code editorData}, {@code perspectiveData})
 * defined by a script that runs before the platform scripts. A {@code .json} module cannot be
 * included as a script, so the page includes
 * {@code /services/platform/extensions/modules/<module>?global=<name>} instead, and the module
 * stays the one source of its descriptor for both the extension aggregation and the page.
 *
 * <p>
 * Only a module registered by an extension the current user may see is served - the descriptors the
 * aggregation already answers - so no other file of the registry can be read through this endpoint.
 */
@RestController
class ExtensionModuleScriptEndpoint {

    /** The globals the platform's view, editor and perspective pages read their descriptor from. */
    private static final List<String> PAGE_GLOBALS = List.of("viewData", "editorData", "perspectiveData");

    private static final MediaType JAVASCRIPT = MediaType.parseMediaType("text/javascript;charset=UTF-8");

    private static final JsonMapper JSON = JsonMapper.builder()
                                                     .build();

    private final ExtensionModuleReader reader;

    /**
     * Instantiates a new extension module script endpoint.
     *
     * @param reader the extension module reader
     */
    ExtensionModuleScriptEndpoint(ExtensionModuleReader reader) {
        this.reader = reader;
    }

    @GetMapping("/services/platform/extensions/modules/{*module}")
    ResponseEntity<String> script(@PathVariable("module") String module, @RequestParam("global") String global) {
        // The script names one of the known page globals - never the request's own text.
        String pageGlobal = PAGE_GLOBALS.stream()
                                        .filter(global::equals)
                                        .findFirst()
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "The global must be one of " + PAGE_GLOBALS + "."));
        ExtensionModule descriptor = reader.readRegisteredJsonModule(module)
                                           .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                   "No JSON extension module is registered at this path."));
        // Every answer is the content of a published module filtered by the user's roles, never an asset.
        return ResponseEntity.ok()
                             .header(HttpHeaders.CACHE_CONTROL, "private, no-cache")
                             .contentType(JAVASCRIPT)
                             .body("const " + pageGlobal + " = " + JSON.writeValueAsString(descriptor.content()) + ";\n");
    }
}
