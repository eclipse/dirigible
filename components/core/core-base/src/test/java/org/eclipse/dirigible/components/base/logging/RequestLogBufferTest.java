/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * The excerpt of a request serves only the tenant and the principal the request ran for, and the
 * buffer forgets by age and by count.
 */
class RequestLogBufferTest {

    private final RequestLogBuffer buffer = new RequestLogBuffer();

    @Test
    void anExcerptHoldsTheLinesOfTheAskedRequestsOnly() {
        buffer.add(line("r1", "t1", "alice", "first"));
        buffer.add(line("r2", "t1", "alice", "other request"));
        buffer.add(line("r1", "t1", "alice", "second"));

        List<RequestLogBuffer.Line> excerpt = buffer.excerpt(List.of("r1"), "t1", "alice");

        assertEquals(List.of("first", "second"), excerpt.stream()
                                                        .map(RequestLogBuffer.Line::message)
                                                        .toList());
    }

    @Test
    void aRequestIdQuotedByAnotherPrincipalOrTenantRevealsNothing() {
        buffer.add(line("r1", "t1", "alice", "alice's line"));

        assertTrue(buffer.excerpt(List.of("r1"), "t1", "mallory")
                         .isEmpty());
        assertTrue(buffer.excerpt(List.of("r1"), "t2", "alice")
                         .isEmpty());
    }

    @Test
    void theOldestLinesGoWhenTheCapacityIsReached() {
        buffer.configure(Duration.ofMinutes(10), 2);
        buffer.add(line("r1", "t1", "alice", "one"));
        buffer.add(line("r1", "t1", "alice", "two"));
        buffer.add(line("r1", "t1", "alice", "three"));

        assertEquals(List.of("two", "three"), buffer.excerpt(List.of("r1"), "t1", "alice")
                                                    .stream()
                                                    .map(RequestLogBuffer.Line::message)
                                                    .toList());
    }

    @Test
    void linesOlderThanTheRetentionAreForgotten() {
        buffer.configure(Duration.ofMinutes(10), 100);
        buffer.add(new RequestLogBuffer.Line(Instant.now()
                                                    .minus(Duration.ofMinutes(11)),
                "r1", "t1", "alice", "INFO", "logger", "stale"));
        buffer.add(line("r1", "t1", "alice", "fresh"));

        assertEquals(List.of("fresh"), buffer.excerpt(List.of("r1"), "t1", "alice")
                                             .stream()
                                             .map(RequestLogBuffer.Line::message)
                                             .toList());
    }

    private static RequestLogBuffer.Line line(String requestId, String tenantId, String principal, String message) {
        return new RequestLogBuffer.Line(Instant.now(), requestId, tenantId, principal, "INFO", "logger", message);
    }
}
