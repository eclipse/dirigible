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

import static org.eclipse.dirigible.components.extensions.aggregation.Contributions.putIfPresent;
import static org.eclipse.dirigible.components.extensions.aggregation.Contributions.truthy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModule;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.springframework.stereotype.Component;

/**
 * Aggregates the main menu of the IDE: the contributed menus, then the derived Window menu (the
 * shells, the perspectives with the utility perspectives after a separator, and the views), then
 * the Help system menu, which is pulled to the end wherever it was contributed.
 *
 * <p>
 * A contributed item that names a project in {@code requiresProject} is shown only while that
 * project is published - how the Help menu offers OpenAPI only where the Swagger view is deployed.
 */
@Component
class MenusAggregator {

    /** The extension point read when the request names none. */
    static final String DEFAULT_EXTENSION_POINT = "platform-menus";

    private static final List<String> EXPORTS = List.of("getMenu");

    private static final String ID = "id";

    private static final String ITEMS = "items";

    private static final String LABEL = "label";

    private static final String TRANSLATION = "translation";

    private static final String REQUIRES_PROJECT = "requiresProject";

    private final ExtensionModuleReader reader;

    private final IRepository repository;

    private final PerspectivesAggregator perspectives;

    private final ViewsAggregator views;

    private final ShellsAggregator shells;

    /**
     * Instantiates a new menus aggregator.
     *
     * @param reader the extension module reader
     * @param repository the repository, read for the projects items require
     * @param perspectives the perspectives aggregator
     * @param views the views aggregator
     * @param shells the shells aggregator
     */
    MenusAggregator(ExtensionModuleReader reader, IRepository repository, PerspectivesAggregator perspectives, ViewsAggregator views,
            ShellsAggregator shells) {
        this.reader = reader;
        this.repository = repository;
        this.perspectives = perspectives;
        this.views = views;
        this.shells = shells;
    }

    /**
     * Aggregates the main menu.
     *
     * @param extensionPoints the menu extension points
     * @param perspectiveExtensionPoints the perspective extension points the Window menu lists
     * @param viewExtensionPoints the view extension points the Window menu lists
     * @param shellExtensionPoints the shell extension points the Window menu lists
     * @return the menus; the last one is the Help menu, {@code null} when none was contributed
     */
    List<Map<String, Object>> aggregate(List<String> extensionPoints, List<String> perspectiveExtensionPoints,
            List<String> viewExtensionPoints, List<String> shellExtensionPoints) {
        List<Map<String, Object>> menus = new ArrayList<>();
        Map<String, Object> helpMenu = null;
        for (ExtensionModule module : reader.read(extensionPoints, EXPORTS)) {
            Map<String, Object> menu = module.content();
            removeUnavailableItems(menu);
            if (truthy(menu, "systemMenu") && "help".equals(menu.get(ID))) {
                helpMenu = menu;
            } else {
                menus.add(menu);
            }
        }
        menus.add(windowMenu(perspectiveExtensionPoints, viewExtensionPoints, shellExtensionPoints));
        // Added even when absent - the menu has always ended with this slot, null or not.
        menus.add(helpMenu);
        return menus;
    }

    /**
     * Removes, at every depth of a contributed menu, the items whose required project is not published,
     * and the evaluated condition from the items kept, so the shell receives the menu it renders.
     */
    private void removeUnavailableItems(Map<?, ?> node) {
        if (node.get("menu") instanceof Map<?, ?> menu) {
            removeUnavailableItems(menu);
        }
        if (node.get(ITEMS) instanceof List<?> items) {
            items.removeIf(item -> item instanceof Map<?, ?> entry && !isAvailable(entry));
            for (Object item : items) {
                if (item instanceof Map<?, ?> entry) {
                    entry.remove(REQUIRES_PROJECT);
                    removeUnavailableItems(entry);
                }
            }
        }
    }

    private boolean isAvailable(Map<?, ?> item) {
        return !(item.get(REQUIRES_PROJECT) instanceof String project)
                || repository.hasResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + project + "/project.json");
    }

    private Map<String, Object> windowMenu(List<String> perspectiveExtensionPoints, List<String> viewExtensionPoints,
            List<String> shellExtensionPoints) {
        List<Map<String, Object>> shellItems = new ArrayList<>();
        for (Map<String, Object> shell : shells.aggregate(shellExtensionPoints)) {
            Map<String, Object> item = item(shell, "open");
            item.put("link", shell.get("path"));
            shellItems.add(item);
        }

        PerspectivesAggregator.SidebarConfiguration sidebar = perspectives.aggregate(perspectiveExtensionPoints);
        List<Map<String, Object>> perspectiveItems = new ArrayList<>();
        for (Map<String, Object> perspective : sidebar.perspectives()) {
            perspectiveItems.add(item(perspective, "showPerspective"));
        }
        if (!perspectiveItems.isEmpty()) {
            perspectiveItems.get(perspectiveItems.size() - 1)
                            .put("separator", Boolean.TRUE);
        }
        for (Map<String, Object> utility : sidebar.utilities()) {
            perspectiveItems.add(item(utility, "showPerspective"));
        }

        List<Map<String, Object>> viewItems = new ArrayList<>();
        for (Map<String, Object> view : views.aggregate(viewExtensionPoints)) {
            viewItems.add(item(view, "openView"));
        }

        Map<String, Object> menu = new LinkedHashMap<>();
        menu.put(TRANSLATION, translation("common:window"));
        menu.put(LABEL, "Window");
        menu.put(ITEMS, List.of(group("common:shells", "Shells", shellItems),
                group("common:perspectives", "Perspectives", perspectiveItems), group("common:views", "Views", viewItems)));

        Map<String, Object> windowMenu = new LinkedHashMap<>();
        windowMenu.put("systemMenu", Boolean.TRUE);
        windowMenu.put(ID, "window");
        windowMenu.put("menu", menu);
        return windowMenu;
    }

    private static Map<String, Object> item(Map<String, Object> contribution, String action) {
        Map<String, Object> item = new LinkedHashMap<>();
        putIfPresent(item, ID, contribution.get(ID));
        putIfPresent(item, TRANSLATION, contribution.get(TRANSLATION));
        putIfPresent(item, LABEL, contribution.get(LABEL));
        item.put("action", action);
        return item;
    }

    private static Map<String, Object> group(String translationKey, String label, List<Map<String, Object>> items) {
        Map<String, Object> group = new LinkedHashMap<>();
        group.put(TRANSLATION, translation(translationKey));
        group.put(LABEL, label);
        group.put(ITEMS, items);
        return group;
    }

    private static Map<String, Object> translation(String key) {
        return Map.of("key", key);
    }
}
