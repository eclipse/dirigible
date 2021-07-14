/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.test.hana;

import org.eclipse.dirigible.database.sql.DatabaseArtifactTypes;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.hana.HanaSqlDialect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * The Class SynonymTest.
 */
public class SynonymTest {

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
    }


    @Test
    public void executeCreateSynonym() {
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .synonym("CUSTOMERS_SEQUENCE")
                .forSource("CUSTOMERS")
                .build();

        assertNotNull(sql);
        assertEquals("CREATE SYNONYM CUSTOMERS_SEQUENCE FOR CUSTOMERS", sql);
    }


    @Test
    public void executeDropSynonym() {
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .drop()
                .synonym("CUSTOMERS_SEQUENCE")
                .build();

        assertNotNull(sql);
        assertEquals("DROP SYNONYM CUSTOMERS_SEQUENCE", sql);
    }

    @Test
    public void checkIfSynonymExist() throws SQLException {
        String synonymName = "\"MYSCHEMA\".\"namespace.path::MySynonym\"";
        when(mockConnection.getMetaData()).thenReturn(mockDatabaseMetaData);
        when(mockDatabaseMetaData.getTables(null, null, synonymName, new String[]{ISqlKeywords.KEYWORD_SYNONYM})).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        boolean exist = SqlFactory.getNative(new HanaSqlDialect())
                .exists(mockConnection, synonymName, DatabaseArtifactTypes.SYNONYM);
        assertTrue(exist);
    }

    @Test
    public void checkIfSynonymDoesNotExist() throws SQLException {
        when(mockConnection.prepareStatement(any())).thenReturn(mockPrepareStatement);
        when(mockPrepareStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        boolean exist = SqlFactory.getNative(new HanaSqlDialect())
                .exists(mockConnection, "\"MYSCHEMA\".\"namespace.path::MySynonym\"", DatabaseArtifactTypes.SYNONYM);
        assertFalse(exist);
    }

}
