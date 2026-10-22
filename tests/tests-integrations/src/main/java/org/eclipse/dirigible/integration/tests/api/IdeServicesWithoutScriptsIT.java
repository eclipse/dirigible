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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.integration.tests.api.ShellBootstrapWithoutScriptsIT.CountingExtensionModuleEvaluator;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.restassured.RestAssuredExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

/**
 * The IDE services that ran server-side JavaScript are answered by Java (#8023): the code editor's
 * file types, the Operations perspective's artefacts, the extension editor's extension points, the
 * tracing switch and the terminal token. The scripts are gone from the registry, so an answer can
 * only come from Java; the file types, the one service still reading extension modules, are read
 * without the JavaScript engine's evaluator being called.
 */
@Import(ShellBootstrapWithoutScriptsIT.CountingEvaluatorConfiguration.class)
// One Dirigible boot for the whole class: each method cleans up after itself.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class IdeServicesWithoutScriptsIT extends IntegrationTest {

    private static final String FILE_TYPES = "/services/js/editor-monaco/api/fileTypes.js";

    private static final String TRACING_SWITCH = "/services/js/perspective-tracing/service/enable-tracing.js";

    private static final String TERMINAL_TOKEN = "/services/js/view-terminal/js/token.js";

    private static final String TRACING_ENABLED = "DIRIGIBLE_TRACING_TASK_ENABLED";

    /** The scripts that used to answer, none of which may be published any more. */
    private static final List<String> RETIRED_SCRIPTS = List.of( //
            "/editor-monaco/api/fileTypes.js", //
            "/editor-monaco/api/extensions/core.js", //
            "/perspective-operations/api/artefacts/artefacts.mjs", //
            "/service-extensions/extensionPoints.mjs", //
            "/perspective-tracing/service/enable-tracing.js", //
            "/view-terminal/js/token.js", //
            "/view-databases/js/databaseTable.js");

    @Autowired
    private IRepository repository;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private CountingExtensionModuleEvaluator evaluator;

    @AfterEach
    void resetTracingFlag() {
        Configuration.remove(TRACING_ENABLED);
    }

    @Test
    void the_retired_scripts_are_gone_from_the_registry() {
        for (String script : RETIRED_SCRIPTS) {
            assertFalse(repository.hasResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + script), script + " must be gone");
        }
    }

    @Test
    void the_code_editor_reads_its_file_types_without_a_script() {
        evaluator.reset();

        restAssuredExecutor.execute(() -> given().when()
                                                 .get(FILE_TYPES)
                                                 .then()
                                                 .statusCode(200)
                                                 .contentType(startsWith("application/json"))
                                                 .body("'.java'", equalTo("java"))
                                                 .body("'.ts'", equalTo("typescript"))
                                                 .body("'.html.template'", equalTo("html")));

        assertEquals(List.of(), evaluator.evaluatedModules(), "the core file types must be read as a JSON module");
        assertEquals(0, evaluator.sessions(), "no script engine may be started to read the file types");
    }

    @Test
    void the_operations_artefacts_and_the_extension_points_come_from_the_java_endpoints() {
        restAssuredExecutor.execute(() -> given().when()
                                                 .get("/services/core/artefacts")
                                                 .then()
                                                 .statusCode(200)
                                                 .body("findAll { it.type == 'extensionpoint' }.name", hasItem("platform-file-types"))
                                                 .body("findAll { it.name == 'platform-file-types' }.status", hasItem("CREATED")));
        restAssuredExecutor.execute(() -> given().when()
                                                 .get("/services/core/extensionpoints")
                                                 .then()
                                                 .statusCode(200)
                                                 .body("name", hasItem("platform-file-types")));
    }

    @Test
    void the_tracing_switch_turns_tracing_over() {
        Configuration.set(TRACING_ENABLED, "false");

        restAssuredExecutor.execute(() -> {
            given().when()
                   .get(TRACING_SWITCH)
                   .then()
                   .statusCode(200)
                   .contentType(startsWith("text/plain"))
                   .body(equalTo("false"));
            given().when()
                   .post(TRACING_SWITCH)
                   .then()
                   .statusCode(200)
                   .body(equalTo("true"));
            given().when()
                   .get(TRACING_SWITCH)
                   .then()
                   .statusCode(200)
                   .body(equalTo("true"));
            given().when()
                   .post(TRACING_SWITCH)
                   .then()
                   .statusCode(200)
                   .body(equalTo("false"));
        });
        assertEquals("false", Configuration.get(TRACING_ENABLED));
    }

    @Test
    void the_terminal_reads_an_empty_token() {
        restAssuredExecutor.execute(() -> given().when()
                                                 .get(TERMINAL_TOKEN)
                                                 .then()
                                                 .statusCode(200)
                                                 .contentType(startsWith("application/json"))
                                                 .body("token", equalTo("")));
    }
}
