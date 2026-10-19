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
 * A process moves the status of the record its trigger record belongs to - dirigible #7934.
 *
 * <p>
 * {@code setRelationField: purchaseOrder.Status} in an approval request's flow writes the
 * {@code PurchaseOrder} the request points at, through the ORDER's generated repository, so
 * everything the order declares on its own writes applies: its {@code lifecycle:} refuses a move it
 * does not model (and the step fails, as the order's own setter would), its {@code stampOn} field
 * records the arrival in the same write, and its {@code -transitioned} event goes out - observed
 * here by a process the order itself triggers {@code onTransition}. And because flows move it, the
 * order's status is the flow's column: a plain edit of it is refused.
 *
 * <p>
 * The refused move runs first, on a fresh DRAFT order, so the order it leaves untouched is the one
 * the legal moves then walk - an order that read DRAFT at the end could otherwise simply never have
 * been written.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class IntentOneHopSetterIT extends IntegrationTest {

    private static final String WORKSPACE = "workspace";
    private static final String PROJECT = "onehop";
    private static final String PROJECT_PATH = IRepositoryStructure.PATH_USERS + "/admin/" + WORKSPACE + "/" + PROJECT;
    private static final String API = "/services/java/" + PROJECT + "/gen/" + PROJECT + "/api";
    private static final String ORDERS = API + "/purchaseorder/PurchaseOrderController";
    private static final String APPROVALS = API + "/approvalrequest/ApprovalRequestController";
    private static final String SHIPMENTS = API + "/shipmentrequest/ShipmentRequestController";
    private static final int DRAFT = 1;
    private static final int APPROVED = 2;
    private static final int SHIPPED = 3;
    private static final long TIMEOUT_SECONDS = 90;
    /** The steps run once the create event has started the instance. */
    private static final long PROCESS_TIMEOUT_SECONDS = 60;
    /** A setter step takes no retry:, so its job dead-letters on the engine's default retry cycle. */
    private static final long DEAD_LETTER_TIMEOUT_SECONDS = 180;

    private static final String INTENT_YAML =
            """
                    name: onehop
                    description: an approval request moves its purchase order to APPROVED, a shipment request moves it to SHIPPED

                    seeds:
                      - name: order-statuses
                        entity: OrderStatus
                        rows:
                          - { id: 1, name: DRAFT }
                          - { id: 2, name: APPROVED }
                          - { id: 3, name: SHIPPED }

                    entities:
                      - name: OrderStatus
                        kind: setting
                        fields:
                          - { name: id,   type: integer, primaryKey: true, generated: true }
                          - { name: name, type: string, length: 50 }
                      - name: PurchaseOrder
                        fields:
                          - { name: id,         type: integer, primaryKey: true, generated: true }
                          - { name: note,       type: string, length: 200 }
                          - { name: witness,    type: string, length: 50 }
                          - { name: approvedAt, type: timestamp, stampOn: { status: APPROVED } }
                        relations:
                          - { name: Status, kind: manyToOne, to: OrderStatus, function: EntityStatus, init: DRAFT }
                        lifecycle:
                          edges:
                            - { from: DRAFT,    to: [APPROVED] }
                            - { from: APPROVED, to: [SHIPPED] }
                      - name: ApprovalRequest
                        fields:
                          - { name: id,      type: integer, primaryKey: true, generated: true }
                          - { name: comment, type: string, length: 200 }
                        relations:
                          - { name: purchaseOrder, kind: manyToOne, to: PurchaseOrder, required: true }
                      - name: ShipmentRequest
                        fields:
                          - { name: id,      type: integer, primaryKey: true, generated: true }
                          - { name: carrier, type: string, length: 100 }
                        relations:
                          - { name: purchaseOrder, kind: manyToOne, to: PurchaseOrder, required: true }

                    processes:
                      - name: Approve
                        trigger: { onCreate: ApprovalRequest }
                        steps:
                          - { name: approveOrder, kind: serviceTask, args: { setRelationField: purchaseOrder.Status, value: APPROVED, next: noteOrder } }
                          - { name: noteOrder,    kind: serviceTask, args: { setField: purchaseOrder.note, value: approved by request, next: done } }
                          - { name: done,         kind: end }
                      - name: Ship
                        trigger: { onCreate: ShipmentRequest }
                        steps:
                          - { name: shipOrder, kind: serviceTask, args: { setRelationField: purchaseOrder.Status, value: SHIPPED, next: done } }
                          - { name: done,      kind: end }
                      - name: WitnessApproval
                        trigger: { onTransition: PurchaseOrder, when: "Status == APPROVED" }
                        steps:
                          - { name: witness, kind: serviceTask, args: { setField: witness, value: transitioned, next: done } }
                          - { name: done,    kind: end }
                    """;

    @Autowired
    private IRepository repository;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Test
    void a_childs_process_moves_its_parents_status_through_the_parents_repository() {
        generateProject();
        publishProject();
        synchronizationProcessor.forceProcessSynchronizers();

        int order = create(ORDERS, "{\"Note\":\"draft\"}");

        // The other entities' flows move the order's status, so it is the flow's column (#7339): a plain
        // edit cannot skip them straight to APPROVED.
        restAssuredExecutor.execute(() -> given().contentType("application/json")
                                                 .body("{\"Id\":" + order + ",\"Note\":\"draft\",\"Status\":" + APPROVED + "}")
                                                 .when()
                                                 .put(ORDERS + "/" + order)
                                                 .then()
                                                 .statusCode(409)
                                                 .body(containsString("changes through the workflow")));

        // DRAFT -> SHIPPED is no edge of the order's lifecycle: the step fails, the order is untouched.
        int earlyShipment = create(SHIPMENTS, "{\"PurchaseOrder\":" + order + ",\"Carrier\":\"acme\"}");
        awaitStepFailure(processOf(earlyShipment), "cannot move from");
        assertOrder(order, DRAFT);

        // DRAFT -> APPROVED is: the move and its stamp land, then the second hop writes the note...
        create(APPROVALS, "{\"PurchaseOrder\":" + order + ",\"Comment\":\"looks fine\"}");
        restAssuredExecutor.execute(() -> given().when()
                                                 .get(ORDERS + "/" + order)
                                                 .then()
                                                 .statusCode(200)
                                                 .body("Status", equalTo(APPROVED))
                                                 .body("ApprovedAt", notNullValue())
                                                 .body("Note", equalTo("approved by request")),
                PROCESS_TIMEOUT_SECONDS);
        // ...and the order's own -transitioned consumer saw the move.
        restAssuredExecutor.execute(() -> given().when()
                                                 .get(ORDERS + "/" + order)
                                                 .then()
                                                 .statusCode(200)
                                                 .body("Witness", equalTo("transitioned")),
                PROCESS_TIMEOUT_SECONDS);

        // APPROVED -> SHIPPED is an edge, so the same step that failed above now moves the order.
        create(SHIPMENTS, "{\"PurchaseOrder\":" + order + ",\"Carrier\":\"acme\"}");
        restAssuredExecutor.execute(() -> given().when()
                                                 .get(ORDERS + "/" + order)
                                                 .then()
                                                 .statusCode(200)
                                                 .body("Status", equalTo(SHIPPED)),
                PROCESS_TIMEOUT_SECONDS);
    }

    private void assertOrder(int order, int status) {
        restAssuredExecutor.execute(() -> given().when()
                                                 .get(ORDERS + "/" + order)
                                                 .then()
                                                 .statusCode(200)
                                                 .body("Status", equalTo(status))
                                                 .body("ApprovedAt", nullValue()));
    }

    /** The instance the trigger started for a shipment request, read off the id it stamped back. */
    private String processOf(int shipment) {
        AtomicReference<String> process = new AtomicReference<>();
        restAssuredExecutor.execute(() -> process.set(given().when()
                                                             .get(SHIPMENTS + "/" + shipment)
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
