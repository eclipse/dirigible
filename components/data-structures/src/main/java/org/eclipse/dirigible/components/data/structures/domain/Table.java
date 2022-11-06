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

/**
 * The Class Extension.
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
	
	/** The columns. */
	@OneToMany(mappedBy = "table", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<TableColumn> columns = new ArrayList<TableColumn>();
	
	/** The indexes. */
	@OneToMany(mappedBy = "table", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Nullable
	private List<TableIndex> indexes = new ArrayList<TableIndex>();

	/** The constraints. */
	@OneToOne(mappedBy = "table", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
	@Nullable
	private TableConstraints constraints = new TableConstraints();

	/**
	 * Instantiates a new table.
	 *
	 * @param location the location
	 * @param name the name
	 * @param description the description
	 */
	public Table(String location, String name, String description, String dependencies) {
		super(location, name, ARTEFACT_TYPE, description, dependencies);
	}
	
	/**
	 * Instantiates a new extension.
	 */
	public Table() {
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
	 * @return the columns
	 */
	public List<TableColumn> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<TableColumn> columns) {
		this.columns = columns;
	}

	/**
	 * @return the indexes
	 */
	public List<TableIndex> getIndexes() {
		return indexes;
	}

	/**
	 * @param indexes the indexes to set
	 */
	public void setIndexes(List<TableIndex> indexes) {
		this.indexes = indexes;
	}
	
	/**
	 * @return the constraints
	 */
	public TableConstraints getConstraints() {
		return constraints;
	}

	/**
	 * @param constraints the constraints to set
	 */
	public void setConstraints(TableConstraints constraints) {
		this.constraints = constraints;
	}

	@Override
	public String toString() {
		return "Table [id=" + id + ", columns=" + (columns != null ? Objects.toString(columns) : "null") 
				+ ", indexes=" + (indexes != null ? Objects.toString(indexes) : "null")
				+ ", constraints=" + constraints
				+ "]";
	}

	public TableColumn addColumn(String name, String type, String length, boolean nullable, boolean primaryKey,
			String defaultValue, String scale, boolean unique) {
		TableColumn tableColumn = new TableColumn(name, type, length, nullable, primaryKey, defaultValue, scale, unique, this);
		columns.add(tableColumn);
		return tableColumn;
	}
	
	public TableIndex addIndex(String name, String type, boolean unique, String columns) {
		TableIndex tableIndex = new TableIndex(name, type, unique, columns, this);
		indexes.add(tableIndex);
		return tableIndex;
	}
	
	
	
}
