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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModule;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.springframework.stereotype.Component;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Aggregates the locales of extension points and the translation catalogs of each.
 *
 * <p>
 * A locale carries {@code id}, {@code label}, an optional {@code order}, and {@code common}: the
 * registry path of its common catalog, served under the {@code common} namespace. Every other
 * namespace is a top-level registry folder, whose catalogs are the {@code i18n/<locale>/*.json}
 * files in it, merged.
 */
@Component
class LocalesAggregator {

    /** The extension point read when the request names none. */
    static final String DEFAULT_EXTENSION_POINT = "platform-locales";

    private static final List<String> EXPORTS = List.of("getLocale");

    private static final JsonMapper JSON = JsonMapper.builder()
                                                     .build();

    private static final TypeReference<Map<String, Object>> OBJECT = new TypeReference<>() {};

    private final ExtensionModuleReader reader;

    private final IRepository repository;

    /**
     * Instantiates a new locales aggregator.
     *
     * @param reader the extension module reader
     * @param repository the repository
     */
    LocalesAggregator(ExtensionModuleReader reader, IRepository repository) {
        this.reader = reader;
        this.repository = repository;
    }

    /**
     * Aggregates the locales of the given extension points with their translations.
     *
     * @param extensionPoints the extension points
     * @param languages the locales to translate, or {@code null} for every registered one
     * @param namespaces the namespaces to include besides {@code common}, or {@code null} for every one
     * @return the locales and the translations, keyed by locale id and then by namespace
     * @throws IllegalArgumentException when a requested locale is not registered
     * @throws IllegalStateException when a locale's common catalog does not exist
     */
    Map<String, Object> aggregate(List<String> extensionPoints, List<String> languages, List<String> namespaces) {
        List<Map<String, Object>> locales = reader.read(extensionPoints, EXPORTS)
                                                  .stream()
                                                  .map(ExtensionModule::content)
                                                  .sorted(ExtensionOrdering.byOrderThenExactText("label"))
                                                  .toList();
        Map<String, Object> translations = new LinkedHashMap<>();
        if (languages == null) {
            for (Map<String, Object> locale : locales) {
                translations.put(String.valueOf(locale.get("id")), translations(locale, namespaces));
            }
        } else {
            for (String language : languages) {
                Map<String, Object> locale = find(locales, language).orElseThrow(
                        () -> new IllegalArgumentException("Language(s) '" + String.join(",", languages) + "' not registered"));
                translations.put(language, translations(locale, namespaces));
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("locales", locales);
        result.put("translations", translations);
        return result;
    }

    private static Optional<Map<String, Object>> find(List<Map<String, Object>> locales, String id) {
        return locales.stream()
                      .filter(locale -> Objects.equals(locale.get("id"), id))
                      .findFirst();
    }

    private Map<String, Object> translations(Map<String, Object> locale, List<String> namespaces) {
        String language = String.valueOf(locale.get("id"));
        String commonPath = String.valueOf(locale.get("common"));
        Map<String, Object> translations = new LinkedHashMap<>();
        translations.put("common", readCatalog(commonPath));
        ICollection registry = repository.getCollection(IRepositoryStructure.PATH_REGISTRY_PUBLIC);
        for (String module : registry.getCollectionsNames()) {
            if (namespaces != null && !namespaces.contains(module)) {
                continue;
            }
            ICollection catalogs = registry.getCollection(module + "/i18n/" + language);
            if (!catalogs.exists()) {
                continue;
            }
            for (String catalog : catalogs.getResourcesNames()) {
                String catalogPath = "/" + module + "/i18n/" + language + "/" + catalog;
                if (!catalogPath.equals(commonPath)) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> merged = (Map<String, Object>) translations.computeIfAbsent(module, key -> new LinkedHashMap<>());
                    merged.putAll(readCatalog(catalogPath));
                }
            }
        }
        return translations;
    }

    private Map<String, Object> readCatalog(String registryPath) {
        IResource resource = repository.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + registryPath);
        if (!resource.exists()) {
            throw new IllegalStateException("Translation catalog [" + registryPath + "] does not exist in the registry");
        }
        return JSON.readValue(resource.getContent(), OBJECT);
    }

}
