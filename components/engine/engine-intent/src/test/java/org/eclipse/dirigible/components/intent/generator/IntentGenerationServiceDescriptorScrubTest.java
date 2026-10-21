/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.generator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.dirigible.components.intent.generator.IntentGenerationService.GenerationResult;
import org.eclipse.dirigible.components.intent.generator.action.ActionIntentGenerator;
import org.eclipse.dirigible.components.intent.generator.edm.EdmIntentGenerator;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * An action descriptor is scrubbed by its twin {@code .extension}, never by its own extension
 * (dirigible #7981): {@code .json} cannot be intent-owned while {@code project.json} and
 * hand-written {@code .json} files share the project root.
 */
class IntentGenerationServiceDescriptorScrubTest {

    private static final String PROJECT = "orders";
    private static final String PROJECT_ROOT = "/users/admin/workspace/" + PROJECT;

    private static final String WITHOUT_ACTION = """
            name: orders
            entities:
              - name: Order
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: number, type: string }
            """;

    private static final String WITH_ACTION = WITHOUT_ACTION + """
            actions:
              - { name: approve, forEntity: Order, page: /services/web/orders/approve.html }
            """;

    private static final String PROJECT_JSON = "{\"guid\": \"orders\"}";

    private LocalRepository repository;
    private IntentGenerationService service;

    @BeforeEach
    void setUp(@TempDir Path root) {
        repository = new LocalRepository(root.toString(), true);
        repository.createResource(PROJECT_ROOT + "/project.json", PROJECT_JSON.getBytes(StandardCharsets.UTF_8));
        service = new IntentGenerationService(List.of(new EdmIntentGenerator(), new ActionIntentGenerator()), repository, null);
    }

    @Test
    void aRemovedActionTakesItsDescriptorWithIt() {
        generate(WITH_ACTION);
        assertTrue(exists("approve-action.json"), "the first pass writes the descriptor");

        GenerationResult result = generate(WITHOUT_ACTION);

        assertFalse(exists("approve-action.extension"));
        assertFalse(exists("approve-action.json"), "the descriptor must go with the extension that registered it");
        assertTrue(result.scrubbed()
                         .contains("approve-action.json"),
                "the descriptor must be reported as scrubbed: " + result.scrubbed());
        assertTrue(exists("project.json"), "a root .json no extension names must survive");
    }

    @Test
    void thePreviousJavaScriptDescriptorIsRemovedOnceTheJsonReplacesIt() {
        create("approve-action.js", "exports.getAction = function () { return {}; };");

        GenerationResult result = generate(WITH_ACTION);

        assertTrue(exists("approve-action.json"));
        assertFalse(exists("approve-action.js"), "the .js twin is dead once the .extension names the .json");
        assertTrue(result.scrubbed()
                         .contains("approve-action.js"),
                "the .js twin must be reported as scrubbed: " + result.scrubbed());
        assertTrue(exists("project.json"));
    }

    @Test
    void aModuleTheExtensionDoesNotNameAsItsTwinIsLeftAlone() {
        create("legacy-action.extension", "{\"module\": \"orders/handwritten.js\", \"extensionPoint\": \"orders-custom-action\"}");
        create("handwritten.js", "exports.getAction = function () { return {}; };");

        GenerationResult result = generate(WITHOUT_ACTION);

        assertFalse(exists("legacy-action.extension"), "the root .extension is intent-owned, as before");
        assertTrue(exists("handwritten.js"), "a module that is not the extension's same-named twin is not generator output");
        assertFalse(result.scrubbed()
                          .contains("handwritten.js"));
    }

    private GenerationResult generate(String yaml) {
        return service.generate(yaml, PROJECT_ROOT, PROJECT, "workspace", PROJECT);
    }

    private void create(String fileName, String content) {
        repository.createResource(PROJECT_ROOT + "/" + fileName, content.getBytes(StandardCharsets.UTF_8));
    }

    private boolean exists(String fileName) {
        return repository.getResource(PROJECT_ROOT + "/" + fileName)
                         .exists();
    }
}
