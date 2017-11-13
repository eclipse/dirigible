/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.builders.table;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.dirigible.database.sql.ISqlDialect;

/**
 * The Create Table Foreign Key Builder.
 */
public class CreateTableForeignKeyBuilder extends AbstractCreateTableConstraintBuilder<CreateTableForeignKeyBuilder> {

	private String referencedTable;

	private Set<String> referencedColumns = new TreeSet<String>();

	/**
	 * Instantiates a new creates the table foreign key builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param name
	 *            the name
	 */
	CreateTableForeignKeyBuilder(ISqlDialect dialect, String name) {
		super(dialect, name);
	}

	/**
	 * Gets the referenced table.
	 *
	 * @return the referenced table
	 */
	public String getReferencedTable() {
		return referencedTable;
	}

	/**
	 * Gets the referenced columns.
	 *
	 * @return the referenced columns
	 */
	public Set<String> getReferencedColumns() {
		return referencedColumns;
	}

	/**
	 * Referenced table.
	 *
	 * @param referencedTable
	 *            the referenced table
	 * @return the creates the table foreign key builder
	 */
	public CreateTableForeignKeyBuilder referencedTable(String referencedTable) {
		this.referencedTable = referencedTable;
		return this;
	}

	/**
	 * Referenced column.
	 *
	 * @param referencedColumn
	 *            the referenced column
	 * @return the creates the table foreign key builder
	 */
	public CreateTableForeignKeyBuilder referencedColumn(String referencedColumn) {
		this.referencedColumns.add(referencedColumn);
		return this;
	}

}
