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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class TableConstraints.
 */
@Entity
@javax.persistence.Table(name = "DIRIGIBLE_TABLE_CONSTRAINTS")
public class TableConstraints {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CONSTRAINTS_ID", nullable = false)
	private Long id;
	
	/** The primary key. */
	@OneToOne(mappedBy = "constraints", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
	@Nullable
	private TableConstraintPrimaryKey primaryKey;

	/** The foreign keys. */
	@OneToMany(mappedBy = "constraints", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Nullable
	private List<TableConstraintForeignKey> foreignKeys = new ArrayList<TableConstraintForeignKey>();

	/** The unique indices. */
	@OneToMany(mappedBy = "constraints", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Nullable
	private List<TableConstraintUnique> uniqueIndexes = new ArrayList<TableConstraintUnique>();

	/** The checks. */
	@OneToMany(mappedBy = "constraints", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Nullable
	private List<TableConstraintCheck> checks = new ArrayList<TableConstraintCheck>();
	
	/** The table. */
	@OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "TABLE_ID", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Table table;

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
	 * Gets the primary key.
	 *
	 * @return the primaryKey
	 */
	public TableConstraintPrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * Sets the primary key.
	 *
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(TableConstraintPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * Gets the foreign keys.
	 *
	 * @return the foreignKeys
	 */
	public List<TableConstraintForeignKey> getForeignKeys() {
		return foreignKeys;
	}

	/**
	 * Sets the foreign keys.
	 *
	 * @param foreignKeys the foreignKeys to set
	 */
	public void setForeignKeys(List<TableConstraintForeignKey> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	/**
	 * Gets the unique indexes.
	 *
	 * @return the uniqueIndexes
	 */
	public List<TableConstraintUnique> getUniqueIndexes() {
		return uniqueIndexes;
	}

	/**
	 * Sets the unique indexes.
	 *
	 * @param uniqueIndexes the uniqueIndexes to set
	 */
	public void setUniqueIndexes(List<TableConstraintUnique> uniqueIndexes) {
		this.uniqueIndexes = uniqueIndexes;
	}

	/**
	 * Gets the checks.
	 *
	 * @return the checks
	 */
	public List<TableConstraintCheck> getChecks() {
		return checks;
	}

	/**
	 * Sets the checks.
	 *
	 * @param checks the checks to set
	 */
	public void setChecks(List<TableConstraintCheck> checks) {
		this.checks = checks;
	}

	/**
	 * Gets the table.
	 *
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Sets the table.
	 *
	 * @param table the table to set
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "TableConstraints [id=" + id + ", primaryKey=" + primaryKey + ", foreignKeys=" + Objects.toString(foreignKeys)
				+ ", uniqueIndexes=" + Objects.toString(uniqueIndexes)  + ", checks=" + Objects.toString(checks) + ", table=" + table.getName() + "]";
	}
	
	

}
