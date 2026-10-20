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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
 * A posting item row's {@code when:} (dirigible #7993), run as the ledger reads it: a purchase with
 * no right to tax credit, or one outside the EU, must not debit deductible VAT.
 *
 * <p>
 * The guard used to be one {@code <field> ==|!= <number>} term, so the rule could not be written -
 * a list of terms, a string comparison and an ordering were all refused. The fixture carries one
 * row guarded by two string terms and one guarded by an ordering, and three documents that each
 * fail a different term, so a guard that read only its first term, or ORed them, books a line the
 * assertions do not allow.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class IntentPostingItemGuardIT extends IntegrationTest {

    private static final String WORKSPACE = "workspace";
    private static final String PROJECT = "itemguard";
    private static final String PROJECT_PATH = IRepositoryStructure.PATH_USERS + "/admin/" + WORKSPACE + "/" + PROJECT;
    private static final String API = "/services/java/" + PROJECT + "/gen/" + PROJECT + "/api";
    private static final String BILLS = API + "/bill/BillController";
    private static final String ENTRIES = API + "/entry/EntryController";
    private static final String LINES = API + "/entry/EntryLineController";
    private static final long TIMEOUT_SECONDS = 90;
    /** The posting handler runs off the source's create topic, after the commit. */
    private static final long POSTING_TIMEOUT_SECONDS = 30;

    private static final String INTENT_YAML = """
            name: itemguard
            description: posting item guards - a list of terms ANDed, a string equality and an ordering

            entities:
              - name: Bill
                fields:
                  - { name: id,      type: integer, primaryKey: true, generated: true }
                  - { name: net,     type: decimal, precision: 18, scale: 2 }
                  - { name: vat,     type: decimal, precision: 18, scale: 2 }
                  - { name: vatCode, type: string, length: 10 }
                  - { name: region,  type: string, length: 10 }

              - name: Entry
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                relations:
                  - { name: Bill, kind: manyToOne, to: Bill }

              - name: EntryLine
                fields:
                  - { name: id,     type: integer, primaryKey: true, generated: true }
                  - { name: debit,  type: decimal, precision: 18, scale: 2 }
                  - { name: credit, type: decimal, precision: 18, scale: 2 }
                relations:
                  - { name: Entry, kind: manyToOne, to: Entry, composition: true, required: true }

            postings:
              - name: billPosting
                event: { onCreate: Bill }
                creates: Entry
                backReference: Bill
                items:
                  - { debit: "Net" }
                  # Deductible VAT: with a right to tax credit, and inside the EU.
                  - { debit: "Vat", when: ["VatCode != 'NOCR'", "Region == 'EU'"] }
                  - { credit: "Net + Vat", when: "Net > 500" }
            """;

    @Autowired
    private IRepository repository;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Test
    void a_line_is_booked_only_for_the_source_its_guard_matches() {
        writeIntent(INTENT_YAML);
        generateProject();
        publishProject();
        synchronizationProcessor.forceProcessSynchronizers();

        // Both string terms hold and Net exceeds 500: every line is booked.
        int matching = create("{\"Net\":1000,\"Vat\":200,\"VatCode\":\"STD\",\"Region\":\"EU\"}");
        // No right to tax credit: the first string term fails, and Net does not exceed 500.
        int noCredit = create("{\"Net\":100,\"Vat\":20,\"VatCode\":\"NOCR\",\"Region\":\"EU\"}");
        // Outside the EU: only the second string term fails.
        int outsideEu = create("{\"Net\":100,\"Vat\":20,\"VatCode\":\"STD\",\"Region\":\"US\"}");

        // The whole entry is one unit of work, so once its lines are visible they are all there.
        assertLines(onlyEntryOf(matching), List.of(1000.0f, 200.0f), List.of(1200.0f));
        assertLines(onlyEntryOf(noCredit), List.of(100.0f), List.of());
        assertLines(onlyEntryOf(outsideEu), List.of(100.0f), List.of());
    }

    @Test
    void a_guard_that_does_not_read_fails_the_generate_naming_the_posting_and_the_item() {
        writeIntent(INTENT_YAML.replace("when: \"Net > 500\"", "when: \"Net >> 500\""));
        restAssuredExecutor.execute(() -> given().when()
                                                 .post("/services/ide/intent/generate?workspace=" + WORKSPACE + "&project=" + PROJECT
                                                         + "&path=app.intent")
                                                 .then()
                                                 .statusCode(422)
                                                 .body(containsString("posting [billPosting] item 3 when [Net >> 500] must be")));
        assertFalse(repository.getResource(PROJECT_PATH + "/itemguard.model")
                              .exists(),
                "a refused Generate must write no model file");
    }

    /**
     * The entry's debit and credit amounts - a guarded line that must be absent is an amount missing.
     */
    private void assertLines(int entry, List<Float> debits, List<Float> credits) {
        restAssuredExecutor.execute(() -> given().when()
                                                 .get(LINES + "?Entry=" + entry)
                                                 .then()
                                                 .statusCode(200)
                                                 .body("", hasSize(debits.size() + credits.size()))
                                                 .body("findAll { it.Debit != null }.Debit", containsInAnyOrder(debits.toArray()))
                                                 .body("findAll { it.Credit != null }.Credit", containsInAnyOrder(credits.toArray())),
                POSTING_TIMEOUT_SECONDS);
    }

    /**
     * The single entry posted for the bill. The back-reference is a plain association, so the list
     * endpoint ignores a {@code ?Bill=} filter and the rows are picked out here.
     */
    private int onlyEntryOf(int bill) {
        String ofBill = "findAll { it.Bill == " + bill + " }";
        AtomicInteger entry = new AtomicInteger();
        restAssuredExecutor.execute(() -> entry.set(given().when()
                                                           .get(ENTRIES)
                                                           .then()
                                                           .statusCode(200)
                                                           .body(ofBill, hasSize(1))
                                                           .extract()
                                                           .jsonPath()
                                                           .getInt(ofBill + "[0].Id")),
                POSTING_TIMEOUT_SECONDS);
        return entry.get();
    }

    private int create(String body) {
        AtomicInteger id = new AtomicInteger();
        restAssuredExecutor.execute(() -> id.set(given().contentType("application/json")
                                                        .body(body)
                                                        .when()
                                                        .post(BILLS)
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

    private void writeIntent(String yaml) {
        String path = PROJECT_PATH + "/app.intent";
        IResource existing = repository.getResource(path);
        if (existing.exists()) {
            existing.setContent(yaml.getBytes(StandardCharsets.UTF_8));
        } else {
            repository.createResource(path, yaml.getBytes(StandardCharsets.UTF_8));
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
