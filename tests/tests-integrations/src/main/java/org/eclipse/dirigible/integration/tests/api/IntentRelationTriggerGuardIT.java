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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
 * A process trigger guarded by a seeded NAME on a to-one relation that is not the status starts on
 * exactly the records of that kind - dirigible #8015.
 *
 * <p>
 * {@code when: "type == CLOSE"} names a seed of the request-type nomenclature. Before the fix the
 * name reached the generated listener as a string compared with the integer foreign key, so the
 * guard never held and neither flow ever started, with generation, compile and publish all green.
 * Each flow here stamps its own field, so a request of each type proves both that its own flow ran
 * and that the other one did not.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class IntentRelationTriggerGuardIT extends IntegrationTest {

    private static final String WORKSPACE = "workspace";
    private static final String PROJECT = "requestflows";
    private static final String PROJECT_PATH = IRepositoryStructure.PATH_USERS + "/admin/" + WORKSPACE + "/" + PROJECT;
    private static final String REQUESTS = "/services/java/" + PROJECT + "/gen/" + PROJECT + "/api/request/RequestController";
    private static final int OPEN = 1;
    private static final int CLOSE = 2;
    private static final long TIMEOUT_SECONDS = 90;
    /** The steps run once the create event has started the instance. */
    private static final long PROCESS_TIMEOUT_SECONDS = 60;

    private static final String INTENT_YAML = """
            name: requestflows
            description: a request starts the flow of its own type

            seeds:
              - name: request-types
                entity: RequestType
                rows:
                  - { id: 1, name: OPEN }
                  - { id: 2, name: CLOSE }

            entities:
              - name: RequestType
                kind: setting
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string, length: 50 }
              - name: Request
                fields:
                  - { name: id,       type: integer, primaryKey: true, generated: true }
                  - { name: note,     type: string, length: 200 }
                  - { name: openedBy, type: string, length: 50 }
                  - { name: closedBy, type: string, length: 50 }
                relations:
                  - { name: type, kind: manyToOne, to: RequestType, required: true }

            processes:
              - name: OpenFlow
                trigger: { onCreate: Request, when: "type == OPEN" }
                steps:
                  - { name: markOpened, kind: serviceTask, args: { setField: openedBy, value: open flow, next: done } }
                  - { name: done,       kind: end }
              - name: CloseFlow
                trigger: { onCreate: Request, when: "type == CLOSE" }
                steps:
                  - { name: markClosed, kind: serviceTask, args: { setField: closedBy, value: close flow, next: done } }
                  - { name: done,       kind: end }
            """;

    @Autowired
    private IRepository repository;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Test
    void a_trigger_guard_naming_a_seed_of_a_relation_starts_only_the_flow_of_that_kind() {
        generateProject();
        publishProject();
        synchronizationProcessor.forceProcessSynchronizers();

        int closing = create("{\"Type\":" + CLOSE + ",\"Note\":\"please close\"}");
        awaitRequest(closing, "ClosedBy", "close flow");
        assertNotRun(closing, "OpenedBy");

        int opening = create("{\"Type\":" + OPEN + ",\"Note\":\"please open\"}");
        awaitRequest(opening, "OpenedBy", "open flow");
        assertNotRun(opening, "ClosedBy");
    }

    private void awaitRequest(int request, String property, String value) {
        restAssuredExecutor.execute(() -> given().when()
                                                 .get(REQUESTS + "/" + request)
                                                 .then()
                                                 .statusCode(200)
                                                 .body(property, equalTo(value)),
                PROCESS_TIMEOUT_SECONDS);
    }

    private void assertNotRun(int request, String property) {
        restAssuredExecutor.execute(() -> given().when()
                                                 .get(REQUESTS + "/" + request)
                                                 .then()
                                                 .statusCode(200)
                                                 .body(property, nullValue()));
    }

    private int create(String body) {
        AtomicInteger id = new AtomicInteger();
        restAssuredExecutor.execute(() -> id.set(given().contentType("application/json")
                                                        .body(body)
                                                        .when()
                                                        .post(REQUESTS)
                                                        .then()
                                                        .statusCode(200)
                                                        .extract()
                                                        .path("Id")),
                TIMEOUT_SECONDS);
        return id.get();
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
