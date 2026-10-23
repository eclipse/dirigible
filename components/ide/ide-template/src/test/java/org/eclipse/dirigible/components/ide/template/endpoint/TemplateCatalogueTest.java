/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.template.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.base.script.ScriptEngineAvailability;
import org.eclipse.dirigible.components.extensions.domain.Extension;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleEvaluator;
import org.eclipse.dirigible.components.extensions.service.ExtensionService;
import org.eclipse.dirigible.components.ide.template.service.TemplateDescriptors;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.ObjectProvider;

/**
 * The catalogue lists each readable template with its module as id, leaves out the ones needing the
 * JavaScript engine where it is not deployed and their alternatives where it is, and orders the
 * lists the way the script it replaces did.
 */
@ExtendWith(MockitoExtension.class)
class TemplateCatalogueTest {

    @Mock
    private ExtensionService extensionService;

    @Mock
    private IRepository repository;

    @Mock
    private ObjectProvider<ExtensionModuleEvaluator> evaluatorProvider;

    @Mock
    private ScriptEngineAvailability scriptEngine;

    private final Map<String, String> registry = new HashMap<>();

    @Test
    void eachTemplateCarriesItsModuleAsIdAndAnUnreadableOneIsSkipped() {
        lenient().when(repository.getResource(anyString()))
                 .thenAnswer(invocation -> resource(invocation.getArgument(0)));
        registry.put(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/app/template/template.json",
                "{\"name\": \"App\", \"extension\": \"model\"}");
        when(extensionService.findByExtensionPoint(TemplateCatalogue.TEMPLATES)).thenReturn(
                List.of(extension("app/template/template.js"), extension("script-only/template.js")));

        List<Map<String, Object>> templates = catalogue().read(TemplateCatalogue.TEMPLATES);

        assertEquals(List.of(Map.of("name", "App", "extension", "model", "id", "app/template/template.js")), templates);
    }

    @Test
    void aTemplateRequiringTheScriptEngineIsOfferedOnlyWhereItIsDeployed() {
        when(repository.getResource(anyString())).thenAnswer(invocation -> resource(invocation.getArgument(0)));
        registry.put(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/menu/ts.json",
                "{\"name\": \"typescript\", \"extension\": \"ts\", \"requiresScriptEngine\": true}");
        registry.put(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/menu/java.json", "{\"name\": \"Hello\", \"extension\": \"java\"}");
        when(extensionService.findByExtensionPoint(TemplateCatalogue.MENU_TEMPLATES)).thenReturn(
                List.of(extension("menu/ts.js"), extension("menu/java.js")));

        when(scriptEngine.isAvailable()).thenReturn(false);
        assertEquals(List.of("menu/java.js"), ids(catalogue().read(TemplateCatalogue.MENU_TEMPLATES)));

        when(scriptEngine.isAvailable()).thenReturn(true);
        assertEquals(List.of("menu/ts.js", "menu/java.js"), ids(catalogue().read(TemplateCatalogue.MENU_TEMPLATES)));
    }

    @Test
    void theAlternativeOfAScriptTemplateIsOfferedOnlyWhereTheEngineIsNotDeployed() {
        when(repository.getResource(anyString())).thenAnswer(invocation -> resource(invocation.getArgument(0)));
        registry.put(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/menu/extension.json",
                "{\"name\": \"extension\", \"label\": \"Extension\", \"requiresScriptEngine\": true}");
        registry.put(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/menu/extension-json.json",
                "{\"name\": \"extension\", \"label\": \"Extension\", \"withoutScriptEngine\": true}");
        registry.put(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/menu/roles.json", "{\"name\": \"roles\", \"label\": \"Roles\"}");
        when(extensionService.findByExtensionPoint(TemplateCatalogue.MENU_TEMPLATES)).thenReturn(
                List.of(extension("menu/extension.js"), extension("menu/extension-json.js"), extension("menu/roles.js")));

        when(scriptEngine.isAvailable()).thenReturn(false);
        assertEquals(List.of("menu/extension-json.js", "menu/roles.js"), ids(catalogue().read(TemplateCatalogue.MENU_TEMPLATES)));

        when(scriptEngine.isAvailable()).thenReturn(true);
        assertEquals(List.of("menu/extension.js", "menu/roles.js"), ids(catalogue().read(TemplateCatalogue.MENU_TEMPLATES)));
    }

    @Test
    void theDialogsListTheOrderedFirstThenTheLabelledThenTheRestByName() {
        List<Map<String, Object>> templates =
                templates(template("name", "Zeta"), template("name", "Beta", "label", "service"), template("name", "Late", "order", 20),
                        template("name", "Alpha"), template("name", "Gamma", "label", "Model"), template("name", "Early", "order", 3));

        templates.sort(TemplateCatalogue.DIALOG_ORDER);

        assertEquals(List.of("Early", "Late", "Gamma", "Beta", "Alpha", "Zeta"), names(templates));
    }

    private TemplateCatalogue catalogue() {
        return new TemplateCatalogue(extensionService, new TemplateDescriptors(repository, evaluatorProvider), scriptEngine);
    }

    private static List<Object> ids(List<Map<String, Object>> templates) {
        return templates.stream()
                        .map(template -> template.get("id"))
                        .toList();
    }

    private static Extension extension(String module) {
        Extension extension = new Extension();
        extension.setModule(module);
        return extension;
    }

    private static Map<String, Object> template(Object... keysAndValues) {
        Map<String, Object> template = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            template.put((String) keysAndValues[i], keysAndValues[i + 1]);
        }
        return template;
    }

    @SafeVarargs
    private static List<Map<String, Object>> templates(Map<String, Object>... templates) {
        return new ArrayList<>(List.of(templates));
    }

    private static List<Object> names(List<Map<String, Object>> templates) {
        return templates.stream()
                        .map(template -> template.get("name"))
                        .toList();
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
}
