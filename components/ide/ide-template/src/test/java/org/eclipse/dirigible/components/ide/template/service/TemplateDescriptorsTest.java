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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleEvaluator;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleExport;
import org.eclipse.dirigible.components.ide.template.domain.GenerationTemplateMetadata;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.ObjectProvider;

/**
 * A descriptor is the JSON its id names, or the JSON next to the script module it names; includes
 * resolve in place; a script descriptor is evaluated only when no JSON exists, one session per
 * read.
 */
@ExtendWith(MockitoExtension.class)
class TemplateDescriptorsTest {

    private static final List<String> SCRIPT_EXPORTS = List.of("getTemplate");

    @Mock
    private IRepository repository;

    @Mock
    private ObjectProvider<ExtensionModuleEvaluator> evaluatorProvider;

    @Mock
    private ExtensionModuleEvaluator evaluator;

    @Mock
    private ExtensionModuleEvaluator.Session session;

    private final Map<String, String> registry = new HashMap<>();

    private TemplateDescriptors descriptors;

    @BeforeEach
    void setUp() {
        lenient().when(repository.getResource(anyString()))
                 .thenAnswer(invocation -> resource(invocation.getArgument(0)));
        descriptors = new TemplateDescriptors(repository, evaluatorProvider);
    }

    @Test
    void aScriptIdResolvesToTheJsonNextToItWithoutAScriptEngine() {
        publish("app/template/template.json", "{\"name\": \"App\", \"extension\": \"model\", \"sources\": [], \"parameters\": []}");

        Map<String, Object> descriptor = read("app/template/template.js", Map.of());

        assertEquals("App", descriptor.get("name"));
        assertEquals("model", descriptor.get("extension"));
        verify(evaluatorProvider, never()).getIfAvailable();
    }

    @Test
    void aDescriptorTheRegistryDoesNotHoldIsReadFromTheClasspath() {
        Map<String, Object> descriptor = read("template-descriptors-test/template.js", Map.of());

        assertEquals("Classpath Template", descriptor.get("name"));
    }

    @Test
    void includesAreReplacedInPlaceAndTheFirstParameterOfEachNameWins() {
        publish("lib/template.json", """
                {"sources": [{"location": "/lib/a"}, {"include": "lib/nested.json"}],
                 "parameters": [{"name": "tablePrefix", "label": "From lib"}, {"name": "dataSource"}]}""");
        publish("lib/nested.json", "{\"sources\": [{\"location\": \"/lib/nested\"}]}");
        publish("app/template.json", """
                {"name": "App",
                 "sources": [{"location": "/app/first"}, {"include": "lib/template.json"}, {"location": "/app/last"}],
                 "parameters": [{"name": "brand"}, {"include": "lib/template.json"}, {"name": "tablePrefix", "label": "From app"}]}""");

        Map<String, Object> descriptor = read("app/template.js", Map.of());

        assertEquals(List.of(Map.of("location", "/app/first"), Map.of("location", "/lib/a"), Map.of("location", "/lib/nested"),
                Map.of("location", "/app/last")), descriptor.get("sources"));
        assertEquals(List.of(Map.of("name", "brand"), Map.of("name", "tablePrefix", "label", "From lib"), Map.of("name", "dataSource")),
                descriptor.get("parameters"));
    }

    @Test
    void anIncludeCycleIsRefused() {
        publish("a/template.json", "{\"sources\": [{\"include\": \"b/template.json\"}]}");
        publish("b/template.json", "{\"sources\": [{\"include\": \"a/template.json\"}]}");

        IllegalArgumentException refusal = assertThrows(IllegalArgumentException.class, () -> read("a/template.js", Map.of()));

        assertTrue(refusal.getMessage()
                          .contains("includes itself"),
                refusal.getMessage());
    }

    @Test
    void aMissingIncludeIsRefusedNamingIt() {
        publish("app/template.json", "{\"sources\": [{\"include\": \"gone/template.json\"}]}");

        IllegalArgumentException refusal = assertThrows(IllegalArgumentException.class, () -> read("app/template.js", Map.of()));

        assertTrue(refusal.getMessage()
                          .contains("gone/template.json"),
                refusal.getMessage());
    }

    @Test
    void scriptDescriptorsAreCalledWithTheParametersInOneSessionPerRead() {
        Map<String, Object> parameters = Map.of("projectName", "orders");
        when(evaluatorProvider.getIfAvailable()).thenReturn(evaluator);
        when(evaluator.openSession()).thenReturn(session);
        when(session.evaluate("user/a.js", SCRIPT_EXPORTS, List.of(parameters))).thenReturn(export("A"));
        when(session.evaluate("user/b.mjs", SCRIPT_EXPORTS, List.of(parameters))).thenReturn(export("B"));

        try (TemplateDescriptors.Reading reading = descriptors.open()) {
            assertEquals("A", reading.read("user/a.js", parameters)
                                     .get("name"));
            assertEquals("B", reading.read("user/b.mjs", parameters)
                                     .get("name"));
        }

        verify(evaluator, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    void aScriptDescriptorReadForTheCatalogueIsCalledWithoutArguments() {
        when(evaluatorProvider.getIfAvailable()).thenReturn(evaluator);
        when(evaluator.openSession()).thenReturn(session);
        when(session.evaluate("user/a.js", SCRIPT_EXPORTS, List.of())).thenReturn(export("A"));

        assertEquals("A", read("user/a.js", null).get("name"));
    }

    @Test
    void aScriptDescriptorWithoutAScriptEngineIsRefusedByName() {
        IllegalArgumentException refusal = assertThrows(IllegalArgumentException.class, () -> read("user/a.js", Map.of()));

        assertTrue(refusal.getMessage()
                          .contains("[user/a.js]"),
                refusal.getMessage());
        assertTrue(refusal.getMessage()
                          .contains("no script engine"),
                refusal.getMessage());
    }

    @Test
    void aMissingJsonDescriptorIsNotTriedAsAScript() {
        assertThrows(IllegalArgumentException.class, () -> read("user/template.json", Map.of()));

        verify(evaluatorProvider, never()).getIfAvailable();
    }

    @Test
    void theMetadataCarriesTheSourcesAndWhetherTheyOverwrite() {
        publish("app/template.json", """
                {"name": "App", "sources": [
                  {"location": "/app/project.json.template", "rename": "project.json", "overwrite": false},
                  {"location": "/app/Entity.java.template", "collection": "models"}]}""");

        GenerationTemplateMetadata metadata = descriptors.readMetadata("app/template.js", Map.of());

        assertEquals("App", metadata.getName());
        assertFalse(metadata.getSources()
                            .get(0)
                            .isOverwrite());
        assertTrue(metadata.getSources()
                           .get(1)
                           .isOverwrite());
        assertEquals("models", metadata.getSources()
                                       .get(1)
                                       .getCollection());
    }

    @Test
    void anIdNamingNoExtensionResolvesToItsJson() {
        assertEquals("app/template.json", TemplateDescriptors.descriptorPath("/app/template"));
        assertEquals("app/template.json", TemplateDescriptors.descriptorPath("app/template.mjs"));
        assertEquals("app.v2/template.json", TemplateDescriptors.descriptorPath("app.v2/template"));
        assertEquals("app/template.json", TemplateDescriptors.descriptorPath("app/template.json"));
    }

    private Map<String, Object> read(String templateId, Map<String, Object> parameters) {
        try (TemplateDescriptors.Reading reading = descriptors.open()) {
            return reading.read(templateId, parameters);
        }
    }

    private void publish(String path, String content) {
        registry.put(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + path, content);
    }

    private IResource resource(String path) {
        IResource resource = mock(IResource.class, withSettings().strictness(Strictness.LENIENT));
        String content = registry.get(path);
        when(resource.exists()).thenReturn(content != null);
        if (content != null) {
            when(resource.getContent()).thenReturn(content.getBytes(StandardCharsets.UTF_8));
        }
        return resource;
    }

    private static Optional<ExtensionModuleExport> export(String name) {
        return Optional.of(new ExtensionModuleExport("getTemplate", Map.of("name", name)));
    }
}
