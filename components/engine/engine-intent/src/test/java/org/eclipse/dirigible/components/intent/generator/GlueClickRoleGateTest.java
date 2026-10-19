/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.junit.jupiter.api.Test;

/**
 * The role gates the glue records for the generated click endpoints (#7913): a create-from demands
 * the write gate of its source and of its target, a transition the write gate of its record - each
 * the gate the entity's own generated controller enforces.
 */
class GlueClickRoleGateTest {

    private static final String ENTITIES = """
            entities:
              - name: QuoteStatus
                function: Setting
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
              - name: Quote
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
                relations:
                  - { name: Status, kind: manyToOne, to: QuoteStatus, function: EntityStatus, init: 1 }
              - name: Order
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
            generates:
              - { name: order-from-quote, from: Quote, to: Order, map: { name: name }, sourceStatus: 2 }
            transitions:
              - { name: AcceptQuote, forEntity: Quote, from: [1], setStatus: 3 }
            """;

    @Test
    void withoutGrantsTheClicksDemandTheConventionWriteRoles() {
        IntentModel model = IntentParser.parse("name: sales\n" + ENTITIES);

        Map<String, Object> generate = GlueIntentGenerator.buildGeneratesForTest(model)
                                                          .get(0);
        assertEquals("sales.Quote.QuoteFullAccess", generate.get("sourceWriteRoles"));
        assertEquals("sales.Order.OrderFullAccess", generate.get("targetWriteRoles"));

        Map<String, Object> transition = GlueIntentGenerator.buildTransitionsForTest(model)
                                                            .get(0);
        assertEquals("sales.Quote.QuoteFullAccess", transition.get("writeRoles"));
    }

    @Test
    void authoredGrantsGateEachEndByItsOwnWriters() {
        IntentModel model = IntentParser.parse("name: sales\n" + ENTITIES + """
                permissions:
                  - { role: Clerk, can: [Quote:write] }
                  - { role: Buyer, can: [Order:create, Quote:read] }
                  - { role: Manager, can: [Quote:manage, Order:manage] }
                """);

        Map<String, Object> generate = GlueIntentGenerator.buildGeneratesForTest(model)
                                                          .get(0);
        // A read grant opens no write gate: Buyer may load the quote, not move it.
        assertEquals("Clerk,Manager", generate.get("sourceWriteRoles"));
        assertEquals("Buyer,Manager", generate.get("targetWriteRoles"));

        Map<String, Object> transition = GlueIntentGenerator.buildTransitionsForTest(model)
                                                            .get(0);
        assertEquals("Clerk,Manager", transition.get("writeRoles"));
    }

    @Test
    void aCrossModelTargetOwnerThatCannotBeReadFallsBackToItsConventionRole() {
        IntentModel model = IntentParser.parse("""
                name: projects
                uses:
                  - { model: sales }
                entities:
                  - name: Timesheet
                    fields:
                      - { name: id, type: integer, primaryKey: true, generated: true }
                      - { name: note, type: string }
                generates:
                  - { name: invoice-from-timesheet, from: Timesheet, to: SalesInvoice, uses: sales, map: { Note: note } }
                permissions:
                  - { role: ProjectLead, can: [Timesheet:write] }
                """);

        Map<String, Object> generate = GlueIntentGenerator.buildGeneratesForTest(model)
                                                          .get(0);
        assertEquals("ProjectLead", generate.get("sourceWriteRoles"));
        // The target's grants are authored in its own model; with no .model to read them from, the
        // owner's convention role is the gate.
        assertEquals("sales.SalesInvoice.SalesInvoiceFullAccess", generate.get("targetWriteRoles"));
    }
}
