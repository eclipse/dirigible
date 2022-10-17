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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class TableConstraint.
 */
@MappedSuperclass
public abstract class TableConstraint {
	
	/** The name. */
	@Column(name = "CONSTRAINT_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	protected String name;
	
	/** The modifiers. */
	@Column(name = "CONSTRAINT_MODIFIERS", columnDefinition = "VARCHAR", nullable = false, length = 2000)
	protected String modifiers;
	
	/** The columns. */
	@Column(name = "CONSTRAINT_COLUMNS", columnDefinition = "VARCHAR", nullable = false, length = 2000)
	protected String columns;
	
	/** The constraints. */
	@OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "CONSTRAINTS_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    protected TableConstraints constraints;
	
	/** The modifier names. */
	@Transient
	protected transient Set<String> modifierNames;
	
	/** The column names. */
	@Transient
	protected transient Set<String> columnNames;

	/**
	 * Instantiates a new table constraint.
	 *
	 * @param name the name
	 * @param modifiers the modifiers
	 * @param columns the columns
	 * @param constraints the constraints
	 */
	public TableConstraint(String name, String modifiers, String columns, TableConstraints constraints) {
		super();
		this.name = name;
		this.modifiers = modifiers;
		this.columns = columns;
		this.constraints = constraints;
	}

	/**
	 * Instantiates a new table constraint.
	 */
	public TableConstraint() {
		super();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the modifiers.
	 *
	 * @return the modifiers
	 */
	public String getModifiers() {
		return modifiers;
	}

	/**
	 * Sets the modifiers.
	 *
	 * @param modifiers the modifiers to set
	 */
	public void setModifiers(String modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * Gets the columns.
	 *
	 * @return the columns
	 */
	public String getColumns() {
		return columns;
	}

	/**
	 * Sets the columns.
	 *
	 * @param columns the columns to set
	 */
	public void setColumns(String columns) {
		this.columns = columns;
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
	 * Adds the modifier.
	 *
	 * @param modifier the modifier
	 */
	public void addModifier(String modifier) {
		if (modifiers == null && modifiers.trim().length() == 0) {
			modifiers = modifier;
			return;
		}
		modifiers += "," + modifier;
		if (modifierNames != null) {
			modifierNames.add(modifier);
		}
	}
	
	/**
	 * Removes the modifier.
	 *
	 * @param modifier the modifier
	 */
	public void removeModifier(String modifier) {
		if (modifierNames == null) {
			modifierNames = getModifierNames();
		}
		modifierNames.remove(modifier);
		modifiers = String.join(",", modifierNames);
	}
	
	/**
	 * Gets the modifier names.
	 *
	 * @return the modifier names
	 */
	public Set<String> getModifierNames() {
		if (modifierNames != null) {
			return modifierNames;
		}
		modifierNames = new HashSet<>();
		if (modifiers != null) {
			Arrays.asList(modifiers.split(",")).forEach(n -> modifierNames.add(n));;
		}
		return modifierNames;
	}
	
	/**
	 * Adds the column.
	 *
	 * @param column the column
	 */
	public void addColumn(String column) {
		if (columns == null && columns.trim().length() == 0) {
			columns = column;
			return;
		}
		columns += "," + column;
		if (columnNames != null) {
			columnNames.add(column);
		}
	}
	
	/**
	 * Removes the column.
	 *
	 * @param column the column
	 */
	public void removeColumn(String column) {
		if (columnNames == null) {
			columnNames = getColumnNames();
		}
		columnNames.remove(column);
		columns = String.join(",", columnNames);
	}
	
	/**
	 * Gets the column names.
	 *
	 * @return the column names
	 */
	public Set<String> getColumnNames() {
		if (columnNames != null) {
			return columnNames;
		}
		columnNames = new HashSet<>();
		if (columns != null) {
			Arrays.asList(columns.split(",")).forEach(n -> columnNames.add(n));;
		}
		return columnNames;
	}

}
