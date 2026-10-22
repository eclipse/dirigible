/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.workspace.deprecation;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The server-side JavaScript and TypeScript one project carries, counted by kind.
 */
final class ServerSideScripts {

    /** A project without any. */
    static final ServerSideScripts NONE = new ServerSideScripts(Map.of());

    /**
     * A kind of server-side script, with the words a count of it is reported in.
     */
    enum Kind {

        TYPESCRIPT_FILE("TypeScript file", "TypeScript files", false),

        JAVASCRIPT_FILE("JavaScript file", "JavaScript files", false),

        EXTENSION_MODULE("extension module", "extension modules", true),

        JOB("job", "jobs", true),

        LISTENER("listener", "listeners", true),

        WEBSOCKET("websocket", "websockets", true),

        PROCESS("process", "processes", true),

        ROUTE("route", "routes", true);

        private final String singular;

        private final String plural;

        /**
         * Whether an instance without the JavaScript engine skips it: an extension module the extension
         * reader cannot evaluate, or an artefact registered failed, as opposed to a file that is simply not
         * served.
         */
        private final boolean skippedWithoutEngine;

        Kind(String singular, String plural, boolean skippedWithoutEngine) {
            this.singular = singular;
            this.plural = plural;
            this.skippedWithoutEngine = skippedWithoutEngine;
        }

        private String count(int count) {
            return count + " " + (count == 1 ? singular : plural);
        }
    }

    private final Map<Kind, Integer> counts;

    /**
     * Instantiates the findings of one project.
     *
     * @param counts the count of each kind found; a kind absent or counted zero was not found
     */
    ServerSideScripts(Map<Kind, Integer> counts) {
        Map<Kind, Integer> found = new EnumMap<>(Kind.class);
        counts.forEach((kind, count) -> {
            if (count > 0) {
                found.put(kind, count);
            }
        });
        this.counts = Collections.unmodifiableMap(found);
    }

    /**
     * Whether the project carries no server-side script at all.
     *
     * @return {@code true} when nothing was found
     */
    boolean isEmpty() {
        return counts.isEmpty();
    }

    /**
     * The count of one kind.
     *
     * @param kind the kind
     * @return how many were found
     */
    int count(Kind kind) {
        return counts.getOrDefault(kind, 0);
    }

    /**
     * How many extension modules and artefacts an instance without the JavaScript engine skips.
     *
     * @return the count
     */
    int skippedWithoutEngine() {
        return counts.entrySet()
                     .stream()
                     .filter(entry -> entry.getKey().skippedWithoutEngine)
                     .mapToInt(Map.Entry::getValue)
                     .sum();
    }

    /**
     * Names what was found, kind by kind - {@code 2 TypeScript files, 1 job}.
     *
     * @return the description
     */
    String describe() {
        return counts.entrySet()
                     .stream()
                     .map(entry -> entry.getKey()
                                        .count(entry.getValue()))
                     .collect(Collectors.joining(", "));
    }
}
