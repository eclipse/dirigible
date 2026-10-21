/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.template.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleEvaluator;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleExport;
import org.eclipse.dirigible.components.ide.template.domain.GenerationTemplateMetadata;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Reads template descriptors - what a template generates, and the parameters it asks for.
 *
 * <p>
 * A template is named by its id: the module its {@code .extension} registers on a template
 * extension point (for instance {@code template-application-dao-java/template/template.js}), which
 * is also what a generation request and a {@code .gen} file record. Its descriptor is data - the
 * {@code .json} file the id names or, for an id naming a script module, the {@code .json} file next
 * to that module - read from the public registry, else from the classpath. So an id recorded while
 * its descriptor was a script keeps resolving once the descriptor is data.
 *
 * <p>
 * The {@code sources} and {@code parameters} of a JSON descriptor may take in another descriptor's:
 * an entry {@code {"include": "<descriptor>"}} is replaced, in place, by that descriptor's own
 * entries, with its includes resolved first. The parameters are then deduplicated by name, the
 * first declaration winning, so a composite lists a parameter its parts share once.
 *
 * <p>
 * An id with no JSON descriptor is a script descriptor - a module exporting {@code getTemplate}, as
 * every descriptor was written before descriptors were data. It is called with the generation
 * parameters wherever an {@link ExtensionModuleEvaluator} is deployed (the JavaScript engine
 * provides one), so a descriptor written that way in a user project keeps working. All the script
 * descriptors of one {@link Reading} share one evaluation session, opened only once the first of
 * them is met, so a read that meets JSON descriptors alone starts no script engine.
 */
@Component
public class TemplateDescriptors {

    private static final String JSON_EXTENSION = ".json";

    private static final String CLASSPATH_ROOT = "/META-INF/dirigible/";

    private static final String INCLUDE = "include";

    private static final String SOURCES = "sources";

    private static final String PARAMETERS = "parameters";

    private static final String NAME = "name";

    private static final List<String> SCRIPT_EXPORTS = List.of("getTemplate");

    private static final JsonMapper JSON = JsonMapper.builder()
                                                     .build();

    private static final TypeReference<Map<String, Object>> OBJECT = new TypeReference<>() {};

    private final IRepository repository;

    private final ObjectProvider<ExtensionModuleEvaluator> evaluator;

    /**
     * Instantiates the template descriptors.
     *
     * @param repository the repository holding the published templates
     * @param evaluator the script module evaluator, absent where no script engine is deployed
     */
    public TemplateDescriptors(IRepository repository, ObjectProvider<ExtensionModuleEvaluator> evaluator) {
        this.repository = repository;
        this.evaluator = evaluator;
    }

    /**
     * Opens a read of one or more descriptors, to be closed by the caller.
     *
     * @return the read
     */
    public Reading open() {
        return new Reading();
    }

    /**
     * Reads one template's descriptor as the generation metadata.
     *
     * @param templateId the template id
     * @param parameters the generation parameters a script descriptor is called with
     * @return the metadata
     * @throws IllegalArgumentException when the descriptor does not exist or cannot be read
     */
    public GenerationTemplateMetadata readMetadata(String templateId, Map<String, Object> parameters) {
        try (Reading reading = open()) {
            return GsonHelper.fromJson(GsonHelper.toJson(reading.read(templateId, parameters)), GenerationTemplateMetadata.class);
        }
    }

    /**
     * The path of the JSON descriptor an id names: the id itself when it names one, else the
     * {@code .json} file next to the module it names.
     *
     * @param templateId the template id
     * @return the registry-relative path
     */
    static String descriptorPath(String templateId) {
        String path = templateId.startsWith("/") ? templateId.substring(1) : templateId;
        if (path.endsWith(JSON_EXTENSION)) {
            return path;
        }
        int dot = path.lastIndexOf('.');
        return (dot > path.lastIndexOf('/') ? path.substring(0, dot) : path) + JSON_EXTENSION;
    }

    /**
     * One read of one or more descriptors. The script descriptors it meets share one evaluation
     * session.
     */
    public final class Reading implements AutoCloseable {

        private ExtensionModuleEvaluator.Session session;

        private Reading() {}

        /**
         * Reads a template's descriptor, its includes resolved.
         *
         * @param templateId the template id
         * @param parameters the generation parameters a script descriptor is called with, null to call it
         *        without any
         * @return the descriptor
         * @throws IllegalArgumentException when the descriptor does not exist or cannot be read
         */
        public Map<String, Object> read(String templateId, Map<String, Object> parameters) {
            String path = descriptorPath(templateId);
            Optional<Map<String, Object>> descriptor = readJson(path);
            if (descriptor.isPresent()) {
                return resolve(path, descriptor.get(), new LinkedHashSet<>());
            }
            if (templateId.endsWith(JSON_EXTENSION)) {
                throw new IllegalArgumentException("Template descriptor [" + templateId + "] does not exist");
            }
            return readScript(templateId, path, parameters);
        }

        private Map<String, Object> readScript(String templateId, String path, Map<String, Object> parameters) {
            ExtensionModuleEvaluator.Session current = session();
            if (current == null) {
                throw new IllegalArgumentException("Template [" + templateId + "] has no JSON descriptor [" + path
                        + "], and no script engine is deployed to read it as a script descriptor");
            }
            Optional<ExtensionModuleExport> export;
            try {
                export = current.evaluate(templateId, SCRIPT_EXPORTS, parameters == null ? List.of() : List.of(parameters));
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("Invalid template definition file: [" + templateId + "]", e);
            }
            return export.map(ExtensionModuleExport::value)
                         .orElseThrow(() -> new IllegalArgumentException("Template [" + templateId + "] has no JSON descriptor [" + path
                                 + "], and its module does not export " + SCRIPT_EXPORTS));
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

        @Override
        public void close() {
            if (session != null) {
                session.close();
            }
        }
    }

    private Map<String, Object> resolve(String path, Map<String, Object> descriptor, Set<String> including) {
        if (!including.add(path)) {
            throw new IllegalArgumentException("Template descriptor [" + path + "] includes itself through " + including);
        }
        Map<String, Object> resolved = new LinkedHashMap<>(descriptor);
        if (descriptor.get(SOURCES) instanceof List<?> sources) {
            resolved.put(SOURCES, expand(path, sources, SOURCES, including));
        }
        if (descriptor.get(PARAMETERS) instanceof List<?> parameters) {
            resolved.put(PARAMETERS, distinctByName(expand(path, parameters, PARAMETERS, including)));
        }
        including.remove(path);
        return resolved;
    }

    private List<Object> expand(String path, List<?> entries, String key, Set<String> including) {
        List<Object> expanded = new ArrayList<>(entries.size());
        for (Object entry : entries) {
            if (entry instanceof Map<?, ?> map && map.get(INCLUDE) instanceof String include) {
                String includedPath = descriptorPath(include);
                Map<String, Object> included = readJson(includedPath).orElseThrow(() -> new IllegalArgumentException(
                        "Template descriptor [" + path + "] includes [" + include + "], which does not exist"));
                if (resolve(includedPath, included, including).get(key) instanceof List<?> includedEntries) {
                    expanded.addAll(includedEntries);
                }
            } else {
                expanded.add(entry);
            }
        }
        return expanded;
    }

    private static List<Object> distinctByName(List<Object> parameters) {
        Set<Object> names = new HashSet<>();
        List<Object> distinct = new ArrayList<>(parameters.size());
        for (Object parameter : parameters) {
            if (!(parameter instanceof Map<?, ?> map) || !map.containsKey(NAME) || names.add(map.get(NAME))) {
                distinct.add(parameter);
            }
        }
        return distinct;
    }

    private Optional<Map<String, Object>> readJson(String path) {
        byte[] content = readContent(path);
        if (content == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(JSON.readValue(content, OBJECT));
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Template descriptor [" + path + "] is not a JSON object", e);
        }
    }

    /**
     * Reads a descriptor from the registry, where the template modules publish their content, else from
     * the classpath, as the generation pipelines read the template sources themselves.
     */
    private byte[] readContent(String path) {
        IResource resource = repository.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + path);
        if (resource.exists()) {
            return resource.getContent();
        }
        try (InputStream in = TemplateDescriptors.class.getResourceAsStream(CLASSPATH_ROOT + path)) {
            return in == null ? null : in.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Template descriptor [" + path + "] cannot be read from the classpath", e);
        }
    }
}
