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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModule;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aggregates the editors of extension points, in registration order; the first editor of an id
 * wins.
 */
@Component
class EditorsAggregator {

    /** The extension point read when the request names none. */
    static final String DEFAULT_EXTENSION_POINT = "platform-editors";

    private static final Logger LOGGER = LoggerFactory.getLogger(EditorsAggregator.class);

    private static final List<String> EXPORTS = List.of("getEditor");

    private final ExtensionModuleReader reader;

    /**
     * Instantiates a new editors aggregator.
     *
     * @param reader the extension module reader
     */
    EditorsAggregator(ExtensionModuleReader reader) {
        this.reader = reader;
    }

    /**
     * Aggregates the editors of the given extension points.
     *
     * @param extensionPoints the extension points
     * @return the editors
     */
    List<Map<String, Object>> aggregate(List<String> extensionPoints) {
        List<Map<String, Object>> editors = new ArrayList<>();
        for (ExtensionModule module : reader.read(extensionPoints, EXPORTS)) {
            Map<String, Object> editor = module.content();
            Optional<Map<String, Object>> duplicate = editors.stream()
                                                             .filter(existing -> Objects.equals(existing.get("id"), editor.get("id")))
                                                             .findFirst();
            if (duplicate.isPresent()) {
                LOGGER.warn("Duplication at editor with id [{}] pointing to paths [{}] and [{}]: the latter is skipped.", editor.get("id"),
                        duplicate.get()
                                 .get("path"),
                        editor.get("path"));
            } else {
                editors.add(editor);
            }
        }
        return editors;
    }
}
