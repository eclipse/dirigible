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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModule;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Builds the modules a mocked {@link ExtensionModuleReader} hands an aggregator.
 */
final class Modules {

    private static final JsonMapper JSON = JsonMapper.builder()
                                                     .build();

    private Modules() {}

    /** A JSON module with the given content. */
    static ExtensionModule json(String content) {
        return new ExtensionModule("test/module.json", null, object(content));
    }

    /** A script module whose given export returned the given content. */
    static ExtensionModule script(String exportName, String content) {
        return new ExtensionModule("test/module.js", exportName, object(content));
    }

    /** Parses a JSON object into the mutable map the reader produces. */
    static Map<String, Object> object(String content) {
        return JSON.readValue(content.replace('\'', '"'), new TypeReference<Map<String, Object>>() {});
    }

    /** Makes the reader answer the given extension point with the given modules. */
    static void stub(ExtensionModuleReader reader, String extensionPoint, ExtensionModule... modules) {
        when(reader.read(eq(List.of(extensionPoint)), anyList())).thenReturn(Arrays.asList(modules));
    }

    /** The values of a property across contributions. */
    static List<Object> values(List<Map<String, Object>> contributions, String property) {
        return contributions.stream()
                            .map(contribution -> contribution.get(property))
                            .toList();
    }
}
