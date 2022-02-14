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
package org.eclipse.dirigible.database.sql.builders.schema;

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
import org.mockito.MockitoAnnotations;

import java.sql.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * The Class SchemaTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class SchemaTest {

    private static final String DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE_CONF_KEY = "DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE";
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Connection mockConnection;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DatabaseMetaData mockDatabaseMetaData;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PreparedStatement mockPrepareStatement;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ResultSet mockResultSet;

    @Before
    public void openMocks() {
//        MockitoAnnotations.initMocks(this);
//        PowerMockito.mockStatic(Configuration.class);
    }


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
