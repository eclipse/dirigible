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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.intent.generator.TestContexts;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.model.ReportIntent;
import org.eclipse.dirigible.components.intent.parser.IntentParser;
import org.junit.jupiter.api.Test;

/**
 * The generated {@code kind: ledger} query (dirigible #7887), RUN against a real little chart of
 * accounts and journal - the account analysis is arithmetic over an ordering, and a running balance
 * that restarts per page, a group account that misses a grandchild or a window that disagrees with
 * the trial balance all produce well-formed SQL and a plausible wrong number.
 */
class LedgerReportSqlTest {

    private static final String INTENT = """
            name: ledger
            entities:
              - name: Account
                hierarchy: parent
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: code, type: string }
                relations:
                  - { name: parent, kind: manyToOne, to: Account }
              - name: JournalEntry
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: entryDate, type: date }
                  - { name: reference, type: string }
                  - { name: posted, type: integer }
                relations:
                  - { name: items, kind: oneToMany, to: JournalEntryItem }
              - name: JournalEntryItem
                fields:
                  - { name: id, type: integer, primaryKey: true, generated: true }
                  - { name: debit, type: decimal }
                  - { name: credit, type: decimal }
                  - { name: note, type: string }
                relations:
                  - { name: journalEntry, kind: manyToOne, to: JournalEntry, composition: true }
                  - { name: account, kind: manyToOne, to: Account, required: true }
            reports:
              - name: AccountAnalysis
                kind: ledger
                source: JournalEntryItem
                date: journalEntry.entryDate
                debit: debit
                credit: credit
                account: account
                columns: [journalEntry.reference, note]
                correspondence: account.code
                filter: "journalEntry.posted == 1"
              - name: TrialBalance
                kind: balance
                source: JournalEntryItem
                date: journalEntry.entryDate
                debit: debit
                credit: credit
                dimensions: [account.code]
                filter: "journalEntry.posted == 1"
            """;

    /** Account ids of the chart below. */
    private static final int GROUP_41 = 1;
    private static final int GROUP_411 = 2;
    private static final int LEAF_412 = 3;
    private static final int LEAF_4111 = 6;

    /**
     * 41 is a group over 411 and 412, and 411 is itself a group over 4111 - so a group two levels up
     * must reach a grandchild. The window is 2026: J1 is before it, J6 after it, J5 is unposted.
     */
    private static final String[] SCHEMA = {
            "CREATE TABLE \"LEDGER_ACCOUNT\" (\"ACCOUNT_ID\" INT PRIMARY KEY, \"ACCOUNT_CODE\" VARCHAR(20), \"ACCOUNT_PARENT\" INT)",
            "CREATE TABLE \"LEDGER_JOURNAL_ENTRY\" (\"JOURNAL_ENTRY_ID\" INT PRIMARY KEY, \"JOURNAL_ENTRY_ENTRY_DATE\" DATE,"
                    + " \"JOURNAL_ENTRY_REFERENCE\" VARCHAR(20), \"JOURNAL_ENTRY_POSTED\" INT)",
            "CREATE TABLE \"LEDGER_JOURNAL_ENTRY_ITEM\" (\"JOURNAL_ENTRY_ITEM_ID\" INT PRIMARY KEY,"
                    + " \"JOURNAL_ENTRY_ITEM_DEBIT\" DECIMAL(19,2), \"JOURNAL_ENTRY_ITEM_CREDIT\" DECIMAL(19,2),"
                    + " \"JOURNAL_ENTRY_ITEM_NOTE\" VARCHAR(100), \"JOURNAL_ENTRY_ITEM_JOURNAL_ENTRY\" INT,"
                    + " \"JOURNAL_ENTRY_ITEM_ACCOUNT\" INT)",
            "INSERT INTO \"LEDGER_ACCOUNT\" VALUES (1,'41',NULL), (2,'411',1), (3,'412',1), (4,'50',NULL), (5,'70',NULL), (6,'4111',2)",
            "INSERT INTO \"LEDGER_JOURNAL_ENTRY\" VALUES (1, DATE '2025-12-20', 'J1', 1), (2, DATE '2026-01-10', 'J2', 1),"
                    + " (3, DATE '2026-02-01', 'J3', 1), (4, DATE '2026-02-01', 'J4', 1), (5, DATE '2026-03-01', 'J5', 0),"
                    + " (6, DATE '2027-01-05', 'J6', 1)",
            // J4 is compound: 412 and cash debited against one sales credit.
            "INSERT INTO \"LEDGER_JOURNAL_ENTRY_ITEM\" VALUES (1, 100, NULL, 'sale', 1, 2), (2, NULL, 100, NULL, 1, 5),"
                    + " (3, 40, NULL, 'sale', 2, 6), (4, NULL, 40, NULL, 2, 5), (5, 70, NULL, NULL, 3, 4),"
                    + " (6, NULL, 70, 'paid', 3, 2), (7, 30, NULL, 'sale', 4, 3), (8, 20, NULL, NULL, 4, 4),"
                    + " (9, NULL, 50, NULL, 4, 5), (10, 999, NULL, NULL, 5, 2), (11, NULL, 999, NULL, 5, 5),"
                    + " (12, 5, NULL, NULL, 6, 2), (13, NULL, 5, NULL, 6, 4)"};

    /** One ledger row as read back. */
    private record Row(String line, String reference, BigDecimal debit, BigDecimal credit, BigDecimal balance, String correspondent) {
    }

    @Test
    void aGroupAccountListsEveryLineBelowItWithARunningBalance() throws SQLException {
        List<Row> rows = ledger(GROUP_41, "");

        assertEquals(5, rows.size(), "opening, the three posted 2026 lines of the subtree, closing");
        assertEquals("Opening balance", rows.get(0)
                                            .line());
        assertEquals(new BigDecimal("100.00"), rows.get(0)
                                                   .balance(),
                "the opening balance is everything strictly before :fromDate");
        // The grandchild 4111 is reached, the lines run in date, entry, line order, and the running
        // balance carries the opening balance.
        assertEquals(List.of("J2", "J3", "J4"), rows.subList(1, 4)
                                                    .stream()
                                                    .map(Row::reference)
                                                    .toList());
        assertEquals(List.of(new BigDecimal("140.00"), new BigDecimal("70.00"), new BigDecimal("100.00")), rows.subList(1, 4)
                                                                                                               .stream()
                                                                                                               .map(Row::balance)
                                                                                                               .toList());
        Row closing = rows.get(4);
        assertEquals("Closing balance", closing.line());
        assertEquals(new BigDecimal("70.00"), closing.debit(), "the closing row carries the window's debit turnover");
        assertEquals(new BigDecimal("70.00"), closing.credit(), "the closing row carries the window's credit turnover");
        assertEquals(rows.get(0)
                         .balance()
                         .add(closing.debit())
                         .subtract(closing.credit()),
                closing.balance(), "opening + debit - credit must equal closing");
    }

    @Test
    void aGroupAccountEqualsTheSumOfItsLeaves() throws SQLException {
        assertEquals(new BigDecimal("70.00"), closing(ledger(GROUP_411, "")), "411 includes its child 4111 (100 + 40 - 70)");
        assertEquals(new BigDecimal("40.00"), closing(ledger(LEAF_4111, "")));
        assertEquals(new BigDecimal("30.00"), closing(ledger(LEAF_412, "")));
        // 41 = its children 411 (with its own child 4111 inside) + 412.
        assertEquals(closing(ledger(GROUP_411, "")).add(closing(ledger(LEAF_412, ""))), closing(ledger(GROUP_41, "")),
                "a group's closing balance is the sum of its children's subtrees");
    }

    @Test
    void theLedgerAgreesWithTheTrialBalanceForTheSameAccountAndWindow() throws SQLException {
        for (int account : new int[] {LEAF_412, LEAF_4111}) {
            List<Row> rows = ledger(account, "");
            String code = account == LEAF_412 ? "412" : "4111";
            try (Connection connection = database();
                    Statement statement = connection.createStatement();
                    ResultSet balance = statement.executeQuery("SELECT * FROM (" + bound(query("TrialBalance"), account)
                            + ") AS TB WHERE TB.\"Account Code\" = '" + code + "'")) {
                balance.next();
                assertEquals(balance.getBigDecimal("Opening Debit")
                                    .subtract(balance.getBigDecimal("Opening Credit")),
                        rows.get(0)
                            .balance(),
                        "the opening balance of " + code + " must equal the trial balance's");
                Row closing = rows.get(rows.size() - 1);
                assertEquals(balance.getBigDecimal("Debit"), closing.debit(), "the period debit of " + code);
                assertEquals(balance.getBigDecimal("Credit"), closing.credit(), "the period credit of " + code);
                assertEquals(balance.getBigDecimal("Closing Debit")
                                    .subtract(balance.getBigDecimal("Closing Credit")),
                        closing.balance(), "the closing balance of " + code + " must equal the trial balance's");
            }
        }
    }

    @Test
    void eachLineNamesTheAccountsOnTheOtherSideOfItsEntry() throws SQLException {
        List<Row> rows = ledger(GROUP_41, "");
        assertEquals("70", rows.get(1)
                               .correspondent(),
                "J2's 4111 debit faces the sales credit");
        assertEquals("50", rows.get(2)
                               .correspondent(),
                "J3's 411 credit faces the cash debit");
        assertEquals("70", rows.get(3)
                               .correspondent(),
                "J4's 412 debit faces the credit side only - the same-side cash debit is not a correspondent");
        assertNull(rows.get(0)
                       .correspondent(),
                "the opening row names no correspondent");
    }

    /**
     * The generated repository pages by APPENDING {@code LIMIT}/{@code OFFSET} and counts through a
     * {@code COUNT(*)} wrap. The running balance is a window over the whole period, so a later page
     * must carry on from the earlier one rather than restart at the opening balance.
     */
    @Test
    void aPageCarriesTheRunningBalanceOfTheWholeWindow() throws SQLException {
        List<Row> page = ledger(GROUP_41, " LIMIT 2 OFFSET 2");
        assertEquals(List.of("J3", "J4"), page.stream()
                                              .map(Row::reference)
                                              .toList());
        assertEquals(List.of(new BigDecimal("70.00"), new BigDecimal("100.00")), page.stream()
                                                                                     .map(Row::balance)
                                                                                     .toList());
        try (Connection connection = database();
                Statement statement = connection.createStatement();
                ResultSet count = statement.executeQuery(
                        "SELECT COUNT(*) FROM (" + bound(query("AccountAnalysis"), GROUP_41) + ") AS \"REPORT_TOTAL\"")) {
            count.next();
            assertEquals(5, count.getInt(1), "the count wrap should see every row");
        }
    }

    /**
     * No account chosen yet: the initial key matches nothing, so the ledger is empty, not arbitrary.
     */
    @Test
    void anUnboundAccountRendersAnEmptyLedger() throws SQLException {
        List<Row> rows = ledger(0, "");
        assertEquals(2, rows.size(), "only the framing rows");
        assertEquals(0, rows.get(0)
                            .balance()
                            .signum());
        assertEquals(0, rows.get(1)
                            .balance()
                            .signum());
    }

    @Test
    void theAccountTreeIsAGeneratedViewAndTheAccountAParameter() {
        IntentModel model = IntentParser.parse(INTENT);
        ReportIntent report = model.getReports()
                                   .get(0);
        Map<String, String> views = ReportIntentGenerator.buildViewsForTest(TestContexts.context(model), report);
        assertEquals(List.of("LEDGER_ACCOUNT_ANALYSIS_ACCOUNT_TREE"), new ArrayList<>(views.keySet()));
        Map<String, Object> document = ReportIntentGenerator.buildForTest(TestContexts.context(model), report);
        assertEquals("ledger", document.get("kind"));
        assertEquals(List.of("fromDate", "toDate", "account"), ((List<?>) document.get("parameters")).stream()
                                                                                                     .map(parameter -> ((Map<?, ?>) parameter).get(
                                                                                                             "name"))
                                                                                                     .toList());
        assertNull(document.get("joins"), "the union cannot be owned by the editor's builder, so it carries no join model");
    }

    private static BigDecimal closing(List<Row> rows) {
        return rows.get(rows.size() - 1)
                   .balance();
    }

    private static List<Row> ledger(int account, String paging) throws SQLException {
        List<Row> rows = new ArrayList<>();
        try (Connection connection = database();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(bound(query("AccountAnalysis"), account) + paging)) {
            while (result.next()) {
                rows.add(new Row(result.getString("Line"), result.getString("Journal Entry Reference"), result.getBigDecimal("Debit"),
                        result.getBigDecimal("Credit"), result.getBigDecimal("Balance"), result.getString("Correspondent Accounts")));
            }
        }
        return rows;
    }

    private static String query(String reportName) {
        IntentModel model = IntentParser.parse(INTENT);
        ReportIntent report = model.getReports()
                                   .stream()
                                   .filter(candidate -> reportName.equals(candidate.getName()))
                                   .findFirst()
                                   .orElseThrow();
        return (String) ReportIntentGenerator.buildForTest(TestContexts.context(model), report)
                                             .get("query");
    }

    /** The parameters the generated repository binds, as literals - this test has no repository. */
    private static String bound(String query, int account) {
        return query.replace(":fromDate", "DATE '2026-01-01'")
                    .replace(":toDate", "DATE '2026-12-31'")
                    .replace(":account", String.valueOf(account));
    }

    /** A private in-memory ledger: tables first, then the generated views, as the synchronizers do. */
    private static Connection database() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:ledger;DB_CLOSE_DELAY=-1;INIT=SET SCHEMA PUBLIC");
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            for (String ddl : SCHEMA) {
                statement.execute(ddl);
            }
            IntentModel model = IntentParser.parse(INTENT);
            for (ReportIntent report : model.getReports()) {
                for (Map.Entry<String, String> view : ReportIntentGenerator.buildViewsForTest(TestContexts.context(model), report)
                                                                           .entrySet()) {
                    statement.execute("CREATE VIEW \"" + view.getKey() + "\" AS " + view.getValue());
                }
            }
        }
        return connection;
    }
}
