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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The view validation, de-duplication, focus and ordering rules.
 */
@ExtendWith(MockitoExtension.class)
class ViewsAggregatorTest {

    private static final String POINT = "test-views";

    @Mock
    private ExtensionModuleReader reader;

    @Test
    void skips_a_view_without_id_label_or_target() {
        stub(reader, POINT, json("{'label': 'No id', 'path': '/a'}"), json("{'id': 'no-label', 'path': '/b'}"),
                json("{'id': 'no-target', 'label': 'No target'}"), json("{'id': 'page', 'label': 'Page', 'path': '/c'}"),
                json("{'id': 'action', 'label': 'Action', 'endpoint': '/d'}"));

        assertEquals(List.of("action", "page"), values(aggregate(), "id"));
    }

    @Test
    void keeps_the_first_of_two_views_with_one_id() {
        stub(reader, POINT, json("{'id': 'v', 'label': 'First', 'path': '/a'}"), json("{'id': 'v', 'label': 'Second', 'path': '/b'}"));

        assertEquals(List.of("/a"), values(aggregate(), "path"));
    }

    @Test
    void sets_the_focus_by_region_unless_the_view_says_otherwise() {
        stub(reader, POINT, json("{'id': 'none', 'label': 'a', 'path': '/', 'autoFocusTab': true}"),
                json("{'id': 'center', 'label': 'b', 'path': '/', 'region': 'center'}"),
                json("{'id': 'bottom', 'label': 'c', 'path': '/', 'region': 'bottom', 'autoFocusTab': false}"),
                json("{'id': 'left', 'label': 'd', 'path': '/', 'region': 'left'}"),
                json("{'id': 'right', 'label': 'e', 'path': '/', 'region': 'right', 'autoFocusTab': true}"),
                json("{'id': 'other', 'label': 'f', 'path': '/', 'region': 'top'}"));

        List<Map<String, Object>> views = aggregate();

        // A view without a region never takes the focus, whatever it declares.
        assertEquals(Arrays.asList(false, true, false, false, true, null), values(views, "autoFocusTab"));
    }

    @Test
    void orders_by_order_then_label() {
        stub(reader, POINT, json("{'id': 'b', 'label': 'beta', 'path': '/'}"), json("{'id': 'a', 'label': 'Alpha', 'path': '/'}"),
                json("{'id': 'o', 'label': 'Omega', 'path': '/', 'order': 5}"));

        assertEquals(List.of("o", "a", "b"), values(aggregate(), "id"));
    }

    private List<Map<String, Object>> aggregate() {
        return new ViewsAggregator(reader).aggregate(List.of(POINT));
    }
}
