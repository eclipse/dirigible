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
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import io.restassured.http.ContentType;
import java.util.ArrayList;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The generated power controller honours the page its callers ask for (dirigible #7982), run
 * against a generated, compiled and published project:
 * <ul>
 * <li>a master's lines ({@code ?Order=<id>}) - what {@code api.getAll} walks with
 * {@code $limit}/{@code $offset} on a document page; they used to come back whole on every page, so
 * a document with 1000+ lines never finished loading;</li>
 * <li>{@code POST /search} with {@code $limit}/{@code $offset} in the body next to the conditions -
 * the shell's report widgets ask for 1 or 5 rows and were handed every match;</li>
 * <li>{@code POST /count}, counted in the database.</li>
 * </ul>
 * Without a page the lists stay whole, as before.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class GeneratedListPagingIT extends IntegrationTest {

    private static final String WORKSPACE = "workspace";
    private static final String PROJECT = "list-paging";
    private static final String ORDERS = "/services/java/" + PROJECT + "/gen/paging/api/order/OrderController";
    private static final String ITEMS = "/services/java/" + PROJECT + "/gen/paging/api/order/OrderItemController";
    private static final long TIMEOUT_SECONDS = 90;
    private static final int LINES = 7;

    private static final String INTENT = """
            name: paging
            description: list-paging fixture - an order and its lines

            entities:
              - name: Order
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: note, type: string, length: 40 }

              - name: OrderItem
                fields:
                  - { name: id,       type: integer, primaryKey: true, generated: true }
                  - { name: quantity, type: integer }
                relations:
                  - { name: Order, kind: manyToOne, to: Order, composition: true }
            """;

    @Autowired
    private IRepository repository;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Test
    void theGeneratedListsServeThePageTheyAreAskedFor() {
        writeIntent();
        generateProject();
        publishProject();
        synchronizationProcessor.forceProcessSynchronizers();

        int order = createOrder();
        createOrder(); // a second master, whose lines must not leak into the first one's
        List<Integer> lines = new ArrayList<>();
        for (int i = 0; i < LINES; i++) {
            lines.add(createLine(order, i));
        }

        // a master's lines, page by page: three pages of 3 + 3 + 1, together exactly the lines
        List<Integer> walked = new ArrayList<>();
        for (int offset = 0; offset < LINES; offset += 3) {
            List<Integer> page = ids(get(ITEMS + "?Order=" + order + "&$limit=3&$offset=" + offset));
            assertTrue(page.size() <= 3, "a page of the master's lines is at most the limit: " + page);
            walked.addAll(page);
        }
        assertEquals(LINES, walked.size(), "the pages cover every line once: " + walked);
        assertEquals(new HashSet<>(lines), new HashSet<>(walked));
        assertEquals(LINES, ids(get(ITEMS + "?Order=" + order)).size(), "without a page the master's lines are listed whole");

        // the search: the page rides in the body next to the conditions
        String conditions = "\"conditions\":[{\"propertyName\":\"Order\",\"operator\":\"EQ\",\"value\":" + order + "}]";
        assertEquals(1, ids(post(ITEMS + "/search", "{" + conditions + ",\"$limit\":1}")).size(), "a widget asking for one row gets one");
        assertEquals(List.of(walked.get(6)), ids(post(ITEMS + "/search", "{" + conditions + ",\"$limit\":3,\"$offset\":6}")),
                "the offset skips in the same stable order");
        assertEquals(LINES, ids(post(ITEMS + "/search", "{" + conditions + "}")).size(), "without a page the search is whole");

        // the count, in the database
        AtomicReference<Number> count = new AtomicReference<>();
        restAssuredExecutor.execute(() -> count.set(given().when()
                                                           .contentType(ContentType.JSON)
                                                           .body("{\"equals\":{\"Order\":" + order + "}}")
                                                           .post(ITEMS + "/count")
                                                           .then()
                                                           .statusCode(200)
                                                           .extract()
                                                           .path("count")),
                TIMEOUT_SECONDS);
        assertEquals(LINES, count.get()
                                 .intValue());
    }

    private static List<Integer> ids(List<Map<String, Object>> rows) {
        return rows.stream()
                   .map(row -> ((Number) row.get("Id")).intValue())
                   .toList();
    }

    private List<Map<String, Object>> get(String url) {
        AtomicReference<List<Map<String, Object>>> rows = new AtomicReference<>();
        restAssuredExecutor.execute(() -> rows.set(given().when()
                                                          .get(url)
                                                          .then()
                                                          .statusCode(200)
                                                          .extract()
                                                          .jsonPath()
                                                          .getList("$")),
                TIMEOUT_SECONDS);
        return rows.get();
    }

    private List<Map<String, Object>> post(String url, String body) {
        AtomicReference<List<Map<String, Object>>> rows = new AtomicReference<>();
        restAssuredExecutor.execute(() -> rows.set(given().when()
                                                          .contentType(ContentType.JSON)
                                                          .body(body)
                                                          .post(url)
                                                          .then()
                                                          .statusCode(200)
                                                          .extract()
                                                          .jsonPath()
                                                          .getList("$")),
                TIMEOUT_SECONDS);
        return rows.get();
    }

    private int createOrder() {
        AtomicReference<Integer> id = new AtomicReference<>();
        restAssuredExecutor.execute(() -> id.set(given().when()
                                                        .contentType(ContentType.JSON)
                                                        .body("{\"Note\":\"paging\"}")
                                                        .post(ORDERS)
                                                        .then()
                                                        .statusCode(200)
                                                        .extract()
                                                        .path("Id")),
                TIMEOUT_SECONDS);
        return id.get();
    }

    private int createLine(int order, int quantity) {
        AtomicReference<Integer> id = new AtomicReference<>();
        restAssuredExecutor.execute(() -> id.set(given().when()
                                                        .contentType(ContentType.JSON)
                                                        .body("{\"Order\":" + order + ",\"Quantity\":" + quantity + "}")
                                                        .post(ITEMS)
                                                        .then()
                                                        .statusCode(200)
                                                        .extract()
                                                        .path("Id")),
                TIMEOUT_SECONDS);
        return id.get();
    }

    private void generateProject() {
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
        String path = projectPath() + "/app.intent";
        IResource existing = repository.getResource(path);
        if (existing.exists()) {
            existing.setContent(INTENT.getBytes(StandardCharsets.UTF_8));
        } else {
            repository.createResource(path, INTENT.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static String projectPath() {
        return IRepositoryStructure.PATH_USERS + "/admin/" + WORKSPACE + "/" + PROJECT;
    }

    @AfterEach
    void cleanup() {
        restAssuredExecutor.execute(() -> given().when()
                                                 .delete("/services/ide/publisher/" + WORKSPACE + "/" + PROJECT)
                                                 .then()
                                                 .statusCode(both(greaterThanOrEqualTo(200)).and(lessThan(300))));
        if (repository.hasCollection(projectPath())) {
            repository.removeCollection(projectPath());
        }
        synchronizationProcessor.forceProcessSynchronizers();
    }
}
