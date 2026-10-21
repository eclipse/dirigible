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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The main menu: contributed menus, the derived Window menu, the Help menu last, and an item that
 * requires a project only while that project is published.
 */
@ExtendWith(MockitoExtension.class)
class MenusAggregatorTest {

    @Mock
    private ExtensionModuleReader reader;

    @Mock
    private IRepository repository;

    @Test
    void pulls_the_help_menu_to_the_end_after_the_window_menu() {
        stub(reader, "menus", json("{'id': 'help', 'systemMenu': true, 'label': 'Help'}"), json("{'id': 'file', 'label': 'File'}"));
        stubWindowSources();

        List<Map<String, Object>> menus = aggregate();

        assertEquals(List.of("file", "window", "help"), values(menus, "id"));
    }

    @Test
    void ends_with_an_empty_help_slot_when_no_help_menu_is_contributed() {
        stub(reader, "menus", json("{'id': 'file', 'label': 'File'}"));
        stubWindowSources();

        List<Map<String, Object>> menus = aggregate();

        assertEquals(3, menus.size());
        assertNull(menus.get(2));
    }

    @Test
    @SuppressWarnings("unchecked")
    void derives_the_window_menu_from_shells_perspectives_utilities_and_views() {
        stub(reader, "menus");
        stubWindowSources();

        Map<String, Object> window = (Map<String, Object>) aggregate().get(0)
                                                                      .get("menu");
        List<Map<String, Object>> groups = (List<Map<String, Object>>) window.get("items");
        List<Map<String, Object>> shellItems = (List<Map<String, Object>>) groups.get(0)
                                                                                 .get("items");
        List<Map<String, Object>> perspectiveItems = (List<Map<String, Object>>) groups.get(1)
                                                                                       .get("items");
        List<Map<String, Object>> viewItems = (List<Map<String, Object>>) groups.get(2)
                                                                                .get("items");

        assertEquals(List.of("Shells", "Perspectives", "Views"), values(groups, "label"));
        assertEquals(Map.of("id", "ide", "label", "Workbench", "link", "/ide", "action", "open"), shellItems.get(0));
        assertEquals(List.of("workbench", "settings"), values(perspectiveItems, "id"));
        // The separator closes the perspectives, before the utilities.
        assertEquals(Arrays.asList(true, null), values(perspectiveItems, "separator"));
        assertEquals(Map.of("id", "console", "translation", Map.of("key", "console"), "label", "Console", "action", "openView"),
                viewItems.get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shows_an_item_requiring_a_project_only_while_the_project_is_published() {
        stub(reader, "menus", json("""
                {'id': 'help', 'systemMenu': true, 'menu': {'label': 'Help', 'items': [
                    {'label': 'Portal'},
                    {'label': 'OpenAPI', 'requiresProject': 'view-swagger'},
                    {'label': 'More', 'items': [{'label': 'Removed', 'requiresProject': 'not-published'}, {'label': 'Kept'}]}
                ]}}"""));
        stubWindowSources();
        when(repository.hasResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/view-swagger/project.json")).thenReturn(true);
        when(repository.hasResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/not-published/project.json")).thenReturn(false);

        Map<String, Object> help = (Map<String, Object>) aggregate().get(1)
                                                                    .get("menu");
        List<Map<String, Object>> items = (List<Map<String, Object>>) help.get("items");
        List<Map<String, Object>> nested = (List<Map<String, Object>>) items.get(2)
                                                                            .get("items");

        assertEquals(List.of("Portal", "OpenAPI", "More"), values(items, "label"));
        assertEquals(List.of("Kept"), values(nested, "label"));
        // The shell receives the item as it renders it: the condition is the aggregator's concern.
        assertEquals(Map.of("label", "OpenAPI"), items.get(1));
    }

    private void stubWindowSources() {
        stub(reader, "perspectives", json("{'id': 'workbench', 'label': 'Workbench'}"),
                json("{'id': 'settings', 'label': 'Settings', 'isUtility': true}"));
        stub(reader, "views", json("{'id': 'console', 'label': 'Console', 'path': '/c', 'translation': {'key': 'console'}}"));
        stub(reader, "shells", json("{'id': 'ide', 'label': 'Workbench', 'path': '/ide'}"));
    }

    private List<Map<String, Object>> aggregate() {
        MenusAggregator menus = new MenusAggregator(reader, repository, new PerspectivesAggregator(reader), new ViewsAggregator(reader),
                new ShellsAggregator(reader));
        return menus.aggregate(List.of("menus"), List.of("perspectives"), List.of("views"), List.of("shells"));
    }
}
