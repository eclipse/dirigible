/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.generator.edm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;
import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.junit.jupiter.api.Test;

/**
 * {@code stampOn: { status }} (dirigible #7874) reaches the repository template as one descriptor
 * per stamped field - the property, the status id it records the arrival in, and the Java "now" of
 * the field's type - and makes the field read-only, so no caller writes it.
 */
class EdmStatusStampTest {

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
                  - { name: approvedAt, type: timestamp, stampOn: { status: APPROVED } }
                  - { name: shippedOn, type: date, stampOn: { status: SHIPPED } }
                relations:
                  - { name: Status, kind: manyToOne, to: OrderStatus, function: EntityStatus, init: DRAFT }
            """;

    @Test
    void stampDescriptorCarriesPropertyStatusIdAndNowExpression() {
        Map<String, Object> order = entity(YAML, "Order");
        assertEquals("Status", order.get("statusStampProperty"));
        assertEquals(
                List.of(Map.of("property", "ApprovedAt", "statusId", "2", "nowExpression", "java.time.Instant.now()"),
                        Map.of("property", "ShippedOn", "statusId", "3", "nowExpression", "java.time.LocalDate.now()")),
                order.get("statusStamps"));
    }

    @Test
    void stampedFieldIsReadOnly() {
        Map<String, Object> order = entity(YAML, "Order");
        assertEquals("true", property(order, "ApprovedAt").get("isReadOnlyProperty"));
        assertEquals("true", property(order, "ShippedOn").get("isReadOnlyProperty"));
    }

    @Test
    void entityWithoutStampsHasNoStampVariables() {
        String plain = YAML.replace(", stampOn: { status: APPROVED }", "")
                           .replace(", stampOn: { status: SHIPPED }", "");
        Map<String, Object> order = entity(plain, "Order");
        assertFalse(order.containsKey("statusStamps"));
        assertFalse(order.containsKey("statusStampProperty"));
        assertNull(property(order, "ApprovedAt").get("isReadOnlyProperty"));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> entity(String yaml, String name) {
        Map<String, Object> model = EdmIntentGenerator.buildModelJsonForTest(IntentParser.parse(yaml), "orders");
        List<Map<String, Object>> entities = (List<Map<String, Object>>) ((Map<String, Object>) model.get("model")).get("entities");
        return entities.stream()
                       .filter(entity -> name.equals(entity.get("name")))
                       .findFirst()
                       .orElseThrow(() -> new AssertionError("no entity [" + name + "]"));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> property(Map<String, Object> entity, String name) {
        return ((List<Map<String, Object>>) entity.get("properties")).stream()
                                                                     .filter(property -> name.equals(property.get("name")))
                                                                     .findFirst()
                                                                     .orElseThrow(() -> new AssertionError("no property [" + name + "]"));
    }
}
