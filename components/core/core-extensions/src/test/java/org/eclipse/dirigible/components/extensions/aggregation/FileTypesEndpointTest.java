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

import static org.eclipse.dirigible.components.extensions.aggregation.Modules.json;
import static org.eclipse.dirigible.components.extensions.aggregation.Modules.script;
import static org.eclipse.dirigible.components.extensions.aggregation.Modules.stub;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The code editor's file types, merged from the modules of the file types extension point.
 */
@ExtendWith(MockitoExtension.class)
class FileTypesEndpointTest {

    @Mock
    private ExtensionModuleReader reader;

    @Test
    void merges_every_module_and_the_first_mapping_of_an_extension_wins() {
        stub(reader, FileTypesEndpoint.EXTENSION_POINT, json("{'.js': 'javascript', '.java': 'java'}"),
                script("getFileTypes", "{'.java': 'plaintext', '.kt': 'kotlin'}"));

        Map<String, Object> fileTypes = new FileTypesEndpoint(reader).fileTypes();

        assertEquals(Map.of(".js", "javascript", ".java", "java", ".kt", "kotlin"), fileTypes);
        assertEquals(List.of(".js", ".java", ".kt"), List.copyOf(fileTypes.keySet()), "the mappings keep the modules' order");
    }

    @Test
    void answers_an_empty_mapping_when_no_module_is_registered() {
        stub(reader, FileTypesEndpoint.EXTENSION_POINT);

        assertEquals(Map.of(), new FileTypesEndpoint(reader).fileTypes());
    }
}
