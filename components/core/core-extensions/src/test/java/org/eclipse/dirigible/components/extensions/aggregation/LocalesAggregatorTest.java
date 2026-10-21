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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The locales and their merged catalogs: {@code common} from the locale's own path, then each
 * registry module's {@code i18n/<locale>/*.json}, filtered by namespace.
 */
@ExtendWith(MockitoExtension.class)
class LocalesAggregatorTest {

    private static final String POINT = "test-locales";

    private static final String COMMON = "/resources-locale/i18n/en-US/common.json";

    @Mock
    private ExtensionModuleReader reader;

    @Mock
    private IRepository repository;

    @BeforeEach
    void registry() {
        stub(reader, POINT, json("{'id': 'en-US', 'label': 'English (US)', 'common': '" + COMMON + "'}"));
        ICollection root = mock(ICollection.class);
        lenient().when(repository.getCollection(IRepositoryStructure.PATH_REGISTRY_PUBLIC))
                 .thenReturn(root);
        lenient().when(root.getCollectionsNames())
                 .thenReturn(List.of("resources-locale", "orders", "untranslated"));
        catalogs(root, "resources-locale", List.of("common.json"));
        catalogs(root, "orders", List.of("a.json", "b.json"));
        catalogs(root, "untranslated", null);
        catalog(COMMON, "{\"save\": \"Save\"}");
        catalog("/orders/i18n/en-US/a.json", "{\"title\": \"Orders\", \"shared\": \"from a\"}");
        catalog("/orders/i18n/en-US/b.json", "{\"shared\": \"from b\"}");
    }

    @Test
    void merges_the_catalogs_of_every_module_beside_common() {
        Map<String, Object> result = new LocalesAggregator(reader, repository).aggregate(List.of(POINT), null, null);

        assertEquals(Map.of("en-US", Map.of("common", Map.of("save", "Save"), "orders", Map.of("title", "Orders", "shared", "from b"))),
                result.get("translations"));
    }

    @Test
    void includes_only_the_requested_namespaces() {
        Map<String, Object> result =
                new LocalesAggregator(reader, repository).aggregate(List.of(POINT), List.of("en-US"), List.of("other"));

        assertEquals(Map.of("en-US", Map.of("common", Map.of("save", "Save"))), result.get("translations"));
    }

    @Test
    void refuses_a_language_no_locale_registers() {
        LocalesAggregator aggregator = new LocalesAggregator(reader, repository);
        List<String> languages = List.of("en-US", "xx");

        IllegalArgumentException refusal =
                assertThrows(IllegalArgumentException.class, () -> aggregator.aggregate(List.of(POINT), languages, null));
        assertEquals("Language(s) 'en-US,xx' not registered", refusal.getMessage());
    }

    private static void catalogs(ICollection root, String module, List<String> names) {
        ICollection folder = mock(ICollection.class);
        lenient().when(root.getCollection(module + "/i18n/en-US"))
                 .thenReturn(folder);
        lenient().when(folder.exists())
                 .thenReturn(names != null);
        lenient().when(folder.getResourcesNames())
                 .thenReturn(names);
    }

    private void catalog(String path, String content) {
        IResource resource = mock(IResource.class);
        lenient().when(repository.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + path))
                 .thenReturn(resource);
        lenient().when(resource.exists())
                 .thenReturn(true);
        lenient().when(resource.getContent())
                 .thenReturn(content.getBytes(StandardCharsets.UTF_8));
    }
}
