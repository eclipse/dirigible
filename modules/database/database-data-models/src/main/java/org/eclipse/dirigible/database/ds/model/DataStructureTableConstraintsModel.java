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

import java.util.ArrayList;
import java.util.List;

/**
 * The Data Structure Table Constraints Model.
 */
public class DataStructureTableConstraintsModel {

	/** The primary key. */
	private DataStructureTableConstraintPrimaryKeyModel primaryKey;

	/** The foreign keys. */
	private List<DataStructureTableConstraintForeignKeyModel> foreignKeys = new ArrayList<DataStructureTableConstraintForeignKeyModel>();

	/** The unique indices. */
	private List<DataStructureTableConstraintUniqueModel> uniqueIndices = new ArrayList<DataStructureTableConstraintUniqueModel>();

	/** The checks. */
	private List<DataStructureTableConstraintCheckModel> checks = new ArrayList<DataStructureTableConstraintCheckModel>();

	/**
	 * Gets the primary key.
	 *
	 * @return the primary key
	 */
	public DataStructureTableConstraintPrimaryKeyModel getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * Sets the primary key.
	 *
	 * @param primaryKey the new primary key
	 */
	public void setPrimaryKey(DataStructureTableConstraintPrimaryKeyModel primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * Gets the foreign keys.
	 *
	 * @return the foreign keys
	 */
	public List<DataStructureTableConstraintForeignKeyModel> getForeignKeys() {
		return foreignKeys;
	}

	/**
	 * Gets the unique indices.
	 *
	 * @return the unique indices
	 */
	public List<DataStructureTableConstraintUniqueModel> getUniqueIndices() {
		return uniqueIndices;
	}

	/**
	 * Gets the checks.
	 *
	 * @return the checks
	 */
	public List<DataStructureTableConstraintCheckModel> getChecks() {
		return checks;
	}

}
