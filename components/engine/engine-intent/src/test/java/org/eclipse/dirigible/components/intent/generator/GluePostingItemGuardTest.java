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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.eclipse.dirigible.components.intent.parser.IntentValidationException;
import org.junit.jupiter.api.Test;

/**
 * The {@code when} of a posting item row and of a computed line (dirigible #7993): the event-guard
 * term grammar - a list of terms ANDed, a string equality, an ordering against a number - and never
 * a guard that silently becomes "always".
 */
class GluePostingItemGuardTest {

    private static final String YAML = """
            name: ledger
            entities:
              - name: VatType
                kind: setting
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
              - name: Invoice
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: net, type: decimal, precision: 18, scale: 2 }
                  - { name: vat, type: decimal, precision: 18, scale: 2 }
                  - { name: vatCode, type: string, length: 10 }
                  - { name: region, type: string, length: 10 }
                relations:
                  - { name: VatType, kind: manyToOne, to: VatType }
              - name: Entry
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                relations:
                  - { name: Invoice, kind: manyToOne, to: Invoice }
              - name: EntryLine
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: debit, type: decimal, precision: 18, scale: 2 }
                relations:
                  - { name: Entry, kind: manyToOne, to: Entry, composition: true, required: true }
            postings:
              - name: invoicePosting
                event: { onCreate: Invoice }
                creates: Entry
                backReference: Invoice
                items:
                  - { debit: "Net" }
                  - { debit: "Vat", when: GUARD }
            """;

    private static String guard(String when) {
        List<Map<String, Object>> rows = maps(GlueIntentGenerator.buildPostingsForTest(IntentParser.parse(YAML.replace("GUARD", when)))
                                                                 .get(0)
                                                                 .get("itemRows"));
        return GlueRendering.guard(rows.get(1));
    }

    @Test
    void aListOfTermsIsANDedAndAStringIsComparedAsAString() {
        assertEquals("!java.util.Objects.equals((String) source.VatCode, \"NOCR\") && Calc.eval(\"VatType\", source, 6)"
                + ".compareTo(new java.math.BigDecimal(\"3\")) != 0", guard("[\"vatCode != 'NOCR'\", \"VatType != 3\"]"));
        assertEquals("java.util.Objects.equals((String) source.Region, \"EU\")", guard("\"region == \\\"EU\\\"\""));
    }

    @Test
    void termsJoinedWithAndReadAsTheList() {
        assertEquals(guard("[\"Vat > 0\", \"region == 'EU'\"]"), guard("\"Vat > 0 && region == 'EU'\""));
    }

    @Test
    void aNumberMayBeOrdered() {
        assertEquals("Calc.eval(\"Vat\", source, 6).compareTo(new java.math.BigDecimal(\"-1.5\")) <= 0", guard("\"Vat <= -1.5\""));
    }

    /** One numeric equality keeps the reading it always had, so an existing glue does not change. */
    @Test
    void aSingleNumericEqualityKeepsItsReading() {
        List<Map<String, Object>> rows =
                maps(GlueIntentGenerator.buildPostingsForTest(IntentParser.parse(YAML.replace("GUARD", "\"Vat != 0\"")))
                                        .get(0)
                                        .get("itemRows"));
        assertEquals(Map.of("kind", "calcCompare", "owner", "source", "property", "Vat", "equal", false, "text", "0"), rows.get(1)
                                                                                                                           .get("guardReading"));
    }

    @Test
    void aGuardThatDoesNotReadIsRefusedNamingThePostingAndTheItem() {
        assertIssue("\"Vat ~ 0\"", "posting [invoicePosting] item 2 when [Vat ~ 0] must be");
        assertIssue("[\"Vat != 0\", \"vatCode > 'A'\"]", "posting [invoicePosting] item 2 when [Vat != 0 && vatCode > 'A'] must be");
    }

    @Test
    void aLocalSourceMustDeclareWhatTheGuardReads() {
        assertIssue("\"Vatt != 0\"", "reads [Vatt], which is not a field or to-one relation of the source [Invoice]");
        assertIssue("\"vat == '0'\"", "compares [vat] with a string, but it is not a string field of the source [Invoice]");
    }

    /** A guard let past the parser fails the Generate rather than booking the line always. */
    @Test
    void theGeneratorRefusesAGuardItCannotRead() {
        IntentModel model = IntentParser.parse(YAML.replace("GUARD", "\"Vat != 0\""));
        model.getPostings()
             .get(0)
             .getItems()
             .get(1)
             .put("when", "Vat >< 0");
        IntentValidationException refused =
                assertThrows(IntentValidationException.class, () -> GlueIntentGenerator.buildPostingsForTest(model));
        assertTrue(refused.getMessage()
                          .contains("posting [invoicePosting] item 2 when [Vat >< 0]"),
                refused.getMessage());
    }

    /** A computed line reads the same grammar, through the same parser. */
    @SuppressWarnings("unchecked")
    @Test
    void aComputedLineTakesTheSameGrammar() {
        String yaml = """
                name: billing
                entities:
                  - name: Timesheet
                    fields:
                      - { name: id, type: integer, primaryKey: true, generated: true }
                      - { name: amount, type: decimal, precision: 18, scale: 2 }
                      - { name: kind, type: string, length: 10 }
                  - name: Invoice
                    fields:
                      - { name: id, type: integer, primaryKey: true, generated: true }
                  - name: InvoiceItem
                    fields:
                      - { name: id, type: integer, primaryKey: true, generated: true }
                      - { name: price, type: decimal, precision: 18, scale: 2 }
                    relations:
                      - { name: Invoice, kind: manyToOne, to: Invoice, composition: true, required: true }
                generates:
                  - name: invoice-from-timesheet
                    from: Timesheet
                    to: Invoice
                    forEntity: Timesheet
                    items:
                      - { price: Amount, when: WHEN }
                """;
        Map<String, Object> line = ((List<Map<String, Object>>) GlueIntentGenerator.buildGeneratesForTest(
                IntentParser.parse(yaml.replace("WHEN", "[\"Amount > 0\", \"kind == 'billable'\"]")))
                                                                                   .get(0)
                                                                                   .get("itemLines")).get(0);
        assertEquals("Calc.eval(\"Amount\", source, 6).compareTo(new java.math.BigDecimal(\"0\")) > 0"
                + " && java.util.Objects.equals((String) source.Kind, \"billable\")", GlueRendering.guard(line));

        IntentValidationException refused =
                assertThrows(IntentValidationException.class, () -> IntentParser.parse(yaml.replace("WHEN", "\"Amount <> 0\"")));
        assertTrue(refused.getMessage()
                          .contains("generates [invoice-from-timesheet] item line 1 when [Amount <> 0] must be"),
                refused.getMessage());
    }

    private static void assertIssue(String when, String expected) {
        IntentValidationException refused =
                assertThrows(IntentValidationException.class, () -> IntentParser.parse(YAML.replace("GUARD", when)));
        assertTrue(refused.getMessage()
                          .contains(expected),
                "expected [" + expected + "] in: " + refused.getMessage());
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> maps(Object value) {
        return (List<Map<String, Object>>) value;
    }
}
