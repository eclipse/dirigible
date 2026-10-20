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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.dirigible.components.intent.model.EntityIntent;
import org.eclipse.dirigible.components.intent.model.FieldIntent;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.junit.jupiter.api.Test;

/**
 * A field records when its record entered a status - dirigible #7874.
 *
 * <p>
 * {@code stampOn: { status: X }} makes the generated repository the field's only writer, stamping
 * it with every move into {@code X}. So the parser refuses everything that would be a second
 * writer, a field that cannot hold a moment, and a status the record cannot be in.
 */
class StatusStampIntentTest {

    private static final String YAML = """
            name: orders
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
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string, length: 50 }
              - name: Order
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: note, type: string }
                  - { name: approvedAt, type: timestamp, stampOn: { status: APPROVED } }
                  - { name: shippedOn, type: date }
                relations:
                  - { name: Status, kind: manyToOne, to: OrderStatus, function: EntityStatus, init: DRAFT }
            """;

    @Test
    void acceptsStampOnATimestampField() {
        assertDoesNotThrow(() -> IntentParser.parse(YAML));
    }

    @Test
    void acceptsStampOnADateField() {
        assertDoesNotThrow(() -> IntentParser.parse(
                YAML.replace("{ name: shippedOn, type: date }", "{ name: shippedOn, type: date, stampOn: { status: SHIPPED } }")));
    }

    /** A status is authored by its seeded name and reaches every generator as the seed id. */
    @Test
    void resolvesTheSeedNameToItsId() {
        assertEquals(2, field(IntentParser.parse(YAML), "approvedAt").getStampOn()
                                                                     .getStatus());
    }

    @Test
    void refusesWithoutAnEntityStatusRelation() {
        String yaml = YAML.replace(", function: EntityStatus, init: DRAFT", "")
                          .replace("status: APPROVED", "status: 2");
        assertIssue(yaml, "entity [Order] field [approvedAt] stampOn requires the entity to declare a function: EntityStatus relation");
    }

    @Test
    void refusesAnUnknownStatus() {
        assertIssue(YAML.replace("status: APPROVED", "status: CANCELLED"),
                "names [CANCELLED], which is not a seeded status of [OrderStatus]");
    }

    @Test
    void refusesAnUnseededStatusId() {
        assertIssue(YAML.replace("status: APPROVED", "status: 9"),
                "entity [Order] field [approvedAt] stampOn names [9], which is not a seeded status of [OrderStatus]");
    }

    @Test
    void refusesAStringField() {
        assertIssue(YAML.replace("type: timestamp, stampOn", "type: string, stampOn"),
                "entity [Order] field [approvedAt] stampOn needs a timestamp or date field, not [string]");
    }

    @Test
    void refusesARequiredField() {
        assertIssue(YAML.replace("type: timestamp, stampOn", "type: timestamp, required: true, stampOn"),
                "entity [Order] field [approvedAt] stampOn cannot be required: true");
    }

    @Test
    void refusesWithDefaultValue() {
        assertIssue(YAML.replace("type: timestamp, stampOn", "type: timestamp, defaultValue: CURRENT_TIMESTAMP, stampOn"),
                "entity [Order] field [approvedAt] stampOn cannot declare a defaultValue");
    }

    @Test
    void refusesACalculatedField() {
        assertIssue(YAML.replace("type: timestamp, stampOn", "type: timestamp, calculatedOnCreate: \"java.time.Instant.now()\", stampOn"),
                "entity [Order] field [approvedAt] stampOn cannot be calculated");
    }

    @Test
    void refusesTwoFieldsStampingTheSameStatus() {
        assertIssue(YAML.replace("{ name: shippedOn, type: date }", "{ name: shippedOn, type: date, stampOn: { status: 2 } }"),
                "entity [Order] field [shippedOn] stampOn stamps the same status as field [approvedAt]");
    }

    @Test
    void refusesAnUnknownKeyInsideStampOn() {
        assertIssue(YAML.replace("stampOn: { status: APPROVED }", "stampOn: { status: APPROVED, relation: Status }"),
                "unknown key [relation]");
    }

    @Test
    void refusesAStampWithNoStatus() {
        assertIssue(YAML.replace("stampOn: { status: APPROVED }", "stampOn: {}"),
                "entity [Order] field [approvedAt] stampOn names no status");
    }

    private static FieldIntent field(IntentModel model, String name) {
        for (EntityIntent entity : model.getEntities()) {
            for (FieldIntent field : entity.getFields()) {
                if (name.equals(field.getName())) {
                    return field;
                }
            }
        }
        throw new AssertionError("no field [" + name + "]");
    }

    private static void assertIssue(String yaml, String expected) {
        IntentValidationException ex = assertThrows(IntentValidationException.class, () -> IntentParser.parse(yaml));
        assertTrue(ex.getIssues()
                     .stream()
                     .anyMatch(issue -> issue.contains(expected)),
                "expected an issue containing [" + expected + "] but got " + ex.getIssues());
    }
}
