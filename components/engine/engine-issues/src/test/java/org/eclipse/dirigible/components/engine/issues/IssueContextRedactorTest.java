/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * The context bundle keeps everything an operator needs and loses every credential.
 */
class IssueContextRedactorTest {

    private static final String MASK = IssueContextRedactor.MASK;

    @Test
    void aPropertyNamedAfterACredentialIsMaskedWhereverItIsNested() {
        Object redacted = IssueContextRedactor.redact(Map.of("failedRequest",
                Map.of("headers", Map.of("Authorization", "Basic YWRtaW46YWRtaW4=", "Cookie", "JSESSIONID=abc", "Accept", "text/html"))));

        @SuppressWarnings("unchecked")
        Map<String, Object> headers =
                (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) redacted).get("failedRequest")).get("headers");
        assertEquals(MASK, headers.get("Authorization"));
        assertEquals(MASK, headers.get("Cookie"));
        assertEquals("text/html", headers.get("Accept"));
    }

    @Test
    void credentialsInsideTextAreMasked() {
        String text = "GET /api?access_token=s3cr3t&page=2 Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhIn0.sig "
                + "cookie JSESSIONID=ABC123; password=hunter2 and Basic YWRtaW46YWRtaW4=";

        String redacted = IssueContextRedactor.redactText(text);

        assertFalse(redacted.contains("s3cr3t"), redacted);
        assertFalse(redacted.contains("eyJhbGciOiJIUzI1NiJ9"), redacted);
        assertFalse(redacted.contains("ABC123"), redacted);
        assertFalse(redacted.contains("hunter2"), redacted);
        assertFalse(redacted.contains("YWRtaW46YWRtaW4="), redacted);
        assertTrue(redacted.contains("page=2"), redacted);
        assertTrue(redacted.startsWith("GET /api?access_token=" + MASK), redacted);
    }

    @Test
    void whatAnOperatorNeedsSurvives() {
        Map<String, Object> context = Map.of("requestIds", List.of("4f1c-77"), "url", "/services/web/app/#/Invoice/7", "status", 500,
                "errorType", "InternalServerError");

        assertEquals(context, IssueContextRedactor.redact(context));
    }

    @Test
    void aHostileInputIsRedactedInLinearTime() {
        String hostile = "Bearer " + "a".repeat(200_000) + "!" + " password:" + " ".repeat(200_000) + "x";

        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> IssueContextRedactor.redactText(hostile));
    }
}
