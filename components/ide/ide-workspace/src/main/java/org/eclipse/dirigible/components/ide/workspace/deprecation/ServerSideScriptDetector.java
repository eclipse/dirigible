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

import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.dirigible.components.ide.workspace.deprecation.ServerSideScripts.Kind;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Finds the server-side JavaScript and TypeScript of a published project.
 *
 * <p>
 * The rule, file by file:
 * <ul>
 * <li>a TypeScript source ({@code .ts}, not a {@code .d.ts} declaration) or a JavaScript module
 * ({@code .mjs}) - the platform runs these on the server only;</li>
 * <li>a {@code .js} file that imports the platform's JavaScript SDK ({@code sdk/...} or
 * {@code @aerokit/sdk/...}) - only the server-side runtime resolves it. A {@code .js} beside a
 * {@code .ts} of the same name is that TypeScript's transpiled output, already counted;</li>
 * <li>an extension ({@code .extension}) whose module is not a {@code .json} module - the extension
 * reader evaluates any other module with the JavaScript engine;</li>
 * <li>a job, listener, websocket, process ({@code .bpmn}) or route ({@code .camel}) that names a
 * script file ({@code .js}, {@code .mjs} or {@code .ts}) as what it runs. A Java handler is named
 * by its class, never by a script file.</li>
 * </ul>
 * Any other {@code .js} - a page of a Harmonia or AngularJS UI, a generated UI - is what a browser
 * loads, and is not counted. {@code node_modules} and hidden folders hold no source of the project,
 * and are not read.
 */
final class ServerSideScriptDetector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSideScriptDetector.class);

    private static final String NODE_MODULES = "node_modules";

    private static final String HIDDEN_PREFIX = ".";

    private static final String TYPESCRIPT = ".ts";

    private static final String TYPESCRIPT_DECLARATION = ".d.ts";

    private static final String JAVASCRIPT = ".js";

    private static final String JAVASCRIPT_MODULE = ".mjs";

    private static final String EXTENSION = ".extension";

    private static final String JSON_MODULE = ".json";

    private static final String EXTENSION_MODULE_PROPERTY = "module";

    /** The artefacts that name a handler for the engine to run, by file extension. */
    private static final Map<String, Kind> HANDLER_ARTEFACTS =
            Map.of(".job", Kind.JOB, ".listener", Kind.LISTENER, ".websocket", Kind.WEBSOCKET, ".bpmn", Kind.PROCESS, ".camel", Kind.ROUTE);

    /** An import, a dynamic import or a require of a module of the platform's JavaScript SDK. */
    private static final Pattern SDK_IMPORT = Pattern.compile("\\b(?:from|import|require)\\s*\\(?\\s*['\"](?:@aerokit/)?sdk/");

    /**
     * A script file named in an artefact - {@code orders/handler.ts}, but not a Java package segment.
     */
    private static final Pattern SCRIPT_FILE = Pattern.compile("[\\w$@./-]+\\.(?:m?js|ts)(?![\\w.])");

    private ServerSideScriptDetector() {}

    /**
     * Finds the server-side scripts of a project.
     *
     * @param project the project's folder in the registry
     * @return what was found, counted by kind
     */
    static ServerSideScripts detect(ICollection project) {
        Map<Kind, Integer> counts = new EnumMap<>(Kind.class);
        collect(project, counts);
        return new ServerSideScripts(counts);
    }

    private static void collect(ICollection folder, Map<Kind, Integer> counts) {
        Set<String> names = new HashSet<>(folder.getResourcesNames());
        for (IResource resource : folder.getResources()) {
            kindOf(resource, names).ifPresent(kind -> counts.merge(kind, 1, Integer::sum));
        }
        for (ICollection child : folder.getCollections()) {
            String name = child.getName();
            if (!NODE_MODULES.equals(name) && !name.startsWith(HIDDEN_PREFIX)) {
                collect(child, counts);
            }
        }
    }

    private static Optional<Kind> kindOf(IResource resource, Set<String> siblings) {
        String name = resource.getName();
        if (name.endsWith(TYPESCRIPT_DECLARATION)) {
            return Optional.empty();
        }
        if (name.endsWith(TYPESCRIPT)) {
            return Optional.of(Kind.TYPESCRIPT_FILE);
        }
        if (name.endsWith(JAVASCRIPT_MODULE)) {
            return Optional.of(Kind.JAVASCRIPT_FILE);
        }
        if (name.endsWith(JAVASCRIPT)) {
            return isServerSideJavaScript(resource, siblings) ? Optional.of(Kind.JAVASCRIPT_FILE) : Optional.empty();
        }
        if (name.endsWith(EXTENSION)) {
            return isScriptModule(resource) ? Optional.of(Kind.EXTENSION_MODULE) : Optional.empty();
        }
        return handlerArtefact(name).filter(kind -> SCRIPT_FILE.matcher(content(resource))
                                                               .find());
    }

    private static boolean isServerSideJavaScript(IResource resource, Set<String> siblings) {
        String name = resource.getName();
        String transpiledFrom = name.substring(0, name.length() - JAVASCRIPT.length()) + TYPESCRIPT;
        return !siblings.contains(transpiledFrom) && SDK_IMPORT.matcher(content(resource))
                                                               .find();
    }

    private static boolean isScriptModule(IResource extension) {
        JsonElement parsed;
        try {
            parsed = JsonParser.parseString(content(extension));
        } catch (JsonParseException e) {
            LOGGER.debug("Extension [{}] is not JSON, so it registers no module to count", extension.getPath(), e);
            return false;
        }
        if (!parsed.isJsonObject()) {
            return false;
        }
        JsonElement module = parsed.getAsJsonObject()
                                   .get(EXTENSION_MODULE_PROPERTY);
        return module != null && module.isJsonPrimitive() && !module.getAsString()
                                                                    .endsWith(JSON_MODULE);
    }

    private static Optional<Kind> handlerArtefact(String name) {
        return HANDLER_ARTEFACTS.entrySet()
                                .stream()
                                .filter(entry -> name.endsWith(entry.getKey()))
                                .map(Map.Entry::getValue)
                                .findFirst();
    }

    private static String content(IResource resource) {
        return new String(resource.getContent(), StandardCharsets.UTF_8);
    }
}
