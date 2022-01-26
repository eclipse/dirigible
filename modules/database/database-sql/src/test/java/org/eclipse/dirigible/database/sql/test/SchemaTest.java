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
package org.eclipse.dirigible.database.sql.test;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.sql.DatabaseArtifactTypes;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.hana.HanaSqlDialect;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * The Class SchemaTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class})
public class SchemaTest {

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
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Configuration.class);
    }


    @Test
    public void executeCreateSchemaWithCaseSensitive() {
        when(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false")).thenReturn("true");
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .schema("MySchema_1")
                .build();
        assertNotNull(sql);
        assertEquals("CREATE SCHEMA \"MySchema_1\"", sql);
    }

    @Test
    public void executeCreateSchemaNoCaseSensitive() {
        when(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false")).thenReturn("false");
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .schema("MySchema_1")
                .build();
        assertNotNull(sql);
        assertEquals("CREATE SCHEMA MySchema_1", sql);
    }

    @Test
    public void executeDropSchemaWithCaseSensitive() {
        when(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false")).thenReturn("true");
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .drop()
                .schema("MySchema_1")
                .build();

        assertNotNull(sql);
        assertEquals("DROP SCHEMA \"MySchema_1\"", sql);
    }

    @Test
    public void executeDropSchemaNoCaseSensitive() {
        when(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false")).thenReturn("false");
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .drop()
                .schema("MySchema_1")
                .build();
        assertNotNull(sql);
        assertEquals("DROP SCHEMA MySchema_1", sql);
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
        when(mockResultSet.next()).thenReturn(false);
        boolean exist = SqlFactory.getNative(new HanaSqlDialect())
                .exists(mockConnection, schemaName, DatabaseArtifactTypes.SCHEMA);
        assertFalse(exist);
    }

}
