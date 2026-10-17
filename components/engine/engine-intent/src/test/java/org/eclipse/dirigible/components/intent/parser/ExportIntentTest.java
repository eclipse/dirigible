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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.dirigible.components.intent.model.ExportIntent;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.junit.jupiter.api.Test;

/**
 * The parse-time rules of the {@code exports:} construct (#7855): every reference a field, a
 * parameter, the ordering and the file name make is resolved before anything is generated.
 */
class ExportIntentTest {

    private static final String MODEL = """
            name: vat
            entities:
              - name: Supplier
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
                  - { name: vatNumber, type: string }
              - name: PurchaseInvoice
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: number, type: string }
                  - { name: documentDate, type: date }
                  - { name: postedAt, type: timestamp }
                  - { name: taxBase, type: decimal }
                relations:
                  - { name: supplier, kind: manyToOne, to: Supplier }
              - name: ExportRun
            reports:
              - name: PurchasesBySupplier
                source: PurchaseInvoice
                dimensions: [supplier.name]
                measures: [sum(taxBase), count(*)]
                parameters:
                  - { name: fromDate, target: documentDate, op: ge }
            """;

    private static final String VALID = MODEL + """
            exports:
              - name: VatPurchaseLedger
                source: PurchaseInvoice
                format: fixedWidth
                encoding: windows-1251
                ledger: ExportRun
                fileName: "VAT-{fromDate}.txt"
                orderBy: [documentDate]
                parameters:
                  - { name: fromDate, field: documentDate, op: ge, required: true }
                  - { name: supplier }
                records:
                  - type: header
                    fields: [{ literal: H, width: 1 }, { param: fromDate, width: 8, pattern: ddMMyyyy }]
                  - type: line
                    fields:
                      - { from: supplier.vatNumber, width: 15 }
                      - { from: taxBase, width: 15, scale: 2, align: right, pad: '0' }
                  - type: trailer
                    fields: [{ from: count(), width: 5 }, { from: sum(taxBase), width: 15, scale: 2 }]
              - name: SupplierPurchases
                source: PurchasesBySupplier
                format: xml
                ledger: ExportRun
                parameters: [{ name: fromDate }]
                records:
                  - type: line
                    element: Supplier
                    fields:
                      - { from: supplier.name, name: Name, attribute: true }
                      - { from: count(*), name: Invoices }
                  - type: trailer
                    fields: [{ from: sum(sum(taxBase)), name: TaxBase, scale: 2 }]
              - name: Notes
                source: PurchaseInvoice
                format: delimited
                delimiter: ';'
                quoting: all
                ledger: ExportRun
                records:
                  - type: line
                    fields: [{ from: number }]
            """;

    @Test
    void aWellFormedExportParses() {
        IntentModel model = IntentParser.parse(VALID);

        assertEquals(3, model.getExports()
                             .size());
        ExportIntent ledger = model.getExports()
                                   .get(0);
        assertEquals("windows-1251", ledger.getEncoding());
        assertEquals(3, ledger.getRecords()
                              .size());
        assertEquals(15, ledger.getRecords()
                               .get(1)
                               .getFields()
                               .get(1)
                               .getWidth());
    }

    @Test
    void anUnknownFieldOrHopIsRefused() {
        assertRefused(replace("{ from: supplier.vatNumber, width: 15 }", "{ from: supplier.iban, width: 15 }"),
                "[iban] is not a field of [Supplier]");
        assertRefused(replace("{ from: supplier.vatNumber, width: 15 }", "{ from: amount, width: 15 }"),
                "[amount] is not a field of [PurchaseInvoice]");
    }

    @Test
    void aReportFieldMustBeOneOfItsColumns() {
        assertRefused(replace("{ from: count(*), name: Invoices }", "{ from: supplier.vatNumber, name: Vat }"),
                "[supplier.vatNumber] is not a dimension or measure of report [PurchasesBySupplier]");
    }

    @Test
    void aHeaderCannotReadARow() {
        assertRefused(replace("{ literal: H, width: 1 }", "{ from: number, width: 1 }"), "a header or trailer is written once");
    }

    @Test
    void aSumMustAddANumber() {
        assertRefused(replace("{ from: sum(taxBase), width: 15, scale: 2 }", "{ from: sum(number), width: 15, scale: 2 }"),
                "sums [number], which is a string field, not a number");
    }

    @Test
    void aPositionalFieldNeedsAWidthAndOnlyItMayHaveOne() {
        assertRefused(replace("{ from: supplier.vatNumber, width: 15 }", "{ from: supplier.vatNumber }"), "needs a width of at least 1");
        assertRefused(replace("fields: [{ from: number }]", "fields: [{ from: number, width: 4 }]"),
                "declares width, which only a fixedWidth export has");
    }

    @Test
    void anXmlFieldNeedsAName() {
        assertRefused(replace("{ from: sum(sum(taxBase)), name: TaxBase, scale: 2 }", "{ from: sum(sum(taxBase)), scale: 2 }"),
                "needs a name - the XML element it is written as");
    }

    @Test
    void aParameterMustNameWhatItFilters() {
        assertRefused(replace("- { name: supplier }", "- { name: buyer }"),
                "filters [buyer], which is neither a field nor a to-one relation of [PurchaseInvoice]");
        assertRefused(replace("- { name: supplier }", "- { name: postedAt }"), "filters the timestamp [postedAt]");
        assertRefused(replace("parameters: [{ name: fromDate }]", "parameters: [{ name: toDate }]"),
                "parameter [toDate] is not a parameter of report [PurchasesBySupplier]");
    }

    @Test
    void aFileNameTokenMustBeADeclaredParameter() {
        assertRefused(replace("VAT-{fromDate}.txt", "VAT-{period}.txt"), "token {period} is not a declared parameter of the export");
        assertRefused(replace("VAT-{fromDate}.txt", "out/VAT.txt"), "carries a path separator");
    }

    @Test
    void theLedgerIsADeclaredEntityWithNoFieldsOfItsOwn() {
        assertRefused(VALID.replaceFirst("ledger: ExportRun", "ledger: Missing"), "ledger [Missing] is not a declared entity");
        assertRefused(VALID.replaceFirst("ledger: ExportRun", "ledger: Supplier"),
                "entity [Supplier] is the ledger of export [VatPurchaseLedger] and must declare no fields and no relations");
    }

    @Test
    void anUnsupportedEncodingIsRefused() {
        assertRefused(replace("encoding: windows-1251", "encoding: no-such-charset"), "encoding [no-such-charset] is not supported");
    }

    @Test
    void theSigningStepIsReservedForNow() {
        assertRefused(replace("    format: delimited\n", "    format: delimited\n    submit: { kind: https }\n"),
                "declares submit:, the signed-submission step, which is not implemented yet");
    }

    @Test
    void anExportCannotShareItsNameWithAnEntity() {
        assertRefused(replace("- name: Notes", "- name: Supplier"), "shares its name with an entity, report, process or form");
    }

    private static String replace(String target, String replacement) {
        assertTrue(VALID.contains(target), "the fixture must contain [" + target + "]");
        return VALID.replace(target, replacement);
    }

    private static void assertRefused(String yaml, String expected) {
        IntentValidationException refused = assertThrows(IntentValidationException.class, () -> IntentParser.parse(yaml));
        assertTrue(refused.getMessage()
                          .contains(expected),
                "expected [" + expected + "] in: " + refused.getMessage());
    }
}
