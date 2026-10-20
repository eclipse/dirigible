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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.restassured.RestAssuredExecutor;
import org.eclipse.dirigible.tests.framework.security.SecurityUtil;
import org.eclipse.dirigible.tests.framework.tenant.DirigibleTestTenant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * The issues engine end to end over HTTP (#7948): a tenant user quotes a failed request's id in a
 * report, the instance's operator finds it among every tenant's issues and in the Inbox, works it,
 * asks the reporter something, the reporter answers with a file, and the operator resolves it -
 * while a user of another tenant can read none of it and a tenant's own administrator cannot open
 * the support desk.
 */
class IssuesEngineIT extends IntegrationTest {

    private static final String ISSUES = "/services/issues";

    private static final String OPERATOR_DESK = ISSUES + "/operator";

    private static final String OPERATOR = "issues-it-operator";

    private static final String OPERATOR_PASSWORD = "issues-it-password";

    private static final String MASK = "********";

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private SecurityUtil securityUtil;

    @Test
    void anIssueTravelsFromTheReporterToTheOperatorAndBackWithinItsTenantOnly() {
        DirigibleTestTenant reporterTenant = new DirigibleTestTenant("issues-it-reporter");
        DirigibleTestTenant otherTenant = new DirigibleTestTenant("issues-it-other");
        createTenants(reporterTenant, otherTenant);
        waitForTenantsProvisioning(List.of(reporterTenant, otherTenant));
        // the other tenant's user administers their own tenant - and still must not reach the desk
        securityUtil.assignRoleToUser(otherTenant.getUsername(), otherTenant.getId(), Roles.RoleNames.ADMINISTRATOR);
        securityUtil.ensureUserInDefaultTenant(OPERATOR, OPERATOR_PASSWORD, Roles.RoleNames.OPERATOR);

        // 1. a failed request answers its request id in the body and in the header, the same one
        AtomicReference<String> failedRequestId = new AtomicReference<>();
        restAssuredExecutor.execute(reporterTenant, () -> {
            Response failed = given().accept(ContentType.JSON)
                                     .when()
                                     .get(ISSUES + "/987654321");
            failed.then()
                  .statusCode(404)
                  .body("requestId", equalTo(failed.header("X-Request-Id")));
            failedRequestId.set(failed.header("X-Request-Id"));
        });
        assertNotNull(failedRequestId.get());

        // 2. the reporter files an issue quoting it; the credential in the bundle never reaches the store
        AtomicReference<Integer> issueId = new AtomicReference<>();
        restAssuredExecutor.execute(reporterTenant, () -> {
            issueId.set(given().contentType(ContentType.JSON)
                               .body(Map.of("subject", "Saving an invoice fails", "description", "Expected the invoice to be saved",
                                       "severity", "HIGH", "context",
                                       Map.of("requestIds", List.of(failedRequestId.get()), "url", "/services/web/application/#/invoices",
                                               "failedRequest",
                                               Map.of("method", "GET", "path", ISSUES + "/987654321", "status", 404, "headers",
                                                       Map.of("Authorization", "Basic c2VjcmV0OnNlY3JldA==")))))
                               .when()
                               .post(ISSUES)
                               .then()
                               .statusCode(201)
                               .body("status", equalTo("NEW"))
                               .body("tenantId", equalTo(reporterTenant.getId()))
                               .extract()
                               .path("id"));

            given().when()
                   .get(ISSUES)
                   .then()
                   .statusCode(200)
                   .body("id", hasItem(issueId.get()));

            given().when()
                   .get(ISSUES + "/" + issueId.get())
                   .then()
                   .statusCode(200)
                   .body("context.requestIds", hasItem(failedRequestId.get()))
                   .body("context.failedRequest.headers.Authorization", equalTo(MASK));
        });

        // 3. a user of another tenant reads nothing of it: not the issue, not the list, not the log lines
        restAssuredExecutor.execute(otherTenant, () -> {
            given().when()
                   .get(ISSUES + "/" + issueId.get())
                   .then()
                   .statusCode(404);
            given().when()
                   .get(ISSUES)
                   .then()
                   .statusCode(200)
                   .body("id", not(hasItem(issueId.get())));
            given().contentType(ContentType.JSON)
                   .body(Map.of("requestIds", List.of(failedRequestId.get())))
                   .when()
                   .post(ISSUES + "/excerpt")
                   .then()
                   .statusCode(200)
                   .body("$", empty());
            given().contentType(ContentType.JSON)
                   .body(Map.of("body", "let me in"))
                   .when()
                   .post(ISSUES + "/" + issueId.get() + "/messages")
                   .then()
                   .statusCode(404);
            // ... and administering its own tenant does not open the instance's support desk
            given().when()
                   .get(OPERATOR_DESK)
                   .then()
                   .statusCode(403);
        });

        // 4. the operator finds it under its tenant, and its triage task in the Inbox
        AtomicReference<String> triageTaskId = new AtomicReference<>();
        restAssuredExecutor.execute(() -> {
            given().when()
                   .get(OPERATOR_DESK + "?tenant=" + reporterTenant.getId())
                   .then()
                   .statusCode(200)
                   .body("id", hasItem(issueId.get()));

            String processInstanceId = given().when()
                                              .get(OPERATOR_DESK + "/" + issueId.get())
                                              .then()
                                              .statusCode(200)
                                              .body("issue.tenantId", equalTo(reporterTenant.getId()))
                                              .body("processInstanceId", notNullValue())
                                              .extract()
                                              .path("processInstanceId");

            List<Map<String, Object>> tasks = given().when()
                                                     .get("/services/inbox/tasks?type=groups")
                                                     .then()
                                                     .statusCode(200)
                                                     .extract()
                                                     .jsonPath()
                                                     .getList("findAll { it.processInstanceId == '" + processInstanceId + "' }");
            assertEquals(1, tasks.size(), "the triage task is in the operator's Inbox");
            triageTaskId.set((String) tasks.get(0)
                                           .get("id"));

            given().when()
                   .get(OPERATOR_DESK + "/process/" + processInstanceId)
                   .then()
                   .statusCode(200)
                   .body("id", equalTo(issueId.get()));
        }, OPERATOR, OPERATOR_PASSWORD);

        // 5. the Inbox's generic Complete cannot move the issue's process behind the issue's back
        restAssuredExecutor.execute(() -> given().contentType(ContentType.JSON)
                                                 .body(Map.of("action", "COMPLETE", "data", Map.of("decision", "CONFIRM")))
                                                 .when()
                                                 .post("/services/inbox/tasks/" + triageTaskId.get())
                                                 .then()
                                                 .statusCode(400),
                OPERATOR, OPERATOR_PASSWORD);

        // 6. the operator confirms it, leaves a memo, and asks the reporter for a screenshot
        restAssuredExecutor.execute(() -> {
            decide(issueId.get(), "CONFIRM", null).then()
                                                  .statusCode(200)
                                                  .body("status", equalTo("OPEN"));
            given().contentType(ContentType.JSON)
                   .body(Map.of("body", "Probably the numbering range - check before replying", "internal", true))
                   .when()
                   .post(OPERATOR_DESK + "/" + issueId.get() + "/messages")
                   .then()
                   .statusCode(200);
            decide(issueId.get(), "REQUEST", "Please attach a screenshot of the error").then()
                                                                                       .statusCode(200)
                                                                                       .body("status", equalTo("WAITING_FOR_REPORTER"));
        }, OPERATOR, OPERATOR_PASSWORD);

        // 7. the reporter reads the request - not the memo - and answers with the file
        restAssuredExecutor.execute(reporterTenant, () -> {
            Response thread = given().when()
                                     .get(ISSUES + "/" + issueId.get());
            thread.then()
                  .statusCode(200)
                  .body("issue.status", equalTo("WAITING_FOR_REPORTER"))
                  .body("messages.body", hasItem("Please attach a screenshot of the error"));
            assertFalse(thread.asString()
                              .contains("numbering range"),
                    "an internal memo is never served to the reporter");

            given().contentType(ContentType.JSON)
                   .body(Map.of("body", "Here it is"))
                   .when()
                   .post(ISSUES + "/" + issueId.get() + "/messages")
                   .then()
                   .statusCode(200);
            Integer attachmentId =
                    given().multiPart("file", "screenshot.txt", "the error text".getBytes(StandardCharsets.UTF_8), "text/plain")
                           .when()
                           .post(ISSUES + "/" + issueId.get() + "/attachments")
                           .then()
                           .statusCode(200)
                           .extract()
                           .path("id");
            given().when()
                   .get(ISSUES + "/" + issueId.get())
                   .then()
                   .statusCode(200)
                   .body("issue.status", equalTo("OPEN"));
            String content = given().when()
                                    .get(ISSUES + "/" + issueId.get() + "/attachments/" + attachmentId)
                                    .then()
                                    .statusCode(200)
                                    .header("Content-Disposition", org.hamcrest.Matchers.startsWith("attachment"))
                                    .extract()
                                    .asString();
            assertEquals("the error text", content);
        });

        // 8. the operator reads the answer and resolves the issue; the reporter can no longer post to it
        restAssuredExecutor.execute(() -> {
            given().when()
                   .get(OPERATOR_DESK + "/" + issueId.get())
                   .then()
                   .statusCode(200)
                   .body("messages.body", hasItem("Here it is"))
                   .body("attachments.fileName", hasItem("screenshot.txt"));
            decide(issueId.get(), "RESOLVE", "Fixed in the numbering range configuration").then()
                                                                                          .statusCode(200)
                                                                                          .body("status", equalTo("RESOLVED"));
        }, OPERATOR, OPERATOR_PASSWORD);
        restAssuredExecutor.execute(reporterTenant, () -> {
            given().when()
                   .get(ISSUES + "/" + issueId.get())
                   .then()
                   .statusCode(200)
                   .body("issue.status", equalTo("RESOLVED"))
                   .body("messages.body", hasItem("Fixed in the numbering range configuration"));
            given().contentType(ContentType.JSON)
                   .body(Map.of("body", "one more thing"))
                   .when()
                   .post(ISSUES + "/" + issueId.get() + "/messages")
                   .then()
                   .statusCode(409);
        });
    }

    private static Response decide(Integer issueId, String decision, String message) {
        Map<String, Object> body = message == null ? Map.of("decision", decision) : Map.of("decision", decision, "message", message);
        return given().contentType(ContentType.JSON)
                      .body(body)
                      .when()
                      .post(OPERATOR_DESK + "/" + issueId + "/decision");
    }
}
