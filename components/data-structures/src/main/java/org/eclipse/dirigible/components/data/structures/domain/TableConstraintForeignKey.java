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
package org.eclipse.dirigible.components.data.structures.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.annotation.Transient;

/**
 * The Class TableConstraintForeignKey.
 */
@Entity
@javax.persistence.Table(name = "DIRIGIBLE_TABLE_FOREIGNKEYS")
public class TableConstraintForeignKey extends TableConstraint {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FOREIGNKEY_ID", nullable = false)
	private Long id;
	
	/** The referenced table. */
	@Column(name = "FOREIGNKEY_REF_TABLE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String referencedTable;
	
	/** The referenced columns. */
	@Column(name = "FOREIGNKEY_REF_COLUMNS", columnDefinition = "VARCHAR", nullable = false, length = 2000)
	private String referencedColumns;
	
	/** The column names. */
	@Transient
	protected transient Set<String> referencedColumnNames;

	/**
	 * Instantiates a new table constraint foreign key.
	 *
	 * @param name the name
	 * @param modifiers the modifiers
	 * @param columns the columns
	 * @param referencedTable the referenced table
	 * @param referencedColumns the referenced columns
	 * @param constraints the constraints
	 */
	public TableConstraintForeignKey(String name, String modifiers, String columns,
			String referencedTable, String referencedColumns, TableConstraints constraints) {
		super(name, modifiers, columns, constraints);
		this.referencedTable = referencedTable;
		this.referencedColumns = referencedColumns;
	}

	/**
	 * Instantiates a new table constraint foreign key.
	 */
	public TableConstraintForeignKey() {
		super();
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the referenced table.
	 *
	 * @return the referencedTable
	 */
	public String getReferencedTable() {
		return referencedTable;
	}

	/**
	 * Sets the referenced table.
	 *
	 * @param referencedTable the referencedTable to set
	 */
	public void setReferencedTable(String referencedTable) {
		this.referencedTable = referencedTable;
	}

	/**
	 * Gets the referenced columns.
	 *
	 * @return the referencedColumns
	 */
	public String getReferencedColumns() {
		return referencedColumns;
	}

	/**
	 * Sets the referenced columns.
	 *
	 * @param referencedColumns the referencedColumns to set
	 */
	public void setReferencedColumns(String referencedColumns) {
		this.referencedColumns = referencedColumns;
	}
	
	/**
	 * Adds the referencedColumn.
	 *
	 * @param referencedColumn the referencedColumn
	 */
	public void addReferencedColumn(String referencedColumn) {
		if (referencedColumns == null && referencedColumns.trim().length() == 0) {
			referencedColumns = referencedColumn;
			return;
		}
		referencedColumns += "," + referencedColumn;
		if (referencedColumnNames != null) {
			referencedColumnNames.add(referencedColumn);
		}
	}
	
	/**
	 * Removes the referencedColumn.
	 *
	 * @param referencedColumn the referencedColumn
	 */
	public void removeReferencedColumn(String referencedColumn) {
		if (referencedColumnNames == null) {
			referencedColumnNames = getReferencedColumnNames();
		}
		referencedColumnNames.remove(referencedColumn);
		referencedColumns = String.join(",", referencedColumnNames);
	}
	
	/**
	 * Gets the referenced column names.
	 *
	 * @return the referenced column names
	 */
	public Set<String> getReferencedColumnNames() {
		if (referencedColumnNames != null) {
			return referencedColumnNames;
		}
		referencedColumnNames = new HashSet<>();
		if (referencedColumns != null) {
			Arrays.asList(referencedColumns.split(",")).forEach(n -> referencedColumnNames.add(n));;
		}
		return referencedColumnNames;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "TableConstraintForeignKey [id=" + id + ", referencedTable=" + referencedTable + ", referencedColumns="
				+ referencedColumns + ", name=" + name + ", modifiers=" + modifiers + ", columns=" + columns
				+ ", constraints.table=" + constraints.getTable().getName() + "]";
	}
	
	

}
