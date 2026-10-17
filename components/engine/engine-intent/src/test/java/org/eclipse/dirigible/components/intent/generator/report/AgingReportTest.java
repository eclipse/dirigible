/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.generator.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.intent.generator.TestContexts;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.eclipse.dirigible.components.intent.parser.IntentValidationException;
import org.junit.jupiter.api.Test;

/**
 * The {@code aging:} report shape (dirigible #7888): open documents bucketed by days past due as of
 * a report date. The generated query is RUN over a small invoice book on H2, so the assertions are
 * the figures an aged-receivables report shows - not the tokens of its SQL.
 */
class AgingReportTest {

    private static final String MODEL = """
            name: billing
            entities:
              - name: Customer
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: name, type: string }
              - name: Invoice
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: issuedOn, type: date }
                  - { name: due, type: date }
                  - { name: total, type: decimal }
                  - { name: paid, type: decimal }
                relations:
                  - { name: customer, kind: manyToOne, to: Customer, required: true }
            reports:
            #REPORT#
            """;

    private static final String AGED_RECEIVABLES = """
              - name: AgedReceivables
                source: Invoice
                dimensions: [customer.name]
                aging:
                  due: due
                  date: issuedOn
                  amount: "total - paid"
                  buckets: [0, 30, 60, 90]
            """;

    /** The invoice book the figures below are read off, aged as of 2026-06-30. */
    private static final String[] SCHEMA =
            {"CREATE TABLE \"BILLING_CUSTOMER\" (\"CUSTOMER_ID\" INT PRIMARY KEY, \"CUSTOMER_NAME\" VARCHAR(100))",
                    "CREATE TABLE \"BILLING_INVOICE\" (\"INVOICE_ID\" INT PRIMARY KEY, \"INVOICE_ISSUED_ON\" DATE, \"INVOICE_DUE\" DATE,"
                            + " \"INVOICE_TOTAL\" DECIMAL(19,2), \"INVOICE_PAID\" DECIMAL(19,2), \"INVOICE_CUSTOMER\" INT)",
                    "INSERT INTO \"BILLING_CUSTOMER\" VALUES (1, 'Acme'), (2, 'Settled Ltd')", "INSERT INTO \"BILLING_INVOICE\" VALUES"
                            // Acme: due tomorrow -> not yet due, never 0-30 (base-sales-invoices#27).
                            + " (1, DATE '2026-06-01', DATE '2026-07-01', 100, 0, 1),"
                            // due today, partially paid -> 0 days past due, its REMAINING 150 in 0-30.
                            + " (2, DATE '2026-06-01', DATE '2026-06-30', 200, 50, 1),"
                            // exactly 30 days past due -> still 0-30; 31 days -> 31-60.
                            + " (3, DATE '2026-05-01', DATE '2026-05-31', 10, NULL, 1),"
                            + " (4, DATE '2026-05-01', DATE '2026-05-30', 20, 0, 1),"
                            // 61 days -> 61-90; well past 90 -> 90+.
                            + " (5, DATE '2026-04-01', DATE '2026-04-30', 30, 0, 1),"
                            + " (6, DATE '2026-01-01', DATE '2026-03-01', 40, 0, 1),"
                            // no due date -> cannot be past one: not yet due.
                            + " (7, DATE '2026-06-01', NULL, 7, 0, 1),"
                            // paid in full -> not open; issued after the as-of date -> not open on it yet.
                            + " (8, DATE '2026-01-01', DATE '2026-02-01', 500, 500, 1),"
                            + " (9, DATE '2026-07-05', DATE '2026-08-05', 999, 0, 1),"
                            // Settled Ltd has nothing open, so it has no row at all.
                            + " (10, DATE '2026-01-01', DATE '2026-02-01', 80, 80, 2)"};

    private static Map<String, Object> document(String report) {
        IntentModel model = IntentParser.parse(MODEL.replace("#REPORT#", report));
        return ReportIntentGenerator.buildForTest(TestContexts.context(model), model.getReports()
                                                                                    .get(0));
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> rows(Map<String, Object> document, String key) {
        return (List<Map<String, Object>>) document.get(key);
    }

    @Test
    void theBucketsAreRunOverARealInvoiceBook() throws SQLException {
        Map<String, Map<String, BigDecimal>> report = run(document(AGED_RECEIVABLES), "DATE '2026-06-30'");

        assertEquals(List.of("Acme"), List.copyOf(report.keySet()), "a counterparty with nothing open must not list as a row of zeros");
        Map<String, BigDecimal> acme = report.get("Acme");
        assertAmount("107.00", acme, "Not Yet Due"); // due tomorrow (100) + no due date (7)
        assertAmount("160.00", acme, "Overdue 0-30"); // due today, partially paid (150) + exactly 30 days (10)
        assertAmount("20.00", acme, "Overdue 31-60");
        assertAmount("30.00", acme, "Overdue 61-90");
        assertAmount("40.00", acme, "Overdue 90+");
        assertAmount("357.00", acme, "Open Amount");

        BigDecimal buckets = acme.entrySet()
                                 .stream()
                                 .filter(entry -> !"Open Amount".equals(entry.getKey()))
                                 .map(Map.Entry::getValue)
                                 .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, buckets.compareTo(acme.get("Open Amount")), "the buckets must add up to the open amount: " + acme);
    }

    @Test
    void theAsOfDateMovesEveryDocumentAcrossTheBuckets() throws SQLException {
        // A month later: the invoice issued 2026-07-05 is open now, and everything has aged by 31 days.
        Map<String, BigDecimal> acme = run(document(AGED_RECEIVABLES), "DATE '2026-07-31'").get("Acme");

        assertAmount("1006.00", acme, "Not Yet Due"); // 999 due 2026-08-05 + no due date (7)
        assertAmount("100.00", acme, "Overdue 0-30"); // due 2026-07-01, 30 days
        assertAmount("150.00", acme, "Overdue 31-60"); // due 2026-06-30, 31 days
        assertAmount("30.00", acme, "Overdue 61-90"); // 61 and 62 days
        assertAmount("70.00", acme, "Overdue 90+");
        assertAmount("1356.00", acme, "Open Amount");
    }

    @Test
    void theReportDeclaresItsAsOfParameterDefaultingToTheDayItIsRun() {
        Map<String, Object> document = document(AGED_RECEIVABLES);

        List<Map<String, Object>> parameters = rows(document, "parameters");
        assertEquals(1, parameters.size());
        assertEquals("asOf", parameters.get(0)
                                       .get("name"));
        assertEquals("DATE", parameters.get(0)
                                       .get("type"));
        assertEquals("CURRENT_DATE", parameters.get(0)
                                               .get("initial"),
                "the as-of date defaults to today, resolved per request by the generated repository");

        assertEquals(
                List.of("Customer Name", "Not Yet Due", "Overdue 0-30", "Overdue 31-60", "Overdue 61-90", "Overdue 90+", "Open Amount"),
                rows(document, "columns").stream()
                                         .map(column -> column.get("alias"))
                                         .toList());
        String query = (String) document.get("query");
        assertTrue(query.contains("(COALESCE(Invoice.\"INVOICE_TOTAL\", 0) - COALESCE(Invoice.\"INVOICE_PAID\", 0)) <> 0"), query);
        assertTrue(query.contains("Invoice.\"INVOICE_ISSUED_ON\" <= CAST(:asOf AS DATE)"), query);
        assertTrue(query.contains("GROUP BY Customer.\"CUSTOMER_NAME\""), query);
    }

    @Test
    void aNamedAsOfAndATimestampDueAreHonoured() {
        String model = MODEL.replace("- { name: due, type: date }", "- { name: due, type: timestamp }");
        IntentModel parsed = IntentParser.parse(model.replace("#REPORT#", """
                  - name: AgedReceivables
                    source: Invoice
                    dimensions: [customer.name]
                    aging: { due: due, amount: total, buckets: [30], asOf: reportDate }
                """));
        Map<String, Object> document = ReportIntentGenerator.buildForTest(TestContexts.context(parsed), parsed.getReports()
                                                                                                              .get(0));

        assertEquals("reportDate", rows(document, "parameters").get(0)
                                                               .get("name"));
        String query = (String) document.get("query");
        assertTrue(query.contains("CAST(Invoice.\"INVOICE_DUE\" AS DATE) > CAST(:reportDate AS DATE)"),
                "a timestamp due is aged by its date: " + query);
        assertFalse(query.contains(":asOf"), query);
        assertEquals(List.of("Customer Name", "Not Yet Due", "Overdue 0-30", "Overdue 30+", "Open Amount"),
                rows(document, "columns").stream()
                                         .map(column -> column.get("alias"))
                                         .toList());
    }

    @Test
    void anAgingReportOverAStageClassifiedSourceCountsOnlyLiveDocuments() {
        // An aging report aggregates, so - like any aggregation - it defaults to the live stage: a draft
        // or voided invoice is not an open receivable.
        IntentModel parsed = IntentParser.parse("""
                name: billing
                entities:
                  - name: InvoiceStatus
                    function: Setting
                    fields:
                      - { name: id, type: integer, primaryKey: true, generated: true }
                      - { name: name, type: string }
                  - name: Invoice
                    fields:
                      - { name: id, type: integer, primaryKey: true, generated: true }
                      - { name: due, type: date }
                      - { name: total, type: decimal }
                    relations:
                      - { name: Status, kind: manyToOne, to: InvoiceStatus, function: EntityStatus, init: 1 }
                reports:
                  - name: Aged
                    source: Invoice
                    aging: { due: due, amount: total, buckets: [30] }
                seeds:
                  - name: invoice-statuses
                    entity: InvoiceStatus
                    rows:
                      - { id: 1, name: DRAFT, stage: draft }
                      - { id: 3, name: ISSUED, stage: live }
                """);
        String query = (String) ReportIntentGenerator.buildForTest(TestContexts.context(parsed), parsed.getReports()
                                                                                                       .get(0))
                                                     .get("query");
        assertTrue(query.contains("\"INVOICE_STATUS\" IN (3)"), query);
    }

    @Test
    void anIllFormedAgingBlockIsRefusedAtParse() {
        assertRefused("due: total, amount: total, buckets: [30]", "aging due [total] must be a date/timestamp field");
        assertRefused("amount: total, buckets: [30]", "aging has no due field");
        assertRefused("due: due, buckets: [30]", "aging has no amount");
        assertRefused("due: due, amount: \"total * 2\", buckets: [30]", "must be a numeric field or numeric fields joined by + and -");
        assertRefused("due: due, amount: \"total - customer.name\", buckets: [30]", "amount sums [customer.name], a [string] field");
        assertRefused("due: due, amount: total, buckets: []", "needs at least one bucket threshold");
        assertRefused("due: due, amount: total, buckets: [0]", "needs at least one bucket threshold");
        assertRefused("due: due, amount: total, buckets: [60, 30]", "buckets must ascend - got [30] after [60]");
        assertRefused("due: due, amount: total, buckets: [30], asOf: limit", "asOf [limit] must name the as-of parameter");
    }

    @Test
    void anAuthoredParameterCannotShadowTheAsOfDate() {
        IntentValidationException refused = assertThrows(IntentValidationException.class, () -> document("""
                  - name: Aged
                    source: Invoice
                    aging: { due: due, amount: total, buckets: [30] }
                    parameters:
                      - { name: asOf, target: issuedOn, op: le }
                """));
        assertTrue(refused.getMessage()
                          .contains("declares the as-of parameter [asOf] on its own behalf"),
                refused.getMessage());
    }

    @Test
    void aLedgerKindCannotBeAged() {
        IntentValidationException refused = assertThrows(IntentValidationException.class, () -> document("""
                  - name: Aged
                    kind: balance
                    source: Invoice
                    date: issuedOn
                    debit: total
                    credit: paid
                    dimensions: [customer]
                    aging: { due: due, amount: total, buckets: [30] }
                """));
        assertTrue(refused.getMessage()
                          .contains("aging cannot be combined with kind [balance]"),
                refused.getMessage());
    }

    private static void assertRefused(String aging, String expected) {
        IntentValidationException refused = assertThrows(IntentValidationException.class, () -> document("""
                  - name: Aged
                    source: Invoice
                    aging: { %s }
                """.formatted(aging)));
        assertTrue(refused.getMessage()
                          .contains(expected),
                "expected [" + expected + "] in: " + refused.getMessage());
    }

    private static void assertAmount(String expected, Map<String, BigDecimal> row, String column) {
        assertEquals(0, new BigDecimal(expected).compareTo(row.get(column)), column + " in " + row);
    }

    /**
     * Run the report with the as-of date bound as a literal - this test has no repository to bind it -
     * and read every row back as dimension -> column -> amount.
     */
    private static Map<String, Map<String, BigDecimal>> run(Map<String, Object> document, String asOf) throws SQLException {
        String query = ((String) document.get("query")).replace(":asOf", asOf);
        Map<String, Map<String, BigDecimal>> report = new LinkedHashMap<>();
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:aging;DB_CLOSE_DELAY=-1");
                Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            for (String ddl : SCHEMA) {
                statement.execute(ddl);
            }
            try (ResultSet rows = statement.executeQuery(query)) {
                ResultSetMetaData meta = rows.getMetaData();
                while (rows.next()) {
                    Map<String, BigDecimal> row = new LinkedHashMap<>();
                    for (int i = 2; i <= meta.getColumnCount(); i++) {
                        row.put(meta.getColumnLabel(i), rows.getBigDecimal(i));
                    }
                    report.put(rows.getString(1), row);
                }
            }
        }
        return report;
    }
}
