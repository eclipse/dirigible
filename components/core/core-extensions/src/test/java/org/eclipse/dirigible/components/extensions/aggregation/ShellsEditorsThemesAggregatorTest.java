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
import static org.eclipse.dirigible.components.extensions.aggregation.Modules.stub;
import static org.eclipse.dirigible.components.extensions.aggregation.Modules.values;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The shell, editor and theme rules.
 */
@ExtendWith(MockitoExtension.class)
class ShellsEditorsThemesAggregatorTest {

    private static final String POINT = "test-point";

    @Mock
    private ExtensionModuleReader reader;

    @Test
    void shells_need_id_label_and_path_and_are_ordered() {
        stub(reader, POINT, json("{'label': 'No id', 'path': '/a'}"), json("{'id': 'no-label', 'path': '/b'}"),
                json("{'id': 'no-path', 'label': 'No path'}"), json("{'id': 'ide', 'label': 'Workbench', 'path': '/ide', 'order': 40}"),
                json("{'id': 'app', 'label': 'Applications', 'path': '/app', 'order': 10}"),
                json("{'id': 'app', 'label': 'Duplicate', 'path': '/dup', 'order': 1}"));

        assertEquals(List.of("app", "ide"), values(new ShellsAggregator(reader).aggregate(List.of(POINT)), "id"));
    }

    @Test
    void editors_keep_registration_order_and_the_first_of_an_id() {
        stub(reader, POINT, json("{'id': 'monaco', 'path': '/b'}"), json("{'id': 'csv', 'path': '/a'}"),
                json("{'id': 'monaco', 'path': '/c'}"));

        assertEquals(List.of("/b", "/a"), values(new EditorsAggregator(reader).aggregate(List.of(POINT)), "path"));
    }

    @Test
    void themes_are_ordered_by_order_then_exact_name() {
        stub(reader, POINT, json("{'id': 'b', 'name': 'b-theme'}"), json("{'id': 'B', 'name': 'B-theme'}"),
                json("{'id': 'first', 'name': 'z', 'order': 1}"));

        assertEquals(List.of("first", "B", "b"), values(new ThemesAggregator(reader).aggregate(List.of(POINT)), "id"));
    }
}
