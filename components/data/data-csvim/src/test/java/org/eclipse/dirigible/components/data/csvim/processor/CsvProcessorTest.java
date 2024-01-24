/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.csvim.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import javax.sql.DataSource;

import org.assertj.core.api.ByteArrayAssert;
import org.eclipse.dirigible.components.data.csvim.domain.Csv;
import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.eclipse.dirigible.components.data.csvim.domain.Csvim;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class CsvProcessorTest.
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@Transactional
public class CsvProcessorTest {

    /** The datasource. */
    @Autowired
    private DataSource datasource;

    /** The csvim processor. */
    @Autowired
    private CsvimProcessor csvimProcessor;

    /**
     * Import strict.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void importStrict() throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            connection.createStatement()
                      .execute("CREATE TABLE CSV_A (A1 INT PRIMARY KEY, A2 VARCHAR(20), A3 VARCHAR(20))");
            try {
                byte[] content = "A1,A2,A3\n1,a2_1,a3_1\n2,a2_2,a2_3".getBytes();
                CsvFile csvFile = new CsvFile(null, "CSV_A", null, "import", true, true, ",", "\"", null, false, null);
                csvimProcessor.process(csvFile, new ByteArrayInputStream(content), connection);
                ResultSet rs = connection.createStatement()
                                         .executeQuery("SELECT COUNT(*) FROM CSV_A");
                if (rs.next()) {
                    int c = rs.getInt(1);
                    assertEquals(2, c, "No data has been imported from CSV file CSV_A.csv");
                } else {
                    fail("No data has been imported from CSV file CSV_A.csv");
                }
            } catch (Exception e) {
                fail(e.getMessage(), e);
            } finally {
                connection.createStatement()
                          .execute("DROP TABLE CSV_A");
            }

        }
    }

    /**
     * Import strict negative.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void importStrictNegative() throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            connection.createStatement()
                      .execute("CREATE TABLE CSV_A (A1 INT PRIMARY KEY, A2 VARCHAR(20), A3 VARCHAR(20))");
            try {
                byte[] content = "A1,A2\n1,a2_1\n2,a2_2".getBytes();
                CsvFile csvFile = new CsvFile(null, "CSV_A", null, "import", true, true, ",", "\"", null, false, null);
                try {
                    csvimProcessor.process(csvFile, new ByteArrayInputStream(content), connection);
                } catch (Exception e) {
                    //
                }
                ResultSet rs = connection.createStatement()
                                         .executeQuery("SELECT COUNT(*) FROM CSV_A");
                if (rs.next()) {
                    int c = rs.getInt(1);
                    assertEquals(0, c, "Data has been imported from CSV file CSV_A.csv in a strict mode and wrong CSV file");
                }
            } catch (Exception e) {
                fail(e.getMessage(), e);
            } finally {
                connection.createStatement()
                          .execute("DROP TABLE CSV_A");
            }

        }
    }

    /**
     * The Class TestConfiguration.
     */
    @SpringBootApplication
    static class TestConfiguration {
    }
}
