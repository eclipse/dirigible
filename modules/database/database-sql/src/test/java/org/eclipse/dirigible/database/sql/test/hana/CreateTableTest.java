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

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.hana.HanaSqlDialect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Class CreateTableTest.
 */
public class CreateTableTest {

    /**
     * Creates the table generic.
     */
    @Test
    public void createTableGeneric() {
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .table("CUSTOMERS")
                .column("ID", DataType.INTEGER, true, false, false)
                .column("FIRST_NAME", DataType.VARCHAR, false, false, true, "(20)")
                .column("LAST_NAME", DataType.VARCHAR, false, true, false, "(30)")
                .build();

        assertNotNull(sql);
        assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) NOT NULL UNIQUE , LAST_NAME VARCHAR (30) )", sql);
    }

    /**
     * Creates the table column.
     */
    @Test
    public void createTableColumn() {
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .columnTable("CUSTOMERS")
                .column("ID", DataType.INTEGER, true, false, false)
                .column("FIRST_NAME", DataType.VARCHAR, false, false, true, "(20)")
                .column("LAST_NAME", DataType.VARCHAR, false, true, false, "(30)")
                .build();

        assertNotNull(sql);
        assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) NOT NULL UNIQUE , LAST_NAME VARCHAR (30) )", sql);
    }

    /**
     * Creates the table column type safe.
     */
    @Test
    public void createTableColumnTypeSafe() {
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .columnTable("CUSTOMERS")
                .columnInteger("ID", true, false, false)
                .columnVarchar("FIRST_NAME", 20, false, true, true)
                .columnVarchar("LAST_NAME", 30, false, true, false)
                .build();

        assertNotNull(sql);
        assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) UNIQUE , LAST_NAME VARCHAR (30) )", sql);
    }

    @Test
    public void createTableWithCompositeKeyWithSetPKOnColumnLevel() {
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .columnTable("CUSTOMERS")
                .columnInteger("ID", true, false, false)
                .columnInteger("ID2", true, false, false)
                .columnVarchar("FIRST_NAME", 20, false, true, true)
                .columnVarchar("LAST_NAME", 30, false, true, false)
                .build();

        assertNotNull(sql);
        assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL , ID2 INTEGER NOT NULL , FIRST_NAME VARCHAR (20) UNIQUE , LAST_NAME VARCHAR (30) , PRIMARY KEY(ID , ID2) )", sql);
    }

    @Test
    public void createTableWithCompositeKeyWithSetPKOnConstraintLevel() {
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .columnTable("CUSTOMERS")
                .columnInteger("ID", false, false, false)
                .columnInteger("ID2", false, false, false)
                .columnVarchar("FIRST_NAME", 20, false, true, true)
                .columnVarchar("LAST_NAME", 30, false, true, false)
                .primaryKey(new String[]{"ID", "ID2"})
                .build();

        assertNotNull(sql);
        assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL , ID2 INTEGER NOT NULL , FIRST_NAME VARCHAR (20) UNIQUE , LAST_NAME VARCHAR (30) , PRIMARY KEY ( ID , ID2 ))", sql);
    }

    @Test
    public void createTableWithCompositeKeyWithSetPKOnConstraintAndColumnLevel() {
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .columnTable("CUSTOMERS")
                .columnInteger("ID", true, false, false)
                .columnInteger("ID2", true, false, false)
                .columnVarchar("FIRST_NAME", 20, false, true, true)
                .columnVarchar("LAST_NAME", 30, false, true, false)
                .primaryKey(new String[]{"ID", "ID2"})
                .build();

        assertNotNull(sql);
        assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL , ID2 INTEGER NOT NULL , FIRST_NAME VARCHAR (20) UNIQUE , LAST_NAME VARCHAR (30) , PRIMARY KEY(ID , ID2) )", sql);
    }

    @Test
    public void createTableWithSetPKOnConstraintAndColumnLevel() {
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .columnTable("CUSTOMERS")
                .columnInteger("ID", true, false, false)
                .columnVarchar("FIRST_NAME", 20, false, true, true)
                .primaryKey(new String[]{"ID"})
                .build();

        assertNotNull(sql);
        assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) UNIQUE )", sql);
    }

    @Test
    public void parseTableWithoutPK() {
        String sql = SqlFactory.getNative(new HanaSqlDialect())
                .create()
                .columnTable("CUSTOMERS")
                .columnInteger("ID", false, false, false)
                .columnVarchar("FIRST_NAME", 20, false, true, true)
                .primaryKey(new String[]{})
                .build();

        assertNotNull(sql);
        assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL , FIRST_NAME VARCHAR (20) UNIQUE )", sql);
    }
}
