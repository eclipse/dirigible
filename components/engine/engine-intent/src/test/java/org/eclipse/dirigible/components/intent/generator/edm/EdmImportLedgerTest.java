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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.junit.jupiter.api.Test;

/**
 * An entity a file import writes into is flagged for its {@code
 *
<TABLE>
 * _IMPORTS} ledger (#7856), and no other entity is - the flag is what makes the schema template
 * emit the ledger table.
 */
class EdmImportLedgerTest {

    private static final String YAML = """
            name: bank
            entities:
              - name: StatementLine
                fields:
                  - { name: id,     type: integer, primaryKey: true, generated: true }
                  - { name: amount, type: decimal }
              - name: Account
                fields:
                  - { name: id,   type: integer, primaryKey: true, generated: true }
                  - { name: iban, type: string }
            imports:
              - { name: swift, into: StatementLine, format: mt940, map: { amount: amount } }
            """;

    @Test
    @SuppressWarnings("unchecked")
    void only_the_imported_entity_carries_the_ledger_flag_and_its_write_gate_is_unchanged() {
        Map<String, Object> model = EdmIntentGenerator.buildModelJsonForTest(IntentParser.parse(YAML), "bank");
        List<Map<String, Object>> entities = (List<Map<String, Object>>) ((Map<String, Object>) model.get("model")).get("entities");

        Map<String, Object> line = entity(entities, "StatementLine");
        assertEquals("true", line.get("importLedger"));
        assertEquals("bank.StatementLine.StatementLineFullAccess", line.get("roleWrite"),
                "the shared write-gate derivation keeps the convention role the controller checks");
        assertNull(entity(entities, "Account").get("importLedger"));
    }

    private static Map<String, Object> entity(List<Map<String, Object>> entities, String name) {
        return entities.stream()
                       .filter(entity -> name.equals(entity.get("name")))
                       .findFirst()
                       .orElseThrow();
    }
}
