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

import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.junit.jupiter.api.Test;

/**
 * The ledger of an export that normalises a field's text records what each run changed (#7925);
 * every other ledger keeps the columns it had.
 */
class EdmExportLedgerTest {

    private static final String YAML = """
            name: payroll
            entities:
              - name: Employee
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
              - name: DeclarationRun
            exports:
              - name: Declaration
                source: Employee
                format: delimited
                ledger: DeclarationRun
                records:
                  - type: line
                    fields: [{ from: name NORMALISATION }]
            """;

    @Test
    void aNormalisingExportsLedgerRecordsWhatItChanged() {
        Map<String, Object> normalised = property(entity(YAML.replace(" NORMALISATION", ", case: upper"), "DeclarationRun"), "Normalised");
        assertEquals("CLOB", normalised.get("dataType"));
        assertEquals("true", normalised.get("isReadOnlyProperty"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void anyOtherLedgerKeepsItsColumns() {
        List<Map<String, Object>> properties =
                (List<Map<String, Object>>) entity(YAML.replace(" NORMALISATION", ""), "DeclarationRun").get("properties");
        assertFalse(properties.stream()
                              .anyMatch(property -> "Normalised".equals(property.get("name"))));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> entity(String yaml, String name) {
        Map<String, Object> model = EdmIntentGenerator.buildModelJsonForTest(IntentParser.parse(yaml), "payroll");
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
