/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.ds.model;

// TODO: Auto-generated Javadoc
/**
 * The Class DataStructureTableConstraintsModel.
 */
public class DataStructureTableConstraintsModel {

	/** The primary key. */
	private DataStructureTableConstraintPrimaryKeyModel primaryKey;

	/** The foreign keys. */
	private DataStructureTableConstraintForeignKeyModel[] foreignKeys;

	/** The unique indices. */
	private DataStructureTableConstraintUniqueModel[] uniqueIndices;

	/** The checks. */
	private DataStructureTableConstraintCheckModel[] checks;

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
	public DataStructureTableConstraintForeignKeyModel[] getForeignKeys() {
		return foreignKeys;
	}

	/**
	 * Sets the foreign keys.
	 *
	 * @param foreignKeys the new foreign keys
	 */
	public void setForeignKeys(DataStructureTableConstraintForeignKeyModel[] foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	/**
	 * Gets the unique indices.
	 *
	 * @return the unique indices
	 */
	public DataStructureTableConstraintUniqueModel[] getUniqueIndices() {
		return uniqueIndices;
	}

	/**
	 * Sets the unique indices.
	 *
	 * @param uniqueIndices the new unique indices
	 */
	public void setUniqueIndices(DataStructureTableConstraintUniqueModel[] uniqueIndices) {
		this.uniqueIndices = uniqueIndices;
	}

	/**
	 * Gets the checks.
	 *
	 * @return the checks
	 */
	public DataStructureTableConstraintCheckModel[] getChecks() {
		return checks;
	}

	/**
	 * Sets the checks.
	 *
	 * @param checks the new checks
	 */
	public void setChecks(DataStructureTableConstraintCheckModel[] checks) {
		this.checks = checks;
	}

}
