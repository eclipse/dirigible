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

public class DataStructureTableConstraintsModel {

	private DataStructureTableConstraintPrimaryKeyModel primaryKey;

	private DataStructureTableConstraintForeignKeyModel[] foreignKeys;

	private DataStructureTableConstraintUniqueModel[] uniqueIndices;

	private DataStructureTableConstraintCheckModel[] checks;

	public DataStructureTableConstraintPrimaryKeyModel getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(DataStructureTableConstraintPrimaryKeyModel primaryKey) {
		this.primaryKey = primaryKey;
	}

	public DataStructureTableConstraintForeignKeyModel[] getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(DataStructureTableConstraintForeignKeyModel[] foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	public DataStructureTableConstraintUniqueModel[] getUniqueIndices() {
		return uniqueIndices;
	}

	public void setUniqueIndices(DataStructureTableConstraintUniqueModel[] uniqueIndices) {
		this.uniqueIndices = uniqueIndices;
	}

	public DataStructureTableConstraintCheckModel[] getChecks() {
		return checks;
	}

	public void setChecks(DataStructureTableConstraintCheckModel[] checks) {
		this.checks = checks;
	}

}
