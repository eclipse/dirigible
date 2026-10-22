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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.restassured.RestAssuredExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * A seeded name compared against a to-one relation that is NOT the status resolves in a check's
 * {@code when} and in a {@code visibleWhen} (dirigible #8057), as it does in a trigger guard since
 * #8015: a request whose kinds take different inputs names the kind, not its positional id.
 *
 * <p>
 * The model is generated and published, and the generated controller is asked to refuse a CLOSE
 * without a note and a note on an OPEN with the declared messages; the generated form shows the
 * note only while the dropdown holds the CLOSE seed's id, and the attachment panel only on a CLOSE
 * request.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class IntentRelationNameConditionsIT extends IntegrationTest {

    private static final String WORKSPACE = "workspace";
    private static final String PROJECT = "requestkinds";
    private static final String PROJECT_PATH = IRepositoryStructure.PATH_USERS + "/admin/" + WORKSPACE + "/" + PROJECT;
    private static final String REQUESTS = "/services/java/" + PROJECT + "/gen/" + PROJECT + "/api/request/RequestController";
    private static final String NOTE_REQUIRED = "A CLOSE request says why";
    private static final String NOTE_FORBIDDEN = "Only a CLOSE request takes a closing note";
    private static final int OPEN = 1;
    private static final int CLOSE = 2;
    private static final long TIMEOUT_SECONDS = 90;

    private static final String INTENT_YAML = """
            name: requestkinds
            description: a request whose kinds take different inputs

            entities:
              - name: RequestType
                function: Setting
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string, length: 20 }

              - name: Request
                checks:
                  - { kind: requiredWhen, field: closingNote, when: "type == CLOSE", message: "%s" }
                  - { kind: forbidWhen, when: ["type != CLOSE", "closingNote != null"], message: "%s" }
                fields:
                  - { name: id,          type: integer, primaryKey: true, generated: true }
                  - { name: closingNote, type: string, length: 500, visibleWhen: "type == CLOSE" }
                relations:
                  - { name: type, kind: manyToOne, to: RequestType, required: true }

              - name: RequestAttachment
                visibleWhen: "type == CLOSE"
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: file, type: string, length: 100 }
                relations:
                  - { name: Request, kind: manyToOne, to: Request, composition: true }

            seeds:
              - name: request-types
                entity: RequestType
                rows:
                  - { id: 1, name: OPEN }
                  - { id: 2, name: CLOSE }
            """.formatted(NOTE_REQUIRED, NOTE_FORBIDDEN);

    @Autowired
    private IRepository repository;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Test
    void conditions_naming_a_request_type_compare_its_seed_id() {
        generateProject();

        String form = contentOf("gen/" + PROJECT + "/views/Request/Request-form.html");
        assertTrue(form.contains("String(form.Type)) === '" + CLOSE + "'"),
                "the closing note must be shown only for the CLOSE seed's id, got: " + form);
        String panel = contentOf("gen/" + PROJECT + "/js/components/pages/Request/RequestAttachment.detail.js");
        assertTrue(panel.contains("visibleWhen: [{ property: 'Type', equal: true, value: '" + CLOSE + "' }]"),
                "the attachment panel must be gated on the master's CLOSE seed id, got: " + panel);

        publishProject();
        synchronizationProcessor.forceProcessSynchronizers();

        create("{\"Type\":" + CLOSE + "}", 400, NOTE_REQUIRED);
        create("{\"Type\":" + OPEN + ",\"ClosingNote\":\"done\"}", 400, NOTE_FORBIDDEN);
        create("{\"Type\":" + CLOSE + ",\"ClosingNote\":\"resolved by phone\"}", 200, null);
        create("{\"Type\":" + OPEN + "}", 200, null);
    }

    private void create(String body, int status, String message) {
        restAssuredExecutor.execute(() -> {
            var response = given().contentType("application/json")
                                  .body(body)
                                  .when()
                                  .post(REQUESTS)
                                  .then()
                                  .statusCode(status);
            if (message != null) {
                response.body(containsString(message));
            }
        }, TIMEOUT_SECONDS);
    }

    private String contentOf(String relativePath) {
        IResource resource = repository.getResource(PROJECT_PATH + "/" + relativePath);
        assertTrue(resource.exists(), "not generated: " + relativePath);
        return new String(resource.getContent(), StandardCharsets.UTF_8);
    }

    private void generateProject() {
        writeIntent();
        AtomicReference<List<Map<String, Object>>> plan = new AtomicReference<>();
        restAssuredExecutor.execute(() -> plan.set(given().when()
                                                          .post("/services/ide/intent/generate?workspace=" + WORKSPACE + "&project="
                                                                  + PROJECT + "&path=app.intent")
                                                          .then()
                                                          .statusCode(200)
                                                          .extract()
                                                          .jsonPath()
                                                          .getList("codeGenerations")));
        for (Map<String, Object> codeGeneration : plan.get()) {
            assertEquals(Boolean.TRUE, codeGeneration.get("generated"),
                    "generating code from " + codeGeneration.get("path") + " failed: " + codeGeneration.get("error"));
        }
    }

    private void publishProject() {
        restAssuredExecutor.execute(() -> given().when()
                                                 .post("/services/ide/publisher/" + WORKSPACE + "/" + PROJECT + "/")
                                                 .then()
                                                 .statusCode(200));
    }

    private void writeIntent() {
        String path = PROJECT_PATH + "/app.intent";
        IResource existing = repository.getResource(path);
        if (existing.exists()) {
            existing.setContent(INTENT_YAML.getBytes(StandardCharsets.UTF_8));
        } else {
            repository.createResource(path, INTENT_YAML.getBytes(StandardCharsets.UTF_8));
        }
    }

    @AfterEach
    void cleanup() {
        restAssuredExecutor.execute(() -> given().when()
                                                 .delete("/services/ide/publisher/" + WORKSPACE + "/" + PROJECT)
                                                 .then()
                                                 .statusCode(greaterThanOrEqualTo(200)));
        if (repository.hasCollection(PROJECT_PATH)) {
            repository.removeCollection(PROJECT_PATH);
        }
        synchronizationProcessor.forceProcessSynchronizers();
    }
}
