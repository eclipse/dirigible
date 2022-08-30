/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.dialects.hana;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.sql.DatabaseArtifactTypes;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.hana.HanaSqlDialect;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * The Class SchemaTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class SchemaTest {

    /** The Constant DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE_CONF_KEY. */
    private static final String DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE_CONF_KEY = "DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE";
    
    /** The mock connection. */
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Connection mockConnection;

    /** The mock database meta data. */
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DatabaseMetaData mockDatabaseMetaData;

    /** The mock prepare statement. */
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PreparedStatement mockPrepareStatement;

    /** The mock result set. */
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ResultSet mockResultSet;

    /**
     * Open mocks.
     */
    @Before
    public void openMocks() {
//        MockitoAnnotations.initMocks(this);
//        PowerMockito.mockStatic(Configuration.class);
    }


    /**
     * Execute create schema with case sensitive.
     */
    @Test
    public void executeCreateSchemaWithCaseSensitive() {
        try (MockedStatic<Configuration> configuration = Mockito.mockStatic(Configuration.class)) {
            configuration.when(() -> Configuration.get(
                DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE_CONF_KEY, "false")).thenReturn("true");
            String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .schema("MySchema_1")
                .build();
            assertNotNull(sql);
            assertEquals("CREATE SCHEMA \"MySchema_1\"", sql);
        }
    }

    /**
     * Execute create schema no case sensitive.
     */
    @Test
    public void executeCreateSchemaNoCaseSensitive() {
        try (MockedStatic<Configuration> configuration = Mockito.mockStatic(Configuration.class)) {
            configuration.when(
                    () -> Configuration.get(DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE_CONF_KEY, "false"))
                .thenReturn("false");
            String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .schema("MySchema_1")
                .build();
            assertNotNull(sql);
            assertEquals("CREATE SCHEMA MySchema_1", sql);
        }
    }

    /**
     * Execute drop schema with case sensitive.
     */
    @Test
    public void executeDropSchemaWithCaseSensitive() {
        try (MockedStatic<Configuration> configuration = Mockito.mockStatic(Configuration.class)) {
            configuration.when(
                    () -> Configuration.get(DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE_CONF_KEY, "false"))
                .thenReturn("true");
            String sql = SqlFactory.getNative(new HanaSqlDialect())
                .drop()
                .schema("MySchema_1")
                .build();

            assertNotNull(sql);
            assertEquals("DROP SCHEMA \"MySchema_1\"", sql);
        }
    }

    /**
     * Execute drop schema no case sensitive.
     */
    @Test
    public void executeDropSchemaNoCaseSensitive() {
        try (MockedStatic<Configuration> configuration = Mockito.mockStatic(Configuration.class)) {
            configuration.when(
                    () -> Configuration.get(DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE_CONF_KEY, "false"))
                .thenReturn("false");
            String sql = SqlFactory.getNative(new HanaSqlDialect())
                .drop()
                .schema("MySchema_1")
                .build();
            assertNotNull(sql);
            assertEquals("DROP SCHEMA MySchema_1", sql);
        }
    }

    /**
     * Check if schema exist.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void checkIfSchemaExist() throws SQLException {
        String schemaName = "MySchema_1";
        when(mockConnection.getMetaData()).thenReturn(mockDatabaseMetaData);
        when(mockDatabaseMetaData.getSchemas(null, schemaName)).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        boolean exist = SqlFactory.getNative(new HanaSqlDialect())
                .exists(mockConnection, schemaName, DatabaseArtifactTypes.SCHEMA);
        assertTrue(exist);
    }

    /**
     * Check if schema does not exist.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void checkIfSchemaDoesNotExist() throws SQLException {
        String schemaName = "MySchema_1";
        when(mockConnection.prepareStatement(any())).thenReturn(mockPrepareStatement);
        when(mockPrepareStatement.executeQuery()).thenReturn(mockResultSet);
        boolean exist = SqlFactory.getNative(new HanaSqlDialect())
                .exists(mockConnection, schemaName, DatabaseArtifactTypes.SCHEMA);
        assertFalse(exist);
    }

}
