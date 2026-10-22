/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.integration.tests.api.ShellBootstrapWithoutScriptsIT.CountingExtensionModuleEvaluator;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.restassured.RestAssuredExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.path.json.JsonPath;

/**
 * Template descriptors are data (#8007): the catalogue the IDE's dialogs read and an intent
 * Generate start no script engine, and a descriptor a user project still writes as JavaScript keeps
 * working through the engine. The views, editors and perspectives the IDE's New wizards generate
 * are JSON descriptors too (#8022).
 *
 * <p>
 * The counting double of {@link ShellBootstrapWithoutScriptsIT} wraps the JavaScript engine's
 * extension module evaluator, the only way a template descriptor reaches a script engine.
 */
@Import(ShellBootstrapWithoutScriptsIT.CountingEvaluatorConfiguration.class)
// One Dirigible boot for the whole class: each method cleans up after itself.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TemplateDescriptorsIT extends IntegrationTest {

    private static final String CATALOGUE = "/services/js/service-template/api/templates.js";


    private static final String APPLICATION_TEMPLATE = "template-application-ui-harmonia-java/template/template.js";

    private static final String TEMPLATES_PROJECT = "/template-descriptors-it";

    private static final String SCRIPT_TEMPLATE = TEMPLATES_PROJECT.substring(1) + "/script/template.js";

    private static final String JSON_TEMPLATE = TEMPLATES_PROJECT.substring(1) + "/json/template.json";

    private static final String WORKSPACE = "workspace";

    private static final String PROJECT = "template-descriptors-app";

    private static final String WORKSPACE_PATH = IRepositoryStructure.PATH_USERS + "/admin/" + WORKSPACE;

    private static final String PROJECT_PATH = WORKSPACE_PATH + "/" + PROJECT;

    private static final String GENERATE_FILE = "/services/ide/generate/file/" + WORKSPACE + "/" + PROJECT + "/";

    private static final long ASSERTION_TIMEOUT_SECONDS = 30;

    private static final String INTENT = """
            name: descriptors
            description: An entity with a lookup, generated through every recipe of the intent

            entities:
              - name: Category
                kind: setting
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string,  required: true, length: 100 }

              - name: Ticket
                fields:
                  - { name: id,      type: integer, primaryKey: true, generated: true }
                  - { name: subject, type: string,  required: true, length: 200 }
                relations:
                  - { name: Category, kind: manyToOne, to: Category }
            """;

    @Autowired
    private IRepository repository;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private CountingExtensionModuleEvaluator evaluator;

    /** The repository paths this test created, for the teardown. */
    private final List<String> created = new ArrayList<>();

    @Test
    void lists_the_shipped_templates_without_running_a_script() {
        assertFalse(repository.hasResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/service-template/api/templates.js"),
                "the catalogue script must be gone from the registry");
        evaluator.reset();

        restAssuredExecutor.execute(() -> {
            given().when()
                   .get(CATALOGUE)
                   .then()
                   .statusCode(200)
                   .body("[0].id", equalTo("template-hello-world/template.js"))
                   .body(template(APPLICATION_TEMPLATE) + ".name", equalTo("Application - UI (Harmonia) - Java"))
                   .body(template(APPLICATION_TEMPLATE) + ".parameters.name",
                           equalTo(List.of("brand", "brandUrl", "title", "appIcon", "description", "tablePrefix", "dataSource")))
                   .body(template(APPLICATION_TEMPLATE) + ".sources.location",
                           hasItems("/template-application-dao-java/data/Entity.java.template",
                                   "/template-application-rest-java/api/EntityController.java.template",
                                   "/template-application-ui-harmonia-java/ui/shell/index.html.template"));
            given().when()
                   .get(CATALOGUE + "/menu")
                   .then()
                   .statusCode(200)
                   .body("[0].id", equalTo("menu-projects/templates/menu-mjs.js"))
                   .body(template("menu-projects/templates/menu-java.js") + ".label", equalTo("Java Service"));
            given().when()
                   .get(CATALOGUE + "/extensions")
                   .then()
                   .statusCode(200)
                   .body("$", hasItems("model", "glue", "form", "report", "mapping", "edm"));
        });

        assertEquals(0, evaluator.sessions(), "no script engine may be started to list the shipped templates");
    }

    @Test
    void generates_an_intent_without_running_a_script_and_keeps_the_project_descriptor() {
        writeProjectFile("app.intent", INTENT);
        evaluator.reset();

        generateIntent();
        assertEquals("{\"guid\":\"" + PROJECT + "\"}", readProjectFile("project.json"));
        assertEquals(0, evaluator.sessions(), "no script engine may be started by an intent Generate");

        String edited = "{\"guid\":\"" + PROJECT + "\",\"actions\":[]}";
        writeProjectFile("project.json", edited);
        generateIntent();
        assertEquals(edited, readProjectFile("project.json"), "a regeneration must keep the project descriptor the developer edited");
    }

    @Test
    void lists_and_generates_from_a_descriptor_written_as_javascript() {
        publish("/script/template.js", """
                export function getTemplate() {
                    return {
                        name: "Script Template IT",
                        description: "A descriptor written as JavaScript",
                        sources: [{
                            location: "/template-descriptors-it/script/greeting.txt.template",
                            action: "generate",
                            rename: "{{fileName}}"
                        }],
                        parameters: [{ name: "greeting", label: "Greeting" }]
                    };
                }
                """);
        publish("/script/greeting.txt.template", "Hello {{greeting}}");
        publish("/script/template.extension", extension(SCRIPT_TEMPLATE));
        synchronizationProcessor.forceProcessSynchronizers();

        restAssuredExecutor.execute(() -> {
            evaluator.reset();
            given().when()
                   .get(CATALOGUE)
                   .then()
                   .statusCode(200)
                   .body(template(SCRIPT_TEMPLATE) + ".name", equalTo("Script Template IT"))
                   .body(template(SCRIPT_TEMPLATE) + ".parameters.name", equalTo(List.of("greeting")));
            assertEquals(List.of(SCRIPT_TEMPLATE), evaluator.evaluatedModules(), "only the script descriptor may go through the engine");
        }, ASSERTION_TIMEOUT_SECONDS);

        generateFile("script/greeting.txt", SCRIPT_TEMPLATE, Map.of("greeting", "World"));

        assertEquals("Hello World", readProjectFile("script/greeting.txt"));
    }

    @Test
    void generates_from_a_json_descriptor_and_keeps_a_file_it_does_not_overwrite() {
        publish("/json/template.json", """
                {
                    "name": "JSON Template IT",
                    "sources": [
                        {"location": "/template-descriptors-it/json/notes.txt.template", "action": "generate", "rename": "notes.txt",
                         "overwrite": false},
                        {"location": "/template-descriptors-it/json/readme.txt.template", "action": "generate", "rename": "readme.txt"}
                    ],
                    "parameters": []
                }
                """);
        publish("/json/notes.txt.template", "generated notes");
        publish("/json/readme.txt.template", "generated readme");
        writeProjectFile("docs/notes.txt", "edited by hand");
        writeProjectFile("docs/readme.txt", "an old readme");
        evaluator.reset();

        generateFile("docs/request.txt", JSON_TEMPLATE, Map.of());

        assertEquals("edited by hand", readProjectFile("docs/notes.txt"));
        assertEquals("generated readme", readProjectFile("docs/readme.txt"));
        assertEquals(0, evaluator.sessions(), "no script engine may be started to read a JSON descriptor");
    }

    @Test
    void generates_ide_plug_ins_whose_descriptors_are_json_modules_their_pages_read() {
        writeProjectFile("project.json", "{\"guid\":\"" + PROJECT + "\"}");
        evaluator.reset();

        generateFile("orders", "template-view/template.js", Map.of("viewId", "template-descriptors-it-view", "viewName", "Orders"));
        generateFile("notes", "template-editor/template.js", Map.of("editorId", "template-descriptors-it-editor", "editorName", "Notes"));
        generateFile("sales", "template-perspective/template.js",
                Map.of("perspectiveId", "template-descriptors-it-perspective", "perspectiveName", "Sales"));

        assertPlugIn("orders.html", "extensions/orders.extension", "configs/orders-view.json", "viewData");
        assertPlugIn("notes.html", "extensions/notes.extension", "configs/notes-editor.json", "editorData");
        assertPlugIn("sales.html", "extensions/sales-perspective.extension", "configs/sales-perspective.json", "perspectiveData");
        assertEquals(PROJECT + "/configs/sales-perspective-menu.json",
                JsonPath.from(readProjectFile("extensions/sales-perspective-menu.extension"))
                        .getString("module"));
        assertEquals("template-descriptors-it-perspective", JsonPath.from(readProjectFile("configs/sales-perspective-menu.json"))
                                                                    .getString("perspectiveId"));

        for (String file : List.of("configs/orders-view.json", "extensions/orders.extension", "configs/notes-editor.json",
                "extensions/notes.extension", "configs/sales-perspective.json", "extensions/sales-perspective.extension",
                "configs/sales-perspective-menu.json", "extensions/sales-perspective-menu.extension")) {
            create(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + PROJECT + "/" + file, readProjectFile(file));
        }
        synchronizationProcessor.forceProcessSynchronizers();

        restAssuredExecutor.execute(() -> {
            given().when()
                   .get("/services/platform/extensions/views")
                   .then()
                   .body("find { it.id == 'template-descriptors-it-view' }.label", equalTo("Orders"));
            given().when()
                   .get("/services/platform/extensions/editors")
                   .then()
                   .body("find { it.id == 'template-descriptors-it-editor' }.label", equalTo("Notes"));
            given().when()
                   .get("/services/platform/extensions/perspectives")
                   .then()
                   .body("perspectives.find { it.id == 'template-descriptors-it-perspective' }.label", equalTo("Sales"));
            given().when()
                   .get("/services/platform/extensions/modules/" + PROJECT + "/configs/orders-view.json?global=viewData")
                   .then()
                   .statusCode(200)
                   .body(startsWith("const viewData = {\"id\":\"template-descriptors-it-view\""));
        }, ASSERTION_TIMEOUT_SECONDS);

        assertEquals(0, evaluator.sessions(), "no script engine may be started to generate or load an IDE plug-in");
    }

    /**
     * A generated plug-in registers its JSON descriptor as its extension module, and its page reads the
     * same module as its global.
     */
    private void assertPlugIn(String page, String extension, String descriptor, String global) {
        assertEquals(PROJECT + "/" + descriptor, JsonPath.from(readProjectFile(extension))
                                                         .getString("module"));
        assertEquals("/services/web/" + PROJECT + "/" + page, JsonPath.from(readProjectFile(descriptor))
                                                                      .getString("path"));
        String script = "/services/platform/extensions/modules/" + PROJECT + "/" + descriptor + "?global=" + global;
        assertTrue(readProjectFile(page).contains("src=\"" + script + "\""), page + " must read its descriptor from " + script);
    }

    private void generateIntent() {
        restAssuredExecutor.execute(() -> {
            List<Map<String, Object>> codeGenerations = given().when()
                                                               .post("/services/ide/intent/generate?workspace=" + WORKSPACE + "&project="
                                                                       + PROJECT + "&path=app.intent")
                                                               .then()
                                                               .statusCode(200)
                                                               .extract()
                                                               .jsonPath()
                                                               .getList("codeGenerations");
            assertFalse(codeGenerations.isEmpty(), "the intent must run its code generations");
            for (Map<String, Object> codeGeneration : codeGenerations) {
                assertEquals(Boolean.TRUE, codeGeneration.get("generated"),
                        "generating code from " + codeGeneration.get("path") + " failed: " + codeGeneration.get("error"));
            }
        });
    }

    private void generateFile(String path, String templateId, Map<String, Object> parameters) {
        // the IDE creates the user's workspace when the Workbench first opens; this test opens none
        if (!repository.hasCollection(WORKSPACE_PATH)) {
            repository.createCollection(WORKSPACE_PATH);
        }
        restAssuredExecutor.execute(() -> given().contentType("application/json")
                                                 .body(Map.of("template", templateId, "parameters", parameters))
                                                 .when()
                                                 .post(GENERATE_FILE + path)
                                                 .then()
                                                 .statusCode(201));
    }

    /** A JsonPath expression selecting the catalogue entry of the given template. */
    private static String template(String templateId) {
        return "find { it.id == '" + templateId + "' }";
    }

    private static String extension(String module) {
        return """
                {
                    "module": "%s",
                    "extensionPoint": "platform-templates",
                    "description": "Template descriptors IT - %s"
                }
                """.formatted(module, module);
    }

    private void publish(String location, String content) {
        create(IRepositoryStructure.PATH_REGISTRY_PUBLIC + TEMPLATES_PROJECT + location, content);
    }

    private void writeProjectFile(String path, String content) {
        String location = PROJECT_PATH + "/" + path;
        IResource existing = repository.getResource(location);
        if (existing.exists()) {
            existing.setContent(content.getBytes(StandardCharsets.UTF_8));
        } else {
            create(location, content);
        }
    }

    private String readProjectFile(String path) {
        return new String(repository.getResource(PROJECT_PATH + "/" + path)
                                    .getContent(),
                StandardCharsets.UTF_8);
    }

    private void create(String location, String content) {
        repository.createResource(location, content.getBytes(StandardCharsets.UTF_8));
        created.add(location);
    }

    @AfterEach
    void removeCreatedArtefacts() {
        created.stream()
               .filter(repository::hasResource)
               .forEach(repository::removeResource);
        created.clear();
        for (String folder : List.of(PROJECT_PATH, IRepositoryStructure.PATH_REGISTRY_PUBLIC + TEMPLATES_PROJECT,
                IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + PROJECT)) {
            if (repository.hasCollection(folder)) {
                repository.removeCollection(folder);
            }
        }
        synchronizationProcessor.forceProcessSynchronizers();
    }
}
