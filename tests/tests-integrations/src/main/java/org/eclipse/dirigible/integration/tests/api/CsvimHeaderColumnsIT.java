/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A headered CSV writes only the columns its header names (#7923). The CSVIM import used to insert
 * and update every table column and bind NULL for each one the header lacks, so a column default
 * never applied to a seeded row, and an upsert re-import wiped every column the CSV does not carry
 * - including a value a user had edited since the seeding. A headerless CSV still matches every
 * column by position.
 *
 * <p>
 * Pure HTTP / JDBC through the real CSVIM synchronizer - no Selenide, no IDE.
 */
class CsvimHeaderColumnsIT extends IntegrationTest {

    private static final String PROJECT = "csvim-header-columns-it";

    private static final String BOOK_TABLE = "CSVIM_HDR_BOOK";

    private static final String SHELF_TABLE = "CSVIM_HDR_SHELF";

    private static final String BOOK_TABLE_PATH = registryPath("tables/book.table");
    private static final String SHELF_TABLE_PATH = registryPath("tables/shelf.table");
    private static final String BOOKS_CSV_PATH = registryPath("data/books.csv");
    private static final String SHELVES_CSV_PATH = registryPath("data/shelves.csv");
    private static final String CSVIM_PATH = registryPath("data/library.csvim");

    private static final String BOOK_TABLE_SOURCE = """
            {
                "name": "CSVIM_HDR_BOOK",
                "type": "TABLE",
                "columns": [
                    { "type": "INTEGER", "primaryKey": true, "nullable": false, "name": "BOOK_ID" },
                    { "type": "VARCHAR", "length": 40, "nullable": false, "name": "BOOK_TITLE" },
                    { "type": "INTEGER", "nullable": true, "defaultValue": "1", "name": "BOOK_KIND" },
                    { "type": "VARCHAR", "length": 40, "nullable": true, "name": "BOOK_NOTE" }
                ]
            }
            """;

    private static final String SHELF_TABLE_SOURCE = """
            {
                "name": "CSVIM_HDR_SHELF",
                "type": "TABLE",
                "columns": [
                    { "type": "INTEGER", "primaryKey": true, "nullable": false, "name": "SHELF_ID" },
                    { "type": "VARCHAR", "length": 40, "nullable": true, "name": "SHELF_LABEL" },
                    { "type": "VARCHAR", "length": 40, "nullable": true, "name": "SHELF_ROOM" }
                ]
            }
            """;

    /** Carries neither BOOK_KIND (a column with a default) nor BOOK_NOTE. */
    private static final String BOOKS_CSV_SOURCE = """
            BOOK_ID,BOOK_TITLE
            1,Dune
            2,Foundation
            """;

    private static final String SHELVES_CSV_SOURCE = """
            1,A,Library
            """;

    private static final String CSVIM_SOURCE = """
            {
                "files": [
                    {
                        "table": "CSVIM_HDR_BOOK",
                        "schema": "PUBLIC",
                        "file": "/%1$s/data/books.csv",
                        "header": true,
                        "useHeaderNames": true,
                        "delimField": ",",
                        "distinguishEmptyFromNull": true,
                        "upsert": true,
                        "version": "1.0"
                    },
                    {
                        "table": "CSVIM_HDR_SHELF",
                        "schema": "PUBLIC",
                        "file": "/%1$s/data/shelves.csv",
                        "header": false,
                        "delimField": ",",
                        "distinguishEmptyFromNull": true,
                        "version": "1.0"
                    }
                ]
            }
            """.formatted(PROJECT);

    @Autowired
    private IRepository repository;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Autowired
    private DataSourcesManager dataSourcesManager;

    @Test
    void seededRowGetsTheDefaultOfAColumnTheHeaderOmits_andAHeaderlessCsvStillMatchesByPosition() throws Exception {
        publishLibrary();

        try (Connection connection = dataSourcesManager.getDefaultDataSource()
                                                       .getConnection();
                Statement statement = connection.createStatement()) {
            try (ResultSet book = statement.executeQuery(
                    "SELECT \"BOOK_TITLE\", \"BOOK_KIND\", \"BOOK_NOTE\" FROM \"" + BOOK_TABLE + "\" WHERE \"BOOK_ID\" = 1")) {
                assertThat(book.next()).isTrue();
                assertThat(book.getString(1)).isEqualTo("Dune");
                assertThat(book.getObject(2)).as("BOOK_KIND, absent from the header, takes the column default")
                                             .isEqualTo(1);
                assertThat(book.getString(3)).isNull();
            }
            try (ResultSet shelf =
                    statement.executeQuery("SELECT \"SHELF_LABEL\", \"SHELF_ROOM\" FROM \"" + SHELF_TABLE + "\" WHERE \"SHELF_ID\" = 1")) {
                assertThat(shelf.next()).isTrue();
                assertThat(shelf.getString(1)).isEqualTo("A");
                assertThat(shelf.getString(2)).isEqualTo("Library");
            }
        }
    }

    @Test
    void upsertReimportKeepsAColumnTheHeaderDoesNotCarry() throws Exception {
        publishLibrary();
        try (Connection connection = dataSourcesManager.getDefaultDataSource()
                                                       .getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("UPDATE \"" + BOOK_TABLE + "\" SET \"BOOK_KIND\" = 5, \"BOOK_NOTE\" = 'signed copy' WHERE \"BOOK_ID\" = 1");
        }

        // A changed CSV re-imports it, and "upsert": true updates the rows it already seeded.
        write(BOOKS_CSV_PATH, """
                BOOK_ID,BOOK_TITLE
                1,Dune Messiah
                2,Foundation
                """);
        synchronizationProcessor.forceProcessSynchronizers();

        try (Connection connection = dataSourcesManager.getDefaultDataSource()
                                                       .getConnection();
                Statement statement = connection.createStatement();
                ResultSet book = statement.executeQuery(
                        "SELECT \"BOOK_TITLE\", \"BOOK_KIND\", \"BOOK_NOTE\" FROM \"" + BOOK_TABLE + "\" WHERE \"BOOK_ID\" = 1")) {
            assertThat(book.next()).isTrue();
            assertThat(book.getString(1)).as("the column the CSV carries is updated")
                                         .isEqualTo("Dune Messiah");
            assertThat(book.getObject(2)).as("BOOK_KIND, absent from the header, keeps the edited value")
                                         .isEqualTo(5);
            assertThat(book.getString(3)).as("BOOK_NOTE, absent from the header, keeps the edited value")
                                         .isEqualTo("signed copy");
        }
    }

    private void publishLibrary() {
        write(BOOK_TABLE_PATH, BOOK_TABLE_SOURCE);
        write(SHELF_TABLE_PATH, SHELF_TABLE_SOURCE);
        write(BOOKS_CSV_PATH, BOOKS_CSV_SOURCE);
        write(SHELVES_CSV_PATH, SHELVES_CSV_SOURCE);
        write(CSVIM_PATH, CSVIM_SOURCE);
        synchronizationProcessor.forceProcessSynchronizers();
    }

    private void write(String path, String source) {
        repository.createResource(path, source.getBytes(StandardCharsets.UTF_8), false, "text/plain", true);
    }

    private static String registryPath(String relative) {
        return IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + PROJECT + "/" + relative;
    }

    @AfterEach
    void cleanup() throws Exception {
        for (String path : List.of(CSVIM_PATH, BOOKS_CSV_PATH, SHELVES_CSV_PATH, BOOK_TABLE_PATH, SHELF_TABLE_PATH)) {
            if (repository.hasResource(path)) {
                repository.removeResource(path);
            }
        }
        synchronizationProcessor.forceProcessSynchronizers();
        // The synchronizer keeps a table's data when its .table file goes, so drop both explicitly.
        try (Connection connection = dataSourcesManager.getDefaultDataSource()
                                                       .getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS \"" + BOOK_TABLE + "\"");
            statement.execute("DROP TABLE IF EXISTS \"" + SHELF_TABLE + "\"");
        }
    }
}
