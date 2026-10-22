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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
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
 * One setter step writes several members of one record in one statement - dirigible #8042.
 *
 * <p>
 * {@code set: { slotKey: null, Status: DONE }} is one {@code updateProperties} of the job, so its
 * {@code lifecycle:} judges the change as a whole: a legal move releases the slot, moves the status
 * and stamps {@code finishedAt} together, and an illegal one fails the step with every member left
 * as it was - the slot is not reclaimed by a move the lifecycle refused. The job's own process
 * writes the trigger-record form ({@code errorMessage: null, Status: RUNNING}); the finish and
 * restart flows write the job one hop away, through the job's repository.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class IntentSetManyIT extends IntegrationTest {

    private static final String WORKSPACE = "workspace";
    private static final String PROJECT = "setmany";
    private static final String PROJECT_PATH = IRepositoryStructure.PATH_USERS + "/admin/" + WORKSPACE + "/" + PROJECT;
    private static final String API = "/services/java/" + PROJECT + "/gen/" + PROJECT + "/api";
    private static final String JOBS = API + "/job/JobController";
    private static final String FINISHES = API + "/jobfinish/JobFinishController";
    private static final String RESTARTS = API + "/jobrestart/JobRestartController";
    private static final int RUNNING = 2;
    private static final int DONE = 3;
    private static final long TIMEOUT_SECONDS = 90;
    /** The steps run once the create event has started the instance. */
    private static final long PROCESS_TIMEOUT_SECONDS = 60;
    /** A setter step takes no retry:, so its job dead-letters on the engine's default retry cycle. */
    private static final long DEAD_LETTER_TIMEOUT_SECONDS = 180;

    private static final String INTENT_YAML = """
            name: setmany
            description: a job starts, finishes and refuses a restart, each a single write of several members

            seeds:
              - name: job-statuses
                entity: JobStatus
                rows:
                  - { id: 1, name: QUEUED }
                  - { id: 2, name: RUNNING }
                  - { id: 3, name: DONE }

            entities:
              - name: JobStatus
                kind: setting
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string, length: 50 }
              - name: Job
                fields:
                  - { name: id,           type: integer, primaryKey: true, generated: true }
                  - { name: errorMessage, type: string, length: 2000 }
                  - { name: slotKey,      type: string, length: 64 }
                  - { name: finishedAt,   type: timestamp, stampOn: { status: DONE } }
                relations:
                  - { name: Status, kind: manyToOne, to: JobStatus, function: EntityStatus, init: QUEUED }
                lifecycle:
                  edges:
                    - { from: QUEUED,  to: [RUNNING] }
                    - { from: RUNNING, to: [DONE] }
              - name: JobFinish
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                relations:
                  - { name: job, kind: manyToOne, to: Job, required: true }
              - name: JobRestart
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                relations:
                  - { name: job, kind: manyToOne, to: Job, required: true }

            processes:
              - name: RunJob
                trigger: { onCreate: Job }
                steps:
                  # A step named like the start event deploys - the start event moves aside (#8058).
                  - { name: start, kind: serviceTask, args: { set: { errorMessage: null, Status: RUNNING }, next: done } }
                  - { name: done,  kind: end }
              - name: Finish
                trigger: { onCreate: JobFinish }
                steps:
                  - { name: finishJob, kind: serviceTask, args: { set: { job.slotKey: null, job.Status: DONE }, next: done } }
                  - { name: done,      kind: end }
              - name: Restart
                trigger: { onCreate: JobRestart }
                steps:
                  - { name: restartJob, kind: serviceTask, args: { set: { job.slotKey: reclaimed, job.Status: RUNNING }, next: done } }
                  - { name: done,       kind: end }
            """;

    @Autowired
    private IRepository repository;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Test
    void a_set_map_writes_its_members_in_one_statement_the_lifecycle_judges_as_a_whole() {
        generateProject();
        publishProject();
        synchronizationProcessor.forceProcessSynchronizers();

        int job = create(JOBS, "{\"ErrorMessage\":\"stale\",\"SlotKey\":\"slot-1\"}");

        // The trigger-record form: the old error goes and the status moves, in the job's own write.
        restAssuredExecutor.execute(() -> given().when()
                                                 .get(JOBS + "/" + job)
                                                 .then()
                                                 .statusCode(200)
                                                 .body("Status", equalTo(RUNNING))
                                                 .body("ErrorMessage", nullValue())
                                                 .body("SlotKey", equalTo("slot-1")),
                PROCESS_TIMEOUT_SECONDS);

        // One hop: the slot is released as the job completes - and the stamp lands in the same write.
        create(FINISHES, "{\"Job\":" + job + "}");
        restAssuredExecutor.execute(() -> given().when()
                                                 .get(JOBS + "/" + job)
                                                 .then()
                                                 .statusCode(200)
                                                 .body("Status", equalTo(DONE))
                                                 .body("SlotKey", nullValue())
                                                 .body("FinishedAt", notNullValue()),
                PROCESS_TIMEOUT_SECONDS);

        // DONE -> RUNNING is no edge: the step fails, and the slot it would have reclaimed stays free.
        int restart = create(RESTARTS, "{\"Job\":" + job + "}");
        awaitStepFailure(processOf(restart), "cannot move from");
        restAssuredExecutor.execute(() -> given().when()
                                                 .get(JOBS + "/" + job)
                                                 .then()
                                                 .statusCode(200)
                                                 .body("Status", equalTo(DONE))
                                                 .body("SlotKey", nullValue()));
    }

    /** The instance the trigger started for a restart, read off the id it stamped back. */
    private String processOf(int restart) {
        AtomicReference<String> process = new AtomicReference<>();
        restAssuredExecutor.execute(() -> process.set(given().when()
                                                             .get(RESTARTS + "/" + restart)
                                                             .then()
                                                             .statusCode(200)
                                                             .body("ProcessId", notNullValue())
                                                             .extract()
                                                             .path("ProcessId")),
                PROCESS_TIMEOUT_SECONDS);
        return process.get();
    }

    private void awaitStepFailure(String processInstance, String reason) {
        restAssuredExecutor.execute(() -> given().when()
                                                 .get("/services/bpm/bpm-processes/instance/" + processInstance + "/jobs")
                                                 .then()
                                                 .statusCode(200)
                                                 .body("size()", greaterThan(0))
                                                 .body("[0].exceptionMessage", containsString(reason)),
                DEAD_LETTER_TIMEOUT_SECONDS);
    }

    private int create(String controller, String body) {
        AtomicInteger id = new AtomicInteger();
        restAssuredExecutor.execute(() -> id.set(given().contentType("application/json")
                                                        .body(body)
                                                        .when()
                                                        .post(controller)
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
