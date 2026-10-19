/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * A process step can record when something happened - dirigible #7873.
 *
 * <p>
 * {@code setField} took only {@code string}/{@code text} fields and literal values, so a model that
 * wanted an {@code approvedAt} beside the status its flow writes had to hand-write a
 * {@code delegate:} whose whole body was one
 * {@code updateProperty(id, "ApprovedAt", Instant.now())}. The whole-value {@code {now}} token is
 * that stamp, and the field's type decides what a {@code setField} may assign: a
 * {@code timestamp}/{@code date} field takes only {@code {now}}, and {@code {now}} on a string
 * field - which would store the token's own text - is refused.
 */
class SetFieldNowIntentTest {

    private static final String YAML = """
            name: orders
            entities:
              - name: Order
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: note, type: string }
                  - { name: quantity, type: integer }
                  - { name: approvedAt, type: timestamp, readOnly: true }
                  - { name: approvedOn, type: date, readOnly: true }
            processes:
              - name: OrderApproval
                trigger: { onCreate: Order }
                steps:
                  - { name: stamp, kind: serviceTask, args: { setField: approvedAt, value: "{now}", next: done } }
                  - { name: done, kind: end }
            """;

    @Test
    void acceptsNowOnATimestampField() {
        assertDoesNotThrow(() -> IntentParser.parse(YAML));
    }

    @Test
    void acceptsNowOnADateField() {
        assertDoesNotThrow(() -> IntentParser.parse(YAML.replace("setField: approvedAt", "setField: approvedOn")));
    }

    /** A literal would have to be parsed into a java.time value - the stamp is the only value. */
    @Test
    void refusesALiteralOnATimestampField() {
        assertIssue(YAML.replace("value: \"{now}\"", "value: \"2026-01-01T00:00:00Z\""),
                "setField [approvedAt] on a [timestamp] field takes only the value {now}");
    }

    /** Before #7873 this stored the text "{now}", which is never what was meant. */
    @Test
    void refusesNowOnAStringField() {
        assertIssue(YAML.replace("setField: approvedAt", "setField: note"),
                "setField [note] value {now} needs a timestamp or date field - a string field would store the text \"{now}\"");
    }

    @Test
    void refusesNowInsideALargerValue() {
        assertIssue(YAML.replace("setField: approvedAt, value: \"{now}\"", "setField: note, value: \"at {now}\""),
                "setField value [at {now}] may use {now} only as the whole value");
    }

    /** Every other field type keeps the refusal it had. */
    @Test
    void refusesNowOnANumberField() {
        assertIssue(YAML.replace("setField: approvedAt", "setField: quantity"),
                "setField [quantity] must be a string/text field (only literal string values are supported)");
    }

    private static void assertIssue(String yaml, String expected) {
        IntentValidationException ex = assertThrows(IntentValidationException.class, () -> IntentParser.parse(yaml));
        assertTrue(ex.getIssues()
                     .stream()
                     .anyMatch(issue -> issue.contains(expected)),
                "expected an issue containing [" + expected + "] but got " + ex.getIssues());
    }
}
