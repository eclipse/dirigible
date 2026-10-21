/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.csvim.processor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.eclipse.dirigible.components.data.csvim.domain.CsvRecord;
import org.eclipse.dirigible.components.data.csvim.utils.CsvimUtils;
import org.eclipse.dirigible.components.database.domain.TableMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A headered CSV writes only the columns its header names (#7923): an insert leaves the rest to the
 * database default and an update leaves them as they are. Before, both statements covered every
 * table column and bound NULL for the ones the header lacks, so a column default never applied to a
 * seeded row and an upsert wiped every column the CSV does not carry. A headerless CSV still
 * matches every column by position.
 */
class CsvProcessorHeaderColumnsTest {

    private static final String TABLE = "CSV_HEADER_BOOK";

    private final CsvProcessor csvProcessor = new CsvProcessor();

    private Connection connection;

    @BeforeEach
    void createTable() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:csv-header-columns;DB_CLOSE_DELAY=-1");
        execute("CREATE TABLE \"" + TABLE + "\" (\"BOOK_ID\" INTEGER PRIMARY KEY, \"BOOK_TITLE\" VARCHAR(40), "
                + "\"BOOK_KIND\" INTEGER DEFAULT 1, \"BOOK_NOTE\" VARCHAR(40))");
    }

    @AfterEach
    void dropTable() throws SQLException {
        execute("DROP TABLE IF EXISTS \"" + TABLE + "\"");
        connection.close();
    }

    @Test
    void insertLeavesAColumnTheHeaderDoesNotNameToItsDefault() throws Exception {
        insert(headered("BOOK_ID,BOOK_TITLE\n1,Dune\n"));

        assertThat(row(1)).containsExactly("Dune", 1, null);
    }

    @Test
    void updateLeavesAColumnTheHeaderDoesNotNameAsItIs() throws Exception {
        execute("INSERT INTO \"" + TABLE + "\" VALUES (2, 'Foundation', 5, 'kept')");

        update(headered("BOOK_ID,BOOK_TITLE\n2,Foundation and Empire\n"), "BOOK_ID");

        assertThat(row(2)).containsExactly("Foundation and Empire", 5, "kept");
    }

    @Test
    void headerlessInsertStillMatchesEveryColumnByPosition() throws Exception {
        insert(headerless("3,Hyperion,7,signed\n"));

        assertThat(row(3)).containsExactly("Hyperion", 7, "signed");
    }

    @Test
    void headerNamingNoColumnOfTheTableInsertsNothing() throws Exception {
        insert(headered("ISBN,PAGES\n978-0441013593,412\n"));

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM \"" + TABLE + "\"")) {
            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getInt(1)).isZero();
        }
    }

    @Test
    void updateBindsTheKeyByNameWhenItIsNotTheFirstColumn() throws Exception {
        execute("CREATE TABLE \"CSV_HEADER_SHELF\" (\"SHELF_LABEL\" VARCHAR(40), \"SHELF_ID\" INTEGER PRIMARY KEY, "
                + "\"SHELF_ROOM\" VARCHAR(40))");
        try {
            execute("INSERT INTO \"CSV_HEADER_SHELF\" VALUES ('A', 9, 'Library')");

            ParsedCsv csv = headered("SHELF_ID,SHELF_LABEL\n9,B\n");
            TableMetadata shelf = CsvimUtils.getTableMetadata("CSV_HEADER_SHELF", null, connection);
            csvProcessor.update(connection, null, shelf, records(csv, shelf), csv.headerNames(), "SHELF_ID", csvFile());

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(
                            "SELECT \"SHELF_LABEL\", \"SHELF_ROOM\" FROM \"CSV_HEADER_SHELF\" WHERE \"SHELF_ID\" = 9")) {
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getString(1)).isEqualTo("B");
                assertThat(resultSet.getString(2)).isEqualTo("Library");
            }
        } finally {
            execute("DROP TABLE IF EXISTS \"CSV_HEADER_SHELF\"");
        }
    }

    private void insert(ParsedCsv csv) throws SQLException {
        TableMetadata table = table();
        csvProcessor.insert(connection, null, table, records(csv, table), csv.headerNames(), csvFile());
    }

    private void update(ParsedCsv csv, String pkName) throws SQLException {
        TableMetadata table = table();
        csvProcessor.update(connection, null, table, records(csv, table), csv.headerNames(), pkName, csvFile());
    }

    private TableMetadata table() {
        return CsvimUtils.getTableMetadata(TABLE, null, connection);
    }

    private static List<CsvRecord> records(ParsedCsv csv, TableMetadata table) {
        return csv.records()
                  .stream()
                  .map(record -> new CsvRecord(record, table, csv.headerNames(), false))
                  .toList();
    }

    private static CsvFile csvFile() {
        CsvFile csvFile = new CsvFile();
        csvFile.setFile("/csv-header-columns/books.csv");
        return csvFile;
    }

    private List<Object> row(int id) throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(
                        "SELECT \"BOOK_TITLE\", \"BOOK_KIND\", \"BOOK_NOTE\" FROM \"" + TABLE + "\" WHERE \"BOOK_ID\" = " + id)) {
            assertThat(resultSet.next()).as("row %d", id)
                                        .isTrue();
            List<Object> values = new ArrayList<>();
            values.add(resultSet.getString(1));
            values.add((Integer) resultSet.getObject(2));
            values.add(resultSet.getString(3));
            return values;
        }
    }

    private void execute(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    /**
     * The CSV format the import uses (CsvimProcessor#createCSVFormat), with or without a header row.
     */
    private static ParsedCsv parse(String content, boolean header) throws IOException {
        CSVFormat format = CSVFormat.newFormat(',')
                                    .withIgnoreEmptyLines()
                                    .withQuoteMode(QuoteMode.ALL_NON_NULL)
                                    .withQuote('"');
        if (header) {
            format = format.withFirstRecordAsHeader();
        }
        try (CSVParser parser = CSVParser.parse(new StringReader(content), format)) {
            return new ParsedCsv(parser.getHeaderNames(), parser.getRecords());
        }
    }

    private static ParsedCsv headered(String content) throws IOException {
        return parse(content, true);
    }

    private static ParsedCsv headerless(String content) throws IOException {
        return parse(content, false);
    }

    private record ParsedCsv(List<String> headerNames, List<CSVRecord> records) {
    }
}
