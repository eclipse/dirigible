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

import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModule;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.springframework.stereotype.Component;

/**
 * Aggregates the themes of extension points, ordered by {@code order} and then by {@code name}.
 */
@Component
class ThemesAggregator {

    /** The extension point read when the request names none. */
    static final String DEFAULT_EXTENSION_POINT = "platform-themes";

    private static final List<String> EXPORTS = List.of("getTheme");

    private final ExtensionModuleReader reader;

    /**
     * Instantiates a new themes aggregator.
     *
     * @param reader the extension module reader
     */
    ThemesAggregator(ExtensionModuleReader reader) {
        this.reader = reader;
    }

    /**
     * Aggregates the themes of the given extension points.
     *
     * @param extensionPoints the extension points
     * @return the themes, ordered
     */
    List<Map<String, Object>> aggregate(List<String> extensionPoints) {
        return reader.read(extensionPoints, EXPORTS)
                     .stream()
                     .map(ExtensionModule::content)
                     .sorted(ExtensionOrdering.byOrderThenExactText("name"))
                     .toList();
    }
}
