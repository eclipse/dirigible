/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.ds.model;

/**
 * The Data Structure Table Constraint Foreign Key Model.
 */
public class DataStructureTableConstraintForeignKeyModel extends DataStructureTableConstraintModel {

	/** The referenced table. */
	private String referencedTable;
	
	/** The referenced columns. */
	private String[] referencedColumns;
	
	
	
	/**
	 * Default constructor.
	 */
	public DataStructureTableConstraintForeignKeyModel() {
		super();
	}

	/**
	 * Fields constructor.
	 *
	 * @param referencedTable the table name
	 * @param referencedColumns the column names
	 */
	public DataStructureTableConstraintForeignKeyModel(String referencedTable, String[] referencedColumns) {
		super();
		this.referencedTable = referencedTable;
		this.referencedColumns = referencedColumns;
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
	 * Sets the referenced table.
	 *
	 * @param referencedTable the new referenced table
	 */
	public void setReferencedTable(String referencedTable) {
		this.referencedTable = referencedTable;
	}

	/**
	 * Gets the referenced columns.
	 *
	 * @return the referenced columns
	 */
	public String[] getReferencedColumns() {
		return (referencedColumns != null) ? referencedColumns.clone() : null;
	}

	/**
	 * Sets the referenced columns.
	 *
	 * @param referencedColumns the new referenced columns
	 */
	public void setReferencedColumns(String[] referencedColumns) {
		this.referencedColumns = referencedColumns;
	}

}
