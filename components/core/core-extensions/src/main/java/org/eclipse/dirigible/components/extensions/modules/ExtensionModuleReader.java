/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.dirigible.components.extensions.domain.Extension;
import org.eclipse.dirigible.components.extensions.service.ExtensionService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Reads the extension modules registered on extension points, in the order the extension service
 * returns them - the order the script aggregation read them in.
 *
 * <p>
 * A {@code .json} module is read from the public registry as it is. Any other module is a script
 * module, evaluated through the {@link ExtensionModuleEvaluator} when one is present; all script
 * modules of one read share one evaluation session, opened only once the first of them is met, so a
 * read that meets JSON modules alone starts no script engine. A module that cannot be read is
 * skipped with a warning naming it, and so is a script module where no evaluator is present - a
 * repeat of that one logs at DEBUG, since it recurs on every shell load until the module is
 * converted.
 */
@Component
public class ExtensionModuleReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionModuleReader.class);

    private static final String JSON_MODULE_SUFFIX = ".json";

    private static final JsonMapper JSON = JsonMapper.builder()
                                                     .build();

    private static final TypeReference<Map<String, Object>> OBJECT = new TypeReference<>() {};

    private final ExtensionService extensionService;

    private final IRepository repository;

    private final ObjectProvider<ExtensionModuleEvaluator> evaluator;

    /** The script modules already reported as skipped for want of an evaluator. */
    private final Set<String> reportedUnevaluated = ConcurrentHashMap.newKeySet();

    /**
     * Instantiates a new extension module reader.
     *
     * @param extensionService the extension service
     * @param repository the repository
     * @param evaluator the script module evaluator, absent where no script engine is deployed
     */
    public ExtensionModuleReader(ExtensionService extensionService, IRepository repository,
            ObjectProvider<ExtensionModuleEvaluator> evaluator) {
        this.extensionService = extensionService;
        this.repository = repository;
        this.evaluator = evaluator;
    }

    /**
     * Reads the modules registered on the given extension points, those the current user may see.
     *
     * @param extensionPoints the extension points, read in order
     * @param exportNames the exports to try on a script module, in order
     * @return the modules that could be read
     */
    public List<ExtensionModule> read(List<String> extensionPoints, List<String> exportNames) {
        List<ExtensionModule> modules = new ArrayList<>();
        try (ScriptModules scriptModules = new ScriptModules(exportNames)) {
            for (String extensionPoint : extensionPoints) {
                for (Extension extension : extensionService.findByExtensionPoint(extensionPoint)) {
                    String module = extension.getModule();
                    Optional<ExtensionModule> read =
                            isJsonModule(module) ? readJsonModule(module, extensionPoint) : scriptModules.read(module, extensionPoint);
                    read.ifPresent(modules::add);
                }
            }
        }
        return modules;
    }

    private static boolean isJsonModule(String module) {
        return module != null && module.endsWith(JSON_MODULE_SUFFIX);
    }

    private Optional<ExtensionModule> readJsonModule(String module, String extensionPoint) {
        IResource resource = repository.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + stripLeadingSlash(module));
        if (!resource.exists()) {
            LOGGER.warn("Extension module [{}] of extension point [{}] does not exist in the registry, consider publishing it.",
                    loggable(module), loggable(extensionPoint));
            return Optional.empty();
        }
        try {
            return Optional.of(new ExtensionModule(module, null, JSON.readValue(resource.getContent(), OBJECT)));
        } catch (JacksonException e) {
            LOGGER.warn("Extension module [{}] of extension point [{}] is not a JSON object and is skipped.", loggable(module),
                    loggable(extensionPoint), e);
            return Optional.empty();
        }
    }

    /**
     * Collapses the line breaks in a value that may come from the request (an extension point names
     * what the caller asked for), so it cannot forge a second log entry.
     */
    private static String loggable(String value) {
        return value == null ? null : value.replaceAll("\\R", "_");
    }

    private static String stripLeadingSlash(String module) {
        return module.startsWith("/") ? module.substring(1) : module;
    }

    /**
     * The script modules of one read, evaluated in one session opened on first use.
     */
    private final class ScriptModules implements AutoCloseable {

        private final List<String> exportNames;

        private ExtensionModuleEvaluator.Session session;

        private ScriptModules(List<String> exportNames) {
            this.exportNames = exportNames;
        }

        private Optional<ExtensionModule> read(String module, String extensionPoint) {
            ExtensionModuleEvaluator.Session current = session();
            if (current == null) {
                reportUnevaluated(module, extensionPoint);
                return Optional.empty();
            }
            try {
                Optional<ExtensionModuleExport> export = current.evaluate(module, exportNames);
                if (export.isEmpty()) {
                    LOGGER.warn("Extension module [{}] of extension point [{}] provides none of the exports {} and is skipped.",
                            loggable(module), loggable(extensionPoint), exportNames);
                }
                return export.map(found -> new ExtensionModule(module, found.name(), found.value()));
            } catch (RuntimeException e) {
                LOGGER.warn("Extension module [{}] of extension point [{}] failed to evaluate and is skipped.", loggable(module),
                        loggable(extensionPoint), e);
                return Optional.empty();
            }
        }

        private ExtensionModuleEvaluator.Session session() {
            if (session == null) {
                ExtensionModuleEvaluator available = evaluator.getIfAvailable();
                if (available != null) {
                    session = available.openSession();
                }
            }
            return session;
        }

        private void reportUnevaluated(String module, String extensionPoint) {
            if (reportedUnevaluated.add(extensionPoint + " " + module)) {
                LOGGER.warn(
                        "Extension module [{}] of extension point [{}] is a script module and no script engine is deployed, so it is skipped."
                                + " Convert it to a .json module, or regenerate the application that contributes it.",
                        loggable(module), loggable(extensionPoint));
            } else {
                LOGGER.debug("Extension module [{}] of extension point [{}] is a script module and is skipped again.", loggable(module),
                        loggable(extensionPoint));
            }
        }

        @Override
        public void close() {
            if (session != null) {
                session.close();
            }
        }
    }
}
