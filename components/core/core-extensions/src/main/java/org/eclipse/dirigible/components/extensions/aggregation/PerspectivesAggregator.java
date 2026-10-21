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

import static org.eclipse.dirigible.components.extensions.aggregation.Contributions.truthy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModule;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aggregates the perspectives of extension points into the sidebar configuration of a shell: the
 * navigation groups with their perspectives placed in them, and the utility perspectives.
 *
 * <p>
 * A contribution is a utility perspective when it carries {@code isUtility} (a script module's
 * {@code getUtilityPerspective} export sets it), a navigation group when it carries {@code items},
 * and a perspective otherwise. A perspective goes to the group its {@code groupId} names; one
 * naming no group goes to the default group of the extension point (the group declaring
 * {@code isDefault}), or failing that to the platform's {@code undefined-group} catch-all; one
 * declaring no {@code groupId} goes to the default group. A perspective with nowhere to go is
 * handed back standalone, never dropped (#6646).
 */
@Component
class PerspectivesAggregator {

    /** The extension point read when the request names none. */
    static final String DEFAULT_EXTENSION_POINT = "platform-perspectives";

    private static final Logger LOGGER = LoggerFactory.getLogger(PerspectivesAggregator.class);

    private static final List<String> EXPORTS = List.of("getPerspectiveGroup", "getUtilityPerspective", "getPerspective");

    private static final String UTILITY_EXPORT = "getUtilityPerspective";

    private static final String UNKNOWN_GROUP_ID = "undefined-group";

    private static final String ID = "id";

    private static final String ITEMS = "items";

    private static final String IS_UTILITY = "isUtility";

    private static final Comparator<Map<String, Object>> ORDERING = ExtensionOrdering.byOrderThenText("label");

    private final ExtensionModuleReader reader;

    /**
     * Instantiates a new perspectives aggregator.
     *
     * @param reader the extension module reader
     */
    PerspectivesAggregator(ExtensionModuleReader reader) {
        this.reader = reader;
    }

    /**
     * Aggregates the perspectives of the given extension points.
     *
     * @param extensionPoints the extension points
     * @return the sidebar configuration, with {@code perspectives} and {@code utilities}
     */
    SidebarConfiguration aggregate(List<String> extensionPoints) {
        List<Map<String, Object>> groupsAndStandalone = new ArrayList<>();
        List<Map<String, Object>> utilities = new ArrayList<>();
        List<Map<String, Object>> perspectives = new ArrayList<>();
        Set<Object> ids = new HashSet<>();

        for (ExtensionModule module : reader.read(extensionPoints, EXPORTS)) {
            Map<String, Object> perspective = module.content();
            if (UTILITY_EXPORT.equals(module.exportName())) {
                perspective.put(IS_UTILITY, Boolean.TRUE);
            }
            if (!ids.add(perspective.get(ID))) {
                LOGGER.warn("Perspective with non-unique id [{}] with path [{}] is skipped.", perspective.get(ID), perspective.get("path"));
                continue;
            }
            if (truthy(perspective, IS_UTILITY)) {
                utilities.add(perspective);
            } else if (truthy(perspective, ITEMS)) {
                groupsAndStandalone.add(perspective);
            } else {
                perspectives.add(perspective);
            }
        }

        place(perspectives, groupsAndStandalone);
        groupsAndStandalone.sort(ORDERING);
        utilities.sort(ORDERING);
        return new SidebarConfiguration(groupsAndStandalone, utilities);
    }

    private static void place(List<Map<String, Object>> perspectives, List<Map<String, Object>> groupsAndStandalone) {
        // The declared groups, taken before any perspective is placed, so that a perspective handed
        // back standalone can never be mistaken for a group.
        List<Map<String, Object>> groups = groupsAndStandalone.stream()
                                                              .filter(group -> group.get(ITEMS) instanceof List)
                                                              .toList();
        // A group may declare itself the default of its extension point: a shell with a single
        // well-known group owns the placement, so its perspectives need not repeat the group id, and a
        // rename of that group can never invalidate an already-generated module.
        Map<String, Object> defaultGroup = groups.stream()
                                                 .filter(group -> truthy(group, "isDefault"))
                                                 .findFirst()
                                                 .orElse(null);
        // The platform's catch-all group, used only for a groupId that matches nothing.
        Map<String, Object> unknownGroup = groups.stream()
                                                 .filter(group -> UNKNOWN_GROUP_ID.equals(group.get(ID)))
                                                 .findFirst()
                                                 .orElse(null);

        // Only a group that received a perspective is re-sorted, as the script sorted on each placement.
        Set<Map<String, Object>> filledGroups = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Map<String, Object> perspective : perspectives) {
            Map<String, Object> group = groupOf(perspective, groups, defaultGroup, unknownGroup);
            if (group == null) {
                // Nothing to place it in: hand it back standalone so the consuming shell can still
                // render it, rather than dropping it here.
                groupsAndStandalone.add(perspective);
            } else {
                items(group).add(perspective);
                filledGroups.add(group);
            }
        }
        filledGroups.forEach(group -> items(group).sort(ORDERING));
    }

    private static Map<String, Object> groupOf(Map<String, Object> perspective, List<Map<String, Object>> groups,
            Map<String, Object> defaultGroup, Map<String, Object> unknownGroup) {
        Object groupId = perspective.get("groupId");
        if (!truthy(groupId)) {
            return defaultGroup;
        }
        for (Map<String, Object> group : groups) {
            if (Objects.equals(group.get(ID), groupId)) {
                return group;
            }
        }
        // A groupId matching no group is authoring or rename drift. Neither the perspective nor the
        // diagnosis may be lost over it: a silently dropped perspective is indistinguishable from a
        // module that contributes none.
        LOGGER.warn("Perspective [{}] with path [{}] declares groupId [{}], which matches no perspective group of this extension point.",
                perspective.get(ID), perspective.get("path"), groupId);
        return defaultGroup != null ? defaultGroup : unknownGroup;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> items(Map<String, Object> group) {
        return (List<Map<String, Object>>) group.get(ITEMS);
    }

    /**
     * The sidebar configuration of a shell.
     *
     * @param perspectives the navigation groups, and the perspectives no group could take
     * @param utilities the utility perspectives
     */
    record SidebarConfiguration(List<Map<String, Object>> perspectives, List<Map<String, Object>> utilities) {

        /**
         * The configuration as the JSON object the shells read.
         *
         * @return the JSON-shaped configuration
         */
        Map<String, Object> toJson() {
            Map<String, Object> json = new LinkedHashMap<>();
            json.put("perspectives", perspectives);
            json.put("utilities", utilities);
            return json;
        }
    }
}
