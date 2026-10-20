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
 * Aggregates the shells of extension points - the entries of the shell switcher. A shell needs an
 * {@code id}, a {@code label} and a {@code path}; the first shell of an id wins.
 */
@Component
class ShellsAggregator {

    /** The extension point read when the request names none. */
    static final String DEFAULT_EXTENSION_POINT = "platform-shells";

    private static final Logger LOGGER = LoggerFactory.getLogger(ShellsAggregator.class);

    private static final List<String> EXPORTS = List.of("getShell");

    private final ExtensionModuleReader reader;

    /**
     * Instantiates a new shells aggregator.
     *
     * @param reader the extension module reader
     */
    ShellsAggregator(ExtensionModuleReader reader) {
        this.reader = reader;
    }

    /**
     * Aggregates the shells of the given extension points.
     *
     * @param extensionPoints the extension points
     * @return the shells, ordered
     */
    List<Map<String, Object>> aggregate(List<String> extensionPoints) {
        List<Map<String, Object>> shells = new ArrayList<>();
        for (ExtensionModule module : reader.read(extensionPoints, EXPORTS)) {
            Map<String, Object> shell = module.content();
            if (isValid(shell)) {
                Optional<Map<String, Object>> duplicate = shells.stream()
                                                                .filter(existing -> Objects.equals(existing.get("id"), shell.get("id")))
                                                                .findFirst();
                if (duplicate.isPresent()) {
                    LOGGER.warn("Duplication at shell with id [{}] pointing to paths [{}] and [{}]: the latter is skipped.",
                            shell.get("id"), duplicate.get()
                                                      .get("path"),
                            shell.get("path"));
                } else {
                    shells.add(shell);
                }
            }
        }
        shells.sort(ExtensionOrdering.byOrderThenText("label"));
        return shells;
    }

    private static boolean isValid(Map<String, Object> shell) {
        if (!truthy(shell, "id")) {
            LOGGER.warn("Shell [{}] does not have an id and is skipped.", truthy(shell, "label") ? shell.get("label") : shell.get("path"));
            return false;
        }
        if (!truthy(shell, "label")) {
            LOGGER.warn("Shell [{}] does not have a label and is skipped.", shell.get("id"));
            return false;
        }
        if (!truthy(shell, "path")) {
            LOGGER.warn("Shell [{}] does not have a path and is skipped.", shell.get("id"));
            return false;
        }
        return true;
    }
}
