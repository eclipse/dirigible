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
import static org.eclipse.dirigible.components.extensions.aggregation.Modules.values;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The perspective placement rules, including the #6646 cases.
 */
@ExtendWith(MockitoExtension.class)
class PerspectivesAggregatorTest {

    private static final String POINT = "test-perspectives";

    @Mock
    private ExtensionModuleReader reader;

    @Test
    void places_a_perspective_in_the_group_its_groupId_names() {
        stub(reader, POINT, json("{'id': 'sales', 'label': 'Sales', 'items': []}"), json("{'id': 'hr', 'label': 'HR', 'items': []}"),
                json("{'id': 'orders', 'label': 'Orders', 'groupId': 'sales'}"));

        PerspectivesAggregator.SidebarConfiguration sidebar = aggregate();

        assertEquals(List.of("hr", "sales"), values(sidebar.perspectives(), "id"));
        assertEquals(List.of("orders"), values(items(sidebar, "sales"), "id"));
        assertEquals(List.of(), items(sidebar, "hr"));
    }

    @Test
    void adopts_an_unmatched_groupId_into_the_default_group() {
        stub(reader, POINT, json("{'id': 'personal', 'label': 'Personal', 'isDefault': true, 'items': []}"),
                json("{'id': 'undefined-group', 'label': 'Unknown', 'items': []}"),
                json("{'id': 'stale', 'label': 'Stale', 'groupId': 'renamed-away'}"));

        PerspectivesAggregator.SidebarConfiguration sidebar = aggregate();

        assertEquals(List.of("stale"), values(items(sidebar, "personal"), "id"));
        assertEquals(List.of(), items(sidebar, "undefined-group"));
    }

    @Test
    void sends_an_unmatched_groupId_to_the_catch_all_where_no_default_is_declared() {
        stub(reader, POINT, json("{'id': 'undefined-group', 'label': 'Unknown', 'items': []}"),
                json("{'id': 'stale', 'label': 'Stale', 'groupId': 'renamed-away'}"));

        assertEquals(List.of("stale"), values(items(aggregate(), "undefined-group"), "id"));
    }

    @Test
    void hands_a_perspective_with_nowhere_to_go_back_standalone() {
        stub(reader, POINT, json("{'id': 'sales', 'label': 'Sales', 'items': []}"),
                json("{'id': 'stale', 'label': 'Stale', 'groupId': 'renamed-away'}"), json("{'id': 'loose', 'label': 'Loose'}"));

        PerspectivesAggregator.SidebarConfiguration sidebar = aggregate();

        assertEquals(List.of("loose", "sales", "stale"), values(sidebar.perspectives(), "id"));
        assertEquals(List.of(), items(sidebar, "sales"));
    }

    @Test
    void places_a_perspective_without_groupId_in_the_default_group_of_a_single_group_shell() {
        stub(reader, POINT, json("{'id': 'partner', 'label': 'Partner', 'isDefault': true, 'items': []}"),
                json("{'id': 'tickets', 'label': 'Tickets'}"));

        PerspectivesAggregator.SidebarConfiguration sidebar = aggregate();

        assertEquals(List.of("partner"), values(sidebar.perspectives(), "id"));
        assertEquals(List.of("tickets"), values(items(sidebar, "partner"), "id"));
    }

    @Test
    void separates_utility_perspectives_by_flag_or_by_export() {
        stub(reader, POINT, json("{'id': 'settings', 'label': 'Settings', 'isUtility': true}"),
                script("getUtilityPerspective", "{'id': 'legacy', 'label': 'Legacy'}"), json("{'id': 'main', 'label': 'Main'}"));

        PerspectivesAggregator.SidebarConfiguration sidebar = aggregate();

        assertEquals(List.of("legacy", "settings"), values(sidebar.utilities(), "id"));
        assertEquals(Boolean.TRUE, sidebar.utilities()
                                          .get(0)
                                          .get("isUtility"));
        assertEquals(List.of("main"), values(sidebar.perspectives(), "id"));
    }

    @Test
    void mixes_json_and_script_modules_on_one_point() {
        stub(reader, POINT, json("{'id': 'sales', 'label': 'Sales', 'items': []}"),
                script("getPerspective", "{'id': 'legacy', 'label': 'Legacy', 'groupId': 'sales'}"),
                json("{'id': 'current', 'label': 'Current', 'groupId': 'sales'}"));

        assertEquals(List.of("current", "legacy"), values(items(aggregate(), "sales"), "id"));
    }

    @Test
    void keeps_the_first_of_two_perspectives_with_one_id() {
        stub(reader, POINT, json("{'id': 'orders', 'label': 'First'}"), json("{'id': 'orders', 'label': 'Second'}"));

        assertEquals(List.of("First"), values(aggregate().perspectives(), "label"));
    }

    @Test
    void orders_by_order_then_label() {
        stub(reader, POINT, json("{'id': 'g', 'label': 'G', 'items': []}"), json("{'id': 'c', 'label': 'charlie', 'groupId': 'g'}"),
                json("{'id': 'b', 'label': 'Bravo', 'groupId': 'g'}"), json("{'id': 'z', 'label': 'Zulu', 'order': 2, 'groupId': 'g'}"),
                json("{'id': 'y', 'label': 'Yankee', 'order': '1', 'groupId': 'g'}"));

        assertEquals(List.of("y", "z", "b", "c"), values(items(aggregate(), "g"), "id"));
    }

    private PerspectivesAggregator.SidebarConfiguration aggregate() {
        return new PerspectivesAggregator(reader).aggregate(List.of(POINT));
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> items(PerspectivesAggregator.SidebarConfiguration sidebar, String groupId) {
        return sidebar.perspectives()
                      .stream()
                      .filter(group -> groupId.equals(group.get("id")))
                      .map(group -> (List<Map<String, Object>>) group.get("items"))
                      .findFirst()
                      .orElseThrow();
    }
}
