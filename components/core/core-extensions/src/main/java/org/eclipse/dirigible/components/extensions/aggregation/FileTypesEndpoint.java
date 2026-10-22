/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.aggregation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModule;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The code editor's file types - file extension to editor language - merged from the modules
 * registered on {@value #EXTENSION_POINT}, answered without running a script.
 *
 * <p>
 * It is served at the script URL that used to answer it, so the editor keeps calling what it
 * called. A literal mapping wins over the JavaScript endpoint's pattern, and the script itself is
 * gone.
 */
@RestController
class FileTypesEndpoint {

    /** The extension point the file type modules are registered on. */
    static final String EXTENSION_POINT = "platform-file-types";

    private static final List<String> EXPORTS = List.of("getFileTypes");

    private final ExtensionModuleReader reader;

    /**
     * Instantiates a new file types endpoint.
     *
     * @param reader the extension module reader
     */
    FileTypesEndpoint(ExtensionModuleReader reader) {
        this.reader = reader;
    }

    /**
     * The file types of every module, in registration order. A file extension keeps the language of the
     * first module that maps it, and the editor relies on the order of the mappings, the last matching
     * suffix winning.
     *
     * @return file extension to editor language
     */
    @GetMapping(value = "/services/js/editor-monaco/api/fileTypes.js", produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> fileTypes() {
        Map<String, Object> fileTypes = new LinkedHashMap<>();
        for (ExtensionModule module : reader.read(List.of(EXTENSION_POINT), EXPORTS)) {
            module.content()
                  .forEach(fileTypes::putIfAbsent);
        }
        return fileTypes;
    }
}
