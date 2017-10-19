/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.sql.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.Modifiers;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.junit.Test;

public class CreateTableTest {

	@Test
	public void createTableGeneric() {
		String sql = SqlFactory.getDefault().create()
				.table("CUSTOMERS")
				.column("ID", DataType.INTEGER, Modifiers.PRIMARY_KEY, Modifiers.NOT_NULL, Modifiers.NON_UNIQUE)
				.column("FIRST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NOT_NULL, Modifiers.UNIQUE, "(20)")
				.column("LAST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(30)")
				.build();

		assertNotNull(sql);
		assertEquals("CREATE TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) NOT NULL UNIQUE , LAST_NAME VARCHAR (30) )",
				sql);
	}

	@Test
	public void createTableTypeSafe() {
		String sql = SqlFactory.getDefault().create()
				.table("CUSTOMERS")
				.columnInteger("ID", true, false, false)
				.columnVarchar("FIRST_NAME", 20, false, true, true)
				.columnVarchar("LAST_NAME", 30, false, true, false)
				.build();

		assertNotNull(sql);
		assertEquals("CREATE TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) UNIQUE , LAST_NAME VARCHAR (30) )", sql);
	}

	@Test
	public void createTableTypeConstraintPrimaryKey() {
		String sql = SqlFactory.getDefault().create().table("CUSTOMERS")
				.column("FIRST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(20)")
				.column("LAST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(30)")
				.primaryKey("PRIMARY_KEY", new String[] { "FIRST_NAME", "LAST_NAME" })
				.build();

		assertNotNull(sql);
		assertEquals(
				"CREATE TABLE CUSTOMERS ( FIRST_NAME VARCHAR (20) , LAST_NAME VARCHAR (30) , CONSTRAINT PRIMARY_KEY PRIMARY KEY ( FIRST_NAME , LAST_NAME ))",
				sql);
	}

	@Test
	public void createTableTypeConstraintPrimaryKeyNoName() {
		String sql = SqlFactory.getDefault().create()
				.table("CUSTOMERS")
				.column("FIRST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(20)")
				.column("LAST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(30)")
				.primaryKey(new String[] { "FIRST_NAME", "LAST_NAME" })
				.build();

		assertNotNull(sql);
		assertEquals("CREATE TABLE CUSTOMERS ( FIRST_NAME VARCHAR (20) , LAST_NAME VARCHAR (30) , PRIMARY KEY ( FIRST_NAME , LAST_NAME ))", sql);
	}

	@Test
	public void createTableTypeConstraintForegnKey() {
		String sql = SqlFactory.getDefault().create()
				.table("CUSTOMERS")
				.column("FIRST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(20)")
				.column("LAST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(30)")
				.foreignKey("FOREIGN_KEY", new String[] { "PERSON_ADDRESS_ID" }, "ADDRESSES", new String[] { "ADDRESS_ID" })
				.build();

		assertNotNull(sql);
		assertEquals(
				"CREATE TABLE CUSTOMERS ( FIRST_NAME VARCHAR (20) , LAST_NAME VARCHAR (30) , CONSTRAINT FOREIGN_KEY FOREIGN KEY ( PERSON_ADDRESS_ID ) REFERENCES ADDRESSES( ADDRESS_ID ))",
				sql);
	}

	@Test
	public void createTableTypeConstraintUniqueIndex() {
		String sql = SqlFactory.getDefault().create()
				.table("CUSTOMERS")
				.column("FIRST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(20)")
				.column("LAST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(30)")
				.unique("LAST_NAME_UNIQUE", new String[] { "LAST_NAME" })
				.build();

		assertNotNull(sql);
		assertEquals("CREATE TABLE CUSTOMERS ( FIRST_NAME VARCHAR (20) , LAST_NAME VARCHAR (30) , CONSTRAINT LAST_NAME_UNIQUE UNIQUE ( LAST_NAME ))",
				sql);
	}
	
	@Test
	public void createTableTypeConstraintCheck() {
		String sql = SqlFactory.getDefault().create()
				.table("CUSTOMERS")
				.column("FIRST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(20)")
				.column("LAST_NAME", DataType.VARCHAR, Modifiers.REGULAR, Modifiers.NULLABLE, Modifiers.NON_UNIQUE, "(30)")
				.check("LAST_NAME_CHECK", "LAST_NAME = 'Smith'")
				.build();

		assertNotNull(sql);
		assertEquals("CREATE TABLE CUSTOMERS ( FIRST_NAME VARCHAR (20) , LAST_NAME VARCHAR (30) , CONSTRAINT LAST_NAME_CHECK CHECK (LAST_NAME = 'Smith'))",
				sql);
	}

}
