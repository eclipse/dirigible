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

import org.eclipse.dirigible.database.sql.DatabaseArtifactTypes;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcedureTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private DatabaseMetaData mockDatabaseMetaData;

    @Mock
    private ResultSet mockResultSet;

    @Test
    public void checkIfProcedureExist() throws SQLException {
        String funcName = "\"namespace.path::MyFunction\"";
        when(mockConnection.getMetaData()).thenReturn(mockDatabaseMetaData);
        when(mockDatabaseMetaData.getProcedures(null, "MYSCHEMA", funcName)).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        boolean exist = SqlFactory.getNative(new HanaSqlDialect())
                .exists(mockConnection, "MYSCHEMA", funcName, DatabaseArtifactTypes.PROCEDURE);
        assertTrue(exist);
    }

    @Test
    public void checkIfProcedureDoesNotExist() throws SQLException {
        String funcName = "\"namespace.path::MyProcedure\"";
        when(mockConnection.getMetaData()).thenReturn(mockDatabaseMetaData);
        when(mockDatabaseMetaData.getProcedures(null, "MYSCHEMA", funcName)).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        boolean exist = SqlFactory.getNative(new HanaSqlDialect())
                .exists(mockConnection, "MYSCHEMA", funcName, DatabaseArtifactTypes.PROCEDURE);
        assertFalse(exist);
    }
}
