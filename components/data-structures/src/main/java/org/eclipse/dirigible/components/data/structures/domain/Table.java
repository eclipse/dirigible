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
package org.eclipse.dirigible.components.data.structures.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.google.gson.annotations.Expose;

/**
 * The Class Table.
 */
@Entity
@javax.persistence.Table(name = "DIRIGIBLE_DATA_TABLES")
public class Table extends Artefact {
	
	/** The Constant ARTEFACT_TYPE. */
	public static final String ARTEFACT_TYPE = "table";
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TABLE_ID", nullable = false)
	private Long id;
	
	/** The name. */
	@Column(name = "TABLE_NAME", columnDefinition = "VARCHAR", nullable = true, length = 255)
	@Expose
	protected String tableName;
	
	/** The key. */
	@Column(name = "TABLE_TYPE", columnDefinition = "VARCHAR", nullable = true, length = 255)
	@Expose
	protected String tableType;
	
	/** The name. */
	@Column(name = "TABLE_SCHEMA", columnDefinition = "VARCHAR", nullable = true, length = 255)
	@Expose
	protected String schemaName;
	
	/** The columns. */
	@OneToMany(mappedBy = "table", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Expose
	private List<TableColumn> columns = new ArrayList<TableColumn>();
	
	/** The indexes. */
	@OneToMany(mappedBy = "table", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Nullable
	@Expose
	private List<TableIndex> indexes = new ArrayList<TableIndex>();

	/** The constraints. */
	@OneToOne(mappedBy = "table", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
	@Nullable
	@Expose
	private TableConstraints constraints;

	/**
	 * Instantiates a new table.
	 *
	 * @param location the location
	 * @param name the name
	 * @param description the description
	 * @param dependencies the dependencies
	 * @param tableName the table name
	 * @param tableType the table type
	 * @param schemaName the schema name
	 */
	public Table(String location, String name, String description, String dependencies, String tableName, String tableType, String schemaName) {
		super(location, name, ARTEFACT_TYPE, description, dependencies);
		this.constraints = new TableConstraints(this);
		this.tableName = tableName;
		this.tableType = tableType;
		this.schemaName = schemaName;
	}
	
	/**
	 * Instantiates a new table.
	 *
	 * @param tableName the table name
	 */
	public Table(String tableName) {
		this(tableName, tableName, null, null, tableName, "TABLE", "");
	}
	
	/**
	 * Instantiates a new table.
	 */
	public Table() {
		super();
		this.constraints = new TableConstraints();
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
	 * Gets the table name.
	 *
	 * @return the tableName
	 */
	public String getTableName() {
		if (tableName == null) {
			return name;
		}
		return tableName;
	}

	/**
	 * Sets the table name.
	 *
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Gets the table type.
	 *
	 * @return the tableType
	 */
	public String getTableType() {
		if (tableType == null) {
			return type;
		}
		return tableType;
	}

	/**
	 * Sets the table type.
	 *
	 * @param tableType the tableType to set
	 */
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	
	/**
	 * Gets the schema name.
	 *
	 * @return the schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * Sets the schema name.
	 *
	 * @param schemaName the schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * Gets the columns.
	 *
	 * @return the columns
	 */
	public List<TableColumn> getColumns() {
		return columns;
	}

	/**
	 * Sets the columns.
	 *
	 * @param columns the columns to set
	 */
	public void setColumns(List<TableColumn> columns) {
		this.columns = columns;
	}

	/**
	 * Gets the indexes.
	 *
	 * @return the indexes
	 */
	public List<TableIndex> getIndexes() {
		return indexes;
	}

	/**
	 * Sets the indexes.
	 *
	 * @param indexes the indexes to set
	 */
	public void setIndexes(List<TableIndex> indexes) {
		this.indexes = indexes;
	}
	
	/**
	 * Gets the constraints.
	 *
	 * @return the constraints
	 */
	public TableConstraints getConstraints() {
		return constraints;
	}

	/**
	 * Sets the constraints.
	 *
	 * @param constraints the constraints to set
	 */
	public void setConstraints(TableConstraints constraints) {
		this.constraints = constraints;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "Table [id=" + id + ", columns=" + (columns != null ? Objects.toString(columns) : "null") 
				+ ", indexes=" + (indexes != null ? Objects.toString(indexes) : "null")
				+ ", constraints=" + constraints
				+ "]";
	}

	/**
	 * Adds the column.
	 *
	 * @param name the name
	 * @param type the type
	 * @param length the length
	 * @param nullable the nullable
	 * @param primaryKey the primary key
	 * @param defaultValue the default value
	 * @param scale the scale
	 * @param unique the unique
	 * @return the table column
	 */
	public TableColumn addColumn(String name, String type, String length, boolean nullable, boolean primaryKey,
			String defaultValue, String scale, boolean unique) {
		TableColumn tableColumn = new TableColumn(name, type, length, nullable, primaryKey, defaultValue, scale, unique, this);
		columns.add(tableColumn);
		return tableColumn;
	}
	
	/**
	 * Adds the index.
	 *
	 * @param name the name
	 * @param type the type
	 * @param unique the unique
	 * @param columns the columns
	 * @return the table index
	 */
	public TableIndex addIndex(String name, String type, boolean unique, String[] columns) {
		TableIndex tableIndex = new TableIndex(name, type, unique, columns, this);
		indexes.add(tableIndex);
		return tableIndex;
	}
	
	
	
}
